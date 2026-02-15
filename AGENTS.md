## SESSION ONBOARDING (READ FIRST)

When starting a new session in this repository, apply these rules first:

1. Read `AGENTS.md` and `docs/DEVELOPMENT_GUIDELINES.md` before making changes.
2. Enforce core coding rules:
   - No magic values in code (use constants or config properties).
   - Environment-dependent values must be centralized in config with env overrides.
   - API error responses must follow RFC9457 `ProblemDetail` with `ErrConst` as source of truth.
3. Follow hexagonal boundaries strictly:
   - `adapter -> application(port/service) -> domain`
   - Do not leak adapter or persistence types into application ports/services.
4. For every code change, run related tests first, then final verification with `./gradlew test` or `./gradlew build`.
5. Treat generated artifacts as build-managed unless explicitly requested to modify.

# PROJECT KNOWLEDGE BASE

**Generated:** 2026-02-15 15:09:51 +0900
**Commit:** 60e85f7
**Branch:** main

## OVERVIEW
Kotlin + Spring Boot API server for personal finance features (user, account book, purchases, financial products).
Architecture follows Hexagonal style (domain/application/adapter), with security, JWT/OAuth2, Redis cache, and generated API docs.

## STRUCTURE
```text
./
|- src/                  # application code, resources, tests, querydsl output
|- db/                   # local PostgreSQL container data mount + db config
|- docs/                 # project workflow guidance
|- .github/workflows/    # CI + Docker publish + helm chart update
|- build.gradle.kts      # single-module Gradle build and task wiring
|- docker-compose.yaml   # local postgresql + app runtime
```

## WHERE TO LOOK
| Task | Location | Notes |
|------|----------|-------|
| App entry | `src/main/kotlin/com/hjj/apiserver/ApiServerApplication.kt` | Spring Boot main |
| Architecture rules | `docs/DEVELOPMENT_GUIDELINES.md` | Hexagonal workflow is explicit |
| Runtime config | `src/main/resources/application.yml` | profiles local/prod + redis + datasource |
| OAuth2 config | `src/main/resources/application-oauth.yml` | provider/client settings |
| Security and auth helpers | `src/main/kotlin/com/hjj/apiserver/common` | filters, JWT, argument resolver, exceptions |
| CI and image release | `.github/workflows/ci.yml` | release branch trigger only |
| Local infra | `docker-compose.yaml`, `db/postgres-data`, `db/initdb.d` | postgresql container mounts |

## CODE MAP
LSP symbols for Kotlin are unavailable in this environment (`kotlin-lsp` not installed).
Use path-level map below for navigation:

| Area | Location | Role |
|------|----------|------|
| Controllers | `src/main/kotlin/com/hjj/apiserver/adapter/input/web` | inbound HTTP adapters |
| Persistence adapters | `src/main/kotlin/com/hjj/apiserver/adapter/out/persistence` | DB-facing adapters |
| Use case ports | `src/main/kotlin/com/hjj/apiserver/application/port` | input/output boundaries |
| Use case services | `src/main/kotlin/com/hjj/apiserver/application/service` | business orchestration |
| Core entities/enums | `src/main/kotlin/com/hjj/apiserver/domain` | domain model |

## CONVENTIONS
- Follow explicit Hexagonal sequence from `docs/DEVELOPMENT_GUIDELINES.md`: input port -> output port -> service -> adapter -> controller wiring.
- Runtime code is Kotlin-first; Gradle Kotlin DSL is used (`build.gradle.kts`).
- API docs are served by `springdoc-openapi` at runtime (`/v3/api-docs`, `/swagger-ui/index.html`).
- Test profile uses Testcontainers JDBC (`jdbc:tc:postgresql`) from `src/test/resources/application.yml`.

## ANTI-PATTERNS (THIS PROJECT)
- Do not bypass port boundaries by calling persistence logic directly from controllers.
- Do not edit generated/runtime DB engine files under `db/data/*` as source artifacts.
- Do not assume CI runs on all branches: workflow is scoped to `releases-**`.
- Do not place long global instructions in child AGENTS files; child docs should stay delta-focused.

## UNIQUE STYLES
- Mixed operational assets in-repo: application source + mounted DB state for local docker.
- API docs are served by `springdoc-openapi` at runtime (`/v3/api-docs`, `/swagger-ui/index.html`).
- Security stack is hybrid: form/JWT/OAuth2/resource-server patterns coexist.

## COMMANDS
```bash
./gradlew bootRun
./gradlew test
./gradlew build
docker compose up -d postgresql
docker compose up -d
```

## NOTES
- `system.properties` and Gradle toolchain are aligned to OpenJDK 25.
- Use nearest AGENTS.md precedence: check child docs before editing files in those directories.
