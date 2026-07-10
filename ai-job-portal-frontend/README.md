# AI Job Portal — Frontend

An AI-powered job search and hiring platform frontend: candidates get resume
analysis, ATS scoring, job matching, and cover-letter generation; recruiters
get candidate recommendations, AI job descriptions, and interview question
generation — all backed by a real Spring Boot microservice stack, not mocked
data.

Built strictly against a frozen spec (`01_UI_DESIGN.md` →
`05_FRONTEND_ROADMAP.md`) and the **actual backend source code**, service by
service, endpoint by endpoint. See `HISTORY.md` for the day-by-day build log
and `KNOWN_BACKEND_LIMITATIONS.md` for every place the UI is an honest
subset of the spec because of a genuine backend gap, rather than fabricated
data.

---

## Tech Stack

| Layer | Choice |
|---|---|
| Framework | React 19 + Vite 6 + TypeScript |
| Routing | React Router v7 (role-guarded, lazy-loaded) |
| Styling | Tailwind CSS v4 + shadcn-style primitives |
| Animation | Framer Motion |
| Server state | TanStack Query v5 |
| Forms | React Hook Form + Zod |
| HTTP | Axios (single client, interceptor-based auth) |
| Charts | Recharts |
| Icons | Lucide React |
| Toasts | Sonner |
| PWA | vite-plugin-pwa (Workbox) |

## Architecture

Feature-based: every business capability (`auth`, `jobs`, `applications`,
`ai`, `notifications`, `profile`, `recruiter-*`, `admin`) owns its own
`components/ hooks/ pages/ schemas/ services/ types/`. Components never call
Axios directly — always `component → hook (TanStack Query) → service →
apiClient → API Gateway → backend service`. See `02_FRONTEND_ARCHITECTURE.md`
for the full rules this was built against.

```
src/
├── app/            # AppProviders, query-client
├── components/     # Shared UI: ui/ primitives, layout/, common/
├── constants/       # routes, nav-config, env
├── contexts/        # Auth, Theme
├── features/         # auth, jobs, applications, ai, notifications,
│                      # profile, recruiter-company, recruiter-jobs,
│                      # recruiter-applications, recruiter-profile, admin
├── layouts/          # Guest, Auth, Candidate, Recruiter, Admin, Error
├── pages/            # Route-level pages per role
├── routes/           # AppRouter, ProtectedRoute, PublicRoute, RoleRoute
├── services/         # apiClient (Axios instance + interceptors)
├── styles/           # Tailwind entry, design tokens
└── types/            # Shared cross-feature types
```

## Features

**Guest** — landing page, public job search/filter/pagination, login,
register (role toggle), forgot/reset password, email verification.

**Candidate** — dashboard, profile (education/experience/skills/
certifications/portfolio), resume manager, job search + saved jobs, apply +
application timeline, AI tools (resume analysis, ATS score, job
recommendations, cover letter generator, skill gap analysis), notifications,
settings.

**Recruiter** — dashboard, company profile + branding, job CRUD, applicant
review + hiring pipeline, AI tools (candidate recommendations, job
description generator, interview question generator), notifications,
settings.

**Admin** — dashboard, users, companies, jobs overview.

**Cross-cutting** — light/dark/system theme, global search, debounced
search everywhere it's needed, reusable pagination, skeleton loading (no
full-page spinners), custom empty states with CTAs, 401/403/404/500 error
pages, notification drawer with unread badge.

## Installation

```bash
git clone <this-repo>
cd frontend
cp .env.example .env.local   # adjust VITE_API_BASE_URL if your gateway runs elsewhere
npm install
npm run dev                  # http://localhost:5173
```

Requires the backend stack (API Gateway + microservices) running — see the
backend repo's own README/`docker-compose.yml`. This frontend never talks to
a microservice directly; every request goes through the API Gateway.

## Environment Variables

| Variable | Default | Purpose |
|---|---|---|
| `VITE_API_BASE_URL` | `http://localhost:8080/api/v1` | API Gateway base URL. Baked into the build — changing it requires a rebuild, not just a restart. |
| `VITE_APP_NAME` | `AI Job Portal` | Display name used in a few UI strings. |
| `VITE_APP_SHORT_NAME` | `AJP` | Short name (PWA manifest, mobile labels). |

See `.env.example`.

## Available Scripts

```bash
npm run dev        # dev server, http://localhost:5173
npm run build       # tsc -b && vite build → dist/
npm run preview     # serve the production build locally
npm run lint         # eslint .
```

Verified clean as of this pass: `npm install`, `npx tsc -b` (0 errors),
`npm run build` (production build succeeds, service worker + manifest
generated), `npm run lint` (0 errors — 3 pre-existing `react-refresh`
warnings on files that intentionally export both a component and a small
constant/context, which is safe and common in this codebase).

## PWA

Configured via `vite-plugin-pwa`: installable (manifest + icons + our own
install-prompt banner, since we suppress the browser's default one), app
shell + static assets are precached by a generated service worker for
offline load, dark/light theme colors. **API responses are deliberately
excluded from any cache** — job listings, applications, and AI results must
always be fetched fresh; only the compiled JS/CSS/HTML/icons are
precached.

## SEO

Full meta description, Open Graph, and Twitter Card tags in `index.html`,
plus `robots.txt` (disallowing authenticated app routes, allowing public
job listings) and a `sitemap.xml` for the public routes. The canonical URL
and OG image currently point at a placeholder domain
(`aijobportal.example.com`) — replace with your real production domain
before deploying.

## Docker

```bash
docker build -t ai-job-portal-frontend .
docker run -p 8080:80 ai-job-portal-frontend
```

Multi-stage build: `node:22-alpine` builds the Vite bundle, then
`nginx:1.27-alpine` serves the static `dist/` output. `nginx.conf` handles
SPA client-side routing fallback, gzip, immutable caching for hashed
assets, and no-cache headers for `sw.js`/`manifest.webmanifest` so PWA
updates propagate immediately. Override the API URL at build time:

```bash
docker build --build-arg VITE_API_BASE_URL=https://api.yourdomain.com/api/v1 -t ai-job-portal-frontend .
```

## Deployment

- **Docker / Nginx** — use the Dockerfile above behind any reverse proxy.
- **Vercel / Netlify** — standard Vite SPA deploy (`npm run build`, publish
  `dist/`, SPA rewrite rule `/* → /index.html`); set `VITE_API_BASE_URL` as
  a build-time environment variable in the platform's dashboard.

In every case, `VITE_API_BASE_URL` must point at a publicly reachable API
Gateway — this app never bypasses the gateway to call a microservice
directly.

## Known Limitations

See `KNOWN_BACKEND_LIMITATIONS.md` — every item there is a real backend gap
(missing endpoint, missing field, no pagination on a given endpoint, etc.),
each with the exact fix needed. Nothing in this frontend fabricates data to
paper over a gap; where the backend can't support a piece of the spec, the
UI honestly shows a smaller, real feature instead.

## License

Portfolio / educational project. No license granted for commercial reuse
without permission from the author.
