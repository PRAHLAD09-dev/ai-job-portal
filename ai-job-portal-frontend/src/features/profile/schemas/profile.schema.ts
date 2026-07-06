import { z } from "zod";

/** Mirrors CreateCandidateProfileRequest/UpdateCandidateProfileRequest validation exactly. */
export const profileFormSchema = z.object({
  headline: z.string().max(200, "Headline must not exceed 200 characters").optional().or(z.literal("")),
  summary: z.string().max(4000, "Summary must not exceed 4000 characters").optional().or(z.literal("")),
  phoneNumber: z
    .string()
    .regex(/^\+?[0-9\-\s()]{7,20}$/, "Phone number must be a valid phone number")
    .optional()
    .or(z.literal("")),
  dateOfBirth: z.string().optional().or(z.literal("")),
  city: z.string().max(100, "City must not exceed 100 characters").optional().or(z.literal("")),
  state: z.string().max(100, "State must not exceed 100 characters").optional().or(z.literal("")),
  country: z.string().max(100, "Country must not exceed 100 characters").optional().or(z.literal("")),
  portfolioUrl: z
    .string()
    .max(500, "Portfolio URL must not exceed 500 characters")
    .url("Must be a valid URL")
    .optional()
    .or(z.literal("")),
  linkedinUrl: z
    .string()
    .max(500, "LinkedIn URL must not exceed 500 characters")
    .url("Must be a valid URL")
    .optional()
    .or(z.literal("")),
  githubUrl: z
    .string()
    .max(500, "GitHub URL must not exceed 500 characters")
    .url("Must be a valid URL")
    .optional()
    .or(z.literal("")),
  visibility: z.enum(["PUBLIC", "PRIVATE"]),
});
export type ProfileFormValues = z.infer<typeof profileFormSchema>;

/** Mirrors EducationRequest validation, including the endDate/currentlyStudying cross-field rule. */
export const educationFormSchema = z
  .object({
    institutionName: z.string().min(1, "Institution name is required").max(200),
    degreeType: z.enum([
      "HIGH_SCHOOL",
      "DIPLOMA",
      "ASSOCIATE",
      "BACHELOR",
      "MASTER",
      "DOCTORATE",
      "CERTIFICATION",
      "OTHER",
    ]),
    fieldOfStudy: z.string().min(1, "Field of study is required").max(150),
    startDate: z.string().min(1, "Start date is required"),
    endDate: z.string().optional().or(z.literal("")),
    currentlyStudying: z.boolean(),
    grade: z.string().max(50, "Grade must not exceed 50 characters").optional().or(z.literal("")),
    description: z.string().max(4000, "Description must not exceed 4000 characters").optional().or(z.literal("")),
  })
  .refine((data) => (data.currentlyStudying ? !data.endDate : !!data.endDate), {
    message: "Provide an end date, or check \"Currently studying\" instead",
    path: ["endDate"],
  });
export type EducationFormValues = z.infer<typeof educationFormSchema>;

/** Mirrors ExperienceRequest validation, including the endDate/currentlyWorking cross-field rule. */
export const experienceFormSchema = z
  .object({
    companyName: z.string().min(1, "Company name is required").max(200),
    jobTitle: z.string().min(1, "Job title is required").max(150),
    employmentType: z.enum(["FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "FREELANCE"]),
    location: z.string().max(150, "Location must not exceed 150 characters").optional().or(z.literal("")),
    startDate: z.string().min(1, "Start date is required"),
    endDate: z.string().optional().or(z.literal("")),
    currentlyWorking: z.boolean(),
    description: z.string().max(4000, "Description must not exceed 4000 characters").optional().or(z.literal("")),
  })
  .refine((data) => (data.currentlyWorking ? !data.endDate : !!data.endDate), {
    message: "Provide an end date, or check \"Currently working here\" instead",
    path: ["endDate"],
  });
export type ExperienceFormValues = z.infer<typeof experienceFormSchema>;

/** Mirrors SkillRequest validation exactly. */
export const skillFormSchema = z.object({
  name: z.string().min(1, "Skill name is required").max(100),
  proficiency: z.enum(["BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT"]),
  yearsOfExperience: z
    .number({ message: "Years of experience must be a number" })
    .min(0, "Years of experience cannot be negative")
    .max(60, "Years of experience must be realistic")
    .nullable()
    .optional(),
});
export type SkillFormValues = z.infer<typeof skillFormSchema>;
