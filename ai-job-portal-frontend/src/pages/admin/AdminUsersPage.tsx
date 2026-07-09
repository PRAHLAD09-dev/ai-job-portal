import { useState } from "react";
import { Search, ShieldOff, ShieldCheck, Trash2, Users } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { Pagination } from "@/components/common/Pagination";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { useDebouncedValue } from "@/hooks/useDebouncedValue";
import { useAuth } from "@/hooks/useAuth";
import { formatEnumLabel } from "@/utils/format";
import { AdminStatusBadge } from "@/features/admin/components/AdminStatusBadge";
import { useAdminUsers, useDeleteUser, useDisableUser, useEnableUser } from "@/features/admin/hooks/useAdminUsers";
import type { AdminUserResponse } from "@/features/admin/types";

const ROLE_OPTIONS = ["ALL", "CANDIDATE", "RECRUITER", "ADMIN", "SUPER_ADMIN"];
const STATUS_OPTIONS = ["ALL", "ACTIVE", "PENDING_VERIFICATION", "DISABLED"];
const PAGE_SIZE = 10;

export default function AdminUsersPage() {
  const { user: currentUser } = useAuth();
  const isSuperAdmin = currentUser?.roles.includes("SUPER_ADMIN") ?? false;

  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [role, setRole] = useState("ALL");
  const [status, setStatus] = useState("ALL");
  const [deleting, setDeleting] = useState<AdminUserResponse | null>(null);
  const debouncedKeyword = useDebouncedValue(keyword, 300);

  const { data, isLoading, isFetching } = useAdminUsers({
    page,
    size: PAGE_SIZE,
    keyword: debouncedKeyword || undefined,
    role: role === "ALL" ? undefined : role,
    status: status === "ALL" ? undefined : status,
  });

  const enableUser = useEnableUser();
  const disableUser = useDisableUser();
  const deleteUser = useDeleteUser();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Users</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Search and manage candidate, recruiter, and admin accounts.</p>
      </div>

      <Card>
        <div className="flex flex-col gap-3 sm:flex-row">
          <div className="relative flex-1">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[hsl(var(--muted))]" />
            <Input
              className="pl-9"
              placeholder="Search users by name or email..."
              value={keyword}
              onChange={(e) => {
                setKeyword(e.target.value);
                setPage(0);
              }}
            />
          </div>
          <Select
            className="sm:w-48"
            value={role}
            onChange={(e) => {
              setRole(e.target.value);
              setPage(0);
            }}
          >
            {ROLE_OPTIONS.map((r) => (
              <option key={r} value={r}>
                {r === "ALL" ? "All roles" : formatEnumLabel(r)}
              </option>
            ))}
          </Select>
          <Select
            className="sm:w-48"
            value={status}
            onChange={(e) => {
              setStatus(e.target.value);
              setPage(0);
            }}
          >
            {STATUS_OPTIONS.map((s) => (
              <option key={s} value={s}>
                {s === "ALL" ? "All statuses" : formatEnumLabel(s)}
              </option>
            ))}
          </Select>
        </div>
      </Card>

      {isLoading ? (
        <div className="space-y-2">
          <Skeleton className="h-14 w-full" />
          <Skeleton className="h-14 w-full" />
          <Skeleton className="h-14 w-full" />
        </div>
      ) : !data || data.content.length === 0 ? (
        <EmptyState icon={<Users className="h-10 w-10" />} title="No records found" message="No users match your current search or filters." />
      ) : (
        <div className="overflow-x-auto rounded-xl border border-[hsl(var(--border-color))]">
          <table className="w-full text-left text-sm">
            <thead className="bg-[hsl(var(--surface))] text-xs uppercase text-[hsl(var(--muted))]">
              <tr>
                <th className="px-4 py-3 font-medium">Name</th>
                <th className="px-4 py-3 font-medium">Email</th>
                <th className="px-4 py-3 font-medium">Roles</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((u) => (
                <tr key={u.id} className="border-t border-[hsl(var(--border-color))]">
                  <td className="px-4 py-3 font-medium">
                    {u.firstName} {u.lastName}
                  </td>
                  <td className="px-4 py-3 text-[hsl(var(--muted))]">{u.email}</td>
                  <td className="px-4 py-3">
                    <div className="flex flex-wrap gap-1">
                      {u.roles.map((r) => (
                        <Badge key={r} variant="outline">
                          {formatEnumLabel(r)}
                        </Badge>
                      ))}
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <AdminStatusBadge status={u.status} />
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex justify-end gap-1">
                      {u.status === "DISABLED" ? (
                        <Button
                          variant="ghost"
                          size="sm"
                          isLoading={enableUser.isPending}
                          onClick={() => enableUser.mutate(u.id)}
                          aria-label="Enable user"
                        >
                          <ShieldCheck className="h-4 w-4 text-success-500" />
                        </Button>
                      ) : (
                        <Button
                          variant="ghost"
                          size="sm"
                          isLoading={disableUser.isPending}
                          onClick={() => disableUser.mutate(u.id)}
                          aria-label="Disable user"
                        >
                          <ShieldOff className="h-4 w-4 text-warning-500" />
                        </Button>
                      )}
                      {isSuperAdmin && (
                        <Button variant="ghost" size="sm" onClick={() => setDeleting(u)} aria-label="Delete user">
                          <Trash2 className="h-4 w-4 text-danger-500" />
                        </Button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {data && !isFetching && (
        <Pagination pageNumber={data.pageNumber} totalPages={data.totalPages} onPageChange={setPage} />
      )}

      <ConfirmDialog
        open={!!deleting}
        onOpenChange={(open) => !open && setDeleting(null)}
        title="Delete user"
        description={`Permanently delete ${deleting?.email ?? "this user"}? This cannot be undone.`}
        confirmLabel="Delete"
        isLoading={deleteUser.isPending}
        onConfirm={() => {
          if (deleting) deleteUser.mutate(deleting.id, { onSuccess: () => setDeleting(null) });
        }}
      />
    </div>
  );
}
