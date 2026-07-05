import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { ROUTES } from "@/constants/routes";

export default function ForbiddenPage() {
  return (
    <div className="space-y-3">
      <p className="text-6xl font-bold text-danger-500">403</p>
      <h1 className="text-xl font-semibold">Access denied</h1>
      <p className="max-w-sm text-sm text-[hsl(var(--muted))]">
        Your account doesn't have permission to view this page.
      </p>
      <Link to={ROUTES.HOME}>
        <Button className="mt-2" variant="outline">Back to Home</Button>
      </Link>
    </div>
  );
}
