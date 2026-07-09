import { useState } from "react";
import { useForm } from "react-hook-form";
import { Copy, Download, RefreshCw, Sparkles } from "lucide-react";
import { toast } from "sonner";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { useGenerateJobDescription } from "@/features/ai/hooks/useAi";

interface FormValues {
  jobTitle: string;
  jobType: string;
  experienceLevel: string;
  keyPointsRaw: string;
}

export function JobDescriptionGenerator() {
  const generate = useGenerateJobDescription();
  const [lastPayload, setLastPayload] = useState<FormValues | null>(null);
  const { register, handleSubmit } = useForm<FormValues>({
    defaultValues: { jobTitle: "", jobType: "FULL_TIME", experienceLevel: "MID_LEVEL", keyPointsRaw: "" },
  });

  const submit = (values: FormValues) => {
    setLastPayload(values);
    generate.mutate({
      jobTitle: values.jobTitle,
      jobType: values.jobType,
      experienceLevel: values.experienceLevel,
      keyPoints: values.keyPointsRaw
        .split("\n")
        .map((line) => line.trim())
        .filter(Boolean),
    });
  };

  const handleCopy = () => {
    if (!generate.data) return;
    navigator.clipboard.writeText(generate.data.description);
    toast.success("Copied to clipboard");
  };

  const handleDownload = () => {
    if (!generate.data) return;
    const blob = new Blob([generate.data.description], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "job-description.txt";
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <Card>
      <div className="flex items-center gap-2">
        <Sparkles className="h-5 w-5 text-primary-600" />
        <h2 className="text-lg font-semibold">Job description generator</h2>
      </div>
      <form onSubmit={handleSubmit(submit)} className="mt-4 space-y-4">
        <FormField label="Job title" htmlFor="jd-title" required>
          <Input id="jd-title" {...register("jobTitle", { required: true })} />
        </FormField>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Job type" htmlFor="jd-jobType">
            <Select id="jd-jobType" {...register("jobType")}>
              {["FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "FREELANCE", "TEMPORARY"].map((t) => (
                <option key={t} value={t}>
                  {t.replace("_", " ")}
                </option>
              ))}
            </Select>
          </FormField>
          <FormField label="Experience level" htmlFor="jd-level">
            <Select id="jd-level" {...register("experienceLevel")}>
              {["ENTRY_LEVEL", "ASSOCIATE", "MID_LEVEL", "SENIOR_LEVEL", "LEAD", "MANAGER", "EXECUTIVE"].map((l) => (
                <option key={l} value={l}>
                  {l.replace("_", " ")}
                </option>
              ))}
            </Select>
          </FormField>
        </div>
        <FormField label="Key points (one per line)" htmlFor="jd-keypoints">
          <Textarea
            id="jd-keypoints"
            rows={4}
            placeholder={"Own the checkout experience\nCollaborate with design and product\n5+ years of React experience"}
            {...register("keyPointsRaw")}
          />
        </FormField>
        <Button type="submit" isLoading={generate.isPending}>
          <Sparkles className="h-4 w-4" /> Generate
        </Button>
      </form>

      {generate.isPending && (
        <div className="mt-4 space-y-2">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-2/3" />
        </div>
      )}

      {generate.data && !generate.isPending && (
        <div className="mt-6 rounded-xl border border-[hsl(var(--border-color))] p-4">
          <div className="whitespace-pre-wrap text-sm leading-relaxed">{generate.data.description}</div>
          {generate.data.requiredSkills.length > 0 && (
            <div className="mt-4 flex flex-wrap gap-1.5">
              {generate.data.requiredSkills.map((skill) => (
                <Badge key={skill} variant="outline">
                  {skill}
                </Badge>
              ))}
            </div>
          )}
          <div className="mt-4 flex gap-2">
            <Button size="sm" variant="outline" onClick={handleCopy}>
              <Copy className="h-3.5 w-3.5" /> Copy
            </Button>
            <Button size="sm" variant="outline" onClick={handleDownload}>
              <Download className="h-3.5 w-3.5" /> Download
            </Button>
            <Button
              size="sm"
              variant="outline"
              isLoading={generate.isPending}
              onClick={() => lastPayload && submit(lastPayload)}
            >
              <RefreshCw className="h-3.5 w-3.5" /> Regenerate
            </Button>
          </div>
        </div>
      )}
    </Card>
  );
}
