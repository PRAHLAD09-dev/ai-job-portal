import { Badge } from "@/components/ui/badge";
import { formatEnumLabel } from "@/utils/format";

const POSITIVE = new Set(["ACTIVE", "VERIFIED", "PUBLISHED", "ENABLED"]);
const NEGATIVE = new Set(["DISABLED", "REJECTED", "SUSPENDED", "ARCHIVED", "LOCKED"]);
const WARNING = new Set(["PENDING", "PENDING_VERIFICATION", "DRAFT", "CLOSED"]);

export function AdminStatusBadge({ status }: { status: string }) {
  const normalized = status.toUpperCase();
  const variant = POSITIVE.has(normalized)
    ? "success"
    : NEGATIVE.has(normalized)
      ? "danger"
      : WARNING.has(normalized)
        ? "warning"
        : "outline";
  return <Badge variant={variant}>{formatEnumLabel(status)}</Badge>;
}
