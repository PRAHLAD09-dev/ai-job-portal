import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { LogOut, Monitor, Moon, Sun } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { FormField } from "@/components/ui/form-field";
import { useAuth } from "@/hooks/useAuth";
import { useTheme } from "@/hooks/useTheme";
import { tokenStorage } from "@/lib/storage";
import { useChangePassword, useLogout } from "@/features/auth/hooks/useAuthMutations";
import { changePasswordSchema, type ChangePasswordFormValues } from "@/features/auth/schemas/auth.schema";
import type { Theme } from "@/types/auth";

const THEME_OPTIONS: { value: Theme; label: string; icon: typeof Sun }[] = [
  { value: "light", label: "Light", icon: Sun },
  { value: "dark", label: "Dark", icon: Moon },
  { value: "system", label: "System", icon: Monitor },
];

export default function CandidateSettingsPage() {
  const { user } = useAuth();
  const { theme, setTheme } = useTheme();
  const changePassword = useChangePassword();
  const logout = useLogout();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ChangePasswordFormValues>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: { currentPassword: "", newPassword: "", confirmPassword: "" },
  });

  const onSubmit = (values: ChangePasswordFormValues) => {
    changePassword.mutate(
      { currentPassword: values.currentPassword, newPassword: values.newPassword },
      { onSuccess: () => reset() },
    );
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Settings</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Manage your account, security, and preferences.</p>
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
        <h2 className="text-base font-semibold">Change Password</h2>
        <form onSubmit={handleSubmit(onSubmit)} className="mt-4 max-w-md space-y-4">
          <FormField label="Current Password" htmlFor="currentPassword" required error={errors.currentPassword?.message}>
            <Input id="currentPassword" type="password" {...register("currentPassword")} />
          </FormField>
          <FormField label="New Password" htmlFor="newPassword" required error={errors.newPassword?.message}>
            <Input id="newPassword" type="password" {...register("newPassword")} />
          </FormField>
          <FormField label="Confirm New Password" htmlFor="confirmPassword" required error={errors.confirmPassword?.message}>
            <Input id="confirmPassword" type="password" {...register("confirmPassword")} />
          </FormField>
          <Button type="submit" isLoading={changePassword.isPending}>
            Update Password
          </Button>
        </form>
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
