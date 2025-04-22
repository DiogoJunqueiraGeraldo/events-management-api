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

## ✅ MVP Infra Checklist

> Production readiness starts here. If you're missing items below, you're not running in prod — you're cosplaying.

### Logging & Observability
- [x] Structured logging (JSON format)
- [x] Exception handling with stacktrace logged
- [x] Centralized error format (`code`, `message`, `issues`, etc.)

### Metrics & Tracing
- [x] Health check endpoint (`/actuator/health`)
- [x] Metrics endpoint (`/actuator/metrics`)

### Reliability Patterns
- [x] Timeout for all external calls (HTTP, DB, etc.)
- [x] Graceful shutdown (SIGTERM handling)

### Configuration & Secrets
- [x] No hardcoded values
- [x] External config via profiles (`application.yml`, `application-prod.yml`)
- [x] Secrets via environment variables or secret manager

### Security Basics
- [x] No sensitive info exposed in error responses

### Infrastructure & Packaging
- [x] Dockerfile using lightweight base image
- [x] Build is reproducible (e.g., via Maven wrapper)

### Data & Persistence
- [x] Schema migrations via Flyway or Liquibase
- [x] Migrations are versioned and idempotent

---

**Optional but appreciated:**
- [x] Docker Compose or Helm chart for local/test deploy


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

