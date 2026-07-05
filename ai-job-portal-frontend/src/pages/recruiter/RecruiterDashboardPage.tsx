import { Card } from "@/components/ui/card";
import { useAuth } from "@/hooks/useAuth";

export default function RecruiterDashboardPage() {
  const { user } = useAuth();
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">Welcome back, {user?.firstName}</h1>
        <p className="text-sm text-[hsl(var(--muted))]">Your hiring overview.</p>
      </div>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {["Open Jobs", "Applicants", "Interviews", "Hires"].map((label) => (
          <Card key={label}>
            <p className="text-sm text-[hsl(var(--muted))]">{label}</p>
            <p className="mt-2 text-2xl font-semibold">0</p>
          </Card>
        ))}
      </div>
    </div>
  );
}
