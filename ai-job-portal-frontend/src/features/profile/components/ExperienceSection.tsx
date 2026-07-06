import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Briefcase, Pencil, Plus, Trash2 } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select } from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import { FormField } from "@/components/ui/form-field";
import { Modal } from "@/components/ui/modal";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { EmptyState } from "@/components/common/EmptyState";
import { Skeleton } from "@/components/ui/skeleton";
import { formatEnumLabel } from "@/utils/format";
import { experienceFormSchema, type ExperienceFormValues } from "@/features/profile/schemas/profile.schema";
import {
  useCreateExperience,
  useDeleteExperience,
  useExperienceList,
  useUpdateExperience,
} from "@/features/profile/hooks/useExperience";
import type { ExperienceResponse } from "@/features/profile/types";

const EMPLOYMENT_OPTIONS: ExperienceResponse["employmentType"][] = [
  "FULL_TIME",
  "PART_TIME",
  "CONTRACT",
  "INTERNSHIP",
  "FREELANCE",
];

function toDefaultValues(entry?: ExperienceResponse): ExperienceFormValues {
  return {
    companyName: entry?.companyName ?? "",
    jobTitle: entry?.jobTitle ?? "",
    employmentType: entry?.employmentType ?? "FULL_TIME",
    location: entry?.location ?? "",
    startDate: entry?.startDate ?? "",
    endDate: entry?.endDate ?? "",
    currentlyWorking: entry?.currentlyWorking ?? false,
    description: entry?.description ?? "",
  };
}

function ExperienceFormDialog({
  open,
  onOpenChange,
  entry,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  entry?: ExperienceResponse;
}) {
  const createExperience = useCreateExperience();
  const updateExperience = useUpdateExperience();
  const isSaving = createExperience.isPending || updateExperience.isPending;

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<ExperienceFormValues>({
    resolver: zodResolver(experienceFormSchema),
    defaultValues: toDefaultValues(entry),
  });

  const currentlyWorking = watch("currentlyWorking");

  const onSubmit = (values: ExperienceFormValues) => {
    const payload = {
      companyName: values.companyName,
      jobTitle: values.jobTitle,
      employmentType: values.employmentType,
      location: values.location || null,
      startDate: values.startDate,
      endDate: values.currentlyWorking ? null : values.endDate || null,
      currentlyWorking: values.currentlyWorking,
      description: values.description || null,
    };

    const onSuccess = () => onOpenChange(false);

    if (entry) {
      updateExperience.mutate({ experienceId: entry.id, payload }, { onSuccess });
    } else {
      createExperience.mutate(payload, { onSuccess });
    }
  };

  return (
    <Modal open={open} onOpenChange={onOpenChange} title={entry ? "Edit Experience" : "Add Experience"}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Company Name" htmlFor="companyName" required error={errors.companyName?.message}>
            <Input id="companyName" {...register("companyName")} />
          </FormField>
          <FormField label="Job Title" htmlFor="jobTitle" required error={errors.jobTitle?.message}>
            <Input id="jobTitle" {...register("jobTitle")} />
          </FormField>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Employment Type" htmlFor="employmentType" required error={errors.employmentType?.message}>
            <Select id="employmentType" {...register("employmentType")}>
              {EMPLOYMENT_OPTIONS.map((type) => (
                <option key={type} value={type}>
                  {formatEnumLabel(type)}
                </option>
              ))}
            </Select>
          </FormField>
          <FormField label="Location" htmlFor="location" error={errors.location?.message}>
            <Input id="location" placeholder="e.g. Remote, Bengaluru" {...register("location")} />
          </FormField>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Start Date" htmlFor="startDate" required error={errors.startDate?.message}>
            <Input id="startDate" type="date" {...register("startDate")} />
          </FormField>
          <FormField label="End Date" htmlFor="endDate" error={errors.endDate?.message}>
            <Input id="endDate" type="date" disabled={currentlyWorking} {...register("endDate")} />
          </FormField>
        </div>

        <label className="flex items-center gap-2 text-sm">
          <Checkbox {...register("currentlyWorking")} />
          Currently working here
        </label>

        <FormField label="Description" htmlFor="description" error={errors.description?.message}>
          <Textarea id="description" rows={3} {...register("description")} />
        </FormField>

        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isSaving}>
            {entry ? "Save Changes" : "Add Experience"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}

export function ExperienceSection() {
  const { data: experiences, isLoading } = useExperienceList();
  const deleteExperience = useDeleteExperience();
  const [dialogEntry, setDialogEntry] = useState<ExperienceResponse | "new" | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<ExperienceResponse | null>(null);

  return (
    <Card>
      <div className="flex items-center justify-between">
        <h3 className="text-base font-semibold">Work Experience</h3>
        <Button size="sm" variant="outline" onClick={() => setDialogEntry("new")}>
          <Plus className="h-4 w-4" /> Add
        </Button>
      </div>

      <div className="mt-4">
        {isLoading && <Skeleton className="h-20 w-full" />}

        {!isLoading && experiences?.length === 0 && (
          <EmptyState
            icon={<Briefcase className="h-8 w-8" />}
            title="No work experience added yet"
            message="Add your work history to showcase your professional background."
            actionLabel="Add Experience"
            onAction={() => setDialogEntry("new")}
          />
        )}

        {experiences && experiences.length > 0 && (
          <ol className="relative space-y-6 border-l border-[hsl(var(--border-color))] pl-6">
            {experiences.map((entry) => (
              <li key={entry.id} className="relative">
                <span className="absolute -left-[1.6rem] top-1 h-3 w-3 rounded-full border-2 border-[hsl(var(--surface))] bg-primary-600" />
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <p className="font-medium">{entry.jobTitle}</p>
                    <p className="text-sm text-[hsl(var(--muted))]">
                      {entry.companyName} · {formatEnumLabel(entry.employmentType)}
                      {entry.location ? ` · ${entry.location}` : ""}
                    </p>
                    <p className="mt-1 text-xs text-[hsl(var(--muted))]">
                      {entry.startDate} — {entry.currentlyWorking ? "Present" : entry.endDate}
                    </p>
                    {entry.description && <p className="mt-2 text-sm">{entry.description}</p>}
                  </div>
                  <div className="flex shrink-0 gap-1">
                    <button
                      type="button"
                      aria-label="Edit"
                      onClick={() => setDialogEntry(entry)}
                      className="rounded-md p-1.5 text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40"
                    >
                      <Pencil className="h-4 w-4" />
                    </button>
                    <button
                      type="button"
                      aria-label="Delete"
                      onClick={() => setDeleteTarget(entry)}
                      className="rounded-md p-1.5 text-danger-500 hover:bg-[hsl(var(--border-color))]/40"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              </li>
            ))}
          </ol>
        )}
      </div>

      {dialogEntry && (
        <ExperienceFormDialog
          open={!!dialogEntry}
          onOpenChange={(open) => !open && setDialogEntry(null)}
          entry={dialogEntry === "new" ? undefined : dialogEntry}
        />
      )}

      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title="Delete experience entry"
        description={`Are you sure you want to delete "${deleteTarget?.jobTitle}" at "${deleteTarget?.companyName}"? This cannot be undone.`}
        confirmLabel="Delete"
        isLoading={deleteExperience.isPending}
        onConfirm={() =>
          deleteTarget &&
          deleteExperience.mutate(deleteTarget.id, { onSuccess: () => setDeleteTarget(null) })
        }
      />
    </Card>
  );
}
