import { Badge } from "@/components/ui/badge";
import { formatEnumLabel } from "@/utils/format";
import type { ApplicationStatus } from "@/features/applications/types";

const VARIANT: Record<ApplicationStatus, "success" | "outline" | "warning" | "primary" | "danger" | "default"> = {
  APPLIED: "outline",
  UNDER_REVIEW: "default",
  SHORTLISTED: "primary",
  INTERVIEW: "warning",
  OFFERED: "primary",
  HIRED: "success",
  REJECTED: "danger",
  WITHDRAWN: "default",
};

export function ApplicationStatusBadge({ status }: { status: ApplicationStatus }) {
  return <Badge variant={VARIANT[status]}>{formatEnumLabel(status)}</Badge>;
}
