import { useEffect, useState } from "react";
import { Download, FileWarning, MessageCircleQuestion, RefreshCw } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { cn } from "@/lib/cn";
import { isNotFoundError } from "@/services/api-client";
import {
  useDownloadInterviewPrepPdf,
  useGenerateInterviewPrep,
  useInterviewPrepTopics,
  useLatestInterviewPrep,
} from "@/features/ai/hooks/useAi";
import type { InterviewPrepQuestionSetResponse, PrepDifficulty, PrepQuestionType } from "@/features/ai/types";

const DIFFICULTY_OPTIONS: { value: PrepDifficulty; label: string }[] = [
  { value: "EASY", label: "Easy" },
  { value: "MEDIUM", label: "Medium" },
  { value: "HARD", label: "Hard" },
];

const QUESTION_TYPE_OPTIONS: { value: PrepQuestionType; label: string }[] = [
  { value: "MIXED", label: "Mixed" },
  { value: "TECHNICAL", label: "Technical" },
  { value: "HR", label: "HR / Behavioral" },
  { value: "PROJECT_BASED", label: "Project-based" },
];

const COUNT_OPTIONS = [10, 20, 30] as const;

function TopicChip({ label, selected, onToggle }: { label: string; selected: boolean; onToggle: () => void }) {
  return (
    <button
      type="button"
      onClick={onToggle}
      className={cn(
        "rounded-full border px-3 py-1 text-xs font-medium transition-colors",
        selected
          ? "border-primary-600 bg-primary-600/10 text-primary-600"
          : "border-[hsl(var(--border-color))] text-[hsl(var(--muted))] hover:border-primary-600/50",
      )}
    >
      {label}
    </button>
  );
}

function QuestionSetResult({
  questionSet,
  onDownloadPdf,
  isDownloading,
}: {
  questionSet: InterviewPrepQuestionSetResponse;
  onDownloadPdf: () => void;
  isDownloading: boolean;
}) {
  return (
    <div className="mt-6 space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <p className="text-sm text-[hsl(var(--muted))]">
          {questionSet.totalQuestions} questions · {questionSet.difficulty.toLowerCase()} ·{" "}
          {questionSet.questionType.toLowerCase().replace("_", " ")}
        </p>
        <Button size="sm" variant="outline" isLoading={isDownloading} onClick={onDownloadPdf}>
          <Download className="h-3.5 w-3.5" /> Download PDF
        </Button>
      </div>

      <div className="space-y-4">
        {questionSet.sections.map((section, i) => (
          <div key={i} className="rounded-lg border border-[hsl(var(--border-color))] p-3">
            <p className="text-sm font-medium">
              {section.topic} <span className="text-[hsl(var(--muted))]">({section.questions.length})</span>
            </p>
            <ol className="mt-2 list-decimal space-y-1.5 pl-5 text-sm">
              {section.questions.map((q, qi) => (
                <li key={qi}>{q}</li>
              ))}
            </ol>
          </div>
        ))}
      </div>

      <p className="text-xs text-[hsl(var(--muted))]">
        Generated {new Date(questionSet.generatedAt).toLocaleString()}
      </p>
    </div>
  );
}

/**
 * AI Interview Generator — resume-based practice questions, per the AI
 * Interview Generator PRD. Sits after Resume Analysis in the flow: ai-service
 * derives topics and questions from the candidate's most recently analyzed
 * resume, so a candidate who hasn't analyzed a resume yet gets a gate here
 * pointing them at the Resume tab instead of an upload form.
 */
