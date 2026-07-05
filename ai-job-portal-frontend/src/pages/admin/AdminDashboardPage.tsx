import { Card } from "@/components/ui/card";
import { useAuth } from "@/hooks/useAuth";

export default function AdminDashboardPage() {
  const { user } = useAuth();
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold">Welcome, {user?.firstName}</h1>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {["Users", "Companies", "Jobs", "Reports"].map((label) => (
          <Card key={label}>
            <p className="text-sm text-[hsl(var(--muted))]">{label}</p>
            <p className="mt-2 text-2xl font-semibold">0</p>
          </Card>
        ))}
      </div>
    </div>
  );
}
