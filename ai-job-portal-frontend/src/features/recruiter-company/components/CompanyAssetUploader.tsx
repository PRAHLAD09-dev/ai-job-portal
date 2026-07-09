import { useRef } from "react";
import { ImageIcon, Trash2, Upload } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useCompanyBanner, useCompanyLogo } from "@/features/recruiter-company/hooks/useCompanyAssets";

interface CompanyAssetUploaderProps {
  logoUrl: string | null;
  bannerUrl: string | null;
}

/** Company logo + banner upload/replace/delete — multipart against CompanyAssetController. */
export function CompanyAssetUploader({ logoUrl, bannerUrl }: CompanyAssetUploaderProps) {
  const logo = useCompanyLogo(!!logoUrl);
  const banner = useCompanyBanner(!!bannerUrl);
  const logoInputRef = useRef<HTMLInputElement>(null);
  const bannerInputRef = useRef<HTMLInputElement>(null);

  return (
    <Card>
      <h2 className="text-lg font-semibold">Branding</h2>
      <div className="mt-4 grid gap-6 sm:grid-cols-2">
        <div>
          <p className="mb-2 text-sm font-medium">Logo</p>
          <div className="flex h-24 w-24 items-center justify-center overflow-hidden rounded-xl border border-[hsl(var(--border-color))] bg-[hsl(var(--background))]">
            {logoUrl ? (
              <img src={logoUrl} alt="Company logo" className="h-full w-full object-cover" />
            ) : (
              <ImageIcon className="h-8 w-8 text-[hsl(var(--muted))]" />
            )}
          </div>
          <input
            ref={logoInputRef}
            type="file"
            accept="image/*"
            className="hidden"
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) logo.upload.mutate(file);
              e.target.value = "";
            }}
          />
          <div className="mt-2 flex gap-2">
            <Button
              size="sm"
              variant="outline"
              isLoading={logo.upload.isPending}
              onClick={() => logoInputRef.current?.click()}
            >
              <Upload className="h-3.5 w-3.5" /> {logoUrl ? "Replace" : "Upload"}
            </Button>
            {logoUrl && (
              <Button size="sm" variant="ghost" isLoading={logo.remove.isPending} onClick={() => logo.remove.mutate()}>
                <Trash2 className="h-3.5 w-3.5 text-danger-500" />
              </Button>
            )}
          </div>
        </div>

        <div>
          <p className="mb-2 text-sm font-medium">Banner</p>
          <div className="flex h-24 w-full items-center justify-center overflow-hidden rounded-xl border border-[hsl(var(--border-color))] bg-[hsl(var(--background))]">
            {bannerUrl ? (
              <img src={bannerUrl} alt="Company banner" className="h-full w-full object-cover" />
            ) : (
              <ImageIcon className="h-8 w-8 text-[hsl(var(--muted))]" />
            )}
          </div>
          <input
            ref={bannerInputRef}
            type="file"
            accept="image/*"
            className="hidden"
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) banner.upload.mutate(file);
              e.target.value = "";
            }}
          />
          <div className="mt-2 flex gap-2">
            <Button
              size="sm"
              variant="outline"
              isLoading={banner.upload.isPending}
              onClick={() => bannerInputRef.current?.click()}
            >
              <Upload className="h-3.5 w-3.5" /> {bannerUrl ? "Replace" : "Upload"}
            </Button>
            {bannerUrl && (
              <Button
                size="sm"
                variant="ghost"
                isLoading={banner.remove.isPending}
                onClick={() => banner.remove.mutate()}
              >
                <Trash2 className="h-3.5 w-3.5 text-danger-500" />
              </Button>
            )}
          </div>
        </div>
      </div>
    </Card>
  );
}
