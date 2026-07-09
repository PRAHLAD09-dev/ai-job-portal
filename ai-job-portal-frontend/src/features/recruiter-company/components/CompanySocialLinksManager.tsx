import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Globe, Plus, Trash2, Pencil } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { Modal } from "@/components/ui/modal";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { EmptyState } from "@/components/common/EmptyState";
import { Skeleton } from "@/components/ui/skeleton";
import { formatEnumLabel } from "@/utils/format";
import {
  companySocialLinkFormSchema,
  type CompanySocialLinkFormValues,
} from "@/features/recruiter-company/schemas/company.schema";
import type { CompanySocialLinkResponse } from "@/features/recruiter-company/types";
import {
  useCompanySocialLinks,
  useCreateCompanySocialLink,
  useDeleteCompanySocialLink,
  useUpdateCompanySocialLink,
} from "@/features/recruiter-company/hooks/useCompany";

const PLATFORMS = ["LINKEDIN", "TWITTER", "FACEBOOK", "INSTAGRAM", "YOUTUBE", "GITHUB", "WEBSITE"] as const;

export function CompanySocialLinksManager({ enabled }: { enabled: boolean }) {
  const { data: links, isLoading } = useCompanySocialLinks(enabled);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<CompanySocialLinkResponse | null>(null);
  const [deleting, setDeleting] = useState<CompanySocialLinkResponse | null>(null);

  const createLink = useCreateCompanySocialLink();
  const updateLink = useUpdateCompanySocialLink();
  const deleteLink = useDeleteCompanySocialLink();

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };
  const openEdit = (link: CompanySocialLinkResponse) => {
    setEditing(link);
    setFormOpen(true);
  };

  const handleSubmit = (values: CompanySocialLinkFormValues, onDone: () => void) => {
    if (editing) {
      updateLink.mutate({ linkId: editing.id, payload: values }, { onSuccess: onDone });
    } else {
      createLink.mutate(values, { onSuccess: onDone });
    }
  };

  return (
    <Card>
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Social &amp; web links</h2>
        <Button size="sm" onClick={openCreate}>
          <Plus className="h-4 w-4" /> Add link
        </Button>
      </div>

      <div className="mt-4 space-y-2">
        {isLoading && (
          <>
            <Skeleton className="h-12 w-full" />
            <Skeleton className="h-12 w-full" />
          </>
        )}

        {!isLoading && (!links || links.length === 0) && (
          <EmptyState
            icon={<Globe className="h-10 w-10" />}
            title="No links yet"
            message="Add LinkedIn, website, or other social links to your company profile."
            actionLabel="Add link"
            onAction={openCreate}
          />
        )}

        {links?.map((link) => (
          <div
            key={link.id}
            className="flex items-center justify-between gap-3 rounded-lg border border-[hsl(var(--border-color))] p-3"
          >
            <div className="flex min-w-0 items-center gap-3">
              <Globe className="h-4 w-4 shrink-0 text-[hsl(var(--muted))]" />
              <div className="min-w-0">
                <p className="text-sm font-medium">{formatEnumLabel(link.platform)}</p>
                <p className="truncate text-xs text-[hsl(var(--muted))]">{link.url}</p>
              </div>
            </div>
            <div className="flex shrink-0 gap-1">
              <Button variant="ghost" size="sm" onClick={() => openEdit(link)} aria-label="Edit link">
                <Pencil className="h-4 w-4" />
              </Button>
              <Button variant="ghost" size="sm" onClick={() => setDeleting(link)} aria-label="Delete link">
                <Trash2 className="h-4 w-4 text-danger-500" />
              </Button>
            </div>
          </div>
        ))}
      </div>

      {formOpen && (
        <SocialLinkFormModal
          open={formOpen}
          onOpenChange={setFormOpen}
          link={editing}
          isLoading={createLink.isPending || updateLink.isPending}
          onSubmit={handleSubmit}
        />
      )}

      <ConfirmDialog
        open={!!deleting}
        onOpenChange={(open) => !open && setDeleting(null)}
        title="Delete social link"
        description={`Remove this ${deleting ? formatEnumLabel(deleting.platform) : ""} link?`}
        confirmLabel="Delete"
        isLoading={deleteLink.isPending}
        onConfirm={() => {
          if (deleting) deleteLink.mutate(deleting.id, { onSuccess: () => setDeleting(null) });
        }}
      />
    </Card>
  );
}

function SocialLinkFormModal({
  open,
  onOpenChange,
  link,
  isLoading,
  onSubmit,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  link: CompanySocialLinkResponse | null;
  isLoading: boolean;
  onSubmit: (values: CompanySocialLinkFormValues, onDone: () => void) => void;
}) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CompanySocialLinkFormValues>({
    resolver: zodResolver(companySocialLinkFormSchema),
    defaultValues: link ? { platform: link.platform, url: link.url } : { platform: "LINKEDIN", url: "" },
  });

  return (
    <Modal open={open} onOpenChange={onOpenChange} title={link ? "Edit link" : "Add link"}>
      <form onSubmit={handleSubmit((values) => onSubmit(values, () => onOpenChange(false)))} className="space-y-4">
        <FormField label="Platform" htmlFor="platform" required error={errors.platform?.message}>
          <Select id="platform" {...register("platform")}>
            {PLATFORMS.map((p) => (
              <option key={p} value={p}>
                {formatEnumLabel(p)}
              </option>
            ))}
          </Select>
        </FormField>
        <FormField label="URL" htmlFor="url" required error={errors.url?.message}>
          <Input id="url" placeholder="https://" {...register("url")} />
        </FormField>
        <div className="flex justify-end gap-2">
          <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isLoading}>
            {link ? "Save changes" : "Add link"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
