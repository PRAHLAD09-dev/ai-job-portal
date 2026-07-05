import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link } from "react-router-dom";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import { loginSchema, type LoginFormValues } from "@/features/auth/schemas/auth.schema";
import { useLogin } from "@/features/auth/hooks/useAuthMutations";
import { ROUTES } from "@/constants/routes";

export default function LoginPage() {
  const login = useLogin();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema) });

  return (
    <Card>
      <h1 className="text-xl font-semibold">Welcome back</h1>
      <p className="mt-1 text-sm text-[hsl(var(--muted))]">Log in to continue to AI Job Portal.</p>

      <form className="mt-6 space-y-4" onSubmit={handleSubmit((values) => login.mutate(values))}>
        <FormField label="Email" htmlFor="email" required error={errors.email?.message}>
          <Input id="email" type="email" autoComplete="email" {...register("email")} />
        </FormField>

        <FormField label="Password" htmlFor="password" required error={errors.password?.message}>
          <Input id="password" type="password" autoComplete="current-password" {...register("password")} />
        </FormField>

        <div className="flex justify-end text-sm">
          <Link to={ROUTES.FORGOT_PASSWORD} className="text-primary-600 hover:underline">
            Forgot password?
          </Link>
        </div>

        <Button type="submit" className="w-full" isLoading={login.isPending}>
          Log In
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-[hsl(var(--muted))]">
        Don't have an account?{" "}
        <Link to={ROUTES.REGISTER} className="font-medium text-primary-600 hover:underline">
          Register
        </Link>
      </p>
    </Card>
  );
}
