import { Card } from "@/components/ui/card";
import { useAuth } from "@/hooks/useAuth";

export default function CandidateDashboardPage() {
  const { user } = useAuth();
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">Welcome back, {user?.firstName}</h1>
        <p className="text-sm text-[hsl(var(--muted))]">Here's what's happening with your job search.</p>
      </div>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {["Applications", "Saved Jobs", "Profile Score", "AI Credits"].map((label) => (
          <Card key={label}>
            <p className="text-sm text-[hsl(var(--muted))]">{label}</p>
            <p className="mt-2 text-2xl font-semibold">0</p>
          </Card>
        ))}
      </div>
      <Card>
        <p className="text-sm text-[hsl(var(--muted))]">
          Candidate profile, resume manager, applications, and AI features will be built out in
          Phase 3 &amp; 4 per 05_FRONTEND_ROADMAP.md.
        </p>
      </Card>
    </div>
  );
}
