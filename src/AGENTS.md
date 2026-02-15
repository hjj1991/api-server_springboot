# SRC KNOWLEDGE BASE

## OVERVIEW
`src/` contains all application/runtime code and runtime-adjacent generated artifacts.

## READ WHEN
- Any change touches Kotlin application code, runtime resources, or test configuration.

## STRUCTURE
```text
src/
|- main/      # production app code + resources
|- test/      # test resources (kotlin tests currently sparse)
|- querydsl/  # generated querydsl outputs
```

## WHERE TO LOOK
| Task | Location | Notes |
|------|----------|-------|
| Main API behavior | `src/main/kotlin/com/hjj/apiserver` | adapters/application/domain live here |
| Profile/env config | `src/main/resources/application.yml` | local + prod split documents defaults |
| OAuth2/provider setup | `src/main/resources/application-oauth.yml` | registration/provider map |
| Test infra config | `src/test/resources/application.yml` | Testcontainers postgresql jdbc URL |
| Querydsl generated files | `src/querydsl/java` | generated output; avoid hand edits |

## CONVENTIONS
- Keep hexagonal boundaries: `adapter` -> `application(port/service)` -> `domain`.
- Controllers stay in `adapter/input/web`; DB integration stays in `adapter/out/persistence`.
- Use command/usecase naming in `application/port/input/*/command` and service implementations in `application/service/*`.

## ANTI-PATTERNS
- Do not add direct repository/entity access inside controller packages.
- Do not treat `src/querydsl/java` as hand-authored source.
- Do not put environment-specific secrets directly into new resource files.

## COMMANDS
```bash
./gradlew test
./gradlew build
```
