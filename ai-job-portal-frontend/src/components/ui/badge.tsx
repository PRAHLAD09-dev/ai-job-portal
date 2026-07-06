import type { HTMLAttributes } from "react";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/cn";

const badgeVariants = cva("inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-medium", {
  variants: {
    variant: {
      default: "bg-[hsl(var(--border-color))]/60 text-[hsl(var(--foreground))]",
      primary: "bg-primary-600/10 text-primary-600",
      success: "bg-success-500/10 text-success-500",
      warning: "bg-warning-500/10 text-warning-500",
      danger: "bg-danger-500/10 text-danger-500",
      outline: "border border-[hsl(var(--border-color))] text-[hsl(var(--muted))]",
    },
  },
  defaultVariants: { variant: "default" },
});

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement>, VariantProps<typeof badgeVariants> {}

export function Badge({ className, variant, ...props }: BadgeProps) {
  return <span className={cn(badgeVariants({ variant }), className)} {...props} />;
}
