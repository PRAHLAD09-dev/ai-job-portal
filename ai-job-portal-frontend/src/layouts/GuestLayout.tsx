import { Outlet } from "react-router-dom";
import { GuestNavbar } from "@/components/layout/GuestNavbar";
import { Footer } from "@/components/layout/Footer";

export function GuestLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <GuestNavbar />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
