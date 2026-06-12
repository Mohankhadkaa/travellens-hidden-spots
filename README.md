# TravelLens - Hidden Places & Food Spots

A Spring Boot web application for discovering and sharing hidden travel destinations and local food spots.

## Main Technologies

- Java 21
- Spring Boot 3.2
- Spring Web
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- Maven

## Requirements

- Java 21+
- Maven 3.8+
- PostgreSQL 15+
- Git

## Database Setup

1. Install PostgreSQL and create a database:

```sql
CREATE DATABASE travellens_db;
```

2. Update `src/main/resources/application.properties` with your PostgreSQL credentials:

```properties
spring.datasource.username=postgres
spring.datasource.password=YOUR_POSTGRES_PASSWORD
```

## Local Setup

```bash
# Clone the repository
git clone https://github.com/your-username/travellens-hidden-spots.git
cd travellens-hidden-spots

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## How to Run

```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

## Project Status

This is a foundation project with the home page set up. Features such as user registration, login, post creation, and image upload will be added in subsequent iterations.
