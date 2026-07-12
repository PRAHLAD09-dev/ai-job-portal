# AI Job Portal — Backend

A production-grade, AI-powered Job Portal backend built as Java Spring Boot
microservices. This README reflects the backend **as of Admin Service's
completion** — the final microservice in the planned build order
(`BACKEND_ROADMAP.md`). Frontend, integration testing, and deployment are
the remaining phases.

---

## 1. Project Overview

The platform connects **Candidates**, **Recruiters**, and **Admins**:

- Candidates build a profile, upload resumes, search/apply to jobs, and use
  AI-assisted resume analysis, scoring, and job matching.
- Recruiters manage a company profile, post and moderate their own jobs,
  and review/shortlist/hire applicants.
- Admins moderate the platform itself: users, companies, jobs, and monitor
  application/AI/notification activity, backed by an audit trail.

Architecture: microservices, one service per bounded context, no shared
database, communication via **OpenFeign** (sync) and **Kafka** (async),
fronted by a single **API Gateway**, with **Eureka** service discovery and
a centralized **Config Server**.

---

## 2. Architecture Diagram

```
                                   ┌─────────────────────┐
                                   │   React Frontend      │
                                   │ (not yet built)        │
                                   └──────────┬───────────┘
                                              │ HTTPS
                                   ┌──────────▼───────────┐
                                   │     API Gateway        │  :8080
                                   │ (Spring Cloud Gateway)  │
                                   └──────────┬───────────┘
                                              │ lb://<SERVICE-NAME>
                     ┌─────────────┬──────────┼──────────┬─────────────┬─────────────┐
                     │             │          │          │             │             │
                ┌────▼───┐   ┌────▼───┐  ┌───▼────┐ ┌───▼────┐  ┌─────▼─────┐ ┌─────▼──────┐
                │  Auth  │   │Candidate│  │Recruiter│ │  Job   │  │Application│ │     AI     │
                │ :8081  │   │ :8082   │  │ :8083   │ │ :8084  │  │  :8085    │ │   :8086    │
                └───┬────┘   └────┬────┘  └────┬────┘ └───┬────┘  └─────┬─────┘ └─────┬──────┘
                    │             │            │          │             │             │
                    │        ┌────▼─────┐      │          │        ┌────▼─────┐       │
                    │        │Notification│◄───┴──────────┴────────┤  Kafka   │◄──────┘
                    │        │  :8087    │  (async events)          │ (broker) │
                    │        └───────────┘                          └────┬─────┘
                    │                                                    │
                    │              ┌─────────────────────────────────────┘
                    │              │ publishes: UserDisabledEvent, CompanyVerifiedEvent, JobRemovedEvent
                    │        ┌─────▼──────┐
                    └───────►│   Admin    │  :8088   (internal-only Feign calls, never via gateway,
                             │  Service   │           tendpoints)
                             └────────────┘

  Cross-cutting: Eureka (:8761) — service discovery for all of the above
                 Config Server (:8888) — centralized config-repo/*.yml
                 PostgreSQL (:5432) — one database per service
                 Redis (:6379) — caching + gateway rate limiting
                 Kafka (:9092) — async domain events
```

---

## 3. Complete Microservices Architecture

| # | Service | Port | Owns | Status |
|---|---------|------|------|--------|
| 1 | Discovery Server (Eureka) | 8761 | Service registry | Done |
| 2 | Config Server | 8888 | Centralized `config-repo/*.yml` | Done |
| 3 | API Gateway | 8080 | Routing, CORS, rate limiting | Done |
| 4 | Auth Service | 8081 | Users, roles, JWT, refresh tokens | Done |
| 5 | Candidate Service | 8082 | Candidate profile, resumes, education/experience/skills | Done |
| 6 | Recruiter Service | 8083 | Company, recruiter profile, verification | Done |
| 7 | Job Service | 8084 | Job CRUD, search, categories, saved jobs | Done |
| 8 | Application Service | 8085 | Applications, interviews, offers | Done |
| 9 | AI Service | 8086 | Resume analysis, scoring, job matching, interview questions | Done |
| 10 | Notification Service | 8087 | Email + in-app notifications | Done |
| 11 | **Admin Service** | **8088** | **Dashboard, moderation, monitoring, audit logs** | **Done (new)** |

