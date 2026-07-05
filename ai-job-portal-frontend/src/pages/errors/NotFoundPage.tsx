import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { ROUTES } from "@/constants/routes";

export default function NotFoundPage() {
  return (
    <div className="space-y-3">
      <p className="text-6xl font-bold text-primary-600">404</p>
      <h1 className="text-xl font-semibold">Page not found</h1>
      <p className="max-w-sm text-sm text-[hsl(var(--muted))]">
        The page you're looking for doesn't exist or has moved.
      </p>
      <Link to={ROUTES.HOME}>
        <Button className="mt-2">Back to Home</Button>
      </Link>
    </div>
  );
}
