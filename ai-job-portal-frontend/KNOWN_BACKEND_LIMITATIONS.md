# Known Backend Limitations — DAY03 (Recruiter & Admin Module)

This document lists every place where the frontend's behavior is
constrained by a genuine gap in the backend API surface, verified
directly against the backend source (`ai-job-portal-backend.zip`). No
frontend code fabricates data or simulates an endpoint that doesn't
exist — where a capability is missing, the UI shows a real subset of
data plus a clean empty state, or hides the action entirely.

Re-check this list every time the backend zip is updated; several
items may already be resolved (e.g. Admin module gaps were closed in
the July 8 backend update that added `admin-service`).

---

## 1. Recruiter team management (Company → Team tab)

**Spec asked for:** Team Members list, Invite Recruiters, Edit Recruiters, Remove Recruiters.

**Backend reality:** `recruiter-service`'s `RecruiterController` only
exposes:
- `GET /recruiter/profile` — the calling recruiter's own profile
- `PUT /recruiter/profile` — update the calling recruiter's own profile

There is no endpoint to list other recruiters in the same company, no
invite endpoint, and no remove endpoint. A company can currently only
ever have the one recruiter who registered it (`CreateCompanyRequest`
creates exactly one owner recruiter).

**Frontend behavior:** `RecruiterCompanyPage` → **Team** tab shows the
current recruiter's own real profile (name, email, role badge) fetched
from `GET /recruiter/profile`, followed by a generic empty state
("No other team members — inviting and managing additional recruiters
isn't available yet."). No invite form, no fake teammates list.

**To unblock:** add `GET /recruiter/team`, `POST /recruiter/team/invite`,
`PUT /recruiter/team/{id}`, `DELETE /recruiter/team/{id}` (or similar)
to `recruiter-service`, then build out `features/recruiter-company`'s
Team tab the same way Locations/Social Links were built.

---

## 2. Recruiter's own job list — search/filter is page-local only

**Spec asked for:** Search, Filters, Pagination, Sorting on the
recruiter's job list.

**Backend reality:** `JobController`'s recruiter-scoped endpoint
(`GET /jobs/me`) only accepts a `Pageable` (`page`, `size`, `sort`) —
it has no `keyword`, `status`, or other filter query params, unlike
the public `GET /jobs/search` endpoint which has a full
`JobSearchCriteria`.

**Frontend behavior:** `RecruiterJobsPage` fetches one real page of
the recruiter's jobs (with working pagination and sorting, since
`sort` is genuinely supported), then applies keyword/status filtering
client-side **only within that already-loaded page** — the search box
is explicitly labeled "Search jobs on this page..." so it doesn't
overclaim full-dataset search. No fabricated results.

**To unblock:** add `keyword`/`status` query params to
`GET /jobs/me` in `job-service`, mirroring `JobSearchCriteria`, then
swap the client-side filter for a server-side one in
`useMyCompanyJobs`.

---

## 3. Admin platform analytics — snapshot only, no historical trends

**Spec asked for:** User Growth, Company Growth, Job Growth,
Application Growth, Hiring Analytics (implying time-series/trend
charts).

**Backend reality:** `admin-service`'s `DashboardController`
(`GET /admin/dashboard`) returns `DashboardResponse`, which aggregates
current **point-in-time totals** from every service
(`UserStatisticsResponse`, `CompanyStatisticsResponse`,
`JobStatisticsResponse`, `ApplicationStatisticsResponse`,
`AiStatisticsResponse`, `NotificationStatisticsResponse`) plus a
`recentActivity` audit log feed. None of these carry a date dimension
or historical series — there's no "users per day/week/month" data
anywhere in the backend.

**Frontend behavior:** `AdminDashboardPage` shows every one of these
statistics as real current totals (Total Users, Candidates,
Recruiters, Companies, Active Jobs, Applications, company verification
breakdown, AI usage, recent activity). No line/trend charts are drawn
because there is no time-series data to plot — drawing one would mean
fabricating history.

**To unblock:** add a time-series endpoint (e.g.
`GET /admin/dashboard/trends?metric=users&range=30d`) backed by either
periodic snapshots or a proper analytics store, then add a trend chart
component using the existing Recharts setup.

---

## 4. Large tables are paginated, not virtualized

**Spec asked for:** "Virtualized Tables where required" (Performance
section).

**Backend reality:** N/A — this one is a frontend implementation gap,
not a backend gap. Every list (jobs, applications, users, companies)
is served paginated (typically 10–20 rows/page) via Spring Data
`Pageable`, so no single request can return an unbounded row count
today.

