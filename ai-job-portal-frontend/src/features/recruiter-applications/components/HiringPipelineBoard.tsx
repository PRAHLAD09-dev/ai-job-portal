import { useState } from "react";
import { Skeleton } from "@/components/ui/skeleton";
import { Modal } from "@/components/ui/modal";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { FormField } from "@/components/ui/form-field";
import { formatEnumLabel } from "@/utils/format";
import {
  useRecruiterApplications,
  useScheduleInterview,
  useUpdateApplicationStatus,
} from "@/features/recruiter-applications/hooks/useRecruiterApplications";
import type { ApplicationStatus, ApplicationSummaryResponse } from "@/features/applications/types";

const STAGES: ApplicationStatus[] = [
  "APPLIED",
  "UNDER_REVIEW",
  "SHORTLISTED",
  "INTERVIEW",
  "OFFERED",
  "HIRED",
  "REJECTED",
];

interface HiringPipelineBoardProps {
  jobId?: string;
  onSelectApplication: (applicationId: string) => void;
}

export function HiringPipelineBoard({ jobId, onSelectApplication }: HiringPipelineBoardProps) {
  const [draggedApplication, setDraggedApplication] = useState<ApplicationSummaryResponse | null>(null);
  const [interviewTarget, setInterviewTarget] = useState<ApplicationSummaryResponse | null>(null);
  const [interviewDate, setInterviewDate] = useState("");

  const updateStatus = useUpdateApplicationStatus();
  const scheduleInterview = useScheduleInterview();

  const handleDrop = (targetStatus: ApplicationStatus) => {
    if (!draggedApplication || draggedApplication.status === targetStatus) {
      setDraggedApplication(null);
      return;
    }
    if (targetStatus === "INTERVIEW") {
      setInterviewTarget(draggedApplication);
      setDraggedApplication(null);
      return;
    }
    updateStatus.mutate({
      applicationId: draggedApplication.id,
      payload: { status: targetStatus, interviewDate: null, remarks: null },
    });
    setDraggedApplication(null);
  };

  return (
    <div className="flex gap-4 overflow-x-auto pb-2">
      {STAGES.map((status) => (
        <PipelineColumn
          key={status}
          status={status}
          jobId={jobId}
          onDragStart={setDraggedApplication}
          onDrop={handleDrop}
          onSelectApplication={onSelectApplication}
        />
      ))}

      <Modal open={!!interviewTarget} onOpenChange={(open) => !open && setInterviewTarget(null)} title="Schedule interview">
        <div className="space-y-4">
          <p className="text-sm text-[hsl(var(--muted))]">
            Set an interview date for <span className="font-medium">{interviewTarget?.candidateName}</span>.
          </p>
          <FormField label="Interview date &amp; time" htmlFor="interviewDate" required>
            <Input
              id="interviewDate"
              type="datetime-local"
              value={interviewDate}
              onChange={(e) => setInterviewDate(e.target.value)}
            />
          </FormField>
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => setInterviewTarget(null)}>
              Cancel
            </Button>
            <Button
              isLoading={scheduleInterview.isPending}
              disabled={!interviewDate}
              onClick={() => {
                if (!interviewTarget || !interviewDate) return;
                scheduleInterview.mutate(
                  { applicationId: interviewTarget.id, interviewDate: new Date(interviewDate).toISOString() },
                  {
                    onSuccess: () => {
                      setInterviewTarget(null);
                      setInterviewDate("");
                    },
                  },
                );
              }}
            >
              Schedule
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

interface PipelineColumnProps {
  status: ApplicationStatus;
  jobId?: string;
  onDragStart: (application: ApplicationSummaryResponse) => void;
  onDrop: (status: ApplicationStatus) => void;
  onSelectApplication: (applicationId: string) => void;
}

function PipelineColumn({ status, jobId, onDragStart, onDrop, onSelectApplication }: PipelineColumnProps) {
  const { data, isLoading } = useRecruiterApplications({ page: 0, size: 50, jobId, status });

  return (
    <div
      className="w-72 shrink-0 rounded-xl border border-[hsl(var(--border-color))] bg-[hsl(var(--surface))]"
      onDragOver={(e) => e.preventDefault()}
      onDrop={() => onDrop(status)}
    >
      <div className="flex items-center justify-between border-b border-[hsl(var(--border-color))] px-3 py-2.5">
        <p className="text-sm font-semibold">{formatEnumLabel(status)}</p>
        <span className="rounded-full bg-[hsl(var(--background))] px-2 py-0.5 text-xs text-[hsl(var(--muted))]">
          {data?.totalElements ?? 0}
        </span>
      </div>
      <div className="min-h-[120px] space-y-2 p-2">
        {isLoading && <Skeleton className="h-16 w-full" />}
        {data?.content.map((application) => (
          <div
            key={application.id}
            draggable
            onDragStart={() => onDragStart(application)}
            onClick={() => onSelectApplication(application.id)}
            className="cursor-pointer rounded-lg border border-[hsl(var(--border-color))] bg-[hsl(var(--background))] p-3 text-sm shadow-sm transition hover:shadow-md"
          >
            <p className="font-medium">{application.candidateName}</p>
            <p className="mt-0.5 truncate text-xs text-[hsl(var(--muted))]">{application.jobTitle}</p>
          </div>
        ))}
        {data && data.content.length === 0 && !isLoading && (
          <p className="px-1 py-2 text-xs text-[hsl(var(--muted))]">No data available.</p>
        )}
      </div>
    </div>
  );
}