export function InterviewPrepPanel({ onNavigateToResume }: { onNavigateToResume?: () => void }) {
  const { data: topics, isLoading: isLoadingTopics, error: topicsError } = useInterviewPrepTopics();
  const { data: latest, isLoading: isLoadingLatest, isError: hasNoLatest } = useLatestInterviewPrep();
  const generate = useGenerateInterviewPrep();
  const downloadPdf = useDownloadInterviewPrepPdf();

  const [selectedTopics, setSelectedTopics] = useState<string[]>([]);
  const [difficulty, setDifficulty] = useState<PrepDifficulty>("MEDIUM");
  const [questionCount, setQuestionCount] = useState<(typeof COUNT_OPTIONS)[number]>(20);
  const [questionType, setQuestionType] = useState<PrepQuestionType>("MIXED");

  const allTopics = [...(topics?.skills ?? []), ...(topics?.projects ?? [])];

  // Pre-select every detected topic once topics load, so "Generate" works with zero clicks.
  useEffect(() => {
    if (topics && selectedTopics.length === 0) {
      setSelectedTopics(allTopics);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [topics]);

  const toggleTopic = (topic: string) => {
    setSelectedTopics((prev) => (prev.includes(topic) ? prev.filter((t) => t !== topic) : [...prev, topic]));
  };

  const handleGenerate = () => {
    if (selectedTopics.length === 0) return;
    generate.mutate({
      selectedTopics,
      difficulty,
      questionCount,
      questionType,
    });
  };

  const displayedSet = generate.data ?? (!hasNoLatest ? latest : undefined);

  if (isLoadingTopics) {
    return (
      <Card>
        <Skeleton className="h-5 w-56" />
        <Skeleton className="mt-4 h-10 w-full" />
        <Skeleton className="mt-2 h-24 w-full" />
      </Card>
    );
  }

  if (isNotFoundError(topicsError)) {
    return (
      <Card>
        <EmptyState
          icon={<FileWarning className="h-8 w-8" />}
          title="Analyze your resume first"
          message="The AI Interview Generator builds practice questions from your resume. Head to the Resume tab and analyze a resume, then come back here."
          actionLabel={onNavigateToResume ? "Go to Resume tab" : undefined}
          onAction={onNavigateToResume}
        />
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      <Card>
        <div className="flex items-center gap-2">
          <MessageCircleQuestion className="h-5 w-5 text-primary-600" />
          <h2 className="text-lg font-semibold">AI interview practice questions</h2>
        </div>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          Pick the topics from your resume you want to practice, then generate a fresh set of questions.
        </p>

        {allTopics.length === 0 ? (
          <div className="mt-4">
            <EmptyState
              title="No topics detected"
              message="We couldn't detect any skills or projects on your latest resume analysis. Try re-analyzing your resume with a more detailed version."
            />
          </div>
        ) : (
          <div className="mt-4 space-y-4">
            <div>
              <div className="flex items-center justify-between">
                <p className="text-xs font-medium text-[hsl(var(--muted))]">Topics</p>
                <div className="flex gap-2">
                  <button
                    type="button"
                    className="text-xs font-medium text-primary-600 hover:underline"
                    onClick={() => setSelectedTopics(allTopics)}
                  >
                    Select all
                  </button>
                  <button
                    type="button"
                    className="text-xs font-medium text-[hsl(var(--muted))] hover:underline"
                    onClick={() => setSelectedTopics([])}
                  >
                    Clear
                  </button>
                </div>
              </div>
              <div className="mt-2 flex flex-wrap gap-1.5">
                {allTopics.map((topic) => (
                  <TopicChip
                    key={topic}
                    label={topic}
                    selected={selectedTopics.includes(topic)}
                    onToggle={() => toggleTopic(topic)}
                  />
                ))}
              </div>
            </div>

            <div className="grid gap-4 sm:grid-cols-3">
              <FormField label="Difficulty" htmlFor="prep-difficulty">
                <Select
                  id="prep-difficulty"
                  value={difficulty}
                  onChange={(e) => setDifficulty(e.target.value as PrepDifficulty)}
                >
                  {DIFFICULTY_OPTIONS.map((opt) => (
                    <option key={opt.value} value={opt.value}>
                      {opt.label}
                    </option>
                  ))}
                </Select>
              </FormField>

              <FormField label="Question count" htmlFor="prep-count">
                <Select
                  id="prep-count"
                  value={questionCount}
                  onChange={(e) => setQuestionCount(Number(e.target.value) as (typeof COUNT_OPTIONS)[number])}
                >
                  {COUNT_OPTIONS.map((count) => (
                    <option key={count} value={count}>
                      {count} questions
                    </option>
                  ))}
                </Select>
              </FormField>

              <FormField label="Question type" htmlFor="prep-type">
                <Select
                  id="prep-type"
                  value={questionType}
                  onChange={(e) => setQuestionType(e.target.value as PrepQuestionType)}
                >
                  {QUESTION_TYPE_OPTIONS.map((opt) => (
                    <option key={opt.value} value={opt.value}>
                      {opt.label}
                    </option>
                  ))}
                </Select>
              </FormField>
            </div>

            <div className="flex flex-wrap gap-2">
              <Button
                disabled={selectedTopics.length === 0}
                isLoading={generate.isPending}
                onClick={handleGenerate}
              >
                <MessageCircleQuestion className="h-4 w-4" /> Generate questions
              </Button>
              {displayedSet && (
                <Button
                  variant="outline"
                  disabled={selectedTopics.length === 0}
                  isLoading={generate.isPending}
                  onClick={handleGenerate}
                >
                  <RefreshCw className="h-3.5 w-3.5" /> Regenerate
                </Button>
              )}
            </div>
          </div>
        )}

        {isLoadingLatest && !generate.data && (
          <div className="mt-6 space-y-2">
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-24 w-full" />
          </div>
        )}

        {displayedSet && (
          <QuestionSetResult
            questionSet={displayedSet}
            onDownloadPdf={() => downloadPdf.mutate(displayedSet.id)}
            isDownloading={downloadPdf.isPending}
          />
        )}

        {!displayedSet && !isLoadingLatest && !generate.isPending && allTopics.length > 0 && (
          <div className="mt-6">
            <EmptyState
              title="No practice questions yet"
              message="Select your topics above and click Generate questions to get your first set."
            />
          </div>
        )}
      </Card>
    </div>
  );
}
