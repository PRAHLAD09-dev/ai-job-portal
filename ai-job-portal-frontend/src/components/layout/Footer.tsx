import { Logo } from "@/components/common/Logo";

export function Footer() {
  return (
    <footer className="border-t border-[hsl(var(--border-color))] px-4 py-8 md:px-8">
      <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-4 text-sm text-[hsl(var(--muted))] md:flex-row">
        <Logo showName={true} />
        <p>&copy; {new Date().getFullYear()} AI Job Portal. All rights reserved.</p>
      </div>
    </footer>
  );
}
