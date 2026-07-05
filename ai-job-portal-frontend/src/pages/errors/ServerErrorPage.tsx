import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { ROUTES } from "@/constants/routes";

export default function ServerErrorPage() {
  return (
    <div className="space-y-3">
      <p className="text-6xl font-bold text-danger-500">500</p>
      <h1 className="text-xl font-semibold">Something went wrong</h1>
      <p className="max-w-sm text-sm text-[hsl(var(--muted))]">
        An unexpected error occurred on our end. Please try again shortly.
      </p>
      <div className="flex justify-center gap-2">
        <Button variant="outline" onClick={() => window.location.reload()}>Retry</Button>
        <Link to={ROUTES.HOME}><Button>Back to Home</Button></Link>
      </div>
    </div>
  );
}
