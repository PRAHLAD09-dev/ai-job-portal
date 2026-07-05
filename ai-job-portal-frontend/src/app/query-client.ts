import { QueryClient } from "@tanstack/react-query";

/** Central TanStack Query client — caching strategy per 04_BACKEND_INTEGRATION.md. */
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
});