---

## 4. Project Structure

```
ai-job-portal-backend/
├── pom.xml                        # Parent reactor POM (12 modules)
├── docker-compose.yml
├── .env / .env.example
├── config-repo/                   # Config Server's file-based backend
│   ├── application.yml            # Shared config (Kafka, resilience4j defaults, etc.)
│   ├── api-gateway.yml
│   ├── auth-service.yml
│   ├── candidate-service.yml
│   ├── recruiter-service.yml
│   ├── job-service.yml
│   ├── application-service.yml
│   ├── ai-service.yml
│   ├── notification-service.yml
│   └── admin-service.yml         
├── docker/postgres-init/          # Multi-database init script (now incl. admin_service_db)
├── common/                        # Shared, framework-light: ApiResponse, exceptions, correlation ID
├── discovery-server/
├── config-server/
├── api-gateway/
├── auth-service/
│   └── .../admin/                 
├── candidate-service/
├── recruiter-service/
│   └── .../admin/              
├── job-service/
│   └── .../admin/                 
├── application-service/
│   └── .../admin/                 
├── ai-service/
│   └── .../admin/                 
├── notification-service/
│   └── .../admin/                
└── admin-service/                 
    ├── pom.xml
    ├── Dockerfile
    └── src/main/java/com/prahlad/aijobportal/adminservice/
        ├── AdminServiceApplication.java
        ├── security/               # JWT validation, ADMIN/SUPER_ADMIN RBAC
        ├── config/                 # BaseEntity, JPA auditing, OpenAPI
        ├── feign/                  # 6 Feign clients + local DTO mirrors
        ├── event/                  # Kafka publisher + event DTOs
        ├── exception/               # GlobalExceptionHandler
        ├── auditlog/                # The ONLY entity Admin Service persists
        ├── dashboard/
        ├── usermanagement/
        ├── companymanagement/
        ├── jobmanagement/
        ├── applicationmonitoring/
        ├── aimonitoring/
        └── notificationmonitoring/
```

Every service follows the same **feature-based package structure**
(`controller / service / service.impl / repository / entity / dto /
mapper / exception), per `PROJECT_RULES.md` §5.

---

## 5. Service Responsibilities

See `PROJECT_SPECIFICATION.md` §18 for the authoritative module-boundary
table. In short: each service owns exactly one bounded context and never
reaches into another service's database. **Admin Service owns only its
own `audit_logs` table** — every statistic and moderation action is a
live call into the service that actually owns that data (Auth, Recruiter,
Job, Application, AI, Notification), never a duplicated business rule.

---

## 6. Communication Flow

- **Synchronous** reads/writes between services → **OpenFeign**, resolved
  via Eureka (`lb://SERVICE-NAME`), wrapped in **Resilience4j**
  Circuit Breaker + Retry.
- **Asynchronous** domain events → **Kafka**. Producers never know their
  consumers.
- **Client → backend** → always through the **API Gateway**; no service
  is meant to be called directly from outside the Docker network.

---

## 7. OpenFeign Architecture

### Pre-existing (Phases 2–8)
Candidate/Recruiter/Job/Application/AI/Notification services call each
other's regular authenticated APIs where needed (e.g. Application Service
→ Candidate/Job Service).

### New: Admin Service's internal-only endpoints (Admin Service addition)

Because none of the six downstream services previously exposed
admin/moderation/statistics endpoints, each gained a small, additive,
**internal-only** API surface that Admin Service calls via Feign:

| Downstream service | New internal path (never routed through the Gateway) |
|---|---|
| Auth Service | `/api/v1/auth/internal/admin/users/**` |
| Recruiter Service | `/api/v1/companies/internal/admin/**` |
| Job Service | `/api/v1/jobs/internal/admin/**` |
| Application Service | `/api/v1/applications/internal/admin/statistics` |
| AI Service | `/api/v1/ai/internal/admin/statistics` |
| Notification Service | `/api/v1/notifications/internal/admin/statistics` |

**Two independent layers of protection** on every one of these:

1. **`InternalServiceAuthFilter`** on each downstream service validates a
   shared secret header (`X-Internal-Service-Token`) before granting
   `ROLE_INTERNAL_SERVICE` — the same pattern Auth Service already used
   for Notification Service's user lookups.
