import { useState } from "react";
import { FileText, LayoutDashboard, Mail, Sparkles, TrendingUp } from "lucide-react";
import { Tabs, type TabItem } from "@/components/ui/tabs";
import { AiDashboardOverview } from "@/features/ai/components/AiDashboardOverview";
import { ResumeAnalysisPanel } from "@/features/ai/components/ResumeAnalysisPanel";
import { JobRecommendations } from "@/features/ai/components/JobRecommendations";
import { CoverLetterGenerator } from "@/features/ai/components/CoverLetterGenerator";
import { SkillGapAnalysis } from "@/features/ai/components/SkillGapAnalysis";

const TAB_ITEMS: TabItem[] = [
  { value: "dashboard", label: "Dashboard", icon: <LayoutDashboard className="h-4 w-4" /> },
  { value: "resume", label: "Resume & ATS", icon: <FileText className="h-4 w-4" /> },
  { value: "jobs", label: "Job Matches", icon: <Sparkles className="h-4 w-4" /> },
  { value: "cover-letter", label: "Cover Letter", icon: <Mail className="h-4 w-4" /> },
  { value: "skills", label: "Skill Gap", icon: <TrendingUp className="h-4 w-4" /> },
];

export default function CandidateAiPage() {
  const [activeTab, setActiveTab] = useState("dashboard");

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">AI career tools</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          Analyze your resume, discover matched jobs, generate cover letters, and close your skill gaps — all powered
          by the AI service.
        </p>
      </div>

      <Tabs items={TAB_ITEMS} value={activeTab} onChange={setActiveTab} />

      {activeTab === "dashboard" && <AiDashboardOverview onNavigate={setActiveTab} />}
      {activeTab === "resume" && <ResumeAnalysisPanel />}
      {activeTab === "jobs" && <JobRecommendations />}
      {activeTab === "cover-letter" && <CoverLetterGenerator />}
      {activeTab === "skills" && <SkillGapAnalysis />}
    </div>
  );
}
