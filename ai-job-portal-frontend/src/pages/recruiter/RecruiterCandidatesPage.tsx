import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Kanban, List } from "lucide-react";
import { Select } from "@/components/ui/select";
import { Tabs, type TabItem } from "@/components/ui/tabs";
import { ROUTES, buildRoute } from "@/constants/routes";
import { useMyCompanyJobs } from "@/features/recruiter-jobs/hooks/useRecruiterJobs";
import { HiringPipelineBoard } from "@/features/recruiter-applications/components/HiringPipelineBoard";
import { ApplicationsListView } from "@/features/recruiter-applications/components/ApplicationsListView";
import { ApplicationDetailModal } from "@/features/recruiter-applications/components/ApplicationDetailModal";

const TAB_ITEMS: TabItem[] = [
  { value: "pipeline", label: "Pipeline", icon: <Kanban className="h-4 w-4" /> },
  { value: "list", label: "List", icon: <List className="h-4 w-4" /> },
];

export default function RecruiterCandidatesPage() {
  const { applicationId } = useParams<{ applicationId?: string }>();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("pipeline");
  const [pipelineJobId, setPipelineJobId] = useState<string>("ALL");

  const { data: jobsPage } = useMyCompanyJobs({ page: 0, size: 100 });
  const jobs = jobsPage?.content ?? [];

  const closeDetail = () => navigate(ROUTES.RECRUITER_CANDIDATES);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Candidates</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          Track applicants through your hiring pipeline and manage every stage of the process.
        </p>
      </div>

      <div className="flex flex-wrap items-center justify-between gap-3">
        <Tabs items={TAB_ITEMS} value={activeTab} onChange={setActiveTab} />
        {activeTab === "pipeline" && (
          <Select className="sm:w-56" value={pipelineJobId} onChange={(e) => setPipelineJobId(e.target.value)}>
            <option value="ALL">All jobs</option>
            {jobs.map((job) => (
              <option key={job.id} value={job.id}>
                {job.title}
              </option>
            ))}
          </Select>
        )}
      </div>

      {activeTab === "pipeline" ? (
        <HiringPipelineBoard
          jobId={pipelineJobId === "ALL" ? undefined : pipelineJobId}
          onSelectApplication={(id) => navigate(buildRoute.recruiterApplicationDetails(id))}
        />
      ) : (
        <ApplicationsListView
          jobs={jobs}
          onSelectApplication={(id) => navigate(buildRoute.recruiterApplicationDetails(id))}
        />
      )}

      <ApplicationDetailModal applicationId={applicationId ?? null} onClose={closeDetail} />
    </div>
  );
}
