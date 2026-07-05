import { Link, Outlet } from "react-router-dom";
import { Logo } from "@/components/common/Logo";
import { ROUTES } from "@/constants/routes";

export function ErrorLayout() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-8 bg-[hsl(var(--background))] px-4 text-center">
      <Link to={ROUTES.HOME}>
        <Logo />
      </Link>
      <Outlet />
    </div>
  );
}
