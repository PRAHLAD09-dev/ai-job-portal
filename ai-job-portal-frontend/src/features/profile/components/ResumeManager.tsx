import { useRef, useState, type DragEvent } from "react";
import { Download, Eye, FileText, RefreshCw, Trash2, UploadCloud } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { EmptyState } from "@/components/common/EmptyState";
import { Skeleton } from "@/components/ui/skeleton";
import { formatFileSize } from "@/utils/format";
import { useDeleteResume, useReplaceResume, useResumesList, useUploadResume } from "@/features/profile/hooks/useResumes";
import type { ResumeResponse } from "@/features/profile/types";

const ALLOWED_EXTENSIONS = [".pdf", ".doc", ".docx"];
const MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB — mirrors candidate-service.yml resume.max-file-size-bytes

function isAllowedFile(file: File): string | null {
  const extension = file.name.slice(file.name.lastIndexOf(".")).toLowerCase();
  if (!ALLOWED_EXTENSIONS.includes(extension)) {
    return "Only PDF, DOC, and DOCX files are supported.";
  }
  if (file.size > MAX_FILE_SIZE_BYTES) {
    return "File size must not exceed 10 MB.";
  }
  return null;
}

export function ResumeManager() {
  const { data: resumes, isLoading } = useResumesList();
  const uploadResume = useUploadResume();
  const replaceResume = useReplaceResume();
  const deleteResume = useDeleteResume();

  const [isDragging, setIsDragging] = useState(false);
  const [validationError, setValidationError] = useState<string | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<ResumeResponse | null>(null);
  const [replaceTargetId, setReplaceTargetId] = useState<string | null>(null);

  const fileInputRef = useRef<HTMLInputElement>(null);
  const replaceInputRef = useRef<HTMLInputElement>(null);

  const handleFile = (file: File) => {
    const error = isAllowedFile(file);
    if (error) {
      setValidationError(error);
      return;
    }
    setValidationError(null);
    uploadResume.mutate(file);
  };

  const handleReplaceFile = (file: File) => {
    const error = isAllowedFile(file);
    if (error || !replaceTargetId) {
      setValidationError(error);
      return;
    }
    setValidationError(null);
    replaceResume.mutate({ resumeId: replaceTargetId, file }, { onSuccess: () => setReplaceTargetId(null) });
  };

  const onDrop = (event: DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setIsDragging(false);
    const file = event.dataTransfer.files?.[0];
    if (file) handleFile(file);
  };

  const isBusy = uploadResume.isPending || replaceResume.isPending;

  return (
    <Card>
      <h3 className="text-base font-semibold">Resumes</h3>

      <div
        onDragOver={(e) => {
          e.preventDefault();
          setIsDragging(true);
        }}
        onDragLeave={() => setIsDragging(false)}
        onDrop={onDrop}
        onClick={() => fileInputRef.current?.click()}
        className={`mt-4 flex cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed p-8 text-center transition-colors ${
          isDragging ? "border-primary-600 bg-primary-600/5" : "border-[hsl(var(--border-color))]"
        }`}
      >
        <UploadCloud className="h-8 w-8 text-[hsl(var(--muted))]" />
        <p className="mt-2 text-sm font-medium">Drag &amp; drop your resume here, or click to browse</p>
        <p className="mt-1 text-xs text-[hsl(var(--muted))]">PDF, DOC, or DOCX — up to 10 MB</p>
        <input
          ref={fileInputRef}
          type="file"
          accept=".pdf,.doc,.docx"
          hidden
          onChange={(e) => e.target.files?.[0] && handleFile(e.target.files[0])}
        />
      </div>

      {validationError && <p className="mt-2 text-sm text-danger-500">{validationError}</p>}

      {isBusy && (
        <div className="mt-3">
          <Progress value={uploadResume.progress || replaceResume.progress} />
        </div>
      )}

      <div className="mt-5 space-y-3">
        {isLoading && <Skeleton className="h-16 w-full" />}

        {!isLoading && resumes?.length === 0 && (
          <EmptyState
            icon={<FileText className="h-8 w-8" />}
            title="No resumes uploaded yet"
            message="Upload a resume so recruiters and our AI engine can review it."
          />
        )}

        {resumes?.map((resume) => (
          <div
            key={resume.id}
            className="flex items-center justify-between gap-4 rounded-lg border border-[hsl(var(--border-color))] p-4"
          >
            <div className="flex min-w-0 items-center gap-3">
              <FileText className="h-8 w-8 shrink-0 text-primary-600" />
              <div className="min-w-0">
                <p className="truncate font-medium">{resume.fileName}</p>
                <p className="text-xs text-[hsl(var(--muted))]">
                  {resume.fileFormat.toUpperCase()} · {formatFileSize(resume.fileSizeBytes)} · v{resume.versionNumber}
                </p>
              </div>
              <Badge variant={resume.status === "ACTIVE" ? "success" : "outline"}>{resume.status}</Badge>
            </div>
            <div className="flex shrink-0 gap-1">
              <a
                href={resume.fileUrl}
                target="_blank"
                rel="noopener noreferrer"
                aria-label="Preview"
                className="rounded-md p-1.5 text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40"
              >
                <Eye className="h-4 w-4" />
              </a>
              <a
                href={resume.fileUrl}
                download
                aria-label="Download"
                className="rounded-md p-1.5 text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40"
              >
                <Download className="h-4 w-4" />
              </a>
              <button
                type="button"
                aria-label="Replace"
                onClick={() => {
                  setReplaceTargetId(resume.id);
                  replaceInputRef.current?.click();
                }}
                className="rounded-md p-1.5 text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40"
              >
                <RefreshCw className="h-4 w-4" />
              </button>
              <button
                type="button"
                aria-label="Delete"
                onClick={() => setDeleteTarget(resume)}
                className="rounded-md p-1.5 text-danger-500 hover:bg-[hsl(var(--border-color))]/40"
              >
                <Trash2 className="h-4 w-4" />
              </button>
            </div>
          </div>
        ))}
      </div>

      <input
        ref={replaceInputRef}
        type="file"
        accept=".pdf,.doc,.docx"
        hidden
        onChange={(e) => e.target.files?.[0] && handleReplaceFile(e.target.files[0])}
      />

      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title="Delete resume"
        description={`Are you sure you want to delete "${deleteTarget?.fileName}"? This cannot be undone.`}
        confirmLabel="Delete"
        isLoading={deleteResume.isPending}
        onConfirm={() =>
          deleteTarget && deleteResume.mutate(deleteTarget.id, { onSuccess: () => setDeleteTarget(null) })
        }
      />
    </Card>
  );
}
