import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Modal } from "@/components/ui/modal";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { formatEnumLabel } from "@/utils/format";
import { useJobCategories } from "@/features/jobs/hooks/useJobCategories";
import { jobAlertFormSchema, type JobAlertFormValues } from "@/features/jobs/schemas/job-alert.schema";
import type { ExperienceLevel, JobAlertRequest, JobAlertResponse, JobType, WorkMode } from "@/features/jobs/types";

const JOB_TYPES: JobType[] = ["FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "FREELANCE", "TEMPORARY"];
const EXPERIENCE_LEVELS: ExperienceLevel[] = [
  "ENTRY_LEVEL",
  "ASSOCIATE",
  "MID_LEVEL",
  "SENIOR_LEVEL",
  "LEAD",
  "MANAGER",
  "EXECUTIVE",
];
const WORK_MODES: WorkMode[] = ["ON_SITE", "REMOTE", "HYBRID"];
const FREQUENCIES = ["INSTANT", "DAILY", "WEEKLY"] as const;

interface JobAlertFormModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  alert: JobAlertResponse | null;
  isLoading: boolean;
  onSubmit: (payload: JobAlertRequest, onDone: () => void) => void;
}

function toDefaultValues(alert: JobAlertResponse | null): JobAlertFormValues {
  if (!alert) {
    return {
      keyword: "",
      categoryId: "",
      jobType: "",
      experienceLevel: "",
      workMode: "",
      city: "",
      frequency: "DAILY",
    };
  }
  return {
    keyword: alert.keyword ?? "",
    categoryId: alert.categoryId ?? "",
    jobType: alert.jobType ?? "",
    experienceLevel: alert.experienceLevel ?? "",
    workMode: alert.workMode ?? "",
    city: alert.city ?? "",
    frequency: alert.frequency,
  };
}

/** Maps blank-string form fields back to `null`, matching JobAlertRequest's optional-field shape. */
function toRequestPayload(values: JobAlertFormValues): JobAlertRequest {
  return {
    keyword: values.keyword || null,
    categoryId: values.categoryId || null,
    jobType: values.jobType || null,
    experienceLevel: values.experienceLevel || null,
    workMode: values.workMode || null,
    city: values.city || null,
    frequency: values.frequency,
  };
}

export function JobAlertFormModal({ open, onOpenChange, alert, isLoading, onSubmit }: JobAlertFormModalProps) {
  const { data: categories } = useJobCategories();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<JobAlertFormValues>({
    resolver: zodResolver(jobAlertFormSchema),
    defaultValues: toDefaultValues(alert),
  });

  return (
    <Modal
      open={open}
      onOpenChange={onOpenChange}
      title={alert ? "Edit job alert" : "Create job alert"}
      description="Get notified when new jobs match this search criteria."
    >
      <form
        onSubmit={handleSubmit((values) => onSubmit(toRequestPayload(values), () => onOpenChange(false)))}
        className="space-y-4"
      >
        <FormField label="Keyword" htmlFor="keyword" error={errors.keyword?.message}>
          <Input id="keyword" placeholder="e.g. Frontend Developer" {...register("keyword")} />
        </FormField>

        <FormField label="Category" htmlFor="categoryId" error={errors.categoryId?.message}>
          <Select id="categoryId" {...register("categoryId")}>
            <option value="">Any category</option>
            {categories?.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </Select>
        </FormField>

        <div className="grid grid-cols-2 gap-3">
          <FormField label="Job Type" htmlFor="jobType" error={errors.jobType?.message}>
            <Select id="jobType" {...register("jobType")}>
              <option value="">Any</option>
              {JOB_TYPES.map((type) => (
                <option key={type} value={type}>
                  {formatEnumLabel(type)}
                </option>
              ))}
            </Select>
          </FormField>

          <FormField label="Experience Level" htmlFor="experienceLevel" error={errors.experienceLevel?.message}>
            <Select id="experienceLevel" {...register("experienceLevel")}>
              <option value="">Any</option>
              {EXPERIENCE_LEVELS.map((level) => (
                <option key={level} value={level}>
                  {formatEnumLabel(level)}
                </option>
              ))}
            </Select>
          </FormField>
        </div>

        <div className="grid grid-cols-2 gap-3">
          <FormField label="Work Mode" htmlFor="workMode" error={errors.workMode?.message}>
            <Select id="workMode" {...register("workMode")}>
              <option value="">Any</option>
              {WORK_MODES.map((mode) => (
                <option key={mode} value={mode}>
                  {formatEnumLabel(mode)}
                </option>
              ))}
            </Select>
          </FormField>

          <FormField label="City" htmlFor="city" error={errors.city?.message}>
            <Input id="city" placeholder="e.g. Bengaluru" {...register("city")} />
          </FormField>
        </div>

        <FormField label="Notification Frequency" htmlFor="frequency" required error={errors.frequency?.message}>
          <Select id="frequency" {...register("frequency")}>
            {FREQUENCIES.map((frequency) => (
              <option key={frequency} value={frequency}>
                {formatEnumLabel(frequency)}
              </option>
            ))}
          </Select>
        </FormField>

        <div className="flex justify-end gap-2">
          <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isLoading}>
            {alert ? "Save changes" : "Create alert"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
