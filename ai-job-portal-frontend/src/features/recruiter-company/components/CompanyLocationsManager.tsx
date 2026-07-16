import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { MapPin, Plus, Star, Trash2, Pencil } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { FormField } from "@/components/ui/form-field";
import { Modal } from "@/components/ui/modal";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { EmptyState } from "@/components/common/EmptyState";
import { CompanyLocationMap } from "@/components/common/CompanyLocationMap";
import { Skeleton } from "@/components/ui/skeleton";
import {
  companyLocationFormSchema,
  type CompanyLocationFormValues,
} from "@/features/recruiter-company/schemas/company.schema";
import type { CompanyLocationResponse } from "@/features/recruiter-company/types";
import {
  useCompanyLocations,
  useCreateCompanyLocation,
  useDeleteCompanyLocation,
  useUpdateCompanyLocation,
} from "@/features/recruiter-company/hooks/useCompany";

export function CompanyLocationsManager({ enabled }: { enabled: boolean }) {
  const { data: locations, isLoading } = useCompanyLocations(enabled);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<CompanyLocationResponse | null>(null);
  const [deleting, setDeleting] = useState<CompanyLocationResponse | null>(null);

  const createLocation = useCreateCompanyLocation();
  const updateLocation = useUpdateCompanyLocation();
  const deleteLocation = useDeleteCompanyLocation();

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };
  const openEdit = (location: CompanyLocationResponse) => {
    setEditing(location);
    setFormOpen(true);
  };

  const handleSubmit = (values: CompanyLocationFormValues, onDone: () => void) => {
    const payload = {
      addressLine: values.addressLine,
      city: values.city,
      state: values.state || null,
      country: values.country,
      postalCode: values.postalCode || null,
      headquarters: values.headquarters,
      latitude: values.latitude ?? null,
      longitude: values.longitude ?? null,
    };
    if (editing) {
      updateLocation.mutate(
        { locationId: editing.id, payload },
        { onSuccess: onDone },
      );
    } else {
      createLocation.mutate(payload, { onSuccess: onDone });
    }
  };

  return (
    <Card>
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Office locations</h2>
        <Button size="sm" onClick={openCreate}>
          <Plus className="h-4 w-4" /> Add location
        </Button>
      </div>

      <div className="mt-4 space-y-2">
        {isLoading && (
          <>
            <Skeleton className="h-14 w-full" />
            <Skeleton className="h-14 w-full" />
          </>
        )}

        {!isLoading && (!locations || locations.length === 0) && (
          <EmptyState
            icon={<MapPin className="h-10 w-10" />}
            title="No locations yet"
            message="Add your company's office locations so candidates know where you're hiring."
            actionLabel="Add location"
            onAction={openCreate}
          />
        )}

        {locations?.map((location) => (
          <div
            key={location.id}
            className="flex items-center justify-between gap-3 rounded-lg border border-[hsl(var(--border-color))] p-3"
          >
            <div className="flex items-start gap-3">
              <MapPin className="mt-0.5 h-4 w-4 shrink-0 text-[hsl(var(--muted))]" />
              <div>
                <p className="text-sm font-medium">
                  {location.city}, {location.state ? `${location.state}, ` : ""}
                  {location.country}
                  {location.headquarters && (
                    <span className="ml-2 inline-flex items-center gap-1 text-xs text-primary-600">
                      <Star className="h-3 w-3 fill-current" /> HQ
                    </span>
                  )}
                </p>
                <p className="text-xs text-[hsl(var(--muted))]">
                  {location.addressLine}
                  {location.postalCode ? ` · ${location.postalCode}` : ""}
                </p>
              </div>
            </div>
            <div className="flex shrink-0 gap-1">
              <Button variant="ghost" size="sm" onClick={() => openEdit(location)} aria-label="Edit location">
                <Pencil className="h-4 w-4" />
              </Button>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setDeleting(location)}
                aria-label="Delete location"
              >
                <Trash2 className="h-4 w-4 text-danger-500" />
              </Button>
            </div>
          </div>
        ))}
      </div>

      {locations && locations.some((l) => l.latitude != null && l.longitude != null) && (
        <div className="mt-4">
          <CompanyLocationMap locations={locations} />
        </div>
      )}

      {formOpen && (
        <LocationFormModal
          open={formOpen}
          onOpenChange={setFormOpen}
          location={editing}
          isLoading={createLocation.isPending || updateLocation.isPending}
          onSubmit={handleSubmit}
        />
      )}

      <ConfirmDialog
        open={!!deleting}
        onOpenChange={(open) => !open && setDeleting(null)}
        title="Delete location"
        description={`Remove ${deleting?.city ?? "this location"} from your company profile?`}
        confirmLabel="Delete"
        isLoading={deleteLocation.isPending}
        onConfirm={() => {
          if (deleting) deleteLocation.mutate(deleting.id, { onSuccess: () => setDeleting(null) });
        }}
      />
    </Card>
  );
}

