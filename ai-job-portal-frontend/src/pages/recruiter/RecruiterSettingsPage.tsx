import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { LogOut, Monitor, Moon, Sun } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { Skeleton } from "@/components/ui/skeleton";
import { useAuth } from "@/hooks/useAuth";
import { useTheme } from "@/hooks/useTheme";
import { tokenStorage } from "@/lib/storage";
import { formatEnumLabel } from "@/utils/format";
import { useChangePassword, useLogout } from "@/features/auth/hooks/useAuthMutations";
import { changePasswordSchema, type ChangePasswordFormValues } from "@/features/auth/schemas/auth.schema";
import {
  useRecruiterProfile,
  useUpdateRecruiterProfile,
} from "@/features/recruiter-profile/hooks/useRecruiterProfile";
import {
  recruiterProfileFormSchema,
  type RecruiterProfileFormValues,
} from "@/features/recruiter-profile/schemas/recruiter-profile.schema";
import {
  useNotificationPreferences,
  useUpdateNotificationPreferences,
} from "@/features/notifications/hooks/useNotifications";
import type { Theme } from "@/types/auth";

const THEME_OPTIONS: { value: Theme; label: string; icon: typeof Sun }[] = [
  { value: "light", label: "Light", icon: Sun },
  { value: "dark", label: "Dark", icon: Moon },
  { value: "system", label: "System", icon: Monitor },
];

const RECRUITER_TITLES = [
  "HR_MANAGER",
  "TALENT_ACQUISITION_SPECIALIST",
  "RECRUITMENT_CONSULTANT",
  "HIRING_MANAGER",
  "FOUNDER",
  "CO_FOUNDER",
  "CEO",
  "OTHER",
] as const;

