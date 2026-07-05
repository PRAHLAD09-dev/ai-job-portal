import { Link, Outlet } from "react-router-dom";
import { Logo } from "@/components/common/Logo";
import { ThemeSwitch } from "@/components/layout/ThemeSwitch";
import { ROUTES } from "@/constants/routes";

/** Centered card layout for Login/Register/Forgot/Reset/Verify pages. */
export function AuthLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-[hsl(var(--background))]">
      <header className="flex items-center justify-between p-4 md:p-6">
        <Link to={ROUTES.HOME}>
          <Logo />
        </Link>
        <ThemeSwitch />
      </header>
      <main className="flex flex-1 items-center justify-center px-4 pb-16">
        <div className="w-full max-w-md">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
