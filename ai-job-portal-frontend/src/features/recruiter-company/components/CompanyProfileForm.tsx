import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { Button } from "@/components/ui/button";
import { formatEnumLabel } from "@/utils/format";
import {
  createCompanyFormSchema,
  updateCompanyFormSchema,
  type CreateCompanyFormValues,
  type UpdateCompanyFormValues,
} from "@/features/recruiter-company/schemas/company.schema";
import type { CompanyResponse } from "@/features/recruiter-company/types";
import { useCreateCompany, useUpdateCompany } from "@/features/recruiter-company/hooks/useCompany";

const INDUSTRIES = [
  "INFORMATION_TECHNOLOGY",
  "FINANCE",
  "HEALTHCARE",
  "EDUCATION",
  "MANUFACTURING",
  "RETAIL",
  "CONSTRUCTION",
  "HOSPITALITY",
  "TELECOMMUNICATIONS",
  "MEDIA_AND_ENTERTAINMENT",
  "TRANSPORTATION_AND_LOGISTICS",
  "GOVERNMENT",
  "NON_PROFIT",
  "OTHER",
] as const;

const COMPANY_SIZES = [
  "SIZE_1_10",
  "SIZE_11_50",
  "SIZE_51_200",
  "SIZE_201_500",
  "SIZE_501_1000",
  "SIZE_1001_PLUS",
] as const;

const RECRUITER_TITLES = [
  "HR_MANAGER",
  "TALENT_ACQUISITION_SPECIALIST",
  "RECRUITMENT_CONSULTANT",
  "HIRING_MANAGER",
  "FOUNDER",
  "CO_FOUNDER",
  "CEO",
  "OTHER",
] as const;

function sizeLabel(size: string) {
  return size.replace("SIZE_", "").replace(/_/g, "–") + " employees";
}

interface CompanyProfileFormProps {
  company: CompanyResponse | null;
}

/** Renders the registration form when no company exists yet, otherwise the update form. */
export function CompanyProfileForm({ company }: CompanyProfileFormProps) {
  if (!company) {
    return <CreateCompanyForm />;
  }
  return <UpdateCompanyForm company={company} />;
}

function CreateCompanyForm() {
  const createCompany = useCreateCompany();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateCompanyFormValues>({
    resolver: zodResolver(createCompanyFormSchema),
    defaultValues: {
      industry: "INFORMATION_TECHNOLOGY",
      companySize: "SIZE_1_10",
      recruiterTitle: "HR_MANAGER",
    },
  });

  const onSubmit = (values: CreateCompanyFormValues) => {
    createCompany.mutate({
      name: values.name,
      description: values.description || null,
      industry: values.industry,
      companySize: values.companySize,
      foundedYear: values.foundedYear || null,
      websiteUrl: values.websiteUrl || null,
      email: values.email || null,
      phoneNumber: values.phoneNumber || null,
      recruiterTitle: values.recruiterTitle,
      recruiterDesignation: values.recruiterDesignation || null,
      recruiterPhoneNumber: values.recruiterPhoneNumber || null,
    });
  };

  return (
    <Card>
      <h2 className="text-lg font-semibold">Register your company</h2>
      <p className="mt-1 text-sm text-[hsl(var(--muted))]">
        Create your company profile to start posting jobs and managing candidates.
      </p>
      <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-4">
        <FormField label="Company name" htmlFor="name" required error={errors.name?.message}>
          <Input id="name" {...register("name")} />
        </FormField>
        <FormField label="Description" htmlFor="description" error={errors.description?.message}>
          <Textarea id="description" rows={4} {...register("description")} />
        </FormField>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Industry" htmlFor="industry" required error={errors.industry?.message}>
            <Select id="industry" {...register("industry")}>
              {INDUSTRIES.map((i) => (
                <option key={i} value={i}>
                  {formatEnumLabel(i)}
                </option>
              ))}
            </Select>
          </FormField>
          <FormField label="Company size" htmlFor="companySize" required error={errors.companySize?.message}>
            <Select id="companySize" {...register("companySize")}>
              {COMPANY_SIZES.map((s) => (
                <option key={s} value={s}>
                  {sizeLabel(s)}
                </option>
              ))}
            </Select>
          </FormField>
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Founded year" htmlFor="foundedYear" error={errors.foundedYear?.message}>
            <Input id="foundedYear" type="number" {...register("foundedYear", { valueAsNumber: true })} />
          </FormField>
          <FormField label="Website URL" htmlFor="websiteUrl" error={errors.websiteUrl?.message}>
            <Input id="websiteUrl" placeholder="https://" {...register("websiteUrl")} />
          </FormField>
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Company email" htmlFor="email" error={errors.email?.message}>
            <Input id="email" type="email" {...register("email")} />
          </FormField>
          <FormField label="Company phone" htmlFor="phoneNumber" error={errors.phoneNumber?.message}>
            <Input id="phoneNumber" {...register("phoneNumber")} />
          </FormField>
        </div>

        <div className="border-t border-[hsl(var(--border-color))] pt-4">
          <p className="mb-3 text-sm font-medium">Your recruiter profile</p>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField label="Your title" htmlFor="recruiterTitle" required error={errors.recruiterTitle?.message}>
              <Select id="recruiterTitle" {...register("recruiterTitle")}>
                {RECRUITER_TITLES.map((t) => (
                  <option key={t} value={t}>
                    {formatEnumLabel(t)}
                  </option>
                ))}
              </Select>
            </FormField>
            <FormField label="Designation" htmlFor="recruiterDesignation" error={errors.recruiterDesignation?.message}>
              <Input id="recruiterDesignation" {...register("recruiterDesignation")} />
            </FormField>
          </div>
          <FormField
            label="Your phone number"
            htmlFor="recruiterPhoneNumber"
            error={errors.recruiterPhoneNumber?.message}
          >
            <Input id="recruiterPhoneNumber" {...register("recruiterPhoneNumber")} />
          </FormField>
        </div>

        <Button type="submit" isLoading={createCompany.isPending}>
          Register company
        </Button>
      </form>
    </Card>
  );
}

