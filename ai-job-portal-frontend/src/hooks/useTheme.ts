import { useContext } from "react";
import { ThemeContext } from "@/contexts/ThemeContext";

/** Access/change the persisted light/dark/system theme preference. */
export function useTheme() {
  const ctx = useContext(ThemeContext);
  if (!ctx) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return ctx;
}
