# Personal Finance Manager API

Spring Boot 3 REST API for the **Personal Finance Manager** assignment: user authentication, transactions, categories, savings goals, and financial reports.

**Author:** [Agrim236](https://github.com/Agrim236)

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Security (session-based authentication, `JSESSIONID` cookie)
- Spring Data JPA
- H2 (local & Render), MySQL (optional production profile)
- JUnit 5, Mockito, JaCoCo (≥80% line coverage on services/utilities)

## Quick Start (Local)

**Requirements:** Java 17+, Maven (or use `./mvnw`)

```bash
cd personal-finance-manager   # or this project root
./mvnw spring-boot:run
```

Uses the `local` profile (H2 in-memory). API base URL:

```text
http://localhost:5003/api
```

### Run unit tests + coverage

```bash
./mvnw verify
```

Coverage report: `target/site/jacoco/index.html`

### Run official E2E tests (assignment script)

From Git Bash or WSL (needs `bash`, `curl`, `bc`):

```bash
bash financial_manager_tests.sh http://localhost:5003/api
```

> **Note:** The script defaults to port `8080`; this app uses **5003** unless you set `PORT=8080`.

## Authentication

1. `POST /api/auth/register` — create account (201)
2. `POST /api/auth/login` — returns `JSESSIONID` session cookie (200)
3. Send the cookie on all protected endpoints, or use header `X-Session-Id: <sessionId>`
4. `POST /api/auth/logout` — invalidate session

## API Endpoints

| Area | Method | Endpoint |
|------|--------|----------|
| Auth | POST | `/api/auth/register`, `/api/auth/login`, `/api/auth/logout` |
| Transactions | POST/GET/PUT/DELETE | `/api/transactions`, `/api/transactions/{id}` |
| Categories | GET/POST/DELETE | `/api/categories`, `/api/categories/{name}` |
| Goals | POST/GET/PUT/DELETE | `/api/goals`, `/api/goals/{id}` |
| Reports | GET | `/api/reports/monthly/{year}/{month}`, `/api/reports/yearly/{year}` |

Query params for transactions: `startDate`, `endDate`, `category`, `categoryId`, `type`.

## MySQL (optional)

```bash
# Create database
CREATE DATABASE skye;

# Run with MySQL profile
set SPRING_PROFILES_ACTIVE=mysql
set DB_PASSWORD=your_password
./mvnw spring-boot:run
```

Update credentials in `src/main/resources/application.yml`.

## Deploy on Render (free)

1. Push this repo to GitHub (see [SUBMISSION.md](SUBMISSION.md)).
2. On [Render](https://render.com), create a **Web Service** from the repo (Docker).
3. Set environment variable: `SPRING_PROFILES_ACTIVE=render`
4. After deploy, run E2E tests:

```bash
bash financial_manager_tests.sh https://YOUR-SERVICE.onrender.com/api
```

5. Take a screenshot of **86/86 tests passed** for your submission email.

## Architecture

```text
Controller → Service → Repository → Entity
```

- DTOs separate API contracts from JPA entities
- `@ControllerAdvice` global exception handler
- Per-user data isolation (`user_id` on all owned resources)
- Goal progress: (total income − total expenses) since each goal’s `startDate`
- Transaction `date` is immutable after create

## Design Decisions

- **Sessions:** Spring Security HTTP session; cookie name `JSESSIONID`
- **Categories:** Global defaults + per-user custom; duplicate names rejected (409)
- **JSON amounts:** Two decimal places; zero progress/net savings as `0` where required by E2E tests
- **Default category delete:** Returns 403 Forbidden (per assignment spec)

## Submission Checklist

- [x] Source code & README
- [x] Unit tests with JaCoCo ≥80% coverage (`./mvnw verify`)
- [x] `financial_manager_tests.sh` included
- [x] Render deployment config (`Dockerfile`, `render.yaml`)
- [ ] Public GitHub repo URL
- [ ] Live Render URL + E2E screenshot emailed to instructor

See [SUBMISSION.md](SUBMISSION.md) for GitHub push steps.
