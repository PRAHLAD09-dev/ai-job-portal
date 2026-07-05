import { Link } from "react-router-dom";
import { ChevronRight } from "lucide-react";

interface Crumb {
  label: string;
  path?: string;
}

export function Breadcrumb({ items }: { items: Crumb[] }) {
  return (
    <nav aria-label="Breadcrumb" className="mb-4 flex items-center gap-1.5 text-sm text-[hsl(var(--muted))]">
      {items.map((item, i) => (
        <span key={item.label} className="flex items-center gap-1.5">
          {i > 0 && <ChevronRight className="h-3.5 w-3.5" />}
          {item.path ? (
            <Link to={item.path} className="hover:text-primary-600">{item.label}</Link>
          ) : (
            <span className="text-[hsl(var(--foreground))]">{item.label}</span>
          )}
        </span>
      ))}
    </nav>
  );
}
