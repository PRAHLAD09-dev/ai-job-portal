import { useEffect, useState } from "react";
import { Copy, Download, FileWarning, RefreshCw, Sparkles } from "lucide-react";
import { toast } from "sonner";
import { Card } from "@/components/ui/card";
import { Select } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { FormField } from "@/components/ui/form-field";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { useResumesList } from "@/features/profile/hooks/useResumes";
import { useAnalyzeResume, useLatestResumeAnalysis, useScoreResume } from "@/features/ai/hooks/useAi";
import type { ResumeAnalysisResponse } from "@/features/ai/types";

/** Circular ATS score gauge — plain SVG, no chart library needed for a single value. */
function ScoreGauge({ score }: { score: number }) {
  const radius = 46;
  const circumference = 2 * Math.PI * radius;
  const clamped = Math.min(100, Math.max(0, score));
  const offset = circumference - (clamped / 100) * circumference;
  const color = clamped >= 75 ? "stroke-success-500" : clamped >= 50 ? "stroke-warning-500" : "stroke-danger-500";

  return (
    <div className="relative flex h-32 w-32 items-center justify-center">
      <svg viewBox="0 0 100 100" className="h-32 w-32 -rotate-90">
        <circle cx="50" cy="50" r={radius} strokeWidth="8" fill="none" className="stroke-[hsl(var(--border-color))]" />
        <circle
          cx="50"
          cy="50"
          r={radius}
          strokeWidth="8"
          fill="none"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          className={`transition-all duration-500 ${color}`}
        />
      </svg>
      <div className="absolute flex flex-col items-center">
        <span className="text-2xl font-semibold">{clamped}</span>
        <span className="text-xs text-[hsl(var(--muted))]">/ 100</span>
      </div>
    </div>
  );
}

function ResultList({ title, items, variant }: { title: string; items: string[]; variant: "success" | "danger" | "warning" | "primary" }) {
  if (items.length === 0) return null;
  return (
    <div>
      <p className="text-sm font-medium">{title}</p>
      <div className="mt-2 flex flex-wrap gap-1.5">
        {items.map((item, i) => (
          <Badge key={i} variant={variant}>
            {item}
          </Badge>
        ))}
      </div>
    </div>
  );
}

