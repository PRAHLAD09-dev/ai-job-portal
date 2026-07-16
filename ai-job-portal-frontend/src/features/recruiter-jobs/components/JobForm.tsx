import { useFieldArray, useForm, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { ExternalLink, Plus, Trash2, Zap } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select } from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import { FormField } from "@/components/ui/form-field";
import { Button } from "@/components/ui/button";
import { formatEnumLabel } from "@/utils/format";
import { useJobCategories } from "@/features/jobs/hooks/useJobCategories";
import { jobFormSchema, type JobFormValues } from "@/features/recruiter-jobs/schemas/job-form.schema";
import type { JobResponse } from "@/features/jobs/types";
import type { JobFormRequest } from "@/features/recruiter-jobs/types";

const JOB_TYPES = ["FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "FREELANCE", "TEMPORARY"] as const;
const EXPERIENCE_LEVELS = ["ENTRY_LEVEL", "ASSOCIATE", "MID_LEVEL", "SENIOR_LEVEL", "LEAD", "MANAGER", "EXECUTIVE"] as const;
const WORK_MODES = ["ON_SITE", "REMOTE", "HYBRID"] as const;
const SALARY_TYPES = ["HOURLY", "MONTHLY", "ANNUAL"] as const;
const CURRENCIES = ["USD", "EUR", "GBP", "INR", "AUD", "CAD"] as const;
const PROFICIENCIES = ["BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT"] as const;
const REQUIREMENT_TYPES = ["QUALIFICATION", "RESPONSIBILITY", "NICE_TO_HAVE"] as const;

function toDateTimeLocal(value: string | null): string {
  if (!value) return "";
  const date = new Date(value);
  const offset = date.getTimezoneOffset();
  const local = new Date(date.getTime() - offset * 60_000);
  return local.toISOString().slice(0, 16);
}

interface JobFormProps {
  job?: JobResponse;
  isSubmitting: boolean;
  submitLabel: string;
  onSubmit: (payload: JobFormRequest) => void;
}

export function JobForm({ job, isSubmitting, submitLabel, onSubmit }: JobFormProps) {
  const { data: categories, isLoading: categoriesLoading } = useJobCategories();

  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<JobFormValues>({
    resolver: zodResolver(jobFormSchema),
    defaultValues: job
      ? {
          categoryId: job.category?.id ?? "",
          title: job.title,
          description: job.description,
          jobType: job.jobType,
          experienceLevel: job.experienceLevel,
          workMode: job.workMode,
          minSalary: job.minSalary,
          maxSalary: job.maxSalary,
          salaryType: job.salaryType,
          currency: job.currency,
          vacancies: job.vacancies,
          applicationDeadline: toDateTimeLocal(job.applicationDeadline),
          applyMethod: job.applyMethod,
          externalApplyUrl: job.externalApplyUrl ?? "",
          locations: job.locations.map((l) => ({ city: l.city, state: l.state, country: l.country })),
          skills: job.skills.map((s) => ({
            name: s.name,
            requiredProficiency: s.requiredProficiency,
            mandatory: s.mandatory,
          })),
          benefits: job.benefits.map((b) => ({ title: b.title, description: b.description ?? "" })),
          requirements: job.requirements.map((r) => ({
            type: r.type,
            description: r.description,
            displayOrder: r.displayOrder,
          })),
        }
      : {
          categoryId: "",
          title: "",
          description: "",
          jobType: "FULL_TIME",
          experienceLevel: "MID_LEVEL",
          workMode: "ON_SITE",
          minSalary: null,
          maxSalary: null,
          salaryType: null,
          currency: null,
          vacancies: 1,
          applicationDeadline: "",
          applyMethod: "EASY_APPLY",
          externalApplyUrl: "",
          locations: [{ city: "", state: "", country: "" }],
          skills: [],
          benefits: [],
          requirements: [],
        },
  });

  const applyMethod = useWatch({ control, name: "applyMethod" });

  const locationsArray = useFieldArray({ control, name: "locations" });
  const skillsArray = useFieldArray({ control, name: "skills" });
  const benefitsArray = useFieldArray({ control, name: "benefits" });
  const requirementsArray = useFieldArray({ control, name: "requirements" });

  const submit = (values: JobFormValues) => {
    onSubmit({
      categoryId: values.categoryId,
      title: values.title,
      description: values.description,
      jobType: values.jobType,
      experienceLevel: values.experienceLevel,
      workMode: values.workMode,
      minSalary: values.minSalary ?? null,
      maxSalary: values.maxSalary ?? null,
      salaryType: values.salaryType ?? null,
      currency: values.currency ?? null,
      vacancies: values.vacancies,
      applicationDeadline: values.applicationDeadline ? new Date(values.applicationDeadline).toISOString() : null,
      applyMethod: values.applyMethod,
      externalApplyUrl: values.applyMethod === "EXTERNAL_APPLY" ? values.externalApplyUrl || null : null,
      locations: values.locations.map((l) => ({ city: l.city, state: l.state || null, country: l.country })),
      skills: values.skills,
      benefits: values.benefits.map((b) => ({ title: b.title, description: b.description || null })),
      requirements: values.requirements,
    });
  };

  return (
    <form onSubmit={handleSubmit(submit)} className="space-y-6">
      <Card>
        <h2 className="text-lg font-semibold">Basic details</h2>
        <div className="mt-4 space-y-4">
          <FormField label="Job title" htmlFor="title" required error={errors.title?.message}>
            <Input id="title" {...register("title")} />
          </FormField>
          <FormField label="Category" htmlFor="categoryId" required error={errors.categoryId?.message}>
            <Select id="categoryId" disabled={categoriesLoading} {...register("categoryId")}>
              <option value="">Select a category</option>
              {categories?.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </Select>
          </FormField>
          <FormField label="Description" htmlFor="description" required error={errors.description?.message}>
            <Textarea id="description" rows={8} {...register("description")} />
          </FormField>
          <div className="grid gap-4 sm:grid-cols-3">
            <FormField label="Job type" htmlFor="jobType" required error={errors.jobType?.message}>
              <Select id="jobType" {...register("jobType")}>
                {JOB_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {formatEnumLabel(t)}
                  </option>
                ))}
              </Select>
            </FormField>
            <FormField label="Experience level" htmlFor="experienceLevel" required error={errors.experienceLevel?.message}>
              <Select id="experienceLevel" {...register("experienceLevel")}>
                {EXPERIENCE_LEVELS.map((l) => (
                  <option key={l} value={l}>
                    {formatEnumLabel(l)}
                  </option>
                ))}
              </Select>
            </FormField>
            <FormField label="Work mode" htmlFor="workMode" required error={errors.workMode?.message}>
              <Select id="workMode" {...register("workMode")}>
                {WORK_MODES.map((m) => (
                  <option key={m} value={m}>
                    {formatEnumLabel(m)}
                  </option>
                ))}
              </Select>
            </FormField>
          </div>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField label="Vacancies" htmlFor="vacancies" required error={errors.vacancies?.message}>
              <Input id="vacancies" type="number" min={1} {...register("vacancies", { valueAsNumber: true })} />
            </FormField>
            <FormField
              label="Application deadline"
              htmlFor="applicationDeadline"
              error={errors.applicationDeadline?.message}
            >
              <Input id="applicationDeadline" type="datetime-local" {...register("applicationDeadline")} />
            </FormField>
          </div>
        </div>
      </Card>

      <Card>
        <h2 className="text-lg font-semibold">Apply method</h2>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          Choose how candidates apply for this job. The candidate-facing Apply button adapts automatically.
        </p>
        <div className="mt-4 grid gap-3 sm:grid-cols-3">
          <label
            className={`flex cursor-pointer flex-col gap-1 rounded-lg border p-3 text-sm ${
              applyMethod === "EASY_APPLY" ? "border-primary-600 bg-primary-600/5" : "border-[hsl(var(--border-color))]"
            }`}
          >
            <span className="flex items-center gap-2 font-medium">
              <input type="radio" value="EASY_APPLY" {...register("applyMethod")} /> Easy Apply
            </span>
            <span className="text-xs text-[hsl(var(--muted))]">
              Candidate picks a resume and writes a cover letter in-app.
            </span>
          </label>
          <label
            className={`flex cursor-pointer flex-col gap-1 rounded-lg border p-3 text-sm ${
              applyMethod === "QUICK_APPLY" ? "border-primary-600 bg-primary-600/5" : "border-[hsl(var(--border-color))]"
            }`}
          >
            <span className="flex items-center gap-2 font-medium">
              <input type="radio" value="QUICK_APPLY" {...register("applyMethod")} />
              <Zap className="h-3.5 w-3.5" /> Quick Apply
            </span>
            <span className="text-xs text-[hsl(var(--muted))]">
              In-app, one click — candidate's active resume is used automatically.
            </span>
          </label>
          <label
            className={`flex cursor-pointer flex-col gap-1 rounded-lg border p-3 text-sm ${
              applyMethod === "EXTERNAL_APPLY" ? "border-primary-600 bg-primary-600/5" : "border-[hsl(var(--border-color))]"
            }`}
          >
            <span className="flex items-center gap-2 font-medium">
              <input type="radio" value="EXTERNAL_APPLY" {...register("applyMethod")} />
              <ExternalLink className="h-3.5 w-3.5" /> External Apply
            </span>
            <span className="text-xs text-[hsl(var(--muted))]">
              No in-app application — candidates are redirected to your careers site.
            </span>
          </label>
        </div>
        {applyMethod === "EXTERNAL_APPLY" && (
          <div className="mt-4">
            <FormField
              label="External apply URL"
              htmlFor="externalApplyUrl"
              required
              error={errors.externalApplyUrl?.message}
            >
              <Input
                id="externalApplyUrl"
                type="url"
                placeholder="https://careers.example.com/apply/123"
                {...register("externalApplyUrl")}
              />
            </FormField>
          </div>
        )}
      </Card>

      <Card>
        <h2 className="text-lg font-semibold">Compensation</h2>
        <div className="mt-4 grid gap-4 sm:grid-cols-4">
          <FormField label="Min salary" htmlFor="minSalary" error={errors.minSalary?.message}>
            <Input
              id="minSalary"
              type="number"
              {...register("minSalary", { setValueAs: (v) => (v === "" ? null : Number(v)) })}
            />
          </FormField>
          <FormField label="Max salary" htmlFor="maxSalary" error={errors.maxSalary?.message}>
            <Input
              id="maxSalary"
              type="number"
              {...register("maxSalary", { setValueAs: (v) => (v === "" ? null : Number(v)) })}
            />
          </FormField>
          <FormField label="Salary type" htmlFor="salaryType">
            <Select id="salaryType" {...register("salaryType", { setValueAs: (v) => (v === "" ? null : v) })}>
              <option value="">None</option>
              {SALARY_TYPES.map((s) => (
                <option key={s} value={s}>
                  {formatEnumLabel(s)}
                </option>
              ))}
            </Select>
          </FormField>
          <FormField label="Currency" htmlFor="currency">
            <Select id="currency" {...register("currency", { setValueAs: (v) => (v === "" ? null : v) })}>
              <option value="">None</option>
              {CURRENCIES.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </Select>
          </FormField>
        </div>
      </Card>

      <Card>
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Locations</h2>
          <Button
            type="button"
            size="sm"
            variant="outline"
            onClick={() => locationsArray.append({ city: "", state: "", country: "" })}
          >
            <Plus className="h-4 w-4" /> Add location
          </Button>
        </div>
        {errors.locations?.message && <p className="mt-2 text-xs text-danger-500">{errors.locations.message}</p>}
        <div className="mt-4 space-y-3">
          {locationsArray.fields.map((field, index) => (
            <div key={field.id} className="grid grid-cols-1 gap-2 sm:grid-cols-[1fr_1fr_1fr_auto]">
              <Input placeholder="City" {...register(`locations.${index}.city`)} />
              <Input placeholder="State" {...register(`locations.${index}.state`)} />
              <Input placeholder="Country" {...register(`locations.${index}.country`)} />
              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => locationsArray.remove(index)}
                disabled={locationsArray.fields.length === 1}
                aria-label="Remove location"
              >
                <Trash2 className="h-4 w-4 text-danger-500" />
              </Button>
            </div>
          ))}
        </div>
      </Card>

      <Card>
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Required skills</h2>
          <Button
            type="button"
            size="sm"
            variant="outline"
            onClick={() => skillsArray.append({ name: "", requiredProficiency: "INTERMEDIATE", mandatory: false })}
          >
            <Plus className="h-4 w-4" /> Add skill
          </Button>
        </div>
        <div className="mt-4 space-y-3">
          {skillsArray.fields.map((field, index) => (
            <div key={field.id} className="grid grid-cols-1 gap-2 sm:grid-cols-[1fr_1fr_auto_auto]">
              <Input placeholder="Skill name" {...register(`skills.${index}.name`)} />
              <Select {...register(`skills.${index}.requiredProficiency`)}>
                {PROFICIENCIES.map((p) => (
                  <option key={p} value={p}>
                    {formatEnumLabel(p)}
                  </option>
                ))}
              </Select>
              <label className="flex items-center gap-2 text-sm">
                <Checkbox {...register(`skills.${index}.mandatory`)} /> Mandatory
              </label>
              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => skillsArray.remove(index)}
                aria-label="Remove skill"
              >
                <Trash2 className="h-4 w-4 text-danger-500" />
              </Button>
            </div>
          ))}
        </div>
      </Card>

      <Card>
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Benefits</h2>
          <Button
            type="button"
            size="sm"
            variant="outline"
            onClick={() => benefitsArray.append({ title: "", description: "" })}
          >
            <Plus className="h-4 w-4" /> Add benefit
          </Button>
        </div>
        <div className="mt-4 space-y-3">
          {benefitsArray.fields.map((field, index) => (
            <div key={field.id} className="grid grid-cols-1 gap-2 sm:grid-cols-[1fr_1fr_auto]">
              <Input placeholder="Title" {...register(`benefits.${index}.title`)} />
              <Input placeholder="Description" {...register(`benefits.${index}.description`)} />
              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => benefitsArray.remove(index)}
                aria-label="Remove benefit"
              >
                <Trash2 className="h-4 w-4 text-danger-500" />
              </Button>
            </div>
          ))}
        </div>
      </Card>

      <Card>
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Requirements &amp; responsibilities</h2>
          <Button
            type="button"
            size="sm"
            variant="outline"
            onClick={() =>
              requirementsArray.append({
                type: "QUALIFICATION",
                description: "",
                displayOrder: requirementsArray.fields.length,
              })
            }
          >
            <Plus className="h-4 w-4" /> Add requirement
          </Button>
        </div>
        <div className="mt-4 space-y-3">
          {requirementsArray.fields.map((field, index) => (
            <div key={field.id} className="grid grid-cols-1 gap-2 sm:grid-cols-[160px_1fr_auto]">
              <Select {...register(`requirements.${index}.type`)}>
                {REQUIREMENT_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {formatEnumLabel(t)}
                  </option>
                ))}
              </Select>
              <Input placeholder="Description" {...register(`requirements.${index}.description`)} />
              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => requirementsArray.remove(index)}
                aria-label="Remove requirement"
              >
                <Trash2 className="h-4 w-4 text-danger-500" />
              </Button>
            </div>
          ))}
        </div>
      </Card>

      <div className="flex justify-end gap-2">
        <Button type="submit" isLoading={isSubmitting}>
          {submitLabel}
        </Button>
      </div>
    </form>
  );
}
