import { useEffect, useState } from "react";
import { AnimatePresence } from "framer-motion";
import { AppProviders } from "@/app/AppProviders";
import { AppRouter } from "@/routes/AppRouter";
import { SplashScreen } from "@/components/common/SplashScreen";
import { PwaInstallPrompt } from "@/components/common/PwaInstallPrompt";

function AppShell() {
  // Splash screen: shown only on first load, for 2-3s or until init completes (01_UI_DESIGN.md).
  const [showSplash, setShowSplash] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => setShowSplash(false), 2200);
    return () => clearTimeout(timer);
  }, []);

  return (
    <>
      <AnimatePresence>{showSplash && <SplashScreen />}</AnimatePresence>
      {!showSplash && (
        <>
          <AppRouter />
          <PwaInstallPrompt />
        </>
      )}
    </>
  );
}

export default function App() {
  return (
    <AppProviders>
      <AppShell />
    </AppProviders>
  );
}
