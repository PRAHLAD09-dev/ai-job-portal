import { Badge } from "@/components/ui/badge";
import { formatEnumLabel } from "@/utils/format";
import type { JobStatus } from "@/features/jobs/types";

const VARIANT: Record<JobStatus, "success" | "outline" | "warning" | "default"> = {
  PUBLISHED: "success",
  DRAFT: "outline",
  CLOSED: "warning",
  ARCHIVED: "default",
};

export function JobStatusBadge({ status }: { status: JobStatus }) {
  return <Badge variant={VARIANT[status]}>{formatEnumLabel(status)}</Badge>;
}
