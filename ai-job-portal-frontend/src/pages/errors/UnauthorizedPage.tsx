import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { ROUTES } from "@/constants/routes";

export default function UnauthorizedPage() {
  return (
    <div className="space-y-3">
      <p className="text-6xl font-bold text-primary-600">401</p>
      <h1 className="text-xl font-semibold">You're not signed in</h1>
      <p className="max-w-sm text-sm text-[hsl(var(--muted))]">
        Please log in to access this page.
      </p>
      <Link to={ROUTES.LOGIN}>
        <Button className="mt-2">Go to Login</Button>
      </Link>
    </div>
  );
}
