import { useEffect, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { Download, X } from "lucide-react";
import { Button } from "@/components/ui/button";

interface BeforeInstallPromptEvent extends Event {
  prompt: () => Promise<void>;
  userChoice: Promise<{ outcome: "accepted" | "dismissed" }>;
}

const DISMISSED_KEY = "ajp-pwa-install-dismissed";

/**
 * Native "Add to Home Screen" prompt, deferred and re-shown on our own
 * terms (01_UI_DESIGN.md: "Install Prompt"). Chrome/Edge fire
 * `beforeinstallprompt` once the manifest + service worker requirements
 * are met; we capture it, suppress the browser's default mini-infobar,
 * and show our own banner instead.
 */
export function PwaInstallPrompt() {
  const [deferredPrompt, setDeferredPrompt] = useState<BeforeInstallPromptEvent | null>(null);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    if (localStorage.getItem(DISMISSED_KEY) === "true") return;

    const handler = (event: Event) => {
      event.preventDefault();
      setDeferredPrompt(event as BeforeInstallPromptEvent);
      setVisible(true);
    };

    window.addEventListener("beforeinstallprompt", handler);
    return () => window.removeEventListener("beforeinstallprompt", handler);
  }, []);

  const handleInstall = async () => {
    if (!deferredPrompt) return;
    await deferredPrompt.prompt();
    await deferredPrompt.userChoice;
    setDeferredPrompt(null);
    setVisible(false);
  };

  const handleDismiss = () => {
    localStorage.setItem(DISMISSED_KEY, "true");
    setVisible(false);
  };

  return (
    <AnimatePresence>
      {visible && (
        <motion.div
          initial={{ y: 80, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 80, opacity: 0 }}
          transition={{ duration: 0.25 }}
          className="fixed bottom-4 left-1/2 z-50 flex w-[calc(100%-2rem)] max-w-sm -translate-x-1/2 items-center gap-3 rounded-xl border border-[hsl(var(--border-color))] bg-[hsl(var(--card))] p-4 shadow-lg"
          role="dialog"
          aria-label="Install AI Job Portal"
        >
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary-600/10 text-primary-600">
            <Download className="h-5 w-5" aria-hidden="true" />
          </div>
          <div className="min-w-0 flex-1">
            <p className="text-sm font-medium">Install AI Job Portal</p>
            <p className="text-xs text-[hsl(var(--muted))]">Add it to your home screen for quick access.</p>
          </div>
          <div className="flex shrink-0 items-center gap-1">
            <Button size="sm" onClick={handleInstall}>
              Install
            </Button>
            <Button size="sm" variant="ghost" className="px-2" onClick={handleDismiss} aria-label="Dismiss install prompt">
              <X className="h-4 w-4" />
            </Button>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