function LocationFormModal({
  open,
  onOpenChange,
  location,
  isLoading,
  onSubmit,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  location: CompanyLocationResponse | null;
  isLoading: boolean;
  onSubmit: (values: CompanyLocationFormValues, onDone: () => void) => void;
}) {
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<CompanyLocationFormValues>({
    resolver: zodResolver(companyLocationFormSchema),
    defaultValues: location
      ? {
          addressLine: location.addressLine,
          city: location.city,
          state: location.state ?? "",
          country: location.country,
          postalCode: location.postalCode ?? "",
          headquarters: location.headquarters,
          latitude: location.latitude,
          longitude: location.longitude,
        }
      : {
          addressLine: "",
          city: "",
          state: "",
          country: "",
          postalCode: "",
          headquarters: false,
          latitude: null,
          longitude: null,
        },
  });

  const watchedLat = watch("latitude");
  const watchedLng = watch("longitude");

  return (
    <Modal open={open} onOpenChange={onOpenChange} title={location ? "Edit location" : "Add location"}>
      <form onSubmit={handleSubmit((values) => onSubmit(values, () => onOpenChange(false)))} className="space-y-4">
        <FormField label="Address line" htmlFor="addressLine" required error={errors.addressLine?.message}>
          <Input id="addressLine" {...register("addressLine")} />
        </FormField>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="City" htmlFor="city" required error={errors.city?.message}>
            <Input id="city" {...register("city")} />
          </FormField>
          <FormField label="State" htmlFor="state" error={errors.state?.message}>
            <Input id="state" {...register("state")} />
          </FormField>
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Country" htmlFor="country" required error={errors.country?.message}>
            <Input id="country" {...register("country")} />
          </FormField>
          <FormField label="Postal code" htmlFor="postalCode" error={errors.postalCode?.message}>
            <Input id="postalCode" {...register("postalCode")} />
          </FormField>
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField
            label="Latitude"
            htmlFor="latitude"
            error={errors.latitude?.message}
          >
            <Input
              id="latitude"
              type="number"
              step="any"
              placeholder="e.g. 37.774900"
              {...register("latitude", { setValueAs: (v) => (v === "" ? null : Number(v)) })}
            />
          </FormField>
          <FormField
            label="Longitude"
            htmlFor="longitude"
            error={errors.longitude?.message}
          >
            <Input
              id="longitude"
              type="number"
              step="any"
              placeholder="e.g. -122.419400"
              {...register("longitude", { setValueAs: (v) => (v === "" ? null : Number(v)) })}
            />
          </FormField>
        </div>
        <p className="text-xs text-[hsl(var(--muted))]">
          Optional — add coordinates to show this location on the map. Look them up on{" "}
          <a
            href="https://www.openstreetmap.org"
            target="_blank"
            rel="noopener noreferrer"
            className="text-primary-600 hover:underline"
          >
            OpenStreetMap
          </a>
          .
        </p>
        {watchedLat != null && watchedLng != null && (
          <CompanyLocationMap
            locations={[
              {
                id: "preview",
                city: watch("city") || "Preview",
                country: watch("country") || "",
                latitude: watchedLat,
                longitude: watchedLng,
                headquarters: watch("headquarters"),
              },
            ]}
            height={200}
          />
        )}
        <label className="flex items-center gap-2 text-sm">
          <Checkbox {...register("headquarters")} />
          This is our headquarters
        </label>
        <div className="flex justify-end gap-2">
          <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isLoading}>
            {location ? "Save changes" : "Add location"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
