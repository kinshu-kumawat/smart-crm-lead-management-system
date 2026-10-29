# Smart CRM & Lead Management System — Full Stack Java with AI

A modern, high-performance, enterprise-grade Customer Relationship Management (CRM) and Lead Management platform powered by **Spring Boot 3 (Java 21)**, **MySQL 8+**, **React 18**, **Tailwind CSS**, and **Google Gemini AI**.

---

## Table of Contents
1. [Overview & Key Features](#overview--key-features)
2. [Technology Stack](#technology-stack)
3. [Architecture](#architecture)
4. [Folder Structure](#folder-structure)
5. [Prerequisites](#prerequisites)
6. [Windows Setup & Installation](#windows-setup--installation)
7. [Environment Configuration](#environment-configuration)
8. [Database Setup & Migrations](#database-setup--migrations)
9. [Running the Application Locally](#running-the-application-locally)
10. [AI Integration & Graceful Fallback](#ai-integration--graceful-fallback)
11. [Testing & Quality Verification](#testing--quality-verification)
12. [Production Build & Single-Domain Deployment](#production-build--single-domain-deployment)
13. [Known Limitations](#known-limitations)

---

## 1. Overview & Key Features

### 📊 Real-Time Executive Dashboard
- Dynamic KPIs: Total Leads, New Leads, Qualified, Converted, Lost, and Conversion Rate.
- Financial Metrics: Pipeline Value and Converted Revenue.
- Responsive charts powered by Recharts:
  - Lead Generation & Conversion Trends (Monthly Area Chart)
  - Pipeline Stage Distribution (Bar Chart)
  - Lead Source Breakdown (Donut/Pie Chart)
- Real-time follow-ups due today and overdue alerts.

### 👥 Comprehensive Lead Management
- Full CRUD operations with server-side pagination, search, status filtering, and sorting.
- Detailed lead profile with contact information, deal value, priority badges, and notes.
- CSV export capability for reporting and spreadsheet analytics.
- Deletion confirmation dialogs and server-side validation.

### 📌 Interactive Kanban Sales Pipeline
- Visual stage-by-stage Kanban board (`NEW`, `CONTACTED`, `QUALIFIED`, `PROPOSAL`, `NEGOTIATION`, `CONVERTED`, `LOST`).
- Fast stage progression updates synchronized instantly with MySQL backend.
- Highlighting lead deal values, priority levels, and assigned sales representatives.

### 🤖 AI-Powered Capabilities (Google Gemini)
- **AI Lead Scoring**: Real-time evaluation (0–100 score) based on deal size, pipeline velocity, and activity engagement, with human-readable rationale and next-action recommendation.
- **AI Follow-up Message Generator**: Intelligent draft generation for Email, WhatsApp, and LinkedIn outreach tailored by tone (`PROFESSIONAL`, `CASUAL`, `PERSUASIVE`, `URGENT`). Messages are never auto-sent; drafts are fully reviewable and editable.
- **AI Lead Summarizer**: Synthesizes complete lead history and activities into actionable briefing notes.
- **AI Sales Assistant Chatbot**: Interactive sales assistant providing pipeline insights, overdue reminders, and coaching advice based on real authenticated CRM data.
- **Graceful Fallback**: If Gemini API key is omitted or provider times out, the CRM seamlessly activates built-in deterministic heuristics without crashing or degrading user experience.

### 📅 Activities & Follow-ups Timeline
- Full audit log of sales activities: Calls, Emails, Meetings, and Notes.
- Follow-up scheduling with calendar date picker, due alerts, overdue indicators, and one-click completion.

### 🔒 Enterprise Role-Based Access Control (RBAC)
- Built on Spring Security and JJWT with HMAC-SHA256 password encryption via BCrypt.
- Two distinct roles:
  - `ADMIN`: Full system administration, user management, and global metrics.
  - `SALES_REP`: Lead tracking, activity logging, and assigned account workflow.

---

## 2. Technology Stack

| Layer | Technologies & Versions |
|---|---|
| **Frontend** | React 18.3, Vite 5.4, Tailwind CSS 3.4, React Router 6.26, Axios 1.7, Recharts 2.13, Lucide React 0.453 |
| **Backend** | Java 21 LTS, Spring Boot 3.3.4, Spring Security, Spring Data JPA, Hibernate ORM 6.5, JJWT 0.12.6, Bean Validation |
| **Database** | MySQL 8.0+ / MariaDB 11.4+, Flyway 10.x Database Migrations |
| **API & Docs** | RESTful JSON APIs, Springdoc OpenAPI 2.6 / Swagger UI 3.0, Spring Actuator |
| **AI Provider** | Google Gemini (Gemini 1.5 Flash) via secure backend REST integration |

---

## 3. Architecture

```
[ Browser / Mobile Device ]
            │
            ▼ (HTTP / HTTPS on Single Port 8080)
┌────────────────────────────────────────────────────────┐
│               Spring Boot 3 Application                │
│                                                        │
│  ┌─────────────────────────┐  ┌──────────────────────┐ │
│  │     Static SPA Layer    │  │   REST API Layer     │ │
│  │ (Compiled React 18 App) │  │  (/api/* Controllers) │ │
│  └─────────────────────────┘  └──────────────────────┘ │
│                                          │             │
│                        ┌─────────────────┴──────────┐  │
│                        │    Service & Security      │  │
│                        │ (JWT, BCrypt, Validation)  │  │
│                        └─────────────────┬──────────┘  │
│                                          │             │
│                        ┌─────────────────┴──────────┐  │
│                        │      Hibernate / JPA       │  │
│                        │     Flyway Migrations      │  │
│                        └─────────────────┬──────────┘  │
└──────────────────────────────────────────┼─────────────┘
                                           │
                    ┌──────────────────────┴──────────────────────┐
                    ▼                                             ▼
        [ MySQL / MariaDB Database ]                  [ Google Gemini AI API ]
```

---

## 4. Folder Structure

```
smart-crm-lead-management-system/
├── client/                                 # React + Vite Frontend
│   ├── src/
│   │   ├── api/                            # Axios API service clients
│   │   ├── components/                     # Reusable UI components & modals
│   │   ├── context/                        # Auth context & state management
│   │   ├── pages/                          # CRM application views
│   │   ├── App.jsx                         # App routes & ProtectedRoute guards
│   │   ├── index.css                       # Tailwind directives & styles
│   │   └── main.jsx                        # React root entry
│   ├── package.json
│   ├── tailwind.config.js
│   └── vite.config.js                      # Configured to build into Spring Boot static dir
├── server/                                 # Spring Boot 3 Java Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smartcrm/
│   │   │   │   ├── config/                 # Security, JWT, CORS, OpenAPI, SPA filter
│   │   │   │   ├── controller/             # REST controllers (/api/auth, /api/leads, etc.)
│   │   │   │   ├── dto/                    # Request and response models
│   │   │   │   ├── entity/                 # JPA database entities
│   │   │   │   ├── exception/              # Global error handling
│   │   │   │   ├── repository/             # Spring Data JPA repositories
│   │   │   │   └── service/                # Business logic & Gemini AI client
│   │   │   └── resources/
│   │   │       ├── db/migration/           # Flyway SQL migration scripts (V1__init_schema.sql)
│   │   │       ├── static/                 # Embedded React build output
│   │   │       └── application.properties  # Application configuration
│   │   └── test/                           # JUnit 5 & MockMvc test suites
│   └── pom.xml                             # Maven project dependencies
├── docs/                                   # Architectural & API documentation
├── .env.example                            # Configuration environment template
├── .gitignore
└── README.md
```

---

## 5. Prerequisites

Before running the application on Windows:
- **Java Development Kit (JDK)**: JDK 21 or higher. Verify with `java -version`.
- **Apache Maven**: Version 3.8 or 3.9+. Verify with `mvn -version`.
- **Node.js**: Version 18 or 20 LTS. Verify with `node -v` and `npm -v`.
- **MySQL / MariaDB**: MySQL 8.0+ or MariaDB 10.4+. Running on port 3306.

---

## 6. Windows Setup & Installation

### Step 1: Clone the Repository
```powershell
git clone https://github.com/KinshukPrasad09/smart-crm-lead-management-system.git
cd smart-crm-lead-management-system
```

### Step 2: Install Frontend Dependencies
```powershell
cd client
npm install
cd ..
```

### Step 3: Configure Environment Variables
Copy `.env.example` to `.env` or set environment variables in your terminal:
```powershell
cp .env.example .env
```

---

## 7. Environment Configuration

| Variable | Description | Default Local Value |
|---|---|---|
| `PORT` | Spring Boot HTTP server port | `8080` |
| `DB_URL` | MySQL JDBC connection string | `jdbc:mysql://localhost:3306/smart_crm?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USERNAME` | MySQL database username | `crm_user` |
| `DB_PASSWORD` | MySQL database password | `crm_pass123` |
| `JWT_SECRET` | 32+ character signing key | `smartCrmSuperSecretKeyThatIsAtLeast32BytesLongForHmacSha256Security!` |
| `JWT_EXPIRATION_MS` | Token lifespan in ms | `86400000` (24 Hours) |
| `ADMIN_EMAIL` | Default administrator username | `admin@smartcrm.com` |
| `ADMIN_PASSWORD` | Default administrator password | `Admin@123CRM!` |
| `GEMINI_API_KEY` | Google Gemini API key | *(Optional)* |
| `GEMINI_MODEL` | Gemini AI model identifier | `gemini-1.5-flash` |

---

## 8. Database Setup & Migrations

Create the database in MySQL:
```sql
CREATE DATABASE smart_crm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'crm_user'@'localhost' IDENTIFIED BY 'crm_pass123';
GRANT ALL PRIVILEGES ON smart_crm.* TO 'crm_user'@'localhost';
FLUSH PRIVILEGES;
```

**Flyway Migrations:**
Upon launching the Spring Boot backend, Flyway automatically detects the database and executes `V1__init_schema.sql` to build the required tables, foreign keys, indexes, and constraints.

---

## 9. Running the Application Locally

### Method A: Single-Command Production Mode (Recommended)
Build the frontend and bundle it directly into the Spring Boot executable jar:

```powershell
# 1. Build React frontend into server/src/main/resources/static
cd client
npm run build
cd ..

# 2. Package Spring Boot executable JAR
cd server
mvn package -DskipTests
cd ..

# 3. Launch application
java -jar server/target/smart-crm-backend-1.0.0.jar
```
Open **`http://localhost:8080`** in your browser.

### Method B: Development Mode (Vite Hot-Reload)
Run backend and frontend independently for active coding:

**Terminal 1 (Backend):**
```powershell
cd server
mvn spring-boot:run
```

**Terminal 2 (Frontend with Hot Module Replacement):**
```powershell
cd client
npm run dev
```
Open **`http://localhost:5173`** (Vite automatically proxies `/api/*` to `http://localhost:8080`).

---

## 10. AI Integration & Graceful Fallback

To enable live Google Gemini AI generation:
1. Obtain an API key from Google AI Studio (`https://aistudio.google.com/`).
2. Set the environment variable:
   ```powershell
   $env:GEMINI_API_KEY = "AIzaSy..."
   ```
3. Restart the backend application.

**Heuristic Fallback Engine:**
If no API key is supplied, or if the Gemini API service is temporarily unreachable, the CRM backend dynamically detects this and activates built-in deterministic heuristics:
- Scores leads based on pipeline velocity, deal size, and activity density.
- Drafts context-aware outreach emails, WhatsApp messages, and LinkedIn messages.
- Answers sales questions in the chat assistant based on real database records.
- Indicates `FALLBACK_HEURISTIC` in the response metadata so users know live Gemini is unconfigured.

---

## 11. Testing & Quality Verification

Run the full automated test suite:

```powershell
cd server
mvn test
```

The automated integration tests verify:
- System health status (`/api/health`)
- User registration and JWT authentication
- Authenticated lead creation and validation
- AI Lead Scoring and evaluation
- Dashboard summary analytics calculation

---

## 12. Default Credentials

For quick initial setup and evaluation:

| Role | Email | Password |
|---|---|---|
| **System Admin** | `admin@smartcrm.com` | `Admin@123CRM!` |
| **Sales Representative** | `alex.sales@smartcrm.com` | `Rep@123CRM!` |

---

## 13. License

This project is licensed under the MIT License.
