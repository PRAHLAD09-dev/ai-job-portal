import { Loader2 } from "lucide-react";

/** Suspense fallback for lazy-loaded route chunks. */
export function PageLoader() {
  return (
    <div className="flex min-h-[40vh] items-center justify-center" role="status" aria-label="Loading page">
      <Loader2 className="h-6 w-6 animate-spin text-primary-600" />
    </div>
  );
}
