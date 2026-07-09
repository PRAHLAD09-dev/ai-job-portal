// Mirrors recruiter-service company/location/sociallink enums & DTOs exactly. Do not rename fields.

export type Industry =
  | "INFORMATION_TECHNOLOGY"
  | "FINANCE"
  | "HEALTHCARE"
  | "EDUCATION"
  | "MANUFACTURING"
  | "RETAIL"
  | "CONSTRUCTION"
  | "HOSPITALITY"
  | "TELECOMMUNICATIONS"
  | "MEDIA_AND_ENTERTAINMENT"
  | "TRANSPORTATION_AND_LOGISTICS"
  | "GOVERNMENT"
  | "NON_PROFIT"
  | "OTHER";

export type CompanySize =
  | "SIZE_1_10"
  | "SIZE_11_50"
  | "SIZE_51_200"
  | "SIZE_201_500"
  | "SIZE_501_1000"
  | "SIZE_1001_PLUS";

export type VerificationStatus = "PENDING" | "VERIFIED" | "REJECTED";

export type CompanyAssetType = "LOGO" | "BANNER";

export type SocialPlatform =
  | "LINKEDIN"
  | "TWITTER"
  | "FACEBOOK"
  | "INSTAGRAM"
  | "YOUTUBE"
  | "GITHUB"
  | "WEBSITE";

export interface CompanyLocationResponse {
  id: string;
  addressLine: string;
  city: string;
  state: string | null;
  country: string;
  postalCode: string | null;
  headquarters: boolean;
}

export interface CompanySocialLinkResponse {
  id: string;
  platform: SocialPlatform;
  url: string;
}

/** Full company profile — GET /companies/me. Includes internal management fields. */
export interface CompanyResponse {
  id: string;
  name: string;
  slug: string;
  description: string | null;
  industry: Industry;
  companySize: CompanySize;
  foundedYear: number | null;
  websiteUrl: string | null;
  email: string | null;
  phoneNumber: string | null;
  logoUrl: string | null;
  bannerUrl: string | null;
  verificationStatus: VerificationStatus;
  activeJobCount: number;
  totalHires: number;
  locations: CompanyLocationResponse[];
  socialLinks: CompanySocialLinkResponse[];
}

/** GET /companies/{slug}/public — excludes internal-only fields. */
export interface CompanyPublicResponse {
  id: string;
  name: string;
  slug: string;
  description: string | null;
  industry: Industry;
  companySize: CompanySize;
  foundedYear: number | null;
  websiteUrl: string | null;
  logoUrl: string | null;
  bannerUrl: string | null;
  verificationStatus: VerificationStatus;
  locations: CompanyLocationResponse[];
  socialLinks: CompanySocialLinkResponse[];
}

/** GET /companies/me/statistics — recruiter dashboard widget. */
export interface CompanyStatisticsResponse {
  companyId: string;
  activeJobCount: number;
  totalHires: number;
  recruiterCount: number;
  verificationStatus: VerificationStatus;
}

export interface CompanyAssetResponse {
  assetType: string;
  url: string;
}

/** POST /companies — registers company + creates caller's recruiter profile as owner. */
export interface CreateCompanyRequest {
  name: string;
  description: string | null;
  industry: Industry;
  companySize: CompanySize;
  foundedYear: number | null;
  websiteUrl: string | null;
  email: string | null;
  phoneNumber: string | null;
  recruiterTitle: RecruiterTitle;
  recruiterDesignation: string | null;
  recruiterPhoneNumber: string | null;
}

export interface UpdateCompanyRequest {
  name: string;
  description: string | null;
  industry: Industry;
  companySize: CompanySize;
  foundedYear: number | null;
  websiteUrl: string | null;
  email: string | null;
  phoneNumber: string | null;
}

export interface CompanyLocationRequest {
  addressLine: string;
  city: string;
  state: string | null;
  country: string;
  postalCode: string | null;
  headquarters: boolean;
}

export interface CompanySocialLinkRequest {
  platform: SocialPlatform;
  url: string;
}

/** Recruiter title enum — reused here since CreateCompanyRequest embeds the owner's title. */
export type RecruiterTitle =
  | "HR_MANAGER"
  | "TALENT_ACQUISITION_SPECIALIST"
  | "RECRUITMENT_CONSULTANT"
  | "HIRING_MANAGER"
  | "FOUNDER"
  | "CO_FOUNDER"
  | "CEO"
  | "OTHER";
