import { useEffect, useState } from "react";
import { useSearchParams, Link } from "react-router-dom";
import { CheckCircle2, XCircle, Loader2 } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useVerifyEmail } from "@/features/auth/hooks/useAuthMutations";
import { ROUTES } from "@/constants/routes";

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token") ?? "";
  const verifyEmail = useVerifyEmail();
  const [status, setStatus] = useState<"pending" | "success" | "error">("pending");

  useEffect(() => {
    if (!token) {
      setStatus("error");
      return;
    }
    verifyEmail.mutate(
      { token },
      {
        onSuccess: () => setStatus("success"),
        onError: () => setStatus("error"),
      },
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  return (
    <Card className="flex flex-col items-center gap-4 text-center">
      {status === "pending" && <Loader2 className="h-10 w-10 animate-spin text-primary-600" />}
      {status === "success" && <CheckCircle2 className="h-10 w-10 text-success-500" />}
      {status === "error" && <XCircle className="h-10 w-10 text-danger-500" />}

      <h1 className="text-xl font-semibold">
        {status === "pending" && "Verifying your email..."}
        {status === "success" && "Email verified!"}
        {status === "error" && "Verification failed"}
      </h1>
      <p className="text-sm text-[hsl(var(--muted))]">
        {status === "success" && "Your account is now active. You can log in."}
        {status === "error" && "This link is invalid or has expired. Please request a new one."}
      </p>

      {status !== "pending" && (
        <Link to={ROUTES.LOGIN}>
          <Button>Go to Login</Button>
        </Link>
      )}
    </Card>
  );
}
