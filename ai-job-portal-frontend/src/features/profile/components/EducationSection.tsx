import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { GraduationCap, Pencil, Plus, Trash2 } from "lucide-react";
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
import { educationFormSchema, type EducationFormValues } from "@/features/profile/schemas/profile.schema";
import {
  useCreateEducation,
  useDeleteEducation,
  useEducationList,
  useUpdateEducation,
} from "@/features/profile/hooks/useEducation";
import type { EducationResponse } from "@/features/profile/types";

const DEGREE_OPTIONS: EducationResponse["degreeType"][] = [
  "HIGH_SCHOOL",
  "DIPLOMA",
  "ASSOCIATE",
  "BACHELOR",
  "MASTER",
  "DOCTORATE",
  "CERTIFICATION",
  "OTHER",
];

function toDefaultValues(entry?: EducationResponse): EducationFormValues {
  return {
    institutionName: entry?.institutionName ?? "",
    degreeType: entry?.degreeType ?? "BACHELOR",
    fieldOfStudy: entry?.fieldOfStudy ?? "",
    startDate: entry?.startDate ?? "",
    endDate: entry?.endDate ?? "",
    currentlyStudying: entry?.currentlyStudying ?? false,
    grade: entry?.grade ?? "",
    description: entry?.description ?? "",
  };
}

function EducationFormDialog({
  open,
  onOpenChange,
  entry,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  entry?: EducationResponse;
}) {
  const createEducation = useCreateEducation();
  const updateEducation = useUpdateEducation();
  const isSaving = createEducation.isPending || updateEducation.isPending;

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<EducationFormValues>({
    resolver: zodResolver(educationFormSchema),
    defaultValues: toDefaultValues(entry),
  });

  const currentlyStudying = watch("currentlyStudying");

  const onSubmit = (values: EducationFormValues) => {
    const payload = {
      institutionName: values.institutionName,
      degreeType: values.degreeType,
      fieldOfStudy: values.fieldOfStudy,
      startDate: values.startDate,
      endDate: values.currentlyStudying ? null : values.endDate || null,
      currentlyStudying: values.currentlyStudying,
      grade: values.grade || null,
      description: values.description || null,
    };

    const onSuccess = () => onOpenChange(false);

    if (entry) {
      updateEducation.mutate({ educationId: entry.id, payload }, { onSuccess });
    } else {
      createEducation.mutate(payload, { onSuccess });
    }
  };

  return (
    <Modal open={open} onOpenChange={onOpenChange} title={entry ? "Edit Education" : "Add Education"}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <FormField label="Institution Name" htmlFor="institutionName" required error={errors.institutionName?.message}>
          <Input id="institutionName" {...register("institutionName")} />
        </FormField>

        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Degree Type" htmlFor="degreeType" required error={errors.degreeType?.message}>
            <Select id="degreeType" {...register("degreeType")}>
              {DEGREE_OPTIONS.map((degree) => (
                <option key={degree} value={degree}>
                  {formatEnumLabel(degree)}
                </option>
              ))}
            </Select>
          </FormField>
          <FormField label="Field of Study" htmlFor="fieldOfStudy" required error={errors.fieldOfStudy?.message}>
            <Input id="fieldOfStudy" {...register("fieldOfStudy")} />
          </FormField>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Start Date" htmlFor="startDate" required error={errors.startDate?.message}>
            <Input id="startDate" type="date" {...register("startDate")} />
          </FormField>
          <FormField label="End Date" htmlFor="endDate" error={errors.endDate?.message}>
            <Input id="endDate" type="date" disabled={currentlyStudying} {...register("endDate")} />
          </FormField>
        </div>

        <label className="flex items-center gap-2 text-sm">
          <Checkbox {...register("currentlyStudying")} />
          Currently studying here
        </label>

        <FormField label="Grade" htmlFor="grade" error={errors.grade?.message}>
          <Input id="grade" placeholder="e.g. 3.8 GPA" {...register("grade")} />
        </FormField>

        <FormField label="Description" htmlFor="description" error={errors.description?.message}>
          <Textarea id="description" rows={3} {...register("description")} />
        </FormField>

        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isSaving}>
            {entry ? "Save Changes" : "Add Education"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}

export function EducationSection() {
  const { data: educations, isLoading } = useEducationList();
  const deleteEducation = useDeleteEducation();
  const [dialogEntry, setDialogEntry] = useState<EducationResponse | "new" | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<EducationResponse | null>(null);

  return (
    <Card>
      <div className="flex items-center justify-between">
        <h3 className="text-base font-semibold">Education</h3>
        <Button size="sm" variant="outline" onClick={() => setDialogEntry("new")}>
          <Plus className="h-4 w-4" /> Add
        </Button>
      </div>

      <div className="mt-4 space-y-3">
        {isLoading && <Skeleton className="h-20 w-full" />}

        {!isLoading && educations?.length === 0 && (
          <EmptyState
            icon={<GraduationCap className="h-8 w-8" />}
            title="No education added yet"
            message="Add your academic background so recruiters can see your qualifications."
            actionLabel="Add Education"
            onAction={() => setDialogEntry("new")}
          />
        )}

        {educations?.map((entry) => (
          <div
            key={entry.id}
            className="flex items-start justify-between gap-4 rounded-lg border border-[hsl(var(--border-color))] p-4"
          >
            <div>
              <p className="font-medium">{entry.institutionName}</p>
              <p className="text-sm text-[hsl(var(--muted))]">
                {formatEnumLabel(entry.degreeType)} · {entry.fieldOfStudy}
              </p>
              <p className="mt-1 text-xs text-[hsl(var(--muted))]">
                {entry.startDate} — {entry.currentlyStudying ? "Present" : entry.endDate}
              </p>
              {entry.grade && <p className="mt-1 text-xs text-[hsl(var(--muted))]">Grade: {entry.grade}</p>}
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
        ))}
      </div>

      {dialogEntry && (
        <EducationFormDialog
          open={!!dialogEntry}
          onOpenChange={(open) => !open && setDialogEntry(null)}
          entry={dialogEntry === "new" ? undefined : dialogEntry}
        />
      )}

      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title="Delete education entry"
        description={`Are you sure you want to delete "${deleteTarget?.institutionName}"? This cannot be undone.`}
        confirmLabel="Delete"
        isLoading={deleteEducation.isPending}
        onConfirm={() =>
          deleteTarget &&
          deleteEducation.mutate(deleteTarget.id, { onSuccess: () => setDeleteTarget(null) })
        }
      />
    </Card>
  );
}
