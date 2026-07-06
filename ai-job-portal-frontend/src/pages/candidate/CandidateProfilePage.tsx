import { useState } from "react";
import { User, GraduationCap, Briefcase, Sparkles, FileText } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Tabs, type TabItem } from "@/components/ui/tabs";
import { useCandidateProfile } from "@/features/profile/hooks/useCandidateProfile";
import { ProfileForm } from "@/features/profile/components/ProfileForm";
import { ProfileCompletionCard } from "@/features/profile/components/ProfileCompletionCard";
import { EducationSection } from "@/features/profile/components/EducationSection";
import { ExperienceSection } from "@/features/profile/components/ExperienceSection";
import { SkillsSection } from "@/features/profile/components/SkillsSection";
import { ResumeManager } from "@/features/profile/components/ResumeManager";

const TAB_ITEMS: TabItem[] = [
  { value: "personal", label: "Personal Info", icon: <User className="h-4 w-4" /> },
  { value: "resume", label: "Resumes", icon: <FileText className="h-4 w-4" /> },
  { value: "education", label: "Education", icon: <GraduationCap className="h-4 w-4" /> },
  { value: "experience", label: "Experience", icon: <Briefcase className="h-4 w-4" /> },
  { value: "skills", label: "Skills", icon: <Sparkles className="h-4 w-4" /> },
];

export default function CandidateProfilePage() {
  const { data: profile, isLoading } = useCandidateProfile();
  const [activeTab, setActiveTab] = useState("personal");

  if (isLoading) {
    return (
      <Card>
        <Skeleton className="h-6 w-48" />
        <Skeleton className="mt-4 h-32 w-full" />
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">My Profile</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          Keep your profile up to date to get better job recommendations.
        </p>
      </div>

      {profile ? (
        <div className="grid gap-4 sm:grid-cols-3">
          <div className="sm:col-span-2">
            <Card>
              <p className="text-lg font-semibold">{profile.fullName}</p>
              <p className="text-sm text-[hsl(var(--muted))]">{profile.email}</p>
              {profile.headline && <p className="mt-2 text-sm">{profile.headline}</p>}
            </Card>
          </div>
          <ProfileCompletionCard />
        </div>
      ) : (
        <Card className="border-primary-600/30 bg-primary-600/5">
          <p className="text-sm font-medium">
            You have not created your candidate profile yet. Fill in the form below to get started — education,
            experience, skills, and resume can be added once your profile is created.
          </p>
        </Card>
      )}

      {profile ? (
        <div className="space-y-4">
          <Tabs items={TAB_ITEMS} value={activeTab} onChange={setActiveTab} />
          {activeTab === "personal" && (
            <Card>
              <ProfileForm profile={profile} />
            </Card>
          )}
          {activeTab === "resume" && <ResumeManager />}
          {activeTab === "education" && <EducationSection />}
          {activeTab === "experience" && <ExperienceSection />}
          {activeTab === "skills" && <SkillsSection />}
        </div>
      ) : (
        <Card>
          <ProfileForm profile={null} />
        </Card>
      )}
    </div>
  );
}
