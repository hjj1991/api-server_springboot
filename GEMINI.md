
# Gemini Project Analysis (Deep Dive)

## 1. Project Overview
This project is a comprehensive personal finance management API server built with Kotlin and Spring Boot. It allows users to manage household account books, track income/expenses, and browse financial products. Key features include user management, shared account books, detailed purchase logging, and integration with financial product information.

## 2. Key Technologies
- **Language**: Kotlin `2.0.0`
- **Framework**: Spring Boot `3.2.2`
- **Build Tool**: Gradle (with Kotlin DSL `build.gradle.kts`)
- **Data Access**:
  - Spring Data JPA
  - **QueryDSL `5.0.0`**: For type-safe database queries.
  - **P6Spy**: For SQL query logging in development environments.
- **Database**: MySQL / MariaDB
- **Authentication**: 
  - Spring Security
  - JWT (JSON Web Token) with Refresh Token rotation strategy.
  - OAuth2 for social logins.
- **Caching**: 
  - Spring Cache Abstraction
  - **Redis**: As the primary cache store.
  - **Caffeine**: As a local in-memory cache.
- **API Documentation**:
  - **Spring REST Docs**: To generate API documentation from tests.
  - **OpenAPI 3**: The generated REST Docs are converted to an OpenAPI 3 specification.
  - **Swagger UI**: To visualize and interact with the generated OpenAPI 3 spec.

## 3. Architecture
- **Pattern**: Hexagonal Architecture (Ports and Adapters)
  - `src/main/kotlin/com/hjj/apiserver/domain`: Contains the core business logic and entity models. This is the innermost layer.
  - `src/main/kotlin/com/hjj/apiserver/application`: Defines application services (use cases) and ports (interfaces for driving and driven adapters).
  - `src/main/kotlin/com/hjj/apiserver/adapter`: Contains implementations of the ports.
    - `adapter/input/web`: Driving adapters (e.g., Spring MVC controllers) that handle incoming requests.
    - `adapter/out/persistence`: Driven adapters (e.g., JPA repositories) that interact with the database.
- **Common Utilities**: The `common` package holds cross-cutting concerns like exception handling, JWT logic, security filters, and argument resolvers.

## 4. Database Schema & Domain Model
- **`tb_user`**: Manages user profiles, credentials, and roles (`ROLE_USER`, `ROLE_ADMIN`). Supports both standard registration and OAuth2 providers.
- **`tb_refresh_token`**: Stores refresh tokens for JWT-based authentication, enabling persistent sessions.
- **`tb_account_book` & `tb_account_book_user`**: Core of the application. An `account_book` can be shared among multiple users (`account_book_user`) with different roles (`OWNER`, `MEMBER`).
- **`tb_purchase`**: The transaction log. Each purchase is linked to an account book, category, and card, and has a `purchase_type` (INCOME or OUTLAY).
- **`tb_category` & `tb_card`**: Supporting entities for organizing and detailing purchases.
- **`tb_financial_company`, `tb_financial_product`, `tb_financial_product_option`**: Entities for storing and managing information about external financial products (e.g., savings, deposits), suggesting features for financial product discovery.

## 5. Testing Strategy
- **Frameworks**: JUnit 5, Mockito, and Spring Boot's testing support.
- **Integration Testing**: The project heavily relies on **Testcontainers** for high-fidelity integration tests. It spins up real Docker containers for:
  - `MariaDB` / `MySQL`
  - `Redis`
- This ensures that tests run in an environment that is almost identical to production, verifying the interaction with the database and cache.
- **API Documentation Tests**: Tests in the `controller` package are also used to generate API documentation snippets via Spring REST Docs.

## 6. Core Commands
- **Build Project**: `./gradlew build` (This also runs tests and generates API documentation)
- **Run Application**: `./gradlew bootRun`
- **Run Tests**: `./gradlew test`

## 7. API Documentation Workflow
1.  Tests annotated with `@AutoConfigureRestDocs` are executed (`./gradlew test`).
2.  **Spring REST Docs** captures request/response details and creates AsciiDoc snippets in `build/generated-snippets`.
3.  The `openapi3` Gradle task converts these snippets into a single `openapi3.yaml` file.
4.  The `build` process copies this `openapi3.yaml` to `src/main/resources/static/swagger-ui/`.
5.  When the application is running, the API documentation can be accessed via the integrated Swagger UI, typically at `/swagger-ui/index.html`.
