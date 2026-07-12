import { z } from "zod";

/** Mirrors JobAlertRequest validation exactly (job-service). */
export const jobAlertFormSchema = z.object({
  keyword: z.string().max(200, "Keyword must not exceed 200 characters").optional().or(z.literal("")),
  categoryId: z.string().optional().or(z.literal("")),
  jobType: z
    .enum(["FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "FREELANCE", "TEMPORARY"])
    .optional()
    .or(z.literal("")),
  experienceLevel: z
    .enum(["ENTRY_LEVEL", "ASSOCIATE", "MID_LEVEL", "SENIOR_LEVEL", "LEAD", "MANAGER", "EXECUTIVE"])
    .optional()
    .or(z.literal("")),
  workMode: z.enum(["ON_SITE", "REMOTE", "HYBRID"]).optional().or(z.literal("")),
  city: z.string().max(100, "City must not exceed 100 characters").optional().or(z.literal("")),
  frequency: z.enum(["INSTANT", "DAILY", "WEEKLY"], { required_error: "Frequency is required" }),
});
export type JobAlertFormValues = z.infer<typeof jobAlertFormSchema>;
