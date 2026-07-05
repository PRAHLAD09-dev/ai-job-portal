import { forwardRef, type InputHTMLAttributes } from "react";
import { cn } from "@/lib/cn";

export const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement>>(
  ({ className, ...props }, ref) => (
    <input
      ref={ref}
      className={cn(
        "h-10 w-full rounded-lg border border-[hsl(var(--border-color))] bg-[hsl(var(--surface))] px-3 text-sm",
        "placeholder:text-[hsl(var(--muted))] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-500",
        "disabled:cursor-not-allowed disabled:opacity-50",
        className,
      )}
      {...props}
    />
  ),
);
Input.displayName = "Input";
