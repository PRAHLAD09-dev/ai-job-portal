import { Github, Linkedin, Mail } from "lucide-react";
import { Link } from "react-router-dom";
import { Logo } from "@/components/common/Logo";
import { Button } from "@/components/ui/button";
import { ROUTES } from "@/constants/routes";
import { ENV } from "@/constants/env";

/**
 * Guest-facing footer. Only links to routes that already exist in
 * ROUTES (02_FRONTEND_ARCHITECTURE.md) — no About/Contact links since
 * those pages aren't built yet, and no fake newsletter submit, since
 * there's no backend endpoint for it.
 */
const LINK_GROUPS = [
    {
        title: "For Candidates",
        links: [
            { label: "Browse Jobs", to: ROUTES.JOBS },
            { label: "Create Account", to: ROUTES.REGISTER },
            { label: "Sign In", to: ROUTES.LOGIN },
        ],
    },
    {
        title: "For Recruiters",
        links: [
            { label: "Post a Job", to: ROUTES.REGISTER },
            { label: "Sign In", to: ROUTES.LOGIN },
        ],
    },
];

export function Footer() {
    return (
        <footer className="border-t border-[hsl(var(--border-color))] bg-[hsl(var(--surface))]">
            <div className="mx-auto max-w-6xl px-4 py-14 md:px-8">
                <div className="grid grid-cols-1 gap-10 sm:grid-cols-2 lg:grid-cols-[1.3fr_1fr_1fr_1.1fr]">
                    {/* Brand */}
                    <div>
                        <Link to={ROUTES.HOME}>
                            <Logo />
                        </Link>
                        <p className="mt-4 max-w-xs text-sm leading-relaxed text-[hsl(var(--muted))]">
                            Helping candidates and recruiters connect through AI-powered hiring.
                        </p>
                        <div className="mt-5 flex gap-3">
                            <a
                                href="https://github.com/PRAHLAD09-dev"
                                target="_blank"
                                rel="noreferrer"
                                aria-label="GitHub"
                                className="flex h-9 w-9 items-center justify-center rounded-lg border border-[hsl(var(--border-color))] text-[hsl(var(--muted))] transition-colors hover:bg-primary-600 hover:text-white"
                            >
                                <Github className="h-4 w-4" />
                            </a>
                            <a
                                href="https://www.linkedin.com/in/prahlad-bhakat/"
                                target="_blank"
                                rel="noreferrer"
                                aria-label="LinkedIn"
                                className="flex h-9 w-9 items-center justify-center rounded-lg border border-[hsl(var(--border-color))] text-[hsl(var(--muted))] transition-colors hover:bg-primary-600 hover:text-white"
                            >
                                <Linkedin className="h-4 w-4" />
                            </a>
                        </div>
                    </div>

                    {/* Link groups */}
                    {LINK_GROUPS.map((group) => (
                        <div key={group.title}>
                            <h3 className="mb-4 text-sm font-semibold uppercase tracking-wide">{group.title}</h3>
                            <ul className="space-y-3">
                                {group.links.map((link) => (
                                    <li key={link.label}>
                                        <Link
                                            to={link.to}
                                            className="text-sm text-[hsl(var(--muted))] transition-colors hover:text-primary-600"
                                        >
                                            {link.label}
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    ))}

                    {/* Contact + CTA */}
                    <div>
                        <h3 className="mb-4 text-sm font-semibold uppercase tracking-wide">Get in touch</h3>
                        <div className="space-y-3 text-sm">
                            <a
                                href="mailto:prahladbhakat05@gmail.com"
                                className="flex items-center gap-2.5 text-[hsl(var(--muted))] hover:text-primary-600"
                            >
                                <Mail className="h-4 w-4 shrink-0 text-primary-600" />
                                <span className="truncate">prahladbhakat05@gmail.com</span>
                            </a>
                        </div>

                        <div className="mt-5">
                            <p className="mb-2 text-xs font-medium text-[hsl(var(--muted))]">
                                Ready to get started?
                            </p>
                            <Link to={ROUTES.REGISTER}>
                                <Button size="sm" className="w-full">
                                    Create Free Account
                                </Button>
                            </Link>
                        </div>
                    </div>
                </div>

                <div className="mt-12 flex flex-col items-center justify-between gap-4 border-t border-[hsl(var(--border-color))] pt-6 text-xs text-[hsl(var(--muted))] sm:flex-row">
                    <p>
                        &copy; {new Date().getFullYear()} {ENV.APP_NAME}. All rights reserved.
                    </p>
                    <div className="flex gap-5">
                        <span className="cursor-default hover:text-[hsl(var(--foreground))]">Privacy Policy</span>
                        <span className="cursor-default hover:text-[hsl(var(--foreground))]">Terms of Service</span>
                    </div>
                </div>
            </div>
        </footer>
    );
}
