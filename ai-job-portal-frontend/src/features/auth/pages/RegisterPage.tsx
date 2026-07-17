import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link } from "react-router-dom";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import { Label } from "@/components/ui/label";
import { registerSchema, type RegisterFormValues } from "@/features/auth/schemas/auth.schema";
import { useRegister } from "@/features/auth/hooks/useAuthMutations";
import { GoogleAuthButton } from "@/features/auth/components/GoogleAuthButton";
import { ROUTES } from "@/constants/routes";
import { cn } from "@/lib/cn";

export default function RegisterPage() {
  const registerMutation = useRegister();
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { role: "CANDIDATE" },
  });
  const role = watch("role");

  return (
    <Card>
      <h1 className="text-xl font-semibold">Create your account</h1>
      <p className="mt-1 text-sm text-[hsl(var(--muted))]">Join AI Job Portal as a candidate or recruiter.</p>

      <form
        className="mt-6 space-y-4"
        onSubmit={handleSubmit((values) =>
          registerMutation.mutate({
            firstName: values.firstName,
            lastName: values.lastName,
            email: values.email,
            password: values.password,
            role: values.role,
          }),
        )}
      >
        <div>
          <Label htmlFor="role">I am a</Label>
          <div className="grid grid-cols-2 gap-2">
            {(["CANDIDATE", "RECRUITER"] as const).map((r) => (
              <button
                key={r}
                type="button"
                onClick={() => setValue("role", r, { shouldValidate: true })}
                className={cn(
                  "rounded-lg border px-3 py-2 text-sm font-medium capitalize transition-colors",
                  role === r
                    ? "border-primary-600 bg-primary-600 text-white"
                    : "border-[hsl(var(--border-color))] hover:bg-[hsl(var(--border-color))]/40",
                )}
              >
                {r.charAt(0) + r.slice(1).toLowerCase()}
              </button>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-2 gap-3">
          <FormField label="First name" htmlFor="firstName" required error={errors.firstName?.message}>
            <Input id="firstName" autoComplete="given-name" {...register("firstName")} />
          </FormField>
          <FormField label="Last name" htmlFor="lastName" required error={errors.lastName?.message}>
            <Input id="lastName" autoComplete="family-name" {...register("lastName")} />
          </FormField>
        </div>

        <FormField label="Email" htmlFor="email" required error={errors.email?.message}>
          <Input id="email" type="email" autoComplete="email" {...register("email")} />
        </FormField>

        <FormField label="Password" htmlFor="password" required error={errors.password?.message}>
          <Input id="password" type="password" autoComplete="new-password" {...register("password")} />
        </FormField>

        <FormField
          label="Confirm password"
          htmlFor="confirmPassword"
          required
          error={errors.confirmPassword?.message}
        >
          <Input id="confirmPassword" type="password" autoComplete="new-password" {...register("confirmPassword")} />
        </FormField>

        <Button type="submit" className="w-full" isLoading={registerMutation.isPending}>
          Create Account
        </Button>
      </form>

      <div className="mt-6 flex items-center gap-3 text-xs text-[hsl(var(--muted))]">
        <div className="h-px flex-1 bg-[hsl(var(--border-color))]" />
        <span>or</span>
        <div className="h-px flex-1 bg-[hsl(var(--border-color))]" />
      </div>

      <div className="mt-4">
        <GoogleAuthButton role={role} />
      </div>

      <p className="mt-6 text-center text-sm text-[hsl(var(--muted))]">
        Already have an account?{" "}
        <Link to={ROUTES.LOGIN} className="font-medium text-primary-600 hover:underline">
          Log in
        </Link>
      </p>
    </Card>
  );
}
