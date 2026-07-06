import { Badge, type BadgeProps } from "@/components/ui/badge";
import { formatEnumLabel } from "@/utils/format";
import type { ApplicationStatus } from "@/features/applications/types";

const STATUS_VARIANT: Record<ApplicationStatus, NonNullable<BadgeProps["variant"]>> = {
  APPLIED: "outline",
  UNDER_REVIEW: "default",
  SHORTLISTED: "primary",
  INTERVIEW: "primary",
  OFFERED: "success",
  HIRED: "success",
  REJECTED: "danger",
  WITHDRAWN: "default",
};

export function StatusBadge({ status }: { status: ApplicationStatus }) {
  return <Badge variant={STATUS_VARIANT[status]}>{formatEnumLabel(status)}</Badge>;
}
