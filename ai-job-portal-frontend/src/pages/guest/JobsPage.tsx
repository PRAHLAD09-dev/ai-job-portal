import { EmptyState } from "@/components/common/EmptyState";
import { Briefcase } from "lucide-react";

/** Placeholder for Phase 3 (Job Service integration: search, filters, pagination). */
export default function JobsPage() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-12 md:px-8">
      <h1 className="mb-6 text-2xl font-semibold">Job Listings</h1>
      <EmptyState
        icon={<Briefcase className="h-10 w-10" />}
        title="Job listings coming in Phase 3"
        message="Job search, filters, and pagination will be wired up once the Job Service module is implemented."
      />
    </div>
  );
}
