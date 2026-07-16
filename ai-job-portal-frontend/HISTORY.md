# AI Job Portal — Frontend (Day 01: Foundation & Authentication)

Built strictly against:
- `01_UI_DESIGN.md`, `02_FRONTEND_ARCHITECTURE.md`, `03_MODULES.md`,
  `04_BACKEND_INTEGRATION.md`, `05_FRONTEND_ROADMAP.md`, `DAY01_FRONTEND_FOUNDATION.md`
- The **actual backend source code** (`auth-service`), not assumptions.

## Backend contract source (verified by reading the code directly)

| Frontend file | Backend source it mirrors |
|---|---|
| `src/features/auth/types/index.ts` | `auth-service/.../auth/dto/request/*.java` |
| `src/types/auth.ts` | `AuthResponse.java`, `UserResponse.java`, `RoleName.java`, `AccountStatus.java` |
| `src/types/api.ts` | `common/.../response/ApiResponse.java`, `ApiError.java` |
| `src/features/auth/services/auth.service.ts` | `AuthController.java` (every endpoint, same path, same method) |
| `src/features/auth/schemas/auth.schema.ts` | Jakarta `@Pattern`/`@Size`/`@Email` constraints on each request DTO |
| `src/services/api-client.ts` | `JwtAuthenticationFilter.java` (`Authorization: Bearer <token>`), `RefreshTokenRequest.java` |

Base URL: `VITE_API_BASE_URL` → API Gateway (`http://localhost:8080/api/v1`), matching
`config-repo/api-gateway.yml` route `Path=/api/v1/auth/**` → `lb://AUTH-SERVICE`.
CORS in the gateway allows `http://localhost:5173` with credentials — matches Vite's default dev port used here.

No endpoint URL, field name, or DTO shape was invented or renamed.

## Setup (run these on your machine — this sandbox has no network access)

```bash
cd frontend
cp .env.example .env.local   # adjust VITE_API_BASE_URL if your gateway runs elsewhere
npm install
npm run dev                  # http://localhost:5173
npm run build                # production build
npm run lint
```

## What's implemented (Day 01 scope)

- Vite + React 19 + TypeScript, path alias `@/*`, ESLint + Prettier
- Feature-based folder structure exactly per `02_FRONTEND_ARCHITECTURE.md`
- Tailwind CSS v4 with design tokens from `01_UI_DESIGN.md` (Indigo/Emerald/Sky/Success/Warning/Danger)
- Light/Dark/System theme, persisted in localStorage
- Animated splash screen (fade/scale/fade, ~2.2s)
- Global providers: TanStack Query, Theme, Auth, Router, Sonner toasts
- Layouts: Guest, Auth, Candidate, Recruiter, Admin, Error — with Sidebar (collapsible),
  top Navbar, mobile bottom nav, theme switch, notification bell, user menu
- Auth pages: Login, Register (role toggle), Forgot Password, Reset Password, Verify Email —
  all React Hook Form + Zod, validation mirrored from the backend DTOs exactly
- Axios client with request interceptor (attaches JWT) and response interceptor
  (auto refresh-token-on-401 with request queueing, then forced logout if refresh fails)
- Protected/Public/Role route guards; 401/403/404/500 custom error pages
- Skeleton component + PageLoader (Suspense fallback) instead of blocking spinners
- Route-level code splitting via `React.lazy`

## Verification performed in this sandbox (no network available for `npm install`)

- ✅ Every `@/...` and relative import resolves to a real file (scripted check, 77 files, 0 errors)
- ✅ Braces/parens/brackets balanced across every `.ts`/`.tsx` file
- ✅ Every package imported in source is declared in `package.json`
- ✅ Manually cross-checked every DTO/endpoint/validation rule against the backend source
- ⚠️ Could **not** run `npm install`, `tsc -b`, `npm run build`, or `npm run dev` here — this
  environment's network is disabled. Please run the Setup commands above on your machine;
  if you hit any TypeScript/build error, share the output and it'll be fixed immediately.

## Not in scope for Day 01 (per `05_FRONTEND_ROADMAP.md`)

Candidate/Recruiter/Admin dashboards beyond the shell, Job/Application/AI/Notification
service integration — these are placeholder pages, wired up in Phases 3–4.

## Day 04 — AI Features & Smart Experience

Adds the full candidate-facing AI module (`/candidate/ai`): AI Dashboard,
Resume Analysis, ATS Score, Job Recommendations, Cover Letter Generator,
and Skill Gap Analysis — all calling real `ai-service` endpoints through
the API Gateway (no dummy data). Recruiter AI tools (Job Description,
Interview Questions, Candidate Recommendations) and the Notification
drawer/preferences were already wired up in earlier phases and are
unchanged.

See `KNOWN_BACKEND_LIMITATIONS.md` items 5–8 for every place the AI
module's UI is a real (not fabricated) subset of the Day 04 spec because
of a genuine backend gap — resume text extraction, AI history, skill-gap
roadmap/courses, and recommendation pagination.

Verified in this pass: `npm install`, `npx tsc -b` (0 errors), `npm run
build` (production build succeeds), `npm run lint` (0 errors, 3
pre-existing warnings unrelated to this change).

## Day 07 — ATS & Candidate Experience Enhancement

Wires the frontend up to the DAY11 backend ATS additions
(`job-service`, `application-service`, `recruiter-service`). No new
pages, routes, or microservices — every change extends an existing
feature module or page, per `DAY07_FRONTEND_ATS_ENHANCEMENT.md`.

- **Saved Jobs** — new `SavedJobCard` shows saved date and AI match %
  (sourced from the candidate's real job-recommendation scores, shown
  only when the AI service has actually scored that job — never
  estimated). `SavedJobsPage` rewritten to use it.
- **Viewed by Recruiter** — new shared `ViewedBadge`. Wired into the
  candidate's application detail page + application cards, and the
  recruiter's application list + detail modal (which already triggers
  the view via `GET /recruiter/applications/{id}`).
- **Apply Methods** — `JobResponse`/`JobSummaryResponse` now carry
  `applyMethod`/`externalApplyUrl`. `JobDetailsPage`'s Apply button
  adapts automatically: Easy Apply opens the resume-picker dialog,
  Quick Apply opens a one-click dialog with no resume picker, External
  Apply redirects to the company's site in a new tab. Recruiters choose
  the method (with conditional external-URL validation) in `JobForm`.
- **Company Location Map** — new `leaflet` + `react-leaflet`
  dependency and a shared `CompanyLocationMap` component (OpenStreetMap
  tiles, zoom/marker/popup, HQ highlighted). Recruiters set
  latitude/longitude per location in `CompanyLocationsManager` (with a
  live preview map); candidates see the map on the public
  `CompanyProfilePage`.
- **Recruiter Dashboard** — new `recruiter-dashboard` feature module
  calling `GET /recruiter/dashboard` (a single aggregated call).
  Dashboard now shows a "Recent applications" table with AI Match score
  + viewed status per row, and a "Saved job statistics" card ranking
  jobs by bookmark count.
- **Candidate Dashboard** — added "Viewed by Recruiters" and "AI
  Match" stat cards (the latter shows the candidate's latest ATS score,
  or "Not analyzed yet" — never fabricated).

Verified in this pass: `npm install` (leaflet/react-leaflet/@types/leaflet
added successfully), `npx tsc -b` (0 errors), `npm run build` (production
build succeeds, incl. the new `CompanyLocationMap` chunk), `npm run lint`
(0 errors, same 3 pre-existing warnings, unrelated to this change).

