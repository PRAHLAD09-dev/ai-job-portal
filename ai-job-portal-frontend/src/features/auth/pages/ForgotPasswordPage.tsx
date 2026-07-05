import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link } from "react-router-dom";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import {
  forgotPasswordSchema,
  type ForgotPasswordFormValues,
} from "@/features/auth/schemas/auth.schema";
import { useForgotPassword } from "@/features/auth/hooks/useAuthMutations";
import { ROUTES } from "@/constants/routes";

export default function ForgotPasswordPage() {
  const forgotPassword = useForgotPassword();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordFormValues>({ resolver: zodResolver(forgotPasswordSchema) });

  return (
    <Card>
      <h1 className="text-xl font-semibold">Forgot your password?</h1>
      <p className="mt-1 text-sm text-[hsl(var(--muted))]">
        Enter your email and we'll send you a reset link.
      </p>

      <form
        className="mt-6 space-y-4"
        onSubmit={handleSubmit((values) => forgotPassword.mutate(values))}
      >
        <FormField label="Email" htmlFor="email" required error={errors.email?.message}>
          <Input id="email" type="email" autoComplete="email" {...register("email")} />
        </FormField>

        <Button type="submit" className="w-full" isLoading={forgotPassword.isPending}>
          Send Reset Link
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-[hsl(var(--muted))]">
        Remembered it?{" "}
        <Link to={ROUTES.LOGIN} className="font-medium text-primary-600 hover:underline">
          Back to login
        </Link>
      </p>
    </Card>
  );
}
