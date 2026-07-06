import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Sparkles, X } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { FormField } from "@/components/ui/form-field";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { EmptyState } from "@/components/common/EmptyState";
import { Skeleton } from "@/components/ui/skeleton";
import { formatEnumLabel } from "@/utils/format";
import { skillFormSchema, type SkillFormValues } from "@/features/profile/schemas/profile.schema";
import { useCreateSkill, useDeleteSkill, useSkillsList } from "@/features/profile/hooks/useSkills";
import type { SkillResponse } from "@/features/profile/types";

const PROFICIENCY_OPTIONS: SkillResponse["proficiency"][] = ["BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT"];

const PROFICIENCY_VARIANT: Record<SkillResponse["proficiency"], "default" | "primary" | "success"> = {
  BEGINNER: "default",
  INTERMEDIATE: "default",
  ADVANCED: "primary",
  EXPERT: "success",
};

export function SkillsSection() {
  const { data: skills, isLoading } = useSkillsList();
  const createSkill = useCreateSkill();
  const deleteSkill = useDeleteSkill();
  const [deleteTarget, setDeleteTarget] = useState<SkillResponse | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<SkillFormValues>({
    resolver: zodResolver(skillFormSchema),
    defaultValues: { name: "", proficiency: "INTERMEDIATE", yearsOfExperience: null },
  });

  const onSubmit = (values: SkillFormValues) => {
    createSkill.mutate(
      {
        name: values.name,
        proficiency: values.proficiency,
        yearsOfExperience: values.yearsOfExperience ?? null,
      },
      { onSuccess: () => reset({ name: "", proficiency: "INTERMEDIATE", yearsOfExperience: null }) },
    );
  };

  return (
    <Card>
      <h3 className="text-base font-semibold">Skills</h3>

      <form onSubmit={handleSubmit(onSubmit)} className="mt-4 grid gap-3 sm:grid-cols-[1fr_150px_120px_auto]">
        <FormField label="Skill Name" htmlFor="skillName" error={errors.name?.message}>
          <Input id="skillName" placeholder="e.g. React" {...register("name")} />
        </FormField>
        <FormField label="Proficiency" htmlFor="proficiency" error={errors.proficiency?.message}>
          <Select id="proficiency" {...register("proficiency")}>
            {PROFICIENCY_OPTIONS.map((level) => (
              <option key={level} value={level}>
                {formatEnumLabel(level)}
              </option>
            ))}
          </Select>
        </FormField>
        <FormField label="Years" htmlFor="yearsOfExperience" error={errors.yearsOfExperience?.message}>
          <Input
            id="yearsOfExperience"
            type="number"
            min={0}
            max={60}
            {...register("yearsOfExperience", { valueAsNumber: true, setValueAs: (v) => (v === "" ? null : Number(v)) })}
          />
        </FormField>
        <div className="flex items-end">
          <Button type="submit" isLoading={createSkill.isPending} className="w-full sm:w-auto">
            Add
          </Button>
        </div>
      </form>

      <div className="mt-5">
        {isLoading && <Skeleton className="h-10 w-full" />}

        {!isLoading && skills?.length === 0 && (
          <EmptyState
            icon={<Sparkles className="h-8 w-8" />}
            title="No skills added yet"
            message="List your key skills so recruiters and AI matching can find you."
          />
        )}

        {skills && skills.length > 0 && (
          <div className="flex flex-wrap gap-2">
            {skills.map((skill) => (
              <Badge key={skill.id} variant={PROFICIENCY_VARIANT[skill.proficiency]} className="py-1.5 pl-3 pr-1.5 text-sm">
                {skill.name}
                <span className="text-[10px] opacity-70">
                  · {formatEnumLabel(skill.proficiency)}
                  {skill.yearsOfExperience != null ? ` · ${skill.yearsOfExperience}y` : ""}
                </span>
                <button
                  type="button"
                  aria-label={`Remove ${skill.name}`}
                  onClick={() => setDeleteTarget(skill)}
                  className="ml-1 rounded-full p-0.5 hover:bg-black/10"
                >
                  <X className="h-3 w-3" />
                </button>
              </Badge>
            ))}
          </div>
        )}
      </div>

      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title="Remove skill"
        description={`Remove "${deleteTarget?.name}" from your profile?`}
        confirmLabel="Remove"
        isLoading={deleteSkill.isPending}
        onConfirm={() =>
          deleteTarget && deleteSkill.mutate(deleteTarget.id, { onSuccess: () => setDeleteTarget(null) })
        }
      />
    </Card>
  );
}
