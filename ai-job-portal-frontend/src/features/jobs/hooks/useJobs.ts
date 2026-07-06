import { useQuery } from "@tanstack/react-query";
import { jobService, type JobListParams } from "@/features/jobs/services/job.service";

export function useJobSearch(params: JobListParams) {
  return useQuery({
    queryKey: ["jobs", "search", params],
    queryFn: () => jobService.search(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

export function useJobDetails(jobId: string | undefined) {
  return useQuery({
    queryKey: ["jobs", "detail", jobId],
    queryFn: () => jobService.getById(jobId as string).then((res) => res.data),
    enabled: !!jobId,
  });
}

export function useSimilarJobs(jobId: string | undefined) {
  return useQuery({
    queryKey: ["jobs", "similar", jobId],
    queryFn: () => jobService.getSimilar(jobId as string).then((res) => res.data),
    enabled: !!jobId,
  });
}

export function useFeaturedJobs() {
  return useQuery({
    queryKey: ["jobs", "featured"],
    queryFn: () => jobService.getFeatured().then((res) => res.data),
  });
}
