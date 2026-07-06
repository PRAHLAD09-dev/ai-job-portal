import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { CheckCircle2, FileText } from "lucide-react";
import { Modal } from "@/components/ui/modal";
import { Button } from "@/components/ui/button";
import { Select } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { FormField } from "@/components/ui/form-field";
import { useResumesList } from "@/features/profile/hooks/useResumes";
import { useApplyToJob } from "@/features/applications/hooks/useApplications";

const applyFormSchema = z.object({
  resumeId: z.string().min(1, "Please select a resume"),
  coverLetter: z.string().max(5000, "Cover letter must not exceed 5000 characters").optional().or(z.literal("")),
});
type ApplyFormValues = z.infer<typeof applyFormSchema>;

interface ApplyJobDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  jobId: string;
  jobTitle: string;
}

export function ApplyJobDialog({ open, onOpenChange, jobId, jobTitle }: ApplyJobDialogProps) {
  const { data: resumes, isLoading: isLoadingResumes } = useResumesList();
  const applyToJob = useApplyToJob();
  const [isSuccess, setIsSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ApplyFormValues>({
    resolver: zodResolver(applyFormSchema),
    defaultValues: { resumeId: "", coverLetter: "" },
  });

  const handleClose = (nextOpen: boolean) => {
    if (!nextOpen) {
      setIsSuccess(false);
      reset({ resumeId: "", coverLetter: "" });
    }
    onOpenChange(nextOpen);
  };

  const onSubmit = (values: ApplyFormValues) => {
    applyToJob.mutate(
      { jobId, resumeId: values.resumeId, coverLetter: values.coverLetter || null },
      { onSuccess: () => setIsSuccess(true) },
    );
  };

  return (
    <Modal open={open} onOpenChange={handleClose} title={isSuccess ? "Application Submitted" : `Apply for ${jobTitle}`}>
      {isSuccess ? (
        <div className="flex flex-col items-center gap-3 py-6 text-center">
          <CheckCircle2 className="h-12 w-12 text-success-500" />
          <p className="font-medium">Your application has been submitted successfully!</p>
          <p className="text-sm text-[hsl(var(--muted))]">
            You can track its progress from the Applications page.
          </p>
          <Button onClick={() => handleClose(false)} className="mt-2">
            Done
          </Button>
        </div>
      ) : (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <FormField label="Select Resume" htmlFor="resumeId" required error={errors.resumeId?.message}>
            <Select id="resumeId" disabled={isLoadingResumes} {...register("resumeId")}>
              <option value="">Choose a resume</option>
              {resumes?.map((resume) => (
                <option key={resume.id} value={resume.id}>
                  {resume.fileName} (v{resume.versionNumber})
                </option>
              ))}
            </Select>
            {resumes?.length === 0 && (
              <p className="mt-1 flex items-center gap-1 text-xs text-warning-500">
                <FileText className="h-3.5 w-3.5" /> Upload a resume from your profile before applying.
              </p>
            )}
          </FormField>

          <FormField label="Cover Letter (optional)" htmlFor="coverLetter" error={errors.coverLetter?.message}>
            <Textarea
              id="coverLetter"
              rows={5}
              placeholder="Tell the recruiter why you're a great fit for this role"
              {...register("coverLetter")}
            />
          </FormField>

          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="outline" onClick={() => handleClose(false)}>
              Cancel
            </Button>
            <Button type="submit" isLoading={applyToJob.isPending} disabled={resumes?.length === 0}>
              Submit Application
            </Button>
          </div>
        </form>
      )}
    </Modal>
  );
}
