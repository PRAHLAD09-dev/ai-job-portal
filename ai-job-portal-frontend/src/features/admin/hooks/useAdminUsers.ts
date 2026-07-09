import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { adminUserService, type AdminUserSearchParams } from "@/features/admin/services/admin-user.service";
import { extractErrorMessage } from "@/services/api-client";

export const ADMIN_USERS_QUERY_KEY = ["admin", "users"] as const;

export function useAdminUsers(params: AdminUserSearchParams) {
  return useQuery({
    queryKey: [...ADMIN_USERS_QUERY_KEY, "list", params],
    queryFn: () => adminUserService.search(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

function invalidateUsers(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: ADMIN_USERS_QUERY_KEY });
}

export function useEnableUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (userId: string) => adminUserService.enable(userId),
    onSuccess: (response) => {
      invalidateUsers(queryClient);
      toast.success(response.message || "User enabled successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDisableUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (userId: string) => adminUserService.disable(userId),
    onSuccess: (response) => {
      invalidateUsers(queryClient);
      toast.success(response.message || "User disabled successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDeleteUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (userId: string) => adminUserService.remove(userId),
    onSuccess: (response) => {
      invalidateUsers(queryClient);
      toast.success(response.message || "User deleted successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
