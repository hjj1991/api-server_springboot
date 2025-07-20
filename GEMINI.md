# Gemini Project Analysis

## Project Overview
This project is an API server built with Kotlin and Spring Boot. It appears to be a personal finance or account book management application.

## Key Technologies
- **Language**: Kotlin
- **Framework**: Spring Boot
- **Build Tool**: Gradle (with Kotlin DSL `build.gradle.kts`)
- **Architecture**: Hexagonal Architecture (Ports and Adapters)
  - `domain`: Core business logic and models.
  - `application`: Use cases and application services (ports).
  - `adapter`: Implementations of ports (e.g., web controllers, persistence adapters).
- **Database**: MySQL/MariaDB (inferred from `docker-compose.yaml` and test containers like `TestMariaDBContainer`).
- **Data Access**: Spring Data JPA, QueryDSL.
- **Authentication**: Spring Security with JWT (JSON Web Token) and OAuth2.
- **Caching**: Redis (inferred from `CacheConfig.kt` and `RedisProperties.kt`).
- **Testing**: JUnit 5, Mockito, Spring REST Docs.
- **API Documentation**: Swagger (OpenAPI) and Spring REST Docs.

## Core Commands
- **Build Project**: `./gradlew build`
- **Run Application**: `./gradlew bootRun`
- **Run Tests**: `./gradlew test`

## Project Structure Highlights
- `src/main/kotlin/com/hjj/apiserver`: Main application source code.
- `src/main/resources`: Configuration files (`application.yml`), static assets, and templates.
- `src/test`: Test code, including unit and integration tests.
- `db/initdb.d/create_table.sql`: Database schema initialization script for the local Docker environment.
- `docker-compose.yaml`: Defines services for local development, likely including the MySQL database.

## Authentication & Authorization
- **JWT**: `common/JwtProvider.kt` is likely the central component for creating and validating JWTs.
- **OAuth2**: `handler/OAuth2SuccessHandler.kt` and `config/WebSecurityConfiguration.kt` manage the OAuth2 login flow.
- **Security Configuration**: `config/WebSecurityConfiguration.kt` is the main entry point for security rules.
