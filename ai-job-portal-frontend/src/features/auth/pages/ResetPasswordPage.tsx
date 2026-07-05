import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useSearchParams } from "react-router-dom";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import {
  resetPasswordSchema,
  type ResetPasswordFormValues,
} from "@/features/auth/schemas/auth.schema";
import { useResetPassword } from "@/features/auth/hooks/useAuthMutations";

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token") ?? "";
  const resetPassword = useResetPassword();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetPasswordFormValues>({ resolver: zodResolver(resetPasswordSchema) });

  if (!token) {
    return (
      <Card>
        <h1 className="text-xl font-semibold">Invalid or missing link</h1>
        <p className="mt-2 text-sm text-[hsl(var(--muted))]">
          This password reset link is missing its token. Please request a new one.
        </p>
      </Card>
    );
  }

  return (
    <Card>
      <h1 className="text-xl font-semibold">Set a new password</h1>
      <form
        className="mt-6 space-y-4"
        onSubmit={handleSubmit((values) => resetPassword.mutate({ token, newPassword: values.newPassword }))}
      >
        <FormField label="New password" htmlFor="newPassword" required error={errors.newPassword?.message}>
          <Input id="newPassword" type="password" autoComplete="new-password" {...register("newPassword")} />
        </FormField>
        <FormField
          label="Confirm password"
          htmlFor="confirmPassword"
          required
          error={errors.confirmPassword?.message}
        >
          <Input id="confirmPassword" type="password" autoComplete="new-password" {...register("confirmPassword")} />
        </FormField>
        <Button type="submit" className="w-full" isLoading={resetPassword.isPending}>
          Reset Password
        </Button>
      </form>
    </Card>
  );
}
