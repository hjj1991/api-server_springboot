# MAIN MODULE GUIDE

## OVERVIEW
`src/main` is the runtime payload: Kotlin app code in `kotlin/` and deploy-time resources in `resources/`.

## READ WHEN
- You edit production behavior, app wiring, profiles, static docs, or templates.

## WHERE TO LOOK
| Task | Location | Notes |
|------|----------|-------|
| Spring app entrypoint | `src/main/kotlin/com/hjj/apiserver/ApiServerApplication.kt` | bootstrap only |
| Security/auth stack | `src/main/kotlin/com/hjj/apiserver/common`, `src/main/kotlin/com/hjj/apiserver/config` | JWT/OAuth2/filter/config |
| Domain and use-case flow | `src/main/kotlin/com/hjj/apiserver/domain`, `src/main/kotlin/com/hjj/apiserver/application` | ports + services |
| HTTP adapters | `src/main/kotlin/com/hjj/apiserver/adapter/input/web` | controllers and request/response mapping |
| Persistence adapters | `src/main/kotlin/com/hjj/apiserver/adapter/out/persistence` | entities/repositories/converters |
| Runtime profiles | `src/main/resources/application.yml` | local/prod split + redis + datasource |
| OAuth providers | `src/main/resources/application-oauth.yml` | kakao/naver and redirect config |
| OpenAPI docs (runtime) | `/v3/api-docs`, `/swagger-ui/index.html` | served by springdoc |

## CONVENTIONS
- Prefer small, boundary-safe changes: port interface first, then service, then adapter/controller.
- Keep exception types under `common/exception` and map them through `ExceptionControllerAdvice`.
- Prefer runtime OpenAPI from springdoc over checked-in static swagger assets.

## ANTI-PATTERNS
- Do not hardcode production credentials in `application.yml` or `application-oauth.yml`.
- Do not bypass service/use-case ports by wiring persistence details into web adapters.
- Do not reintroduce static OpenAPI copy pipelines when springdoc runtime docs are the source of truth.

## COMMANDS
```bash
./gradlew bootRun
./gradlew build
```
