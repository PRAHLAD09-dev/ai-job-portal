import { useState } from "react";
import { Building2, Globe, MapPin, Users } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { Tabs, type TabItem } from "@/components/ui/tabs";
import { EmptyState } from "@/components/common/EmptyState";
import { formatEnumLabel } from "@/utils/format";
import { useMyCompany } from "@/features/recruiter-company/hooks/useCompany";
import { useRecruiterProfile } from "@/features/recruiter-profile/hooks/useRecruiterProfile";
import { CompanyProfileForm } from "@/features/recruiter-company/components/CompanyProfileForm";
import { CompanyAssetUploader } from "@/features/recruiter-company/components/CompanyAssetUploader";
import { CompanyLocationsManager } from "@/features/recruiter-company/components/CompanyLocationsManager";
import { CompanySocialLinksManager } from "@/features/recruiter-company/components/CompanySocialLinksManager";

const TAB_ITEMS: TabItem[] = [
  { value: "profile", label: "Profile", icon: <Building2 className="h-4 w-4" /> },
  { value: "locations", label: "Locations", icon: <MapPin className="h-4 w-4" /> },
  { value: "social", label: "Social links", icon: <Globe className="h-4 w-4" /> },
  { value: "team", label: "Team", icon: <Users className="h-4 w-4" /> },
];

function verificationVariant(status: string) {
  if (status === "VERIFIED") return "success" as const;
  if (status === "REJECTED") return "danger" as const;
  return "warning" as const;
}

export default function RecruiterCompanyPage() {
  const { data: company, isLoading } = useMyCompany();
  const [activeTab, setActiveTab] = useState("profile");

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
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Company</h1>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">
            Manage your company profile, branding, locations, and social presence.
          </p>
        </div>
        {company && (
          <Badge variant={verificationVariant(company.verificationStatus)}>
            {formatEnumLabel(company.verificationStatus)}
          </Badge>
        )}
      </div>

      {company && (
        <div className="grid gap-4 sm:grid-cols-3">
          <Card>
            <p className="text-sm text-[hsl(var(--muted))]">Active jobs</p>
            <p className="mt-2 text-2xl font-semibold">{company.activeJobCount}</p>
          </Card>
          <Card>
            <p className="text-sm text-[hsl(var(--muted))]">Total hires</p>
            <p className="mt-2 text-2xl font-semibold">{company.totalHires}</p>
          </Card>
          <Card>
            <p className="text-sm text-[hsl(var(--muted))]">Industry</p>
            <p className="mt-2 text-lg font-semibold">{formatEnumLabel(company.industry)}</p>
          </Card>
        </div>
      )}

      {!company ? (
        <CompanyProfileForm company={null} />
      ) : (
        <>
          <Tabs items={TAB_ITEMS} value={activeTab} onChange={setActiveTab} />
          {activeTab === "profile" && (
            <div className="space-y-6">
              <CompanyAssetUploader logoUrl={company.logoUrl} bannerUrl={company.bannerUrl} />
              <CompanyProfileForm company={company} />
            </div>
          )}
          {activeTab === "locations" && <CompanyLocationsManager enabled />}
          {activeTab === "social" && <CompanySocialLinksManager enabled />}
          {activeTab === "team" && <TeamTab />}
        </>
      )}
    </div>
  );
}

function TeamTab() {
  const { data: profile, isLoading } = useRecruiterProfile();

  return (
    <div className="space-y-4">
      <Card>
        <h2 className="text-lg font-semibold">Team members</h2>
        {isLoading ? (
          <Skeleton className="mt-4 h-14 w-full" />
        ) : profile ? (
          <div className="mt-4 flex items-center justify-between rounded-lg border border-[hsl(var(--border-color))] p-3">
            <div className="flex items-center gap-3">
              {profile.profilePictureUrl ? (
                <img src={profile.profilePictureUrl} alt={profile.fullName} className="h-9 w-9 rounded-full object-cover" />
              ) : (
                <div className="flex h-9 w-9 items-center justify-center rounded-full bg-primary-600/10 text-sm font-medium text-primary-600">
                  {profile.fullName.charAt(0)}
                </div>
              )}
              <div>
                <p className="text-sm font-medium">{profile.fullName}</p>
                <p className="text-xs text-[hsl(var(--muted))]">{profile.email}</p>
              </div>
            </div>
            <Badge variant="outline">{profile.owner ? "Owner" : formatEnumLabel(profile.title)}</Badge>
          </div>
        ) : (
          <p className="mt-4 text-sm text-[hsl(var(--muted))]">No data available.</p>
        )}
      </Card>
      <EmptyState
        icon={<Users className="h-10 w-10" />}
        title="No other team members"
        message="Inviting and managing additional recruiters isn't available yet."
      />
    </div>
  );
}
