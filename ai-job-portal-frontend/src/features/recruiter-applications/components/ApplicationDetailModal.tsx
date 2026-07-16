import { useState } from "react";
import { FileText, Mail } from "lucide-react";
import { Modal } from "@/components/ui/modal";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Skeleton } from "@/components/ui/skeleton";
import { formatEnumLabel } from "@/utils/format";
import { ViewedBadge } from "@/components/common/ViewedBadge";
import { ApplicationStatusBadge } from "@/features/recruiter-applications/components/ApplicationStatusBadge";
import {
  useAddApplicationNotes,
  useHireApplication,
  useOfferApplication,
  useRecruiterApplicationDetail,
  useRecruiterApplicationTimeline,
  useRejectApplication,
  useReviewApplication,
  useShortlistApplication,
} from "@/features/recruiter-applications/hooks/useRecruiterApplications";

interface ApplicationDetailModalProps {
  applicationId: string | null;
  onClose: () => void;
}

export function ApplicationDetailModal({ applicationId, onClose }: ApplicationDetailModalProps) {
  const { data: application, isLoading } = useRecruiterApplicationDetail(applicationId ?? undefined);
  const { data: timeline } = useRecruiterApplicationTimeline(applicationId ?? undefined);
  const [notes, setNotes] = useState("");

  const review = useReviewApplication();
  const shortlist = useShortlistApplication();
  const offer = useOfferApplication();
  const hire = useHireApplication();
  const reject = useRejectApplication();
  const saveNotes = useAddApplicationNotes();

  return (
    <Modal open={!!applicationId} onOpenChange={(open) => !open && onClose()} title="Application details" className="max-w-2xl">
      {isLoading || !application ? (
        <div className="space-y-3">
          <Skeleton className="h-6 w-48" />
          <Skeleton className="h-24 w-full" />
        </div>
      ) : (
        <div className="space-y-6">
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div>
              <h3 className="text-lg font-semibold">{application.candidateName}</h3>
              <a
                href={`mailto:${application.candidateEmail}`}
                className="mt-0.5 flex items-center gap-1 text-sm text-primary-600 hover:underline"
              >
                <Mail className="h-3.5 w-3.5" /> {application.candidateEmail}
              </a>
              <p className="mt-1 text-sm text-[hsl(var(--muted))]">Applied for {application.jobTitle}</p>
            </div>
            <div className="flex flex-col items-end gap-2">
              <ApplicationStatusBadge status={application.status} />
              <ViewedBadge viewed={application.viewed} viewedAt={application.viewedAt} />
            </div>
          </div>

          {application.interviewDate && (
            <div className="rounded-lg border border-primary-600/30 bg-primary-600/5 p-3 text-sm">
              Interview scheduled for {new Date(application.interviewDate).toLocaleString()}
            </div>
          )}

          {application.coverLetter && (
            <div>
              <p className="text-sm font-medium">Cover letter</p>
              <p className="mt-1 whitespace-pre-wrap text-sm text-[hsl(var(--muted))]">{application.coverLetter}</p>
            </div>
          )}

          {application.resumeUrl && (
            <a
              href={application.resumeUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-1.5 text-sm text-primary-600 hover:underline"
            >
              <FileText className="h-4 w-4" /> View resume
            </a>
          )}

          <div className="flex flex-wrap gap-2">
            {application.status === "APPLIED" && (
              <Button size="sm" variant="outline" isLoading={review.isPending} onClick={() => review.mutate(application.id)}>
                Move to review
              </Button>
            )}
            {(application.status === "APPLIED" || application.status === "UNDER_REVIEW") && (
              <Button size="sm" isLoading={shortlist.isPending} onClick={() => shortlist.mutate(application.id)}>
                Shortlist
              </Button>
            )}
            {application.status === "INTERVIEW" && (
              <Button size="sm" isLoading={offer.isPending} onClick={() => offer.mutate(application.id)}>
                Extend offer
              </Button>
            )}
            {application.status === "OFFERED" && (
              <Button size="sm" isLoading={hire.isPending} onClick={() => hire.mutate(application.id)}>
                Mark hired
              </Button>
            )}
            {application.status !== "REJECTED" && application.status !== "HIRED" && (
              <Button
                size="sm"
                variant="outline"
                isLoading={reject.isPending}
                onClick={() => reject.mutate({ applicationId: application.id })}
              >
                Reject
              </Button>
            )}
          </div>

          <div>
            <p className="text-sm font-medium">Recruiter notes</p>
            <Textarea
              rows={3}
              className="mt-2"
              placeholder="Add private notes about this candidate..."
              defaultValue={application.notes ?? ""}
              onChange={(e) => setNotes(e.target.value)}
            />
            <Button
              size="sm"
              className="mt-2"
              isLoading={saveNotes.isPending}
              onClick={() => saveNotes.mutate({ applicationId: application.id, payload: { notes } })}
            >
              Save notes
            </Button>
          </div>

          <div>
            <p className="text-sm font-medium">Timeline</p>
            <div className="mt-2 space-y-2">
              {timeline?.map((entry) => (
                <div key={entry.id} className="flex items-center gap-2 text-sm">
                  <Badge variant="outline">{formatEnumLabel(entry.newStatus)}</Badge>
                  <span className="text-xs text-[hsl(var(--muted))]">
                    {new Date(entry.changedAt).toLocaleString()}
                  </span>
                  {entry.remarks && <span className="text-xs text-[hsl(var(--muted))]">— {entry.remarks}</span>}
                </div>
              ))}
              {timeline?.length === 0 && <p className="text-sm text-[hsl(var(--muted))]">No data available.</p>}
            </div>
          </div>
        </div>
      )}
    </Modal>
  );
}
