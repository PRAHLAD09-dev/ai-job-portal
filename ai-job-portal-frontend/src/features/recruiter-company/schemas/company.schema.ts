import { z } from "zod";

const PHONE_REGEX = /^\+?[0-9\-\s()]{7,20}$/;
const currentYear = new Date().getFullYear();

/** Mirrors CreateCompanyRequest validation exactly. */
export const createCompanyFormSchema = z.object({
  name: z.string().min(1, "Company name is required").max(200, "Company name must not exceed 200 characters"),
  description: z.string().max(4000, "Description must not exceed 4000 characters").optional().or(z.literal("")),
  industry: z.enum([
    "INFORMATION_TECHNOLOGY",
    "FINANCE",
    "HEALTHCARE",
    "EDUCATION",
    "MANUFACTURING",
    "RETAIL",
    "CONSTRUCTION",
    "HOSPITALITY",
    "TELECOMMUNICATIONS",
    "MEDIA_AND_ENTERTAINMENT",
    "TRANSPORTATION_AND_LOGISTICS",
    "GOVERNMENT",
    "NON_PROFIT",
    "OTHER",
  ]),
  companySize: z.enum(["SIZE_1_10", "SIZE_11_50", "SIZE_51_200", "SIZE_201_500", "SIZE_501_1000", "SIZE_1001_PLUS"]),
  foundedYear: z
    .number()
    .min(1800, "Founded year must be realistic")
    .max(currentYear, "Founded year cannot be in the future")
    .nullable()
    .optional(),
  websiteUrl: z.string().max(500).url("Must be a valid URL").optional().or(z.literal("")),
  email: z.string().email("Must be a valid email address").max(255).optional().or(z.literal("")),
  phoneNumber: z.string().regex(PHONE_REGEX, "Must be a valid phone number").optional().or(z.literal("")),
  recruiterTitle: z.enum([
    "HR_MANAGER",
    "TALENT_ACQUISITION_SPECIALIST",
    "RECRUITMENT_CONSULTANT",
    "HIRING_MANAGER",
    "FOUNDER",
    "CO_FOUNDER",
    "CEO",
    "OTHER",
  ]),
  recruiterDesignation: z.string().max(150).optional().or(z.literal("")),
  recruiterPhoneNumber: z.string().regex(PHONE_REGEX, "Must be a valid phone number").optional().or(z.literal("")),
});
export type CreateCompanyFormValues = z.infer<typeof createCompanyFormSchema>;

/** Mirrors UpdateCompanyRequest validation exactly. */
export const updateCompanyFormSchema = createCompanyFormSchema.omit({
  recruiterTitle: true,
  recruiterDesignation: true,
  recruiterPhoneNumber: true,
});
export type UpdateCompanyFormValues = z.infer<typeof updateCompanyFormSchema>;

/** Mirrors CompanyLocationRequest validation exactly. */
export const companyLocationFormSchema = z.object({
  addressLine: z.string().min(1, "Address line is required").max(255),
  city: z.string().min(1, "City is required").max(100),
  state: z.string().max(100).optional().or(z.literal("")),
  country: z.string().min(1, "Country is required").max(100),
  postalCode: z.string().max(20).optional().or(z.literal("")),
  headquarters: z.boolean(),
});
export type CompanyLocationFormValues = z.infer<typeof companyLocationFormSchema>;

/** Mirrors CompanySocialLinkRequest validation exactly. */
export const companySocialLinkFormSchema = z.object({
  platform: z.enum(["LINKEDIN", "TWITTER", "FACEBOOK", "INSTAGRAM", "YOUTUBE", "GITHUB", "WEBSITE"]),
  url: z.string().min(1, "URL is required").max(500),
});
export type CompanySocialLinkFormValues = z.infer<typeof companySocialLinkFormSchema>;
