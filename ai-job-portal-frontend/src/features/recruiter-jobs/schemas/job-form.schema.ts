import { z } from "zod";

const jobLocationSchema = z.object({
  city: z.string().min(1, "City is required").max(100),
  state: z.string().max(100).optional().or(z.literal("")),
  country: z.string().min(1, "Country is required").max(100),
});

const jobSkillSchema = z.object({
  name: z.string().min(1, "Skill name is required").max(100),
  requiredProficiency: z.enum(["BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT"]),
  mandatory: z.boolean(),
});

const jobBenefitSchema = z.object({
  title: z.string().min(1, "Benefit title is required").max(150),
  description: z.string().max(500).optional().or(z.literal("")),
});

const jobRequirementSchema = z.object({
  type: z.enum(["QUALIFICATION", "RESPONSIBILITY", "NICE_TO_HAVE"]),
  description: z.string().min(1, "Description is required").max(1000),
  displayOrder: z.number().min(0),
});

/** Mirrors CreateJobRequest/UpdateJobRequest validation exactly, including the salary-range and future-deadline rules. */
export const jobFormSchema = z
  .object({
    categoryId: z.string().min(1, "Category is required"),
    title: z.string().min(1, "Title is required").max(200, "Title must not exceed 200 characters"),
    description: z
      .string()
      .min(1, "Description is required")
      .max(20000, "Description must not exceed 20000 characters"),
    jobType: z.enum(["FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "FREELANCE", "TEMPORARY"]),
    experienceLevel: z.enum([
      "ENTRY_LEVEL",
      "ASSOCIATE",
      "MID_LEVEL",
      "SENIOR_LEVEL",
      "LEAD",
      "MANAGER",
      "EXECUTIVE",
    ]),
    workMode: z.enum(["ON_SITE", "REMOTE", "HYBRID"]),
    minSalary: z.number().nullable().optional(),
    maxSalary: z.number().nullable().optional(),
    salaryType: z.enum(["HOURLY", "MONTHLY", "ANNUAL"]).nullable().optional(),
    currency: z.enum(["USD", "EUR", "GBP", "INR", "AUD", "CAD"]).nullable().optional(),
    vacancies: z.number().min(1, "Vacancies must be at least 1"),
    applicationDeadline: z.string().nullable().optional(),
    // DAY11/DAY07 "Apply Methods" — mirrors CreateJobRequest's applyMethod + externalApplyUrl exactly.
    applyMethod: z.enum(["EASY_APPLY", "QUICK_APPLY", "EXTERNAL_APPLY"]),
    externalApplyUrl: z.string().max(1000).optional().or(z.literal("")),
    locations: z.array(jobLocationSchema).min(1, "At least one location is required"),
    skills: z.array(jobSkillSchema),
    benefits: z.array(jobBenefitSchema),
    requirements: z.array(jobRequirementSchema),
  })
  .refine((data) => !data.minSalary || !data.maxSalary || data.maxSalary >= data.minSalary, {
    message: "Maximum salary must be greater than or equal to minimum salary",
    path: ["maxSalary"],
  })
  .refine((data) => !data.applicationDeadline || new Date(data.applicationDeadline).getTime() > Date.now(), {
    message: "Application deadline must be in the future",
    path: ["applicationDeadline"],
  })
  .refine((data) => data.applyMethod !== "EXTERNAL_APPLY" || !!data.externalApplyUrl, {
    message: "External apply URL is required when apply method is External Apply",
    path: ["externalApplyUrl"],
  })
  .refine((data) => !data.externalApplyUrl || z.string().url().safeParse(data.externalApplyUrl).success, {
    message: "External apply URL must be a valid URL",
    path: ["externalApplyUrl"],
  });
export type JobFormValues = z.infer<typeof jobFormSchema>;
