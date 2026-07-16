import { Link } from "react-router-dom";
import { Eye, FileText, Pencil, Search, Send, Sparkles } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { buttonVariants } from "@/components/ui/button";
import { EmptyState } from "@/components/common/EmptyState";
import { ROUTES } from "@/constants/routes";
import { useAuth } from "@/hooks/useAuth";
import { ProfileCompletionCard } from "@/features/profile/components/ProfileCompletionCard";
import { useResumesList } from "@/features/profile/hooks/useResumes";
import { useSavedJobsList } from "@/features/jobs/hooks/useSavedJobs";
import { useFeaturedJobs } from "@/features/jobs/hooks/useJobs";
import { useMyApplications } from "@/features/applications/hooks/useApplications";
import { useLatestResumeAnalysis } from "@/features/ai/hooks/useAi";
import { JobCard } from "@/features/jobs/components/JobCard";
import { ApplicationCard } from "@/features/applications/components/ApplicationCard";

export default function CandidateDashboardPage() {
  const { user } = useAuth();
  const { data: resumes, isLoading: isLoadingResumes } = useResumesList();
  const { data: savedJobs } = useSavedJobsList({ page: 0, size: 100 });
  // Fetched once at a larger page size: powers both the "recent applications" list (first 3)
  // and the Applications / Viewed Applications stat cards, without a second network call.
  const { data: applications, isLoading: isLoadingApplications } = useMyApplications({ page: 0, size: 100 });
  const { data: featuredJobs, isLoading: isLoadingFeatured } = useFeaturedJobs();
  // "AI Match" widget — latest ATS/resume analysis score, if the candidate has run one yet.
  const { data: resumeAnalysis } = useLatestResumeAnalysis();

  const activeResumeCount = resumes?.filter((r) => r.status === "ACTIVE").length ?? 0;
  const recentApplications = applications?.content.slice(0, 3) ?? [];
  const viewedApplicationsCount = applications?.content.filter((a) => a.viewed).length ?? 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Welcome back, {user?.firstName}!</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          Here&apos;s what&apos;s happening with your job search today.
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
        <ProfileCompletionCard />

        <Card>
          <p className="text-sm font-medium text-[hsl(var(--muted))]">Resumes</p>
          {isLoadingResumes ? (
            <Skeleton className="mt-2 h-8 w-16" />
          ) : (
            <p className="mt-2 text-2xl font-semibold">{activeResumeCount}</p>
          )}
          <Link to={ROUTES.CANDIDATE_PROFILE} className="mt-2 inline-block text-xs text-primary-600 hover:underline">
            Manage resumes →
          </Link>
        </Card>

        <Card>
          <p className="text-sm font-medium text-[hsl(var(--muted))]">Saved Jobs</p>
          <p className="mt-2 text-2xl font-semibold">{savedJobs?.totalElements ?? 0}</p>
          <Link to={ROUTES.CANDIDATE_SAVED_JOBS} className="mt-2 inline-block text-xs text-primary-600 hover:underline">
            View saved jobs →
          </Link>
        </Card>

        <Card>
          <p className="text-sm font-medium text-[hsl(var(--muted))]">Applications</p>
          <p className="mt-2 text-2xl font-semibold">{applications?.totalElements ?? 0}</p>
          <Link to={ROUTES.CANDIDATE_APPLICATIONS} className="mt-2 inline-block text-xs text-primary-600 hover:underline">
            View applications →
          </Link>
        </Card>

        <Card>
          <p className="flex items-center gap-1 text-sm font-medium text-[hsl(var(--muted))]">
            <Eye className="h-3.5 w-3.5" /> Viewed by Recruiters
          </p>
          {isLoadingApplications ? (
            <Skeleton className="mt-2 h-8 w-16" />
          ) : (
            <p className="mt-2 text-2xl font-semibold">{viewedApplicationsCount}</p>
          )}
          <p className="mt-2 text-xs text-[hsl(var(--muted))]">of {applications?.totalElements ?? 0} applications</p>
        </Card>

        <Card>
          <p className="flex items-center gap-1 text-sm font-medium text-[hsl(var(--muted))]">
            <Sparkles className="h-3.5 w-3.5" /> AI Match
          </p>
          {resumeAnalysis ? (
            <p className="mt-2 text-2xl font-semibold">{Math.round(resumeAnalysis.atsScore)}%</p>
          ) : (
            <p className="mt-2 text-sm text-[hsl(var(--muted))]">Not analyzed yet</p>
          )}
          <Link to={ROUTES.CANDIDATE_AI} className="mt-2 inline-block text-xs text-primary-600 hover:underline">
            {resumeAnalysis ? "View analysis →" : "Analyze resume →"}
          </Link>
        </Card>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <div>
          <div className="mb-3 flex items-center justify-between">
            <h2 className="text-base font-semibold">Recent Applications</h2>
            <Link to={ROUTES.CANDIDATE_APPLICATIONS} className="text-xs text-primary-600 hover:underline">
              View all
            </Link>
          </div>
          {isLoadingApplications && (
            <div className="space-y-3">
              <Skeleton className="h-20 w-full" />
              <Skeleton className="h-20 w-full" />
            </div>
          )}
          {!isLoadingApplications && recentApplications.length === 0 && (
            <EmptyState
              icon={<Send className="h-8 w-8" />}
              title="No applications yet"
              message="Browse jobs and start applying to track them here."
              actionLabel="Find Jobs"
              onAction={() => (window.location.href = ROUTES.CANDIDATE_JOBS)}
            />
          )}
          <div className="space-y-3">
            {recentApplications.map((application) => (
              <ApplicationCard key={application.id} application={application} />
            ))}
          </div>
        </div>

        <div>
          <div className="mb-3 flex items-center justify-between">
            <h2 className="text-base font-semibold">Featured Jobs</h2>
            <Link to={ROUTES.CANDIDATE_JOBS} className="text-xs text-primary-600 hover:underline">
              Browse all
            </Link>
          </div>
          {isLoadingFeatured && (
            <div className="space-y-3">
              <Skeleton className="h-24 w-full" />
              <Skeleton className="h-24 w-full" />
            </div>
          )}
          {!isLoadingFeatured && featuredJobs?.length === 0 && (
            <EmptyState
              icon={<Search className="h-8 w-8" />}
              title="No featured jobs right now"
              message="Check back soon, or browse all open positions."
            />
          )}
          <div className="space-y-3">
            {featuredJobs?.slice(0, 3).map((job) => (
              <JobCard key={job.id} job={job} />
            ))}
          </div>
        </div>
      </div>

      <Card className="flex flex-col items-center justify-between gap-4 sm:flex-row">
        <div className="flex items-center gap-3">
          <FileText className="h-8 w-8 text-primary-600" />
          <div>
            <p className="font-medium">Keep your profile fresh</p>
            <p className="text-sm text-[hsl(var(--muted))]">
              A complete profile with an up-to-date resume gets noticed faster by recruiters.
            </p>
          </div>
        </div>
        <Link to={ROUTES.CANDIDATE_PROFILE} className={buttonVariants({ variant: "outline" })}>
          <Pencil className="h-4 w-4" /> Update Profile
        </Link>
      </Card>
    </div>
  );
}