2. **API Gateway blocking route** (`config-repo/api-gateway.yml`): a
   `block-internal-service-endpoints` route matching `/api/v1/*/internal/**`
   returns `404` before the broader per-service routes get a chance to
   match, so these paths are never reachable from outside the Docker
   network even with a correct-looking URL.

Admin Service's own Feign clients (`AuthServiceClient`,
`RecruiterServiceClient`, `JobServiceClient`, `ApplicationServiceClient`,
`AiServiceClient`, `NotificationServiceClient`) attach this same shared
token via `FeignClientConfig`'s `RequestInterceptor` — they never forward
the calling admin's own bearer token downstream, since an admin's token is
not a credential for another service's API.

---

## 8. Kafka Events

| Event | Producer | Purpose |
|---|---|---|
| `UserRegisteredEvent` | Auth Service | New account created |
| `PasswordResetRequestedEvent` | Auth Service | Password reset requested |
| `JobAppliedEvent` | Application Service | Candidate applied |
| `CandidateHiredEvent` | Application Service | Candidate hired |
| `user-disabled` (`UserDisabledEvent`) | **Admin Service** | Admin disabled a user account |
| `company-verified` (`CompanyVerifiedEvent`) | **Admin Service** | Admin verified a company |
| `job-removed` (`JobRemovedEvent`) | **Admin Service** | Admin removed (archived) a job |

Admin Service **only publishes** — per `DAY09_ADMIN_SERVICE.md`, it
implements no Kafka consumer.

---

## 9. Docker Architecture

Every service is a two-stage Docker build (Maven build stage → slim JRE
runtime stage). Because the parent `pom.xml` is a multi-module Maven
reactor, **every** service's Dockerfile copies **every module's**
`pom.xml` (not source) before building, so the reactor can resolve the
full `<modules>` list — this is why adding `admin-service` to the parent
POM required a one-line addition
(`COPY admin-service/pom.xml admin-service/pom.xml`) to all 10 pre-existing
Dockerfiles, even though only `admin-service`'s own Dockerfile actually
compiles and packages it.

```bash
docker compose build      # builds all 12 service images
docker compose up -d      # starts infra + all services
docker compose logs -f admin-service
docker compose down       # stop
docker compose down -v    # stop + wipe volumes (fresh DBs)
```

---

## 10. API Gateway Routes

| Route ID | Path | Target |
|---|---|---|
| `block-internal-service-endpoints` | `/api/v1/*/internal/**` | **404** (safety net, see §7) |
| `auth-service` | `/api/v1/auth/**` | AUTH-SERVICE |
| `candidate-service` | `/api/v1/candidate/**` | CANDIDATE-SERVICE |
| `recruiter-service-companies` | `/api/v1/companies/**` | RECRUITER-SERVICE |
| `recruiter-service-profile` | `/api/v1/recruiter/**` | RECRUITER-SERVICE |
| `job-service-jobs` | `/api/v1/jobs/**` | JOB-SERVICE |
| `job-service-categories` | `/api/v1/job-categories/**` | JOB-SERVICE |
| `application-service-candidate` | `/api/v1/applications/**` | APPLICATION-SERVICE |
| `application-service-recruiter` | `/api/v1/recruiter/applications/**` | APPLICATION-SERVICE |
| `ai-service` | `/api/v1/ai/**` | AI-SERVICE |
| `notification-service` | `/api/v1/notifications/**` | NOTIFICATION-SERVICE |
| **`admin-service`** | **`/api/v1/admin/**`** | **ADMIN-SERVICE** |

Route order matters: the blocking route is declared first and Spring
Cloud Gateway evaluates routes in list order, first match wins.

---

## 11. Eureka

All 11 business/gateway services + the gateway itself register with
Eureka at `http://localhost:8761` (dashboard reachable there). Admin
Service registers as `ADMIN-SERVICE`, exactly like every other service —
no special-casing.

---

## 12. Config Server

File-backed (`config-repo/`), each service's `spring.application.name`
(set in its local `application.yml`) determines which
`config-repo/<name>.yml` + shared `config-repo/application.yml` it
receives at startup. Admin Service follows the identical convention
(`config-repo/admin-service.yml`).

---

## 13. Swagger URLs

