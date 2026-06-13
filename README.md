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
- Cloudinary

## Requirements

- Java 21+
- Maven 3.8+

## Local Setup

```bash
# Clone the repository
git clone https://github.com/Mohankhadkaa/travellens-hidden-spots.git
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

- **default** (H2 in-memory database) - for development
- **postgres** (PostgreSQL) - for production: `mvn spring-boot:run -Dspring-boot.run.profiles=postgres`

## Cloudinary Image Uploads

Photo uploads use Cloudinary. Set these environment variables before uploading images:

```bash
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

If a user selects an image and Cloudinary is not configured, the app shows a clear error instead of saving the post without the photo.

## Render Deployment

This repo includes `Dockerfile` and `render.yaml` for Render. The blueprint creates and links a Render Postgres database automatically.

When Render prompts for secret values, add:

```bash
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```
