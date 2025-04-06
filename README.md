# 🎟️ Events Management API

An academic project built to explore **Test-Driven Development (TDD)** in Java using Spring Boot.  
It simulates a minimal event management system, while showcasing patterns like **domain-driven modeling**, **value
objects**, and **integration testing** — without slipping into tutorial boilerplate.

> This repo isn't production-ready — but it's aiming higher than "just works".

## ✨ Features

- ✅ Create and manage events
- 🔐 Domain validations with **value objects** and **records**
- 🧪 Integration tests using **MockMvc**
- ⚠️ Basic error handling with **custom exceptions** and proper HTTP mapping
- 🧼 Clean DTO ↔️ Domain mapping

## ⚠️ Warning

**This project does not handle timezones.** All date and time operations assume the local system timezone without
adjustments.

## 🛠️ Possible Overengineering (a.k.a. Learning Goals)

There are many improvements that could be made. Not all of them are strictly necessary (or even justified) for a project
of this size — but exploring them would be a great way to dive into small yet important architectural concepts:

## ✅ MVP Infra Checklist

> Production readiness starts here. If you're missing items below, you're not running in prod — you're cosplaying.

### Logging & Observability
- [x] Structured logging (JSON format)
- [ ] MDC (`requestId`, `userId`, `correlationId`)
- [ ] Exception handling with stacktrace logged
- [ ] Centralized error format (`code`, `message`, `timestamp`, etc.)

### Metrics & Tracing
- [ ] Micrometer metrics exported (e.g., Prometheus)
- [ ] Distributed tracing (OpenTelemetry, Sleuth)
- [ ] Health check endpoint (`/actuator/health`)
- [ ] Metrics endpoint (`/actuator/metrics`)

### Reliability Patterns
- [ ] Timeout for all external calls (HTTP, DB, etc.)
- [ ] Retry with backoff
- [ ] Circuit breaker (e.g., Resilience4j)
- [ ] Graceful shutdown (SIGTERM handling)

### Configuration & Secrets
- [ ] No hardcoded values
- [ ] External config via profiles (`application.yml`, `application-prod.yml`)
- [ ] Secrets via environment variables or secret manager

### Security Basics
- [ ] HTTPS enforced
- [ ] Authentication implemented (JWT, OAuth2, etc.)
- [ ] No sensitive info exposed in error responses

### Infrastructure & Packaging
- [ ] Dockerfile using lightweight base image
- [ ] Build is reproducible (e.g., via Maven wrapper)
- [ ] Readiness and liveness probes for orchestration
- [ ] Container shuts down gracefully

### Data & Persistence
- [ ] Schema migrations via Flyway or Liquibase
- [ ] Migrations are versioned and idempotent

---

**Optional but appreciated:**
- [ ] CI pipeline (GitHub Actions, GitLab CI, etc.)
- [ ] Static code analysis (SonarQube, PMD, etc.)
- [ ] Docker Compose or Helm chart for local/test deploy
- [ ] Coverage reports that actually matter


## ⚙️ Requirements

- Java 21+
- Maven 3.x+

## 🙏 Credits

Special thanks to **Ph.D. Professor Isidro** for the guidance and academic support behind this project.

## 🚀 How to Run

1. **Clone the repository:**
   ```bash
    git clone https://github.com/DiogoJunqueiraGeraldo/events-management-api.git
    cd events-management-api
   ```

2. **Build the project:**
   ```bash
    mvn clean install
   ```

3. **Run the application:**
   ```bash
    mvn spring-boot:run
   ```

## 🧪 Running Tests

To execute all tests:

```bash
  mvn test
```

> Yes, we test our code. It's kind of the point here.

