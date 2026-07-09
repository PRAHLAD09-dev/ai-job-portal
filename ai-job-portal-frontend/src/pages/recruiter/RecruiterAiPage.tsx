import { useState } from "react";
import { FileText, MessageSquareText, Users } from "lucide-react";
import { Tabs, type TabItem } from "@/components/ui/tabs";
import { useMyCompanyJobs } from "@/features/recruiter-jobs/hooks/useRecruiterJobs";
import { JobDescriptionGenerator } from "@/features/ai/components/JobDescriptionGenerator";
import { InterviewQuestionGenerator } from "@/features/ai/components/InterviewQuestionGenerator";
import { CandidateRecommendations } from "@/features/ai/components/CandidateRecommendations";
import { ApplicationDetailModal } from "@/features/recruiter-applications/components/ApplicationDetailModal";

const TAB_ITEMS: TabItem[] = [
  { value: "job-description", label: "Job description", icon: <FileText className="h-4 w-4" /> },
  { value: "interview-questions", label: "Interview questions", icon: <MessageSquareText className="h-4 w-4" /> },
  { value: "candidates", label: "Candidate matches", icon: <Users className="h-4 w-4" /> },
];

export default function RecruiterAiPage() {
  const [activeTab, setActiveTab] = useState("job-description");
  const [selectedApplicationId, setSelectedApplicationId] = useState<string | null>(null);
  const { data: jobsPage } = useMyCompanyJobs({ page: 0, size: 100 });
  const jobs = jobsPage?.content ?? [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">AI recruiting tools</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          Generate job descriptions and interview questions, and find your best-matched candidates.
        </p>
      </div>

      <Tabs items={TAB_ITEMS} value={activeTab} onChange={setActiveTab} />

      {activeTab === "job-description" && <JobDescriptionGenerator />}
      {activeTab === "interview-questions" && <InterviewQuestionGenerator jobs={jobs} />}
      {activeTab === "candidates" && (
        <CandidateRecommendations jobs={jobs} onSelectApplication={setSelectedApplicationId} />
      )}

      <ApplicationDetailModal applicationId={selectedApplicationId} onClose={() => setSelectedApplicationId(null)} />
    </div>
  );
}