function UpdateCompanyForm({ company }: { company: CompanyResponse }) {
  const updateCompany = useUpdateCompany();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<UpdateCompanyFormValues>({
    resolver: zodResolver(updateCompanyFormSchema),
    defaultValues: {
      name: company.name,
      description: company.description ?? "",
      industry: company.industry,
      companySize: company.companySize,
      foundedYear: company.foundedYear,
      websiteUrl: company.websiteUrl ?? "",
      email: company.email ?? "",
      phoneNumber: company.phoneNumber ?? "",
    },
  });

  const onSubmit = (values: UpdateCompanyFormValues) => {
    updateCompany.mutate({
      name: values.name,
      description: values.description || null,
      industry: values.industry,
      companySize: values.companySize,
      foundedYear: values.foundedYear || null,
      websiteUrl: values.websiteUrl || null,
      email: values.email || null,
      phoneNumber: values.phoneNumber || null,
    });
  };

  return (
    <Card>
      <h2 className="text-lg font-semibold">Company profile</h2>
      <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-4">
        <FormField label="Company name" htmlFor="name" required error={errors.name?.message}>
          <Input id="name" {...register("name")} />
        </FormField>
        <FormField label="Description" htmlFor="description" error={errors.description?.message}>
          <Textarea id="description" rows={4} {...register("description")} />
        </FormField>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Industry" htmlFor="industry" required error={errors.industry?.message}>
            <Select id="industry" {...register("industry")}>
              {INDUSTRIES.map((i) => (
                <option key={i} value={i}>
                  {formatEnumLabel(i)}
                </option>
              ))}
            </Select>
          </FormField>
          <FormField label="Company size" htmlFor="companySize" required error={errors.companySize?.message}>
            <Select id="companySize" {...register("companySize")}>
              {COMPANY_SIZES.map((s) => (
                <option key={s} value={s}>
                  {sizeLabel(s)}
                </option>
              ))}
            </Select>
          </FormField>
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Founded year" htmlFor="foundedYear" error={errors.foundedYear?.message}>
            <Input id="foundedYear" type="number" {...register("foundedYear", { valueAsNumber: true })} />
          </FormField>
          <FormField label="Website URL" htmlFor="websiteUrl" error={errors.websiteUrl?.message}>
            <Input id="websiteUrl" placeholder="https://" {...register("websiteUrl")} />
          </FormField>
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Company email" htmlFor="email" error={errors.email?.message}>
            <Input id="email" type="email" {...register("email")} />
          </FormField>
          <FormField label="Company phone" htmlFor="phoneNumber" error={errors.phoneNumber?.message}>
            <Input id="phoneNumber" {...register("phoneNumber")} />
          </FormField>
        </div>
        <Button type="submit" isLoading={updateCompany.isPending}>
          Save changes
        </Button>
      </form>
    </Card>
  );
}