function AnalysisResult({ analysis }: { analysis: ResumeAnalysisResponse }) {
  const handleCopy = () => {
    const text = [
      `ATS Score: ${analysis.atsScore}/100`,
      `\nStrengths:\n${analysis.strengths.map((s) => `- ${s}`).join("\n")}`,
      `\nWeaknesses:\n${analysis.weaknesses.map((s) => `- ${s}`).join("\n")}`,
      `\nMissing Skills:\n${analysis.missingSkills.map((s) => `- ${s}`).join("\n")}`,
      `\nRecommendations:\n${analysis.recommendations.map((s) => `- ${s}`).join("\n")}`,
    ].join("\n");
    navigator.clipboard.writeText(text);
    toast.success("Copied to clipboard");
  };

  const handleDownload = () => {
    const text = [
      `Resume Analysis Report`,
      `Generated: ${new Date(analysis.createdAt).toLocaleString()}`,
      `ATS Score: ${analysis.atsScore}/100`,
      ``,
      `STRENGTHS`,
      ...analysis.strengths.map((s) => `- ${s}`),
      ``,
      `WEAKNESSES`,
      ...analysis.weaknesses.map((s) => `- ${s}`),
      ``,
      `MISSING SKILLS`,
      ...analysis.missingSkills.map((s) => `- ${s}`),
      ``,
      `RECOMMENDATIONS`,
      ...analysis.recommendations.map((s) => `- ${s}`),
    ].join("\n");
    const blob = new Blob([text], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "resume-analysis-report.txt";
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="mt-6 space-y-5">
      <div className="flex flex-col items-center gap-4 sm:flex-row sm:items-start">
        <ScoreGauge score={analysis.atsScore} />
        <div className="flex-1 space-y-4">
          <ResultList title="Strengths" items={analysis.strengths} variant="success" />
          <ResultList title="Weaknesses" items={analysis.weaknesses} variant="danger" />
          <ResultList title="Missing Skills" items={analysis.missingSkills} variant="warning" />
          <ResultList title="Recommendations" items={analysis.recommendations} variant="primary" />
        </div>
      </div>
      <p className="text-xs text-[hsl(var(--muted))]">
        Last analyzed {new Date(analysis.createdAt).toLocaleString()}
      </p>
      <div className="flex flex-wrap gap-2">
        <Button size="sm" variant="outline" onClick={handleCopy}>
          <Copy className="h-3.5 w-3.5" /> Copy result
        </Button>
        <Button size="sm" variant="outline" onClick={handleDownload}>
          <Download className="h-3.5 w-3.5" /> Download report
        </Button>
      </div>
    </div>
  );
}

/**
 * Resume analysis + ATS score. Per KNOWN_BACKEND_LIMITATIONS.md (Day 04),
 * candidate-service stores only the resume file's Cloudinary URL — it never
 * extracts plain text from the file — while ai-service's AnalyzeResumeRequest
 * requires resumeText directly. So the candidate pastes their resume text
 * here (pre-filled resumeUrl from a selected uploaded resume); nothing is
 * fabricated or auto-extracted client-side.
 */
export function ResumeAnalysisPanel() {
  const { data: resumes, isLoading: isLoadingResumes } = useResumesList();
  const { data: latestAnalysis, isLoading: isLoadingLatest, isError: hasNoAnalysisYet } = useLatestResumeAnalysis();
  const analyze = useAnalyzeResume();
  const score = useScoreResume();

  const [resumeId, setResumeId] = useState("");
  const [resumeText, setResumeText] = useState("");

  useEffect(() => {
    if (!resumeId && resumes && resumes.length > 0) {
      setResumeId(resumes.find((r) => r.status === "ACTIVE")?.id ?? resumes[0].id);
    }
  }, [resumes, resumeId]);

  const selectedResume = resumes?.find((r) => r.id === resumeId);
  const canSubmit = !!selectedResume && resumeText.trim().length > 0;

  const handleAnalyze = () => {
    if (!selectedResume) return;
    analyze.mutate({ resumeUrl: selectedResume.fileUrl, resumeText: resumeText.trim() });
  };

  const handleQuickScore = () => {
    if (!selectedResume) return;
    score.mutate({ resumeUrl: selectedResume.fileUrl, resumeText: resumeText.trim() });
  };

  const displayedAnalysis = analyze.data ?? (!hasNoAnalysisYet ? latestAnalysis : undefined);

  return (
    <div className="space-y-6">
      <Card>
        <div className="flex items-center gap-2">
          <Sparkles className="h-5 w-5 text-primary-600" />
          <h2 className="text-lg font-semibold">Resume analysis &amp; ATS score</h2>
        </div>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          Select one of your uploaded resumes and paste its text content — the AI reads the text you provide, since
          resume file text isn&apos;t auto-extracted by the platform yet.
        </p>

        {isLoadingResumes ? (
          <Skeleton className="mt-4 h-10 w-full" />
        ) : !resumes || resumes.length === 0 ? (
          <div className="mt-4">
            <EmptyState
              icon={<FileWarning className="h-8 w-8" />}
              title="No resume uploaded"
              message="Upload a resume on your profile page first, then come back here to analyze it."
            />
          </div>
        ) : (
          <div className="mt-4 space-y-4">
            <FormField label="Resume" htmlFor="resume-select">
              <Select id="resume-select" value={resumeId} onChange={(e) => setResumeId(e.target.value)}>
                {resumes.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.fileName} (v{r.versionNumber}
                    {r.status === "ACTIVE" ? ", active" : ""})
                  </option>
                ))}
              </Select>
            </FormField>
            <FormField label="Resume text" htmlFor="resume-text">
              <Textarea
                id="resume-text"
                rows={8}
                placeholder="Paste the full text content of your resume here..."
                value={resumeText}
                onChange={(e) => setResumeText(e.target.value)}
                maxLength={50000}
              />
            </FormField>
            <div className="flex flex-wrap gap-2">
              <Button disabled={!canSubmit} isLoading={analyze.isPending} onClick={handleAnalyze}>
                <Sparkles className="h-4 w-4" /> Analyze resume
              </Button>
              <Button
                variant="outline"
                disabled={!canSubmit}
                isLoading={score.isPending}
                onClick={handleQuickScore}
              >
                Quick ATS check
              </Button>
              {displayedAnalysis && (
                <Button variant="outline" isLoading={analyze.isPending} onClick={handleAnalyze} disabled={!canSubmit}>
                  <RefreshCw className="h-3.5 w-3.5" /> Re-analyze
                </Button>
              )}
            </div>
          </div>
        )}

        {isLoadingLatest && !analyze.data && (
          <div className="mt-6 space-y-2">
            <Skeleton className="h-32 w-32 rounded-full" />
            <Skeleton className="h-4 w-full" />
          </div>
        )}

        {displayedAnalysis && <AnalysisResult analysis={displayedAnalysis} />}

        {!displayedAnalysis && !isLoadingLatest && !analyze.isPending && resumes && resumes.length > 0 && (
          <div className="mt-6">
            <EmptyState
              title="No resume analysis yet"
              message="Paste your resume text above and click Analyze resume to get your first AI-generated report."
            />
          </div>
        )}
      </Card>

      {score.data && (
        <Card>
          <h3 className="text-base font-semibold">Quick ATS check result</h3>
          <div className="mt-4 flex flex-col items-center gap-4 sm:flex-row sm:items-start">
            <ScoreGauge score={score.data.atsScore} />
            <div className="flex-1 space-y-4">
              <ResultList title="Formatting Issues" items={score.data.formattingIssues} variant="warning" />
              <ResultList title="Keyword Gaps" items={score.data.keywordGaps} variant="danger" />
              {score.data.formattingIssues.length === 0 && score.data.keywordGaps.length === 0 && (
                <p className="text-sm text-[hsl(var(--muted))]">No formatting issues or keyword gaps found.</p>
              )}
            </div>
          </div>
        </Card>
      )}
    </div>
  );
}
