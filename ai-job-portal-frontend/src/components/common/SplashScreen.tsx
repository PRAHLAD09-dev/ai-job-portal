import { motion } from "framer-motion";
import { Logo } from "@/components/common/Logo";

/** Shown only on first app load until initialization completes (01_UI_DESIGN.md). */
export function SplashScreen() {
  return (
    <motion.div
      className="fixed inset-0 z-50 flex flex-col items-center justify-center gap-4 bg-[hsl(var(--background))]"
      initial={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.4 }}
    >
      <motion.div
        initial={{ opacity: 0, scale: 0.85 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, ease: "easeOut" }}
      >
        <Logo className="scale-125" />
      </motion.div>
      <div className="h-1 w-40 overflow-hidden rounded-full bg-[hsl(var(--border-color))]">
        <motion.div
          className="h-full w-1/2 rounded-full bg-primary-600"
          animate={{ x: ["-100%", "200%"] }}
          transition={{ duration: 1.1, repeat: Infinity, ease: "easeInOut" }}
        />
      </div>
    </motion.div>
  );
}
