import { useQuery } from "@tanstack/react-query";
import { categoryService } from "@/features/jobs/services/category.service";

export function useJobCategories() {
  return useQuery({
    queryKey: ["job-categories"],
    queryFn: () => categoryService.getAll().then((res) => res.data),
    staleTime: 5 * 60_000,
  });
}

export function useJobPopularSkills() {
  return useQuery({
    queryKey: ["job-categories", "popular-skills"],
    queryFn: () => categoryService.getPopularSkills().then((res) => res.data),
    staleTime: 5 * 60_000,
  });
}
