# TravelLens - Hidden Places & Food Spots

A Spring Boot web application for discovering and sharing hidden travel destinations and local food spots.

## Main Technologies

- Java 21
- Spring Boot 3.2
- Spring Web
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL / H2
- Maven

## Requirements

- Java 21+
- Maven 3.8+

## Local Setup

```bash
# Clone the repository
git clone https://github.com/your-username/travellens-hidden-spots.git
cd travellens-hidden-spots

# Build the project
mvn clean package

# Run the application (H2 in-memory by default, no database setup needed)
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

## Default Admin Credentials

- Email: `admin@travellens.com`
- Password: `AdminPass123!`

## Profiles

- **default** (H2 in-memory database) — for development
- **postgres** (PostgreSQL) — for production: `mvn spring-boot:run -Dspring-boot.run.profiles=postgres`
