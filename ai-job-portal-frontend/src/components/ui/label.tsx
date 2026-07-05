import { forwardRef, type LabelHTMLAttributes } from "react";
import { cn } from "@/lib/cn";

export const Label = forwardRef<HTMLLabelElement, LabelHTMLAttributes<HTMLLabelElement>>(
  ({ className, children, ...props }, ref) => (
    <label ref={ref} className={cn("mb-1.5 block text-sm font-medium", className)} {...props}>
      {children}
    </label>
  ),
);
Label.displayName = "Label";
