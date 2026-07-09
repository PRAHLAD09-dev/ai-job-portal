// Mirrors recruiter-service recruiter/dto DTOs exactly. Do not rename fields.
import type { RecruiterTitle } from "@/features/recruiter-company/types";

export type { RecruiterTitle };

/** GET/PUT /recruiter/profile. */
export interface RecruiterResponse {
  id: string;
  userId: string;
  email: string;
  fullName: string;
  phoneNumber: string | null;
  title: RecruiterTitle;
  designation: string | null;
  profilePictureUrl: string | null;
  owner: boolean;
  companyId: string;
  companyName: string;
}

export interface UpdateRecruiterProfileRequest {
  phoneNumber: string | null;
  title: RecruiterTitle;
  designation: string | null;
}
