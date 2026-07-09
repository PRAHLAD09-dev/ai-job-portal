import { useState } from "react";
import { Copy, Download, FileWarning, RefreshCw, Sparkles } from "lucide-react";
import { toast } from "sonner";
import { Card } from "@/components/ui/card";
import { Select } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { FormField } from "@/components/ui/form-field";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { useSavedJobsList } from "@/features/jobs/hooks/useSavedJobs";
import { useGenerateCoverLetter } from "@/features/ai/hooks/useAi";

/**
 * Cover letter generator — POST /ai/cover-letter. Job choices are drawn
 * from the candidate's saved jobs (there's no dedicated "all jobs" picker
 * needed here since a cover letter only makes sense for a job the
 * candidate is actually interested in).
 */
export function CoverLetterGenerator() {
  const { data: savedJobsPage, isLoading: isLoadingJobs } = useSavedJobsList({ page: 0, size: 100 });
  const savedJobs = savedJobsPage?.content ?? [];

  const [jobId, setJobId] = useState("");
  const [notes, setNotes] = useState("");
  const [editedText, setEditedText] = useState("");
  const generate = useGenerateCoverLetter();

  const handleGenerate = () => {
    if (!jobId) return;
    generate.mutate(
      { jobId, additionalNotes: notes.trim() || null },
      { onSuccess: (data) => setEditedText(data.coverLetterText) },
    );
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(editedText);
    toast.success("Copied to clipboard");
  };

  const handleDownload = () => {
    const blob = new Blob([editedText], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "cover-letter.txt";
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <Card>
      <div className="flex items-center gap-2">
        <Sparkles className="h-5 w-5 text-primary-600" />
        <h2 className="text-lg font-semibold">Cover letter generator</h2>
      </div>

      {isLoadingJobs ? (
        <Skeleton className="mt-4 h-10 w-full" />
      ) : savedJobs.length === 0 ? (
        <div className="mt-4">
          <EmptyState
            icon={<FileWarning className="h-8 w-8" />}
            title="No saved jobs"
            message="Save a job you're interested in first, then generate a tailored cover letter for it here."
          />
        </div>
      ) : (
        <div className="mt-4 space-y-4">
          <FormField label="Job" htmlFor="cl-job">
            <Select id="cl-job" value={jobId} onChange={(e) => setJobId(e.target.value)}>
              <option value="">Select a job</option>
              {savedJobs.map((entry) => (
                <option key={entry.job.id} value={entry.job.id}>
                  {entry.job.title} — {entry.job.companyName}
                </option>
              ))}
            </Select>
          </FormField>
          <FormField label="Additional notes (optional)" htmlFor="cl-notes">
            <Textarea
              id="cl-notes"
              rows={3}
              placeholder="Anything specific you'd like the cover letter to mention..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              maxLength={2000}
            />
          </FormField>
          <Button disabled={!jobId} isLoading={generate.isPending} onClick={handleGenerate}>
            <Sparkles className="h-4 w-4" /> Generate cover letter
          </Button>
        </div>
      )}

      {generate.isPending && (
        <div className="mt-6 space-y-2">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-3/4" />
        </div>
      )}

      {editedText && !generate.isPending && (
        <div className="mt-6 space-y-3">
          <FormField label="Cover letter (editable)" htmlFor="cl-result">
            <Textarea
              id="cl-result"
              rows={14}
              value={editedText}
              onChange={(e) => setEditedText(e.target.value)}
              className="whitespace-pre-wrap"
            />
          </FormField>
          <div className="flex flex-wrap gap-2">
            <Button size="sm" variant="outline" onClick={handleCopy}>
              <Copy className="h-3.5 w-3.5" /> Copy
            </Button>
            <Button size="sm" variant="outline" onClick={handleDownload}>
              <Download className="h-3.5 w-3.5" /> Download
            </Button>
            <Button size="sm" variant="outline" disabled={!jobId} isLoading={generate.isPending} onClick={handleGenerate}>
              <RefreshCw className="h-3.5 w-3.5" /> Regenerate
            </Button>
          </div>
        </div>
      )}
    </Card>
  );
}