| Service | Swagger UI |
|---|---|
| Auth Service | http://localhost:8081/swagger-ui.html |
| Candidate Service | http://localhost:8082/swagger-ui.html |
| Recruiter Service | http://localhost:8083/swagger-ui.html |
| Job Service | http://localhost:8084/swagger-ui.html |
| Application Service | http://localhost:8085/swagger-ui.html |
| AI Service | http://localhost:8086/swagger-ui.html |
| Notification Service | http://localhost:8087/swagger-ui.html |
| **Admin Service** | **http://localhost:8088/swagger-ui.html** |

Internal-only `/internal/**` endpoints are documented under an
"Internal - Admin" tag in each service's Swagger for maintainer visibility,
but are not reachable externally (see §7).

---

## 14. Environment Variables

See `.env.example` for the full, authoritative list. Newly added for
Admin Service:

```bash
# ---- Admin Service ----
ADMIN_SERVICE_PORT=8088

# ---- Internal service-to-service authentication ----
# Shared by Admin Service (caller) and Auth/Recruiter/Job/Application/AI/
# Notification Service (validators). Generate a long random value for
# real deployments — this is a static shared secret, not a JWT.
INTERNAL_SERVICE_TOKEN=changeme_generate_a_long_random_shared_secret
```

`JWT_SECRET` (already existed) must also be available to Admin Service —
it validates the same tokens Auth Service issues.

---

## 15. Configuration

- **Local fallback**: each service's `src/main/resources/application.yml`
  has just enough (name, port, Eureka URL, Config Server URL) to start
  even if Config Server is briefly unreachable.
- **Authoritative config**: `config-repo/<service-name>.yml`, fetched at
  startup. Admin Service's is `config-repo/admin-service.yml` — DB
  connection, JWT validation, internal token, Resilience4j instance
  bindings for all 6 downstream Feign clients, Swagger paths.
- **Shared config**: `config-repo/application.yml` — Kafka producer
  defaults, Resilience4j `default` base config, Eureka/Config Server
  client defaults. Applies to every service automatically, including
  Admin Service.

---

## 16. Build Instructions

```bash
# From the repository root
mvn clean install                       # builds every module, runs unit tests
mvn clean install -pl admin-service -am # builds only admin-service + its deps
```

> **Note**: this could not be executed inside the sandbox this backend was
> assembled in (no internet access, no Maven/Docker daemon available
> there) — every change was verified by static code review and by diffing
> against the pre-existing codebase instead. Run the above yourself before
> considering this final; see §26 for what to check.

---

## 17. Run Instructions (Local Development, no Docker)

1. Start PostgreSQL, Redis, and Kafka locally (or via `docker compose up postgres redis kafka -d`).
2. Create the databases listed in `docker/postgres-init/init-multiple-databases.sh`
   (now including `admin_service_db`).
3. Copy `.env.example` to `.env` and fill in real secrets.
4. Start, in order: `discovery-server` → `config-server` → `api-gateway` →
   every business service (any order) → `admin-service`.
5. Each service's `application.yml` local fallback lets it start even
   before Config Server is ready; it will re-fetch config as soon as
   Config Server is up.

---

## 18. Docker Commands

```bash
docker compose build                     # build all images
docker compose build admin-service       # build just this one
docker compose up -d                     # start everything, detached
docker compose up -d admin-service       # start just this one (+ its depends_on chain)
docker compose ps                        # container/health status
docker compose logs -f admin-service     # tail logs
docker compose restart admin-service
docker compose down                      # stop everything
docker compose down -v                   # stop + delete volumes (fresh DB state)
```

---

## 19. Local Development

For fast local iteration on a single service without rebuilding the whole
stack: keep infra + other services running in Docker, run the one
service you're editing from your IDE with
`-DCONFIG_SERVER_URI=http://localhost:8888 -DEUREKA_URI=http://localhost:8761/eureka/`
(or the equivalent env vars) so it joins the same Eureka/Config Server as
the containerized services.

---

## 20. Production Deployment

Not yet in scope — per `BACKEND_ROADMAP.md`, deployment is Phase 11,
after Integration Testing (Phase 10) and Frontend (Phase 9). This backend
is Docker Compose-ready for local/staging use; a production rollout
would additionally need: externalized secrets (e.g. a real secrets
manager instead of `.env`), managed Postgres/Redis/Kafka, TLS at the
gateway, and horizontal scaling policy per service.

