import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import {
  companyLocationService,
  companyService,
  companySocialLinkService,
} from "@/features/recruiter-company/services/company.service";
import { extractErrorMessage } from "@/services/api-client";
import type {
  CompanyLocationRequest,
  CompanySocialLinkRequest,
  CreateCompanyRequest,
  UpdateCompanyRequest,
} from "@/features/recruiter-company/types";

export const COMPANY_QUERY_KEY = ["recruiter", "company"] as const;
export const COMPANY_STATISTICS_QUERY_KEY = ["recruiter", "company", "statistics"] as const;
export const COMPANY_LOCATIONS_QUERY_KEY = ["recruiter", "company", "locations"] as const;
export const COMPANY_SOCIAL_LINKS_QUERY_KEY = ["recruiter", "company", "social-links"] as const;
export const COMPANY_PUBLIC_PROFILE_QUERY_KEY = ["company", "public"] as const;

/** GET /companies/{slug}/public — candidate-facing, no authentication required. */
export function useCompanyPublicProfile(slug: string | undefined) {
  return useQuery({
    queryKey: [...COMPANY_PUBLIC_PROFILE_QUERY_KEY, slug],
    queryFn: () => companyService.getPublicProfile(slug as string).then((res) => res.data),
    enabled: !!slug,
    staleTime: 60_000,
  });
}

/** A 404 means the recruiter has not registered a company yet — treated as a normal `null` result. */
export function useMyCompany() {
  return useQuery({
    queryKey: COMPANY_QUERY_KEY,
    queryFn: async () => {
      try {
        const response = await companyService.getMine();
        return response.data;
      } catch (error: unknown) {
        const status = (error as { response?: { status?: number } })?.response?.status;
        if (status === 404) return null;
        throw error;
      }
    },
    staleTime: 30_000,
  });
}

export function useCompanyStatistics(enabled: boolean) {
  return useQuery({
    queryKey: COMPANY_STATISTICS_QUERY_KEY,
    queryFn: () => companyService.getStatistics().then((res) => res.data),
    enabled,
  });
}

export function useCreateCompany() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateCompanyRequest) => companyService.create(payload),
    onSuccess: (response) => {
      queryClient.setQueryData(COMPANY_QUERY_KEY, response.data);
      queryClient.invalidateQueries({ queryKey: COMPANY_STATISTICS_QUERY_KEY });
      toast.success(response.message || "Company registered successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUpdateCompany() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: UpdateCompanyRequest) => companyService.update(payload),
    onSuccess: (response) => {
      queryClient.setQueryData(COMPANY_QUERY_KEY, response.data);
      toast.success(response.message || "Company updated successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useCompanyLocations(enabled: boolean) {
  return useQuery({
    queryKey: COMPANY_LOCATIONS_QUERY_KEY,
    queryFn: () => companyLocationService.getAll().then((res) => res.data),
    enabled,
  });
}

export function useCreateCompanyLocation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CompanyLocationRequest) => companyLocationService.create(payload),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_LOCATIONS_QUERY_KEY });
      toast.success(response.message || "Location added successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUpdateCompanyLocation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ locationId, payload }: { locationId: string; payload: CompanyLocationRequest }) =>
      companyLocationService.update(locationId, payload),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_LOCATIONS_QUERY_KEY });
      toast.success(response.message || "Location updated successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDeleteCompanyLocation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (locationId: string) => companyLocationService.remove(locationId),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_LOCATIONS_QUERY_KEY });
      toast.success(response.message || "Location deleted successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useCompanySocialLinks(enabled: boolean) {
  return useQuery({
    queryKey: COMPANY_SOCIAL_LINKS_QUERY_KEY,
    queryFn: () => companySocialLinkService.getAll().then((res) => res.data),
    enabled,
  });
}

export function useCreateCompanySocialLink() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CompanySocialLinkRequest) => companySocialLinkService.create(payload),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_SOCIAL_LINKS_QUERY_KEY });
      toast.success(response.message || "Social link added successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUpdateCompanySocialLink() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ linkId, payload }: { linkId: string; payload: CompanySocialLinkRequest }) =>
      companySocialLinkService.update(linkId, payload),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_SOCIAL_LINKS_QUERY_KEY });
      toast.success(response.message || "Social link updated successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDeleteCompanySocialLink() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (linkId: string) => companySocialLinkService.remove(linkId),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_SOCIAL_LINKS_QUERY_KEY });
      toast.success(response.message || "Social link deleted successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
