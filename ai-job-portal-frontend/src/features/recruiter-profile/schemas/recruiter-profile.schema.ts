import { z } from "zod";

/** Mirrors UpdateRecruiterProfileRequest validation exactly. */
export const recruiterProfileFormSchema = z.object({
  phoneNumber: z
    .string()
    .regex(/^\+?[0-9\-\s()]{7,20}$/, "Must be a valid phone number")
    .optional()
    .or(z.literal("")),
  title: z.enum([
    "HR_MANAGER",
    "TALENT_ACQUISITION_SPECIALIST",
    "RECRUITMENT_CONSULTANT",
    "HIRING_MANAGER",
    "FOUNDER",
    "CO_FOUNDER",
    "CEO",
    "OTHER",
  ]),
  designation: z.string().max(150, "Designation must not exceed 150 characters").optional().or(z.literal("")),
});
export type RecruiterProfileFormValues = z.infer<typeof recruiterProfileFormSchema>;
