<div align="center">

# AI Job Portal — Frontend

![AI Job Portal Banner](assets/banner.png)

**Production-Ready • AI-Powered • Responsive • Role-Based • Progressive Web App**

A modern frontend for an AI-powered recruitment platform built with **React 19**, **TypeScript**, and **Vite**, enabling intelligent hiring workflows for Candidates, Recruiters, and Administrators.

<br>

[![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.7-3178C6?logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-6-646CFF?logo=vite&logoColor=white)](https://vitejs.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4-06B6D4?logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)
[![TanStack Query](https://img.shields.io/badge/TanStack_Query-5-FF4154?logo=reactquery&logoColor=white)](https://tanstack.com/query)
[![Docker](https://img.shields.io/badge/Docker-Nginx-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Vercel](https://img.shields.io/badge/Vercel-Deployed-000000?logo=vercel)](https://vercel.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](./LICENSE)

</div>

---

## Project Overview

The **AI Job Portal Frontend** is a production-ready React application that provides a complete recruitment experience for **Candidates**, **Recruiters**, and **Administrators**. It communicates exclusively with the backend through a single API Gateway and delivers AI-powered resume analysis, ATS scoring, intelligent job matching, recruiter dashboards, and modern responsive user interfaces.

---

## Project Highlights

| | | |
|---|---|---|
| **React 19** | **TypeScript** | **Vite 6** |
| Tailwind CSS 4 | TanStack Query | React Router 7 |
| React Hook Form | Zod Validation | Axios |
| PWA Ready | Docker | Vercel |
| Dark Mode | Responsive UI | Role-Based Access |

---

## Live Demo

| Resource | URL |
|----------|-----|
| 🌐 Frontend | `https://ai-job-portal-opal-iota.vercel.app/` |
| 🚀 Backend API-Gateway | `https://ample-grace-production-0968.up.railway.app` |
| 📦 Backend Repository | `https://github.com/PRAHLAD09-dev/ai-job-portal/tree/main/ai-job-portal-backend` |
| 🎨 Frontend Repository | `https://github.com/PRAHLAD09-dev/ai-job-portal/tree/main/ai-job-portal-frontend` |

---

# Screenshots

| Feature | Preview |
|----------|---------|
| Landing Page | ![](assets/homepage.png) |
| Login | ![](assets/login.png) |
| Candidate Dashboard | ![](assets/candidate-dashboard.png) |
| Recruiter Dashboard | ![](assets/recruiter-dashboard.png) |
| Admin Dashboard | ![](assets/admin-dashboard.png) |
| Resume Analysis | ![](assets/resume-analysis.png) |
| ATS Score | ![](assets/ats-score.png) |
| AI Job Match | ![](assets/ai-job-match.png) |
| Company Profile | ![](assets/company-profile.png) |
| Mobile View | ![](assets/mobile-view.png) |
| Dark Theme | ![](assets/dark-theme.png) |

---

# Core Features

### 🔐 Authentication

- JWT Authentication
- Email Verification
- Forgot & Reset Password
- Protected Routes
- Role-Based Authorization

### 👨‍💼 Candidate

- Resume Management
- AI Resume Analysis
- ATS Score
- Skill Gap Analysis
- AI Job Recommendations
- Saved Jobs
- Job Alerts
- Application Tracking

### 🏢 Recruiter

- Company Management
- Job Posting
- Candidate Management
- AI Candidate Ranking
- AI Job Description Generation

### 🛡️ Admin

- User Management
- Company Verification
- Job Moderation
- Platform Dashboard

### 🤖 AI Features

- Resume Analysis
- ATS Scoring
- Skill Gap Analysis
- Job Matching
- Candidate Matching
- Cover Letter Generator
- Interview Question Generator

---

# Technology Stack

| Category | Technologies |
|----------|--------------|
| **Frontend** | React 19, TypeScript, Vite |
| **Styling** | Tailwind CSS 4, Radix UI, shadcn/ui |
| **State Management** | TanStack Query |
| **Forms** | React Hook Form, Zod |
| **Routing** | React Router 7 |
| **Charts** | Recharts |
| **Animations** | Framer Motion |
| **Icons** | Lucide React |
| **Notifications** | Sonner |
| **HTTP Client** | Axios |
| **Deployment** | Docker, Nginx, Vercel |

---

# Frontend Architecture

The frontend follows a **Feature-Based Modular Architecture**, separating UI components, business logic, routing, API communication, and reusable utilities. State synchronization is handled using **TanStack Query**, while forms are validated with **React Hook Form** and **Zod**. All backend communication flows through the **API Gateway**, keeping the frontend independent of individual microservices.

```mermaid
flowchart LR

    User["👤 User"]
        --> Router["React Router"]

    Router
        --> Pages["Pages"]

    Pages
        --> Components["Reusable Components"]

    Pages
        --> Hooks["Custom Hooks"]

    Hooks
        --> Query["TanStack Query"]

    Query
        --> Axios["Axios Client"]

    Axios
        --> Gateway["API Gateway"]

    Components
        --> UI["shadcn/ui + Tailwind CSS"]

    Components
        --> Forms["React Hook Form + Zod"]

    Pages
        --> Charts["Recharts"]

    Pages
        --> Toast["Sonner"]

    Pages
        --> Motion["Framer Motion"]
```

---

# Application Flow

```mermaid
flowchart LR

    User["User"]
        --> Login["Authentication"]

    Login
        --> JWT["JWT Access Token"]

    JWT
        --> Dashboard["Role Dashboard"]

    Dashboard
        --> Features["Application Features"]

    Features
        --> API["REST APIs"]

    API
        --> Gateway["Spring Cloud Gateway"]

    Gateway
        --> Services["Backend Microservices"]
```

---

# Role-Based Navigation

```mermaid
flowchart LR

    User["Authenticated User"]

    User --> Candidate["Candidate"]

    User --> Recruiter["Recruiter"]

    User --> Admin["Admin"]

    Candidate --> CandidatePages["Resume • Jobs • ATS • Applications"]

    Recruiter --> RecruiterPages["Company • Jobs • Candidates"]

    Admin --> AdminPages["Dashboard • Moderation • Analytics"]
```

---

# State Management

```mermaid
flowchart LR

    UI["React Components"]

    UI --> Query["TanStack Query"]

    Query --> Cache["Client Cache"]

    Query --> Axios["Axios"]

    Axios --> API["Backend APIs"]

    API --> Query

    Query --> UI
```

---

# API Communication

```mermaid
flowchart LR

    React["React Frontend"]

    React
        --> Axios["Axios"]

    Axios
        --> Gateway["API Gateway"]

    Gateway
        --> Auth["Auth"]

    Gateway
        --> Candidate["Candidate"]

    Gateway
        --> Recruiter["Recruiter"]

    Gateway
        --> Job["Job"]

    Gateway
        --> Application["Application"]

    Gateway
        --> AI["AI"]

    Gateway
        --> Notification["Notification"]

    Gateway
        --> Admin["Admin"]
```

---

# Folder Structure

```text
ai-job-portal-frontend/
│
├── public/
│
├── src/
│   ├── assets/
│   ├── components/
│   │   ├── common/
│   │   ├── ui/
│   │   ├── forms/
│   │   ├── layout/
│   │   └── shared/
│   │
│   ├── pages/
│   │   ├── auth/
│   │   ├── candidate/
│   │   ├── recruiter/
│   │   ├── admin/
│   │   └── public/
│   │
│   ├── hooks/
│   ├── services/
│   ├── lib/
│   ├── utils/
│   ├── types/
│   ├── routes/
│   ├── context/
│   ├── constants/
│   └── App.tsx
│
├── Dockerfile
├── nginx.conf
├── vite.config.ts
├── package.json
└── README.md
```

---

# Frontend Design Principles

| Principle | Description |
|-----------|-------------|
| Component-Based | Reusable UI components with clear separation of concerns |
| Feature-Based Structure | Code organized by business features instead of file types |
| API-First | All data fetched from backend REST APIs through API Gateway |
| Server State | TanStack Query manages caching, synchronization, and background refetching |
| Form Validation | React Hook Form with Zod schema validation |
| Responsive Design | Mobile-first layouts built using Tailwind CSS |
| Role-Based UI | Separate dashboards and navigation for Candidate, Recruiter, and Admin |
| Type Safety | End-to-end TypeScript support across the application |

---

# Authentication Flow

The frontend uses **JWT-based authentication** with role-based routing. After successful login, the access token is securely stored and automatically attached to protected API requests using Axios interceptors. Unauthorized requests trigger token refresh or redirect users back to the login page.

```mermaid
flowchart LR

    User["👤 User"]
        --> Login["Login Form"]

    Login
        --> Auth["Auth Service"]

    Auth
        --> JWT["JWT Access Token"]

    JWT
        --> Storage["Secure Storage"]

    Storage
        --> Axios["Axios Interceptor"]

    Axios
        --> Gateway["API Gateway"]

    Gateway
        --> Services["Protected APIs"]

    Services
        --> UI["Role-Based Dashboard"]
```

---

# AI Features Flow

```mermaid
flowchart TB

    Upload[" Resume Upload"]
        --> Cloudinary["Cloudinary"]

    Cloudinary
        --> PDF[" PDF Text Extraction"]

    PDF
        --> Gemini[" Google Gemini AI"]

    Gemini
        --> AI["Structured AI Analysis"]

    AI --> ATS["ATS Score"]
    AI --> Resume["Resume Analysis"]
    AI --> SkillGap["Skill Gap"]
    AI --> Roadmap["Learning Roadmap"]
    AI --> Match["AI Job Matching"]

    AI --> Redis[" Redis Cache"]

    AI --> Kafka["Kafka"]

    ATS --> Dashboard[" Candidate Dashboard"]
    Resume --> Dashboard
    SkillGap --> Dashboard
    Roadmap --> Dashboard
    Match --> Dashboard
```

---

# Performance Optimizations

| Optimization | Implementation |
|--------------|----------------|
| Code Splitting | React Lazy + Dynamic Imports |
| Data Caching | TanStack Query |
| Background Refetch | Automatic Query Synchronization |
| Optimistic Updates | Instant UI Feedback |
| Image Optimization | Cloudinary |
| Form Validation | React Hook Form + Zod |
| API Reuse | Shared Axios Instance |
| Error Handling | Global Error Boundary |
| Loading States | Skeleton UI & Loaders |
| Responsive Design | Mobile-First Tailwind CSS |

---

# Progressive Web App

```mermaid
flowchart LR

Browser["Browser"]

Browser
    --> Manifest["Web App Manifest"]

Browser
    --> ServiceWorker["Service Worker"]

Manifest
    --> Install["Installable App"]

ServiceWorker
    --> Offline["Offline Support"]

Offline
    --> Faster["Better User Experience"]
```

---

# Docker Deployment

```mermaid
flowchart LR

Source["React Source"]

Source
    --> Build["Vite Production Build"]

Build
    --> Docker["Docker Image"]

Docker
    --> Nginx["Nginx"]

Nginx
    --> Vercel["Production Deployment"]
```

---

# Environment Variables

| Variable | Description |
|----------|-------------|
| `VITE_API_BASE_URL` | Backend API Gateway URL |
| `VITE_APP_NAME` | Application Name |
| `VITE_APP_VERSION` | Frontend Version |

Example:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_NAME=AI Job Portal
VITE_APP_VERSION=1.0.0
```

---

# Local Development

### Clone Repository

```bash
git clone https://github.com/PRAHLAD09-dev/ai-job-portal-frontend.git
cd ai-job-portal-frontend
```

### Install Dependencies

```bash
npm install
```

### Start Development Server

```bash
npm run dev
```

Application:

```
http://localhost:5173
```

---

# Production Build

```bash
npm run build
```

Preview Production Build

```bash
npm run preview
```

---

# Deployment

| Platform | Status |
|----------|--------|
| Vercel | ✅ Production |
| Docker | ✅ Supported |
| Nginx | ✅ Configured |

Deployment Process

```text
GitHub
      │
      ▼
Vercel Build
      │
      ▼
Vite Production Build
      │
      ▼
Static Assets
      │
      ▼
Global CDN
```

---
# Current Features

| Module | Features |
|---------|----------|
| 🔐 Authentication | Login, Registration, JWT Authentication, Email Verification, Password Reset, Protected Routes |
| 👤 Candidate | Resume Upload, Profile Management, ATS Score, Resume Analysis, Skill Gap Analysis, AI Job Matching, Saved Jobs, Job Applications |
| 🏢 Recruiter | Company Profile, Job Management, Candidate Management, AI Candidate Ranking, AI Job Description Generation |
| 🛡️ Admin | Dashboard, User Management, Company Verification, Job Moderation, Platform Analytics |
| 🤖 AI | Resume Analysis, ATS Scoring, Job Matching, Candidate Matching, Learning Roadmap, Cover Letter Generator, Interview Question Generator |
| 📱 Frontend | Responsive Design, Dark Mode, Progressive Web App, Role-Based UI |

---

# Future Roadmap

| Area | Planned Improvements |
|------|----------------------|
| Authentication | Multi-Factor Authentication (MFA) |
| AI | OCR Support for Scanned Resumes |
| Notifications | Real-time WebSocket Notifications |
| Collaboration | Recruiter Notes & Team Collaboration |
| Performance | Advanced Lazy Loading & Bundle Optimization |
| Analytics | Enhanced Recruiter Insights & Reports |
| Mobile | React Native Mobile Application |

---

# Engineering Highlights

| Area | Implementation |
|------|----------------|
|  Architecture | Feature-Based React Architecture with reusable components and modular organization |
|  Performance | TanStack Query caching, lazy loading, code splitting, optimized rendering |
|  Security | JWT Authentication, Role-Based Access Control, Protected Routes |
|  AI Integration | ATS Scoring, Resume Analysis, Job Matching, Candidate Ranking, AI Content Generation |
|  User Experience | Mobile-First Responsive Design, Dark Mode, Accessible Components |
|  Deployment | Dockerized frontend deployed on Vercel with production-ready configuration |

---

# Project Structure Summary

| Category | Technology |
|----------|------------|
| Framework | React 19 |
| Language | TypeScript |
| Build Tool | Vite |
| Styling | Tailwind CSS 4 |
| Components | shadcn/ui + Radix UI |
| Routing | React Router 7 |
| State Management | TanStack Query |
| Forms | React Hook Form + Zod |
| Charts | Recharts |
| Notifications | Sonner |
| Deployment | Docker + Nginx + Vercel |

---

# Contributing

Contributions are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Push to your fork.
5. Open a Pull Request.

---

# License

This project is licensed under the **MIT License**.

---

# Author

## Prahlad Bhakat

Computer Science graduate passionate about building scalable **Java Spring Boot**, **React**, **Microservices**, and **AI-powered** applications.

### Connect with Me

[![GitHub](https://img.shields.io/badge/GitHub-181717?logo=github&logoColor=white)](https://github.com/PRAHLAD09-dev)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?logo=linkedin&logoColor=white)](https://linkedin.com/in/prahlad-bhakat)
[![Email](https://img.shields.io/badge/Email-D14836?logo=gmail&logoColor=white)](mailto:prahladbhakat05@gmail.com)


---

# If you found this project helpful

⭐ Star the repository

🍴 Fork the repository

📝 Share your feedback

---

<div align="center">

## Thank You ❤️

Made with **React**, **TypeScript**, and lots of ☕

</div>