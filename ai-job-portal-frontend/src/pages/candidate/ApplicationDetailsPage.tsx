import { useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Building2, Calendar, FileText, XCircle } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { ViewedBadge } from "@/components/common/ViewedBadge";
import { StatusBadge } from "@/features/applications/components/StatusBadge";
import { ApplicationTimeline } from "@/features/applications/components/ApplicationTimeline";
import {
  useApplicationDetail,
  useApplicationTimeline,
  useWithdrawApplication,
} from "@/features/applications/hooks/useApplications";
import { buildRoute } from "@/constants/routes";

export default function ApplicationDetailsPage() {
  const { applicationId } = useParams<{ applicationId: string }>();
  const { data: application, isLoading } = useApplicationDetail(applicationId);
  const { data: timeline, isLoading: isLoadingTimeline } = useApplicationTimeline(applicationId);
  const withdrawApplication = useWithdrawApplication();
  const [isWithdrawOpen, setIsWithdrawOpen] = useState(false);

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-40 w-full" />
      </div>
    );
  }

  if (!application) {
    return (
      <EmptyState
        icon={<FileText className="h-8 w-8" />}
        title="Application not found"
        message="This application could not be found."
      />
    );
  }

  const canWithdraw = !["HIRED", "REJECTED", "WITHDRAWN"].includes(application.status);

  return (
    <div className="space-y-6">
      <Card>
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-start">
          <div>
            <Link to={buildRoute.candidateJobDetails(application.jobId)} className="text-lg font-semibold hover:underline">
              {application.jobTitle}
            </Link>
            <p className="mt-1 flex items-center gap-1 text-sm text-[hsl(var(--muted))]">
              <Building2 className="h-3.5 w-3.5" /> {application.companyName}
            </p>
            <p className="mt-1 flex items-center gap-1 text-xs text-[hsl(var(--muted))]">
              <Calendar className="h-3.5 w-3.5" /> Applied on {new Date(application.appliedAt).toLocaleDateString()}
            </p>
          </div>
          <div className="flex flex-col items-end gap-2">
            <div className="flex items-center gap-3">
              <StatusBadge status={application.status} />
              {canWithdraw && (
                <Button variant="outline" size="sm" onClick={() => setIsWithdrawOpen(true)}>
                  <XCircle className="h-4 w-4" /> Withdraw
                </Button>
              )}
            </div>
            <ViewedBadge viewed={application.viewed} viewedAt={application.viewedAt} />
          </div>
        </div>

        {application.interviewDate && (
          <div className="mt-4 rounded-lg border border-primary-600/30 bg-primary-600/5 p-3 text-sm">
            Interview scheduled for {new Date(application.interviewDate).toLocaleString()}
          </div>
        )}

        {application.resumeUrl && (
          <a
            href={application.resumeUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="mt-4 inline-flex items-center gap-1.5 text-sm text-primary-600 hover:underline"
          >
            <FileText className="h-4 w-4" /> View submitted resume
          </a>
        )}

        {application.coverLetter && (
          <div className="mt-4 border-t border-[hsl(var(--border-color))] pt-4">
            <p className="text-sm font-medium">Cover Letter</p>
            <p className="mt-2 whitespace-pre-line text-sm text-[hsl(var(--muted))]">{application.coverLetter}</p>
          </div>
        )}
      </Card>

      <Card>
        <h2 className="text-base font-semibold">Application Timeline</h2>
        <div className="mt-4">
          {isLoadingTimeline && <Skeleton className="h-32 w-full" />}
          {!isLoadingTimeline && timeline && timeline.length > 0 && <ApplicationTimeline events={timeline} />}
          {!isLoadingTimeline && timeline?.length === 0 && (
            <p className="text-sm text-[hsl(var(--muted))]">No status updates yet.</p>
          )}
        </div>
      </Card>

      <ConfirmDialog
        open={isWithdrawOpen}
        onOpenChange={setIsWithdrawOpen}
        title="Withdraw application"
        description={`Are you sure you want to withdraw your application for "${application.jobTitle}"? This cannot be undone.`}
        confirmLabel="Withdraw"
        isLoading={withdrawApplication.isPending}
        onConfirm={() =>
          applicationId &&
          withdrawApplication.mutate(applicationId, { onSuccess: () => setIsWithdrawOpen(false) })
        }
      />
    </div>
  );
}
