import type { ReactNode } from "react";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/cn";

interface FormFieldProps {
  label: string;
  htmlFor: string;
  error?: string;
  required?: boolean;
  children: ReactNode;
}

/** Wraps an input with label + inline validation error, per 01_UI_DESIGN.md forms spec. */
export function FormField({ label, htmlFor, error, required, children }: FormFieldProps) {
  return (
    <div>
      <Label htmlFor={htmlFor}>
        {label}
        {required && <span className="text-danger-500"> *</span>}
      </Label>
      {children}
      {error && (
        <p className={cn("mt-1 text-xs text-danger-500")} role="alert">
          {error}
        </p>
      )}
    </div>
  );
}
