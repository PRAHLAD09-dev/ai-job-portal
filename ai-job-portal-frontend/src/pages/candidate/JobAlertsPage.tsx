import { useState } from "react";
import { Bell, Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { useJobCategories } from "@/features/jobs/hooks/useJobCategories";
import {
  useCreateJobAlert,
  useDeleteJobAlert,
  useJobAlerts,
  useUpdateJobAlert,
} from "@/features/jobs/hooks/useJobAlerts";
import { JobAlertFormModal } from "@/features/jobs/components/JobAlertFormModal";
import { JobAlertListItem } from "@/features/jobs/components/JobAlertListItem";
import type { JobAlertRequest, JobAlertResponse } from "@/features/jobs/types";

export default function JobAlertsPage() {
  const { data: alerts, isLoading } = useJobAlerts();
  const { data: categories } = useJobCategories();

  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<JobAlertResponse | null>(null);
  const [deleting, setDeleting] = useState<JobAlertResponse | null>(null);

  const createAlert = useCreateJobAlert();
  const updateAlert = useUpdateJobAlert();
  const deleteAlert = useDeleteJobAlert();

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };
  const openEdit = (alert: JobAlertResponse) => {
    setEditing(alert);
    setFormOpen(true);
  };

  const handleSubmit = (payload: JobAlertRequest, onDone: () => void) => {
    if (editing) {
      updateAlert.mutate({ alertId: editing.id, payload }, { onSuccess: onDone });
    } else {
      createAlert.mutate(payload, { onSuccess: onDone });
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Job Alerts</h1>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">
            Save search criteria and get notified when matching jobs are posted.
          </p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4" /> New Alert
        </Button>
      </div>

      {isLoading && (
        <div className="space-y-3">
          <Skeleton className="h-24 w-full" />
          <Skeleton className="h-24 w-full" />
        </div>
      )}

      {!isLoading && alerts?.length === 0 && (
        <EmptyState
          icon={<Bell className="h-8 w-8" />}
          title="No job alerts yet"
          message="Create an alert to get notified when jobs matching your criteria are posted."
          actionLabel="New Alert"
          onAction={openCreate}
        />
      )}

      {!isLoading && alerts && alerts.length > 0 && (
        <div className="space-y-3">
          {alerts.map((alert) => (
            <JobAlertListItem
              key={alert.id}
              alert={alert}
              categories={categories}
              onEdit={openEdit}
              onDelete={setDeleting}
            />
          ))}
        </div>
      )}

      {formOpen && (
        <JobAlertFormModal
          open={formOpen}
          onOpenChange={setFormOpen}
          alert={editing}
          isLoading={createAlert.isPending || updateAlert.isPending}
          onSubmit={handleSubmit}
        />
      )}

      <ConfirmDialog
        open={!!deleting}
        onOpenChange={(open) => !open && setDeleting(null)}
        title="Delete job alert"
        description={`Remove the alert for "${deleting?.keyword || "Any job"}"? You'll stop receiving notifications for this search.`}
        confirmLabel="Delete"
        isLoading={deleteAlert.isPending}
        onConfirm={() => {
          if (deleting) deleteAlert.mutate(deleting.id, { onSuccess: () => setDeleting(null) });
        }}
      />
    </div>
  );
}
