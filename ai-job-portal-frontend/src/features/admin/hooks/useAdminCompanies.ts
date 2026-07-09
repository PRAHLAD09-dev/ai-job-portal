import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { adminCompanyService, type AdminCompanySearchParams } from "@/features/admin/services/admin-company.service";
import { extractErrorMessage } from "@/services/api-client";

export const ADMIN_COMPANIES_QUERY_KEY = ["admin", "companies"] as const;

export function useAdminCompanies(params: AdminCompanySearchParams) {
  return useQuery({
    queryKey: [...ADMIN_COMPANIES_QUERY_KEY, "list", params],
    queryFn: () => adminCompanyService.search(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

function invalidateCompanies(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: ADMIN_COMPANIES_QUERY_KEY });
}

export function useVerifyCompany() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (companyId: string) => adminCompanyService.verify(companyId),
    onSuccess: (response) => {
      invalidateCompanies(queryClient);
      toast.success(response.message || "Company verified successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useRejectCompany() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (companyId: string) => adminCompanyService.reject(companyId),
    onSuccess: (response) => {
      invalidateCompanies(queryClient);
      toast.success(response.message || "Company rejected successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useSuspendCompany() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (companyId: string) => adminCompanyService.suspend(companyId),
    onSuccess: (response) => {
      invalidateCompanies(queryClient);
      toast.success(response.message || "Company suspended successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
