import { Link } from "react-router-dom";
import { Search, Sparkles, ShieldCheck } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { ROUTES } from "@/constants/routes";

export default function HomePage() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-16 md:px-8 md:py-24">
      <div className="mx-auto max-w-2xl text-center">
        <h1 className="text-3xl font-bold tracking-tight md:text-5xl">
          Find your next role, powered by AI.
        </h1>
        <p className="mt-4 text-lg text-[hsl(var(--muted))]">
          Resume analysis, ATS scoring, and smart job matching — all in one enterprise-grade
          job portal.
        </p>
        <div className="mt-8 flex justify-center gap-3">
          <Link to={ROUTES.JOBS}>
            <Button size="lg">Browse Jobs</Button>
          </Link>
          <Link to={ROUTES.REGISTER}>
            <Button size="lg" variant="outline">Get Started</Button>
          </Link>
        </div>
      </div>

      <div className="mt-20 grid gap-4 md:grid-cols-3">
        <Card>
          <Search className="h-6 w-6 text-primary-600" />
          <h3 className="mt-3 font-semibold">Smart Job Search</h3>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">
            Filter, sort, and find roles that match your skills instantly.
          </p>
        </Card>
        <Card>
          <Sparkles className="h-6 w-6 text-primary-600" />
          <h3 className="mt-3 font-semibold">AI Resume Insights</h3>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">
            Get an ATS score and tailored suggestions before you apply.
          </p>
        </Card>
        <Card>
          <ShieldCheck className="h-6 w-6 text-primary-600" />
          <h3 className="mt-3 font-semibold">Trusted by Recruiters</h3>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">
            Enterprise-ready hiring workflows for growing teams.
          </p>
        </Card>
      </div>
    </div>
  );
}