**Frontend behavior:** All admin/recruiter tables use the existing
`Pagination` component rather than a virtualized list — pagination
alone is sufficient at current page sizes and avoids pulling in a new
dependency (e.g. `react-window`) for lists that never render more than
~20 DOM rows at once.

**Revisit if:** any endpoint starts returning very large unpaginated
arrays (none currently do), or a page size much larger than ~50 is
introduced.

---

## 5. Resume text isn't auto-extracted (Day 04 — AI Module)

**Spec asked for:** Upload Resume → Analyze Resume, with the AI reading the uploaded file directly.

**Backend reality:** `candidate-service`'s `ResumeController` stores only
the Cloudinary file URL (`ResumeResponse.fileUrl`) — it never extracts
plain text from the PDF/DOCX. `ai-service`'s `AnalyzeResumeRequest`
(`POST /ai/resume/analyze`, `POST /ai/resume/score`) requires
`resumeText` to be sent directly in the request body; no service in
this backend performs that extraction.

**Frontend behavior:** `ResumeAnalysisPanel` lets the candidate pick one
of their already-uploaded resumes (for `resumeUrl`) and paste the resume's
text content into a textarea (for `resumeText`), then calls the real
`/ai/resume/analyze` and `/ai/resume/score` endpoints with both. No text
is fabricated or "auto-read" from the PDF client-side.

**To unblock:** add a text-extraction step (e.g. Apache Tika) to
`candidate-service`'s upload flow, store the extracted text on `Resume`,
and expose it via `ResumeResponse`, or add a dedicated extraction
endpoint the frontend can call before analysis.

## 6. No AI history / AI usage log endpoint for candidates or recruiters

**Spec asked for:** "AI Usage History" on the AI Dashboard, and an
"AI History" screen listing past analyses, cover letters, ATS reports,
interview questions, and job descriptions, with search/pagination/delete.

**Backend reality:** `ai-service` persists `ResumeAnalysis`,
`InterviewQuestion`, and `JobRecommendation` entities, but exposes no
list/history endpoint for any of them to the owning user — only
`GET /ai/resume/analyze/latest` (single most recent record) and
`GET /ai/interview/questions/{jobId}` (per-job, not a full history).
Cover letters and job descriptions aren't persisted at all. The only
aggregate AI stats endpoint (`AdminAiUsageService`) is admin-only.

**Frontend behavior:** The AI Dashboard shows the latest resume
analysis, current job-recommendation count, and skill-gap summary —
all real, single most-recent-state data — instead of a history list.
No AI History page/tab was built, since there's no data to list.

**To unblock:** add `GET /ai/resume/analyze/history`,
`GET /ai/interview/questions/history`, and persist+list cover letters
and job descriptions per user, each paginated.

## 7. Skill gap has no learning roadmap or course recommendations

**Spec asked for:** Learning Roadmap and Recommended Courses alongside
Existing/Missing Skills on the Skill Gap Analysis screen.

**Backend reality:** `SkillGapResponse` (`GET /ai/skills/gap`) returns
only `currentSkills`, `missingSkills`, and `careerSuggestions` — no
roadmap or course fields exist.

**Frontend behavior:** `SkillGapAnalysis` renders exactly those three
real fields (plus a derived current/missing ratio bar computed
client-side from the two real arrays); no roadmap or course list is
shown.

**To unblock:** extend `SkillGapResponse` with `learningRoadmap` and
`recommendedCourses` once the AI prompt/service produces them.

## 8. Job recommendations have no server-side search/filter/pagination

**Spec asked for:** Pagination, Filters, Search, Sorting on Job
Recommendations.

**Backend reality:** `POST /ai/jobs/recommend` takes no request body or
query params and returns a plain array — no `Pageable`, no filter
criteria, unlike `job-service`'s `/jobs/search`.

**Frontend behavior:** `JobRecommendations` fetches the full
recommendation batch once and filters it client-side by title/company
only, within that batch — no fabricated pages or fake filter options.

**To unblock:** add pagination/filter query params to
`RecommendationController`'s job-recommend endpoint.

---

## Resolved in the July 8, 2026 backend update

For reference, these were previously listed here as gaps and are now
fully wired to real endpoints:

- Admin Dashboard statistics — `admin-service` `DashboardController`
- Admin Users list + enable/disable/delete — `admin-service`
  `UserManagementController` (delete restricted to `SUPER_ADMIN`,
  matching the backend's `@PreAuthorize`)
- Admin Companies list + verify/reject/suspend — `admin-service`
  `CompanyManagementController`
- Admin Jobs list + remove/restore/feature/unfeature — `admin-service`
  `JobManagementController`
- `SUPER_ADMIN` role support in `RoleName`, `RoleRoute`, and
  registration-role exclusion
