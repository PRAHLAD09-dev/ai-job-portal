import type { HTMLAttributes } from "react";
import { cn } from "@/lib/cn";

/** Skeleton loading block — preferred over spinners per 01_UI_DESIGN.md. */
export function Skeleton({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div className={cn("skeleton", className)} {...props} />;
}
