import { useParams } from "react-router-dom";
import { Building2, Calendar, Globe, MapPin, Users } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { CompanyLocationMap } from "@/components/common/CompanyLocationMap";
import { formatEnumLabel } from "@/utils/format";
import { useCompanyPublicProfile } from "@/features/recruiter-company/hooks/useCompany";

function verificationVariant(status: string) {
  if (status === "VERIFIED") return "success" as const;
  if (status === "REJECTED") return "danger" as const;
  return "warning" as const;
}

export default function CompanyProfilePage() {
  const { slug } = useParams<{ slug: string }>();
  const { data: company, isLoading } = useCompanyPublicProfile(slug);

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-32 w-full" />
        <Skeleton className="h-48 w-full" />
      </div>
    );
  }

  if (!company) {
    return (
      <EmptyState
        icon={<Building2 className="h-8 w-8" />}
        title="Company not found"
        message="This company profile may have been removed or does not exist."
      />
    );
  }

  const headquarters = company.locations.find((l) => l.headquarters) ?? company.locations[0] ?? null;

  return (
    <div className="space-y-6">
      <Card className="overflow-hidden p-0">
        <div className="h-32 w-full bg-[hsl(var(--border-color))]/40">
          {company.bannerUrl && (
            <img src={company.bannerUrl} alt="" className="h-full w-full object-cover" />
          )}
        </div>
        <div className="p-6">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div className="flex items-start gap-4">
              <div className="-mt-12 flex h-20 w-20 shrink-0 items-center justify-center overflow-hidden rounded-xl border-4 border-[hsl(var(--surface))] bg-[hsl(var(--surface))] shadow-sm">
                {company.logoUrl ? (
                  <img src={company.logoUrl} alt={company.name} className="h-full w-full object-cover" />
                ) : (
                  <Building2 className="h-9 w-9 text-[hsl(var(--muted))]" />
                )}
              </div>
              <div>
                <h1 className="text-xl font-semibold">{company.name}</h1>
                <p className="mt-1 text-sm text-[hsl(var(--muted))]">{formatEnumLabel(company.industry)}</p>
              </div>
            </div>
            <Badge variant={verificationVariant(company.verificationStatus)}>
              {formatEnumLabel(company.verificationStatus)}
            </Badge>
          </div>

          <div className="mt-6 grid grid-cols-2 gap-4 border-t border-[hsl(var(--border-color))] pt-4 text-sm sm:grid-cols-4">
            <div>
              <p className="flex items-center gap-1.5 text-[hsl(var(--muted))]">
                <Users className="h-4 w-4" /> Company Size
              </p>
              <p className="mt-1 font-medium">{formatEnumLabel(company.companySize)}</p>
            </div>
            {company.foundedYear && (
              <div>
                <p className="flex items-center gap-1.5 text-[hsl(var(--muted))]">
                  <Calendar className="h-4 w-4" /> Founded
                </p>
                <p className="mt-1 font-medium">{company.foundedYear}</p>
              </div>
            )}
            {company.websiteUrl && (
              <div>
                <p className="flex items-center gap-1.5 text-[hsl(var(--muted))]">
                  <Globe className="h-4 w-4" /> Website
                </p>
                <a
                  href={company.websiteUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="mt-1 block truncate font-medium text-primary-600 hover:underline"
                >
                  {company.websiteUrl}
                </a>
              </div>
            )}
            {headquarters && (
              <div>
                <p className="flex items-center gap-1.5 text-[hsl(var(--muted))]">
                  <MapPin className="h-4 w-4" /> Headquarters
                </p>
                <p className="mt-1 font-medium">
                  {headquarters.city}, {headquarters.country}
                </p>
              </div>
            )}
          </div>
        </div>
      </Card>

      {company.description && (
        <Card>
          <h2 className="text-base font-semibold">About</h2>
          <p className="mt-3 whitespace-pre-line text-sm leading-relaxed">{company.description}</p>
        </Card>
      )}

      {company.locations.length > 0 && (
        <Card>
          <h2 className="text-base font-semibold">Locations</h2>
          <div className="mt-3 grid gap-3 sm:grid-cols-2">
            {company.locations.map((location) => (
              <div key={location.id} className="rounded-lg border border-[hsl(var(--border-color))] p-3">
                <p className="flex items-center gap-1.5 text-sm font-medium">
                  <MapPin className="h-3.5 w-3.5 shrink-0 text-[hsl(var(--muted))]" />
                  {location.city}, {location.country}
                  {location.headquarters && (
                    <Badge variant="primary" className="ml-1">
                      HQ
                    </Badge>
                  )}
                </p>
                <p className="mt-1 text-xs text-[hsl(var(--muted))]">
                  {location.addressLine}
                  {location.state ? `, ${location.state}` : ""}
                  {location.postalCode ? ` ${location.postalCode}` : ""}
                </p>
              </div>
            ))}
          </div>
          <div className="mt-4">
            <CompanyLocationMap locations={company.locations} />
          </div>
        </Card>
      )}

      {company.socialLinks.length > 0 && (
        <Card>
          <h2 className="text-base font-semibold">Social &amp; web links</h2>
          <div className="mt-3 flex flex-wrap gap-2">
            {company.socialLinks.map((link) => (
              <a
                key={link.id}
                href={link.url}
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center gap-1.5 rounded-lg border border-[hsl(var(--border-color))] px-3 py-1.5 text-sm hover:bg-[hsl(var(--border-color))]/40"
              >
                <Globe className="h-3.5 w-3.5 text-[hsl(var(--muted))]" />
                {formatEnumLabel(link.platform)}
              </a>
            ))}
          </div>
        </Card>
      )}
    </div>
  );
}
