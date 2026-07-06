import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { profileFormSchema, type ProfileFormValues } from "@/features/profile/schemas/profile.schema";
import { useCreateProfile, useUpdateProfile } from "@/features/profile/hooks/useCandidateProfile";
import type { CandidateProfileResponse, CreateCandidateProfileRequest } from "@/features/profile/types";

function emptyToNull(value: string | undefined): string | null {
  return value && value.trim().length > 0 ? value.trim() : null;
}

function toDefaultValues(profile: CandidateProfileResponse | null): ProfileFormValues {
  return {
    headline: profile?.headline ?? "",
    summary: profile?.summary ?? "",
    phoneNumber: profile?.phoneNumber ?? "",
    dateOfBirth: profile?.dateOfBirth ?? "",
    city: profile?.city ?? "",
    state: profile?.state ?? "",
    country: profile?.country ?? "",
    portfolioUrl: profile?.portfolioUrl ?? "",
    linkedinUrl: profile?.linkedinUrl ?? "",
    githubUrl: profile?.githubUrl ?? "",
    visibility: profile?.visibility ?? "PUBLIC",
  };
}

/**
 * Handles both profile creation (first login, no profile exists yet)
 * and subsequent updates with the same form, since
 * CreateCandidateProfileRequest and UpdateCandidateProfileRequest share
 * an identical shape on the backend.
 */
export function ProfileForm({ profile }: { profile: CandidateProfileResponse | null }) {
  const createProfile = useCreateProfile();
  const updateProfile = useUpdateProfile();
  const isSaving = createProfile.isPending || updateProfile.isPending;

  const {
    register,
    handleSubmit,
    formState: { errors, isDirty },
  } = useForm<ProfileFormValues>({
    resolver: zodResolver(profileFormSchema),
    defaultValues: toDefaultValues(profile),
  });

  const onSubmit = (values: ProfileFormValues) => {
    const payload: CreateCandidateProfileRequest = {
      headline: emptyToNull(values.headline),
      summary: emptyToNull(values.summary),
      phoneNumber: emptyToNull(values.phoneNumber),
      dateOfBirth: emptyToNull(values.dateOfBirth),
      city: emptyToNull(values.city),
      state: emptyToNull(values.state),
      country: emptyToNull(values.country),
      portfolioUrl: emptyToNull(values.portfolioUrl),
      linkedinUrl: emptyToNull(values.linkedinUrl),
      githubUrl: emptyToNull(values.githubUrl),
      visibility: values.visibility,
    };

    if (profile) {
      updateProfile.mutate(payload);
    } else {
      createProfile.mutate(payload);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div className="grid gap-4 sm:grid-cols-2">
        <div className="sm:col-span-2">
          <FormField label="Headline" htmlFor="headline" error={errors.headline?.message}>
            <Input id="headline" placeholder="e.g. Senior Frontend Engineer" {...register("headline")} />
          </FormField>
        </div>

        <div className="sm:col-span-2">
          <FormField label="Summary" htmlFor="summary" error={errors.summary?.message}>
            <Textarea id="summary" rows={4} placeholder="Tell recruiters about yourself" {...register("summary")} />
          </FormField>
        </div>

        <FormField label="Phone Number" htmlFor="phoneNumber" error={errors.phoneNumber?.message}>
          <Input id="phoneNumber" placeholder="+1 555 123 4567" {...register("phoneNumber")} />
        </FormField>

        <FormField label="Date of Birth" htmlFor="dateOfBirth" error={errors.dateOfBirth?.message}>
          <Input id="dateOfBirth" type="date" {...register("dateOfBirth")} />
        </FormField>

        <FormField label="City" htmlFor="city" error={errors.city?.message}>
          <Input id="city" {...register("city")} />
        </FormField>

        <FormField label="State" htmlFor="state" error={errors.state?.message}>
          <Input id="state" {...register("state")} />
        </FormField>

        <FormField label="Country" htmlFor="country" error={errors.country?.message}>
          <Input id="country" {...register("country")} />
        </FormField>

        <FormField label="Profile Visibility" htmlFor="visibility" error={errors.visibility?.message}>
          <Select id="visibility" {...register("visibility")}>
            <option value="PUBLIC">Public — visible to recruiters</option>
            <option value="PRIVATE">Private — hidden from search</option>
          </Select>
        </FormField>
      </div>

      <div className="border-t border-[hsl(var(--border-color))] pt-6">
        <h3 className="text-sm font-semibold">Portfolio &amp; Social Links</h3>
        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <FormField label="Portfolio Website" htmlFor="portfolioUrl" error={errors.portfolioUrl?.message}>
            <Input id="portfolioUrl" placeholder="https://yourportfolio.com" {...register("portfolioUrl")} />
          </FormField>
          <FormField label="LinkedIn" htmlFor="linkedinUrl" error={errors.linkedinUrl?.message}>
            <Input id="linkedinUrl" placeholder="https://linkedin.com/in/you" {...register("linkedinUrl")} />
          </FormField>
          <FormField label="GitHub" htmlFor="githubUrl" error={errors.githubUrl?.message}>
            <Input id="githubUrl" placeholder="https://github.com/you" {...register("githubUrl")} />
          </FormField>
        </div>
      </div>

      <div className="flex justify-end">
        <Button type="submit" isLoading={isSaving} disabled={!isDirty && !!profile}>
          {profile ? "Save Changes" : "Create Profile"}
        </Button>
      </div>
    </form>
  );
}
