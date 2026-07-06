import { Select } from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import { formatEnumLabel } from "@/utils/format";
import { useJobCategories, useJobPopularSkills } from "@/features/jobs/hooks/useJobCategories";
import type { ExperienceLevel, JobSearchCriteria, JobType, WorkMode } from "@/features/jobs/types";

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

interface JobFiltersProps {
  filters: JobSearchCriteria;
  onChange: (filters: JobSearchCriteria) => void;
  onReset: () => void;
}

export function JobFilters({ filters, onChange, onReset }: JobFiltersProps) {
  const { data: categories } = useJobCategories();
  const { data: popularSkills } = useJobPopularSkills();

  const update = <K extends keyof JobSearchCriteria>(key: K, value: JobSearchCriteria[K]) => {
    onChange({ ...filters, [key]: value || undefined });
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold">Filters</h3>
        <Button variant="ghost" size="sm" onClick={onReset}>
          Reset
        </Button>
      </div>

      <FormField label="Category" htmlFor="categoryId">
        <Select
          id="categoryId"
          value={filters.categoryId ?? ""}
          onChange={(e) => update("categoryId", e.target.value)}
        >
          <option value="">All Categories</option>
          {categories?.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </Select>
      </FormField>

      <FormField label="Job Type" htmlFor="jobType">
        <Select id="jobType" value={filters.jobType ?? ""} onChange={(e) => update("jobType", e.target.value as JobType)}>
          <option value="">Any</option>
          {JOB_TYPES.map((type) => (
            <option key={type} value={type}>
              {formatEnumLabel(type)}
            </option>
          ))}
        </Select>
      </FormField>

      <FormField label="Experience Level" htmlFor="experienceLevel">
        <Select
          id="experienceLevel"
          value={filters.experienceLevel ?? ""}
          onChange={(e) => update("experienceLevel", e.target.value as ExperienceLevel)}
        >
          <option value="">Any</option>
          {EXPERIENCE_LEVELS.map((level) => (
            <option key={level} value={level}>
              {formatEnumLabel(level)}
            </option>
          ))}
        </Select>
      </FormField>

      <FormField label="Work Mode" htmlFor="workMode">
        <Select id="workMode" value={filters.workMode ?? ""} onChange={(e) => update("workMode", e.target.value as WorkMode)}>
          <option value="">Any</option>
          {WORK_MODES.map((mode) => (
            <option key={mode} value={mode}>
              {formatEnumLabel(mode)}
            </option>
          ))}
        </Select>
      </FormField>

      <FormField label="Skill" htmlFor="skill">
        <Input
          id="skill"
          list="popular-skills"
          placeholder="e.g. React"
          value={filters.skill ?? ""}
          onChange={(e) => update("skill", e.target.value)}
        />
        <datalist id="popular-skills">
          {popularSkills?.map((skill) => <option key={skill} value={skill} />)}
        </datalist>
      </FormField>

      <FormField label="City" htmlFor="city">
        <Input id="city" value={filters.city ?? ""} onChange={(e) => update("city", e.target.value)} />
      </FormField>

      <div className="grid grid-cols-2 gap-3">
        <FormField label="Min Salary" htmlFor="minSalary">
          <Input
            id="minSalary"
            type="number"
            value={filters.minSalary ?? ""}
            onChange={(e) => update("minSalary", e.target.value ? Number(e.target.value) : undefined)}
          />
        </FormField>
        <FormField label="Max Salary" htmlFor="maxSalary">
          <Input
            id="maxSalary"
            type="number"
            value={filters.maxSalary ?? ""}
            onChange={(e) => update("maxSalary", e.target.value ? Number(e.target.value) : undefined)}
          />
        </FormField>
      </div>
    </div>
  );
}
