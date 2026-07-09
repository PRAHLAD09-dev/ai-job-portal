import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { companyAssetService } from "@/features/recruiter-company/services/company.service";
import { extractErrorMessage } from "@/services/api-client";
import { COMPANY_QUERY_KEY } from "@/features/recruiter-company/hooks/useCompany";

/** Logo upload/replace/delete — invalidates the company profile so logoUrl refreshes everywhere. */
export function useCompanyLogo(hasExistingLogo: boolean) {
  const queryClient = useQueryClient();

  const upload = useMutation({
    mutationFn: (file: File) =>
      hasExistingLogo ? companyAssetService.replaceLogo(file) : companyAssetService.uploadLogo(file),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_QUERY_KEY });
      toast.success(response.message || "Logo saved successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });

  const remove = useMutation({
    mutationFn: () => companyAssetService.deleteLogo(),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_QUERY_KEY });
      toast.success(response.message || "Logo deleted successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });

  return { upload, remove };
}

/** Banner upload/replace/delete — invalidates the company profile so bannerUrl refreshes everywhere. */
export function useCompanyBanner(hasExistingBanner: boolean) {
  const queryClient = useQueryClient();

  const upload = useMutation({
    mutationFn: (file: File) =>
      hasExistingBanner ? companyAssetService.replaceBanner(file) : companyAssetService.uploadBanner(file),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_QUERY_KEY });
      toast.success(response.message || "Banner saved successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });

  const remove = useMutation({
    mutationFn: () => companyAssetService.deleteBanner(),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: COMPANY_QUERY_KEY });
      toast.success(response.message || "Banner deleted successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });

  return { upload, remove };
}