export default function RecruiterSettingsPage() {
  const { user } = useAuth();
  const { theme, setTheme } = useTheme();
  const changePassword = useChangePassword();
  const logout = useLogout();

  const { data: recruiterProfile, isLoading: profileLoading } = useRecruiterProfile();
  const updateRecruiterProfile = useUpdateRecruiterProfile();
  const { data: preferences, isLoading: preferencesLoading } = useNotificationPreferences();
  const updatePreferences = useUpdateNotificationPreferences();

  const {
    register: registerPassword,
    handleSubmit: handlePasswordSubmit,
    reset: resetPassword,
    formState: { errors: passwordErrors },
  } = useForm<ChangePasswordFormValues>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: { currentPassword: "", newPassword: "", confirmPassword: "" },
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Settings</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Manage your account, company access, and preferences.</p>
      </div>

      <Card>
        <h2 className="text-base font-semibold">Account</h2>
        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <div>
            <p className="text-xs text-[hsl(var(--muted))]">Full Name</p>
            <p className="mt-1 text-sm font-medium">
              {user?.firstName} {user?.lastName}
            </p>
          </div>
          <div>
            <p className="text-xs text-[hsl(var(--muted))]">Email</p>
            <p className="mt-1 text-sm font-medium">{user?.email}</p>
          </div>
        </div>
      </Card>

      <Card>
        <h2 className="text-base font-semibold">Recruiter profile</h2>
        {profileLoading ? (
          <Skeleton className="mt-4 h-24 w-full" />
        ) : recruiterProfile ? (
          <RecruiterProfileForm
            profile={recruiterProfile}
            isSubmitting={updateRecruiterProfile.isPending}
            onSubmit={(values) =>
              updateRecruiterProfile.mutate({
                title: values.title,
                phoneNumber: values.phoneNumber || null,
                designation: values.designation || null,
              })
            }
          />
        ) : (
          <p className="mt-2 text-sm text-[hsl(var(--muted))]">No data available.</p>
        )}
      </Card>

      <Card>
        <h2 className="text-base font-semibold">Change Password</h2>
        <form
          onSubmit={handlePasswordSubmit((values) =>
            changePassword.mutate(
              { currentPassword: values.currentPassword, newPassword: values.newPassword },
              { onSuccess: () => resetPassword() },
            ),
          )}
          className="mt-4 max-w-md space-y-4"
        >
          <FormField label="Current Password" htmlFor="currentPassword" required error={passwordErrors.currentPassword?.message}>
            <Input id="currentPassword" type="password" {...registerPassword("currentPassword")} />
          </FormField>
          <FormField label="New Password" htmlFor="newPassword" required error={passwordErrors.newPassword?.message}>
            <Input id="newPassword" type="password" {...registerPassword("newPassword")} />
          </FormField>
          <FormField label="Confirm New Password" htmlFor="confirmPassword" required error={passwordErrors.confirmPassword?.message}>
            <Input id="confirmPassword" type="password" {...registerPassword("confirmPassword")} />
          </FormField>
          <Button type="submit" isLoading={changePassword.isPending}>
            Update Password
          </Button>
        </form>
      </Card>

      <Card>
        <h2 className="text-base font-semibold">Notification preferences</h2>
        {preferencesLoading ? (
          <Skeleton className="mt-4 h-16 w-full" />
        ) : preferences ? (
          <div className="mt-4 space-y-3">
            {(["emailEnabled", "pushEnabled", "inAppEnabled"] as const).map((key) => (
              <label key={key} className="flex items-center justify-between rounded-lg border border-[hsl(var(--border-color))] px-4 py-3 text-sm">
                <span>
                  {key === "emailEnabled" && "Email notifications"}
                  {key === "pushEnabled" && "Push notifications"}
                  {key === "inAppEnabled" && "In-app notifications"}
                </span>
                <input
                  type="checkbox"
                  checked={preferences[key]}
                  onChange={(e) =>
                    updatePreferences.mutate({
                      emailEnabled: preferences.emailEnabled,
                      pushEnabled: preferences.pushEnabled,
                      inAppEnabled: preferences.inAppEnabled,
                      [key]: e.target.checked,
                    })
                  }
                  className="h-4 w-4 accent-primary-600"
                />
              </label>
            ))}
          </div>
        ) : (
          <p className="mt-2 text-sm text-[hsl(var(--muted))]">No data available.</p>
        )}
      </Card>

      <Card>
        <h2 className="text-base font-semibold">Theme</h2>
        <div className="mt-4 flex gap-2">
          {THEME_OPTIONS.map(({ value, label, icon: Icon }) => (
            <button
              key={value}
              type="button"
              onClick={() => setTheme(value)}
              className={`flex items-center gap-2 rounded-lg border px-4 py-2 text-sm font-medium transition-colors ${
                theme === value
                  ? "border-primary-600 bg-primary-600/10 text-primary-600"
                  : "border-[hsl(var(--border-color))] text-[hsl(var(--muted))] hover:text-[hsl(var(--foreground))]"
              }`}
            >
              <Icon className="h-4 w-4" /> {label}
            </button>
          ))}
        </div>
      </Card>

      <Card>
        <h2 className="text-base font-semibold">Session</h2>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Sign out of your account on this device.</p>
        <Button
          variant="danger"
          className="mt-4"
          onClick={() => logout.mutate(tokenStorage.getRefreshToken() ?? "")}
          isLoading={logout.isPending}
        >
          <LogOut className="h-4 w-4" /> Logout
        </Button>
      </Card>
    </div>
  );
}

function RecruiterProfileForm({
  profile,
  isSubmitting,
  onSubmit,
}: {
  profile: NonNullable<ReturnType<typeof useRecruiterProfile>["data"]>;
  isSubmitting: boolean;
  onSubmit: (values: RecruiterProfileFormValues) => void;
}) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RecruiterProfileFormValues>({
    resolver: zodResolver(recruiterProfileFormSchema),
    defaultValues: {
      phoneNumber: profile.phoneNumber ?? "",
      title: profile.title,
      designation: profile.designation ?? "",
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="mt-4 max-w-md space-y-4">
      <FormField label="Title" htmlFor="rp-title" required error={errors.title?.message}>
        <Select id="rp-title" {...register("title")}>
          {RECRUITER_TITLES.map((t) => (
            <option key={t} value={t}>
              {formatEnumLabel(t)}
            </option>
          ))}
        </Select>
      </FormField>
      <FormField label="Designation" htmlFor="rp-designation" error={errors.designation?.message}>
        <Input id="rp-designation" {...register("designation")} />
      </FormField>
      <FormField label="Phone number" htmlFor="rp-phone" error={errors.phoneNumber?.message}>
        <Input id="rp-phone" {...register("phoneNumber")} />
      </FormField>
      <Button type="submit" isLoading={isSubmitting}>
        Save profile
      </Button>
    </form>
  );
}
