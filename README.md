# Events Management API

This is an academic project to learn Test Driven Development (TDD) using Java and Spring Boot. It demonstrates basic domain modeling, DTO mapping, validations, and integration testing with a simple events management system.

## Features
- Create and manage events
- Domain validations using records and value objects (VOs)
- Integration tests with MockMvc
- Basic error handling and response mapping

## Warning

**This project does not handle timezones.** All date and time operations assume the local system timezone without adjustments.

## Requirements

- Java 21 (or later)
- Maven 3.x

## Credits
Special thanks to **Ph.D. Professor Isidro** for his guidance and support throughout this project.

## How to Run

1. **Clone the repository:**
```bash
  git clone <repository-url>
```

2. **Build the project:**
```bash
  mvn clean install
```

3. **Run the application:**
```bash
  mvn spring-boot:run
```
## Running Tests

To run all tests, simply execute:
```bash
  mvn spring-boot:run
```