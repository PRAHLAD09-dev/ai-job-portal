import { Link } from "react-router-dom";
import { Logo } from "@/components/common/Logo";
import { ThemeSwitch } from "@/components/layout/ThemeSwitch";
import { Button } from "@/components/ui/button";
import { ROUTES } from "@/constants/routes";

/** Public navbar: Home, Jobs, Login, Register (01_UI_DESIGN.md guest navigation). */
export function GuestNavbar() {
  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-[hsl(var(--border-color))] bg-[hsl(var(--surface))]/90 px-4 backdrop-blur md:px-8">
      <Link to={ROUTES.HOME}>
        <Logo />
      </Link>
      <nav className="hidden items-center gap-6 text-sm font-medium md:flex">
        <Link to={ROUTES.HOME} className="hover:text-primary-600">Home</Link>
        <Link to={ROUTES.JOBS} className="hover:text-primary-600">Jobs</Link>
      </nav>
      <div className="flex items-center gap-2">
        <ThemeSwitch />
        <Link to={ROUTES.LOGIN}>
          <Button variant="ghost" size="sm">Login</Button>
        </Link>
        <Link to={ROUTES.REGISTER}>
          <Button size="sm">Register</Button>
        </Link>
      </div>
    </header>
  );
}
