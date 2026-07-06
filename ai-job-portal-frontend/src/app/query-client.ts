import { QueryCache, QueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { extractErrorMessage } from "@/services/api-client";

/**
 * Central TanStack Query client — caching strategy per
 * 04_BACKEND_INTEGRATION.md. A global QueryCache error handler ensures
 * every page has an error state (a toast) even for queries that don't
 * render a bespoke inline error UI — 404s that a query intentionally
 * treats as a normal empty result (e.g. "no profile yet") are excluded
 * by checking the query's own meta flag.
 */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 60_000,
      refetchOnWindowFocus: false,
    },
    mutations: {
      retry: 0,
    },
  },
  queryCache: new QueryCache({
    onError: (error, query) => {
      if (query.meta?.suppressErrorToast) return;
      toast.error(extractErrorMessage(error));
    },
  }),
});
