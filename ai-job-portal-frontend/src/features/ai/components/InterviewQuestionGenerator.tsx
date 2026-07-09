import { useState } from "react";
import { Copy, Download, RefreshCw, Sparkles } from "lucide-react";
import { toast } from "sonner";
import { Card } from "@/components/ui/card";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { useGenerateInterviewQuestions } from "@/features/ai/hooks/useAi";
import type { InterviewQuestionResponse } from "@/features/ai/types";
import type { JobSummaryResponse } from "@/features/jobs/types";

interface InterviewQuestionGeneratorProps {
  jobs: JobSummaryResponse[];
}

export function InterviewQuestionGenerator({ jobs }: InterviewQuestionGeneratorProps) {
  const [jobId, setJobId] = useState("");
  const [count, setCount] = useState(10);
  const generate = useGenerateInterviewQuestions();

  const handleGenerate = () => {
    if (!jobId) return;
    generate.mutate({ jobId, count });
  };

  const handleCopyAll = (questions: InterviewQuestionResponse[]) => {
    navigator.clipboard.writeText(questions.map((q) => `- ${q.question}`).join("\n"));
    toast.success("Copied to clipboard");
  };

  const handleDownload = (questions: InterviewQuestionResponse[]) => {
    const blob = new Blob([questions.map((q) => `- ${q.question}`).join("\n")], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "interview-questions.txt";
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <Card>
      <div className="flex items-center gap-2">
        <Sparkles className="h-5 w-5 text-primary-600" />
        <h2 className="text-lg font-semibold">Interview question generator</h2>
      </div>
      <div className="mt-4 grid gap-4 sm:grid-cols-[1fr_140px_auto] sm:items-end">
        <FormField label="Job" htmlFor="iq-job">
          <Select id="iq-job" value={jobId} onChange={(e) => setJobId(e.target.value)}>
            <option value="">Select a job</option>
            {jobs.map((job) => (
              <option key={job.id} value={job.id}>
                {job.title}
              </option>
            ))}
          </Select>
        </FormField>
        <FormField label="Count" htmlFor="iq-count">
          <Select id="iq-count" value={count} onChange={(e) => setCount(Number(e.target.value))}>
            {[5, 10, 15, 20].map((n) => (
              <option key={n} value={n}>
                {n}
              </option>
            ))}
          </Select>
        </FormField>
        <Button disabled={!jobId} isLoading={generate.isPending} onClick={handleGenerate}>
          <Sparkles className="h-4 w-4" /> Generate
        </Button>
      </div>

      {generate.isPending && (
        <div className="mt-4 space-y-2">
          <Skeleton className="h-10 w-full" />
          <Skeleton className="h-10 w-full" />
          <Skeleton className="h-10 w-full" />
        </div>
      )}

      {generate.data && !generate.isPending && (
        <div className="mt-6 space-y-3">
          {generate.data.length === 0 ? (
            <EmptyState title="No data available" message="No interview questions were generated." />
          ) : (
            <>
              <div className="space-y-2">
                {generate.data.map((q) => (
                  <div key={q.id} className="rounded-lg border border-[hsl(var(--border-color))] p-3 text-sm">
                    <p>{q.question}</p>
                    <div className="mt-2 flex gap-1.5">
                      <Badge variant="outline">{q.difficulty}</Badge>
                      <Badge variant="outline">{q.category}</Badge>
                    </div>
                  </div>
                ))}
              </div>
              <div className="flex gap-2">
                <Button size="sm" variant="outline" onClick={() => handleCopyAll(generate.data)}>
                  <Copy className="h-3.5 w-3.5" /> Copy all
                </Button>
                <Button size="sm" variant="outline" onClick={() => handleDownload(generate.data)}>
                  <Download className="h-3.5 w-3.5" /> Download
                </Button>
                <Button size="sm" variant="outline" isLoading={generate.isPending} onClick={handleGenerate}>
                  <RefreshCw className="h-3.5 w-3.5" /> Regenerate
                </Button>
              </div>
            </>
          )}
        </div>
      )}
    </Card>
  );
}
