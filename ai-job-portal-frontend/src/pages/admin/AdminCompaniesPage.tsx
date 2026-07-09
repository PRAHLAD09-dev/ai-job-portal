import { useState } from "react";
import { Building2, Check, Search, ShieldAlert, X } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { Pagination } from "@/components/common/Pagination";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { useDebouncedValue } from "@/hooks/useDebouncedValue";
import { formatEnumLabel } from "@/utils/format";
import { AdminStatusBadge } from "@/features/admin/components/AdminStatusBadge";
import { useAdminCompanies, useRejectCompany, useSuspendCompany, useVerifyCompany } from "@/features/admin/hooks/useAdminCompanies";
import type { AdminCompanyResponse } from "@/features/admin/types";

const STATUS_OPTIONS = ["ALL", "PENDING", "VERIFIED", "REJECTED", "SUSPENDED"];
const PAGE_SIZE = 10;

export default function AdminCompaniesPage() {
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState("ALL");
  const [suspending, setSuspending] = useState<AdminCompanyResponse | null>(null);
  const debouncedKeyword = useDebouncedValue(keyword, 300);

  const { data, isLoading, isFetching } = useAdminCompanies({
    page,
    size: PAGE_SIZE,
    keyword: debouncedKeyword || undefined,
    status: status === "ALL" ? undefined : status,
  });

  const verifyCompany = useVerifyCompany();
  const rejectCompany = useRejectCompany();
  const suspendCompany = useSuspendCompany();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Companies</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Review, verify, and moderate companies registered on the platform.</p>
      </div>

      <Card>
        <div className="flex flex-col gap-3 sm:flex-row">
          <div className="relative flex-1">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[hsl(var(--muted))]" />
            <Input
              className="pl-9"
              placeholder="Search companies by name..."
              value={keyword}
              onChange={(e) => {
                setKeyword(e.target.value);
                setPage(0);
              }}
            />
          </div>
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
        <EmptyState icon={<Building2 className="h-10 w-10" />} title="No records found" message="No companies match your current search or filters." />
      ) : (
        <div className="overflow-x-auto rounded-xl border border-[hsl(var(--border-color))]">
          <table className="w-full text-left text-sm">
            <thead className="bg-[hsl(var(--surface))] text-xs uppercase text-[hsl(var(--muted))]">
              <tr>
                <th className="px-4 py-3 font-medium">Company</th>
                <th className="px-4 py-3 font-medium">Industry</th>
                <th className="px-4 py-3 font-medium">Active jobs</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((c) => (
                <tr key={c.id} className="border-t border-[hsl(var(--border-color))]">
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-2">
                      {c.logoUrl ? (
                        <img src={c.logoUrl} alt={c.name} className="h-6 w-6 rounded object-cover" />
                      ) : (
                        <Building2 className="h-4 w-4 text-[hsl(var(--muted))]" />
                      )}
                      <span className="font-medium">{c.name}</span>
                    </div>
                  </td>
                  <td className="px-4 py-3 text-[hsl(var(--muted))]">{formatEnumLabel(c.industry)}</td>
                  <td className="px-4 py-3 text-[hsl(var(--muted))]">{c.activeJobCount}</td>
                  <td className="px-4 py-3">
                    <AdminStatusBadge status={c.verificationStatus} />
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex justify-end gap-1">
                      {c.verificationStatus !== "VERIFIED" && (
                        <Button
                          variant="ghost"
                          size="sm"
                          isLoading={verifyCompany.isPending}
                          onClick={() => verifyCompany.mutate(c.id)}
                          aria-label="Verify company"
                        >
                          <Check className="h-4 w-4 text-success-500" />
                        </Button>
                      )}
                      {c.verificationStatus !== "REJECTED" && (
                        <Button
                          variant="ghost"
                          size="sm"
                          isLoading={rejectCompany.isPending}
                          onClick={() => rejectCompany.mutate(c.id)}
                          aria-label="Reject company"
                        >
                          <X className="h-4 w-4 text-danger-500" />
                        </Button>
                      )}
                      {c.verificationStatus !== "SUSPENDED" && (
                        <Button variant="ghost" size="sm" onClick={() => setSuspending(c)} aria-label="Suspend company">
                          <ShieldAlert className="h-4 w-4 text-warning-500" />
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
        open={!!suspending}
        onOpenChange={(open) => !open && setSuspending(null)}
        title="Suspend company"
        description={`Suspend ${suspending?.name ?? "this company"}? Their jobs will no longer be visible to candidates.`}
        confirmLabel="Suspend"
        isLoading={suspendCompany.isPending}
        onConfirm={() => {
          if (suspending) suspendCompany.mutate(suspending.id, { onSuccess: () => setSuspending(null) });
        }}
      />
    </div>
  );
}
