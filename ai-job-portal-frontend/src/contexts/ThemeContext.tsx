import { createContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { STORAGE_KEYS } from "@/constants/storage-keys";
import type { Theme } from "@/types/auth";

interface ThemeContextValue {
  theme: Theme;
  resolvedTheme: "light" | "dark";
  setTheme: (theme: Theme) => void;
}

export const ThemeContext = createContext<ThemeContextValue | undefined>(undefined);

function getSystemTheme(): "light" | "dark" {
  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
}

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setThemeState] = useState<Theme>(
    () => (localStorage.getItem(STORAGE_KEYS.THEME) as Theme | null) ?? "system",
  );
  const [resolvedTheme, setResolvedTheme] = useState<"light" | "dark">(
    theme === "system" ? getSystemTheme() : theme,
  );

  useEffect(() => {
    const applied = theme === "system" ? getSystemTheme() : theme;
    setResolvedTheme(applied);
    document.documentElement.classList.toggle("dark", applied === "dark");
  }, [theme]);

  useEffect(() => {
    if (theme !== "system") return;
    const mql = window.matchMedia("(prefers-color-scheme: dark)");
    const listener = () => {
      const applied = getSystemTheme();
      setResolvedTheme(applied);
      document.documentElement.classList.toggle("dark", applied === "dark");
    };
    mql.addEventListener("change", listener);
    return () => mql.removeEventListener("change", listener);
  }, [theme]);

  const setTheme = (next: Theme) => {
    localStorage.setItem(STORAGE_KEYS.THEME, next);
    setThemeState(next);
  };

  const value = useMemo(() => ({ theme, resolvedTheme, setTheme }), [theme, resolvedTheme]);

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}