---

## 21. Admin Service

### What it does
- **Dashboard**: aggregated platform statistics (users, recruiters,
  candidates, companies, jobs, applications) + recent activity, one call
  (`GET /api/v1/admin/dashboard`).
- **User Management**: list/search/filter/view/enable/disable users;
  **permanently deleting** a user requires `SUPER_ADMIN` specifically
  (irreversible action).
- **Company Management**: list/search/filter/view/verify/reject/suspend.
- **Job Management**: list/search/filter/view/remove/restore/feature/unfeature
  + platform job statistics.
- **Application / AI / Notification Monitoring**: read-only platform
  statistics from each respective service.
- **Audit Logs**: Login Audit, Admin Actions, Company Verification Logs,
  Job Moderation Logs — all filtered views over one `audit_logs` table
  this service owns exclusively.

### What it deliberately does NOT do
- **Does not** duplicate any business logic — every read/write against a
  User/Company/Job goes through that owning service's new internal admin
  endpoint (§7), never a shared database.
- **Does not** consume Kafka — it only publishes (§8).
- **"Login Audit" is scoped to the admin panel itself** — an
  ADMIN/SUPER_ADMIN calls `POST /api/v1/admin/audit-logs/login` right
  after authenticating, self-reporting their own login (their identity
  comes from their own validated JWT, not a request body field, so it
  can't be spoofed). Auditing *every platform user's* login would require
  Auth Service to either duplicate audit-log logic or for Admin Service to
  consume a login-event Kafka topic Auth Service doesn't currently
  publish — either would cross a module boundary the Admin Service spec
  explicitly forbids ("never duplicate business logic" / "only publish,
  never consume"). If full platform-wide login auditing becomes a real
  requirement later, the clean fix is a new `UserLoggedInEvent` published
  by Auth Service, with Admin Service (or a supporting service)
  subscribing.

### Roles
`SUPER_ADMIN` and `ADMIN` (added to Auth Service's `RoleName` enum +
seeded via a new Flyway migration). Every `/api/v1/admin/**` endpoint
requires one of these two roles; a handful of specifically destructive
actions (permanent user deletion) are further restricted to `SUPER_ADMIN`
via method-level `@PreAuthorize`.

---

## 22. Platform Features

See `PROJECT_SPECIFICATION.md` §5 for the full, authoritative feature
list across Candidate/Recruiter/Admin roles. Admin-specific features are
detailed in §21 above.

---

## 23. Technology Stack

Java 21, Spring Boot 3.x, Spring Data JPA, PostgreSQL, Flyway,
Spring Security, JWT (JJWT), OpenFeign, Kafka, Redis, Spring Cloud
Gateway, Eureka, Spring Cloud Config, Resilience4j, MapStruct,
Lombok, springdoc-openapi (Swagger), Docker/Docker Compose, Maven,
JUnit 5, Mockito, AssertJ.

---

## 24. Security

- **Authentication**: stateless JWT (HS256), issued/refreshed only by
  Auth Service; every other service validates the same token.
- **Authorization**: role claims embedded in the JWT
  (`CANDIDATE`/`RECRUITER`/`ADMIN`/`SUPER_ADMIN`); Admin Service is the
  first service with real role-gated (not just authenticated-vs-public)
  access control, enforced via Spring Security's `hasAnyRole`/`@PreAuthorize`.
- **Service-to-service**: a shared static secret
  (`X-Internal-Service-Token` header, `INTERNAL_SERVICE_TOKEN` env var),
  validated by `InternalServiceAuthFilter` in every service that exposes
  `/internal/**` endpoints, plus the API Gateway blocking route as a
  second layer (§7).
- **Passwords**: BCrypt (Auth Service only — no other service ever sees a
  password).
- **Secrets**: environment variables only, never hardcoded; see `.env.example`.

---

## 25. Folder Structure

See §4 above for the full tree.

---

## 26. Static Architecture Audit (performed, results below)

Since `mvn`/`docker` could not be run in the assembly environment, the
following checks were performed instead and all passed:

| # | Check | Result |
|---|---|---|
| 1 | Internal endpoints not publicly routable via Gateway | ✅ `block-internal-service-endpoints` route (`Path=/api/v1/*/internal/**` → 404) is declared **first** in `config-repo/api-gateway.yml`; confirmed route list order via direct file inspection |
| 2 | Internal endpoints require `INTERNAL_SERVICE_TOKEN` | ✅ `InternalServiceAuthFilter` + `hasRole(ROLE_INTERNAL_SERVICE)` confirmed present in all 6 downstream services' `SecurityConfig` |
| 3 | Public endpoints require JWT | ✅ every `SecurityConfig` ends in `.anyRequest().authenticated()`; only explicitly listed `PUBLIC_ENDPOINTS`/public GETs bypass it |
| 4 | Admin endpoints require ADMIN/SUPER_ADMIN | ✅ `admin-service`'s `SecurityConfig`: `.requestMatchers(ADMIN_ENDPOINTS).hasAnyRole("ADMIN", "SUPER_ADMIN")`; permanent user deletion additionally gated by `@PreAuthorize("hasRole('SUPER_ADMIN')")` |
| 5 | Existing routes backward compatible | ✅ diffed every pre-existing file against the original upload: only enums (additive constants), repositories (additive methods), `SecurityConfig` (additive filter wiring), and Dockerfiles (one `COPY` line) changed — zero controllers/services/DTOs of existing business logic touched |
| 6 | Feign clients use internal token | ✅ all 6 of Admin Service's `@FeignClient` interfaces declare `configuration = FeignClientConfig.class`, which attaches `X-Internal-Service-Token` via a `RequestInterceptor` |
| 7 | No duplicate business logic | ✅ every Admin Service mutation/read is a thin pass-through to the owning service's Feign client; Admin Service persists only its own `audit_logs` table |
| 8 | Gateway routing order correct | ✅ blocking route listed before all `lb://` routes (Spring Cloud Gateway matches in list order, first match wins) |
| 9 | Resilience4j applied consistently | ✅ every `@CircuitBreaker`/`@Retry` instance name used in code (`authService`, `recruiterService`, `jobService`, `applicationService`, `aiService`, `notificationService`, `dashboard`) has a matching `baseConfig: default` entry in `config-repo/admin-service.yml`, following the same instance-naming convention already used by `recruiter-service.yml`/`notification-service.yml` |
| 10 | New configuration documented | ✅ this README (§7–§21) |

Additional structural checks performed programmatically across all 99
new/modified Java files: brace/parenthesis balance, package declaration
vs. directory path consistency, class name vs. filename consistency, and
YAML/XML well-formedness for every edited `config-repo/*.yml`,
`docker-compose.yml`, and `pom.xml`. All passed.

**What this audit does NOT replace**: actual compilation
(type-checking, method resolution, MapStruct code generation) and
runtime behavior. Those require `mvn`/`docker`, which were unavailable in
the assembly environment — see the manual checklist below.

---

## 27. Manual verification checklist (run this yourself before deploying)

Before you consider this final, please run and confirm:

- [ ] `mvn clean install` — full reactor build, all unit tests pass
- [ ] `docker compose build` — all 12 images build successfully
- [ ] `docker compose up -d` — all containers reach healthy
- [ ] Eureka dashboard (`:8761`) shows all 11 services registered
- [ ] Each service's Swagger UI (§13) loads and lists its endpoints
- [ ] `GET /api/v1/admin/dashboard` (with an ADMIN/SUPER_ADMIN JWT, via
      the gateway) returns real aggregated numbers
- [ ] A request to `/api/v1/auth/internal/admin/users` through the
      gateway (`:8080`) returns `404` (confirms the blocking route works
      — this is the one piece of gateway config with no precedent
      elsewhere in the codebase, so it's worth checking explicitly)
- [ ] Disabling a user / verifying a company / removing a job produces a
      Kafka message on `user-disabled` / `company-verified` / `job-removed`
      and a new row in `admin_service_db.audit_logs`

---

## 28. Future Improvements

- Publish a `UserLoggedInEvent` from Auth Service if platform-wide (not
  just admin-panel) login auditing becomes a real requirement.
- Consider parallelizing the 6 Feign calls in `DashboardServiceImpl`
  (currently sequential) if dashboard latency becomes noticeable under
  load.
- Frontend, integration testing, and deployment (Phases 9–11 of
  `BACKEND_ROADMAP.md`) are not yet started.
