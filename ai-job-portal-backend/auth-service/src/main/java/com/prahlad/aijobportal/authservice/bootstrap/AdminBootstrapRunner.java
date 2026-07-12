package com.prahlad.aijobportal.authservice.bootstrap;

import com.prahlad.aijobportal.authservice.user.entity.Role;
import com.prahlad.aijobportal.authservice.user.entity.User;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import com.prahlad.aijobportal.authservice.user.repository.RoleRepository;
import com.prahlad.aijobportal.authservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Creates the platform's first administrator account on startup.
 *
 * <p>There is deliberately no self-registration path for ADMIN/SUPER_ADMIN
 * (see {@code AuthServiceImpl#SELF_REGISTERABLE_ROLES}) and no other
 * mechanism anywhere in the backend to promote a user to admin. Without
 * this runner, no admin account could ever come into existence. This
 * runner closes that gap, and only that gap:
 *
 * <ul>
 *   <li>Runs once per startup, but is idempotent: it first checks whether
 *       any user already holds {@code ADMIN} or {@code SUPER_ADMIN}, and
 *       does nothing if so - so across repeated restarts it effectively
 *       executes only once, ever.</li>
 *   <li>Reads credentials exclusively from environment variables
 *       ({@code ADMIN_BOOTSTRAP_EMAIL}, {@code ADMIN_BOOTSTRAP_PASSWORD},
 *       {@code ADMIN_BOOTSTRAP_FIRST_NAME}, {@code ADMIN_BOOTSTRAP_LAST_NAME}
 *       via {@link AdminBootstrapProperties}). Nothing is hardcoded.</li>
 *   <li>Encodes the password with the same {@link PasswordEncoder} bean
 *       (BCrypt, see {@code SecurityConfig}) used for every other account -
 *       the plaintext password is never persisted or logged.</li>
 *   <li>If required properties are missing/blank, it logs a clear warning
 *       and skips bootstrap. It never fails application startup, since a
 *       missing bootstrap credential must not take down auth for every
 *       other user.</li>
 *   <li>The created account is granted both {@code ADMIN} and
 *       {@code SUPER_ADMIN} (SUPER_ADMIN is required for some admin-service
 *       operations, e.g. user deletion), is marked email-verified and
 *       {@code ACTIVE} so it can log in immediately via the normal
 *       {@code POST /auth/login} endpoint, and is not otherwise treated
 *       specially by any other module.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Set<RoleName> ADMIN_TIER_ROLES = Set.of(RoleName.ADMIN, RoleName.SUPER_ADMIN);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminBootstrapProperties properties;

    // Self-injected proxy, resolved lazily to avoid a circular-dependency
    // error at startup. run() below calls bootstrapAdminIfMissing() through
    // THIS field rather than through 'this' — Spring's @Transactional advice
    // only fires on calls that pass through the bean's proxy, and a call
    // from one method to another on 'this' within the same class instance
    // bypasses that proxy entirely (well-known Spring AOP self-invocation
    // limitation). Without this, bootstrapAdminIfMissing() silently runs
    // with no surrounding transaction: roleRepository.findByName(...) opens
    // and commits its own short-lived transaction and returns a Role that
    // is then DETACHED by the time userRepository.save(admin) cascades to
    // persist it, which Hibernate rejects with "detached entity passed to
    // persist". Routing through the proxy keeps both calls in the one
    // @Transactional persistence context, exactly like the normal
    // POST /auth/register path (called externally, through the proxy, so
    // it was never affected by this).
    @Autowired
    @Lazy
    private AdminBootstrapRunner self;

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info("Admin bootstrap is disabled (app.admin-bootstrap.enabled=false); skipping.");
            return;
        }

        if (isBlank(properties.getEmail()) || isBlank(properties.getPassword())) {
            log.warn("Admin bootstrap skipped: ADMIN_BOOTSTRAP_EMAIL and/or ADMIN_BOOTSTRAP_PASSWORD "
                    + "are not set. No administrator account exists yet and none will be created "
                    + "until these environment variables are provided.");
            return;
        }

        try {
            self.bootstrapAdminIfMissing();
        } catch (DataIntegrityViolationException e) {
            // Another instance/replica won the race and created the admin
            // (or the email) concurrently during startup. That's fine - the
            // invariant "exactly one admin gets created" still holds; this
            // instance simply lost the race and must not treat it as fatal.
            log.info("Admin bootstrap skipped: an admin account (or that email) was created "
                    + "concurrently by another instance.");
        }
    }

    @Transactional
    void bootstrapAdminIfMissing() {
        if (userRepository.existsByRoles_NameIn(ADMIN_TIER_ROLES)) {
            log.info("Admin bootstrap skipped: an administrator account already exists.");
            return;
        }

        String normalizedEmail = properties.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            // The email is already taken by a non-admin account. Creating a
            // second account with the same email is impossible (unique
            // constraint) and silently promoting the existing account would
            // be a surprising, unrequested side effect. Fail loudly in logs
            // so an operator picks a different ADMIN_BOOTSTRAP_EMAIL.
            log.error("Admin bootstrap failed: ADMIN_BOOTSTRAP_EMAIL '{}' is already registered to a "
                    + "non-admin account. Choose a different bootstrap email or resolve the "
                    + "conflict manually; no admin account was created.", normalizedEmail);
            return;
        }

        Role adminRole = getRoleOrThrow(RoleName.ADMIN);
        Role superAdminRole = getRoleOrThrow(RoleName.SUPER_ADMIN);

        User admin = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(properties.getPassword()))
                .firstName(defaultIfBlank(properties.getFirstName(), "Platform"))
                .lastName(defaultIfBlank(properties.getLastName(), "Administrator"))
                .status(AccountStatus.ACTIVE)
                .emailVerified(true)
                .build();
        admin.addRole(adminRole);
        admin.addRole(superAdminRole);

        userRepository.save(admin);

        log.info("Admin bootstrap: created default administrator account ({}). "
                + "Log in and rotate this password immediately.", normalizedEmail);
    }

    private Role getRoleOrThrow(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException(
                        "Admin bootstrap failed: required role '" + roleName
                                + "' is missing from the roles table. Check Flyway migrations "
                                + "V2__seed_roles.sql / V3__seed_super_admin_role.sql have run."));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }
}
