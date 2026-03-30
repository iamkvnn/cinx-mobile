# AGENTS.md

## Scope
- Android app module is `:app` (Java, minSdk 31, target/compile 36) from `settings.gradle.kts` and `app/build.gradle.kts`.
- Networking targets local backend by default: `http://10.0.2.2:9090/` and `ws://10.0.2.2:9090/...` (`RetrofitClient`, `WebSocketService`, `network_security_config.xml`).

## Architecture Map (What Talks to What)
- UI is Activity-centric in `app/src/main/java/com/app/cinx/activity` (no Fragments/ViewModels yet).
- API layer is Retrofit interfaces in `app/src/main/java/com/app/cinx/api`; DTO contracts are in `api/dto`.
- State is mostly in-memory singletons:
  - Auth/session: `TokenManager` + `UserManager`
  - Cart/order demo data: `CartRepository`, `OrderRepository`
- Navigation between bottom tabs is centralized via `NavHelper.setupNavigation(...)`.

## Key Flows You Must Preserve
- Login flow: `LoginActivity` -> `AuthService.login` -> `TokenManager.saveTokens(...)` + `UserManager.login(...)` -> open `MainActivity`.
- Auth headers: `AuthInterceptor` auto-adds `Authorization` except `/auth/login`, `/auth/register`, `/auth/refresh-token`.
- Token refresh: `TokenAuthenticator` performs synchronous refresh on 401 and retries request; clears tokens on refresh failure.
- Course discovery/detail: `DiscoveryActivity` paginates with `CourseService.getCourses(page,size,sort)`; `CourseDetailActivity` consumes intent extras (`COURSE_ID`, `COURSE_TITLE`, price fields).
- Notifications combine REST unread count + STOMP websocket (`NotificationService`, `WebSocketService`).

## Project Conventions (Observed)
- Keep features in Activity + Adapter pairs (example: `DiscoveryActivity` + `RecommendedAdapter`).
- Prefer singleton access (`RetrofitClient.getInstance()`, `...Repository.getInstance()`) over DI.
- API response parsing expects wrappers: `ApiResponse<T>` for object endpoints, `ApiListResponse<T>` + `PageMeta` for list endpoints.
- Existing code mixes English identifiers with Vietnamese UI text/toasts; preserve current language per screen.
- Bottom-nav screens should call `NavHelper.setupNavigation(this, <activeTabId>)` (`MainActivity`, `DiscoveryActivity`, `MyLearningActivity`, `ProfileActivity`).

## Build, Test, and Local Dev Commands
```powershell
.\gradlew.bat -q projects
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
```
- Wrapper and `:app` module are present/working (`gradlew -q projects` verified).
- Tests are currently template-only (`ExampleUnitTest`, `ExampleInstrumentedTest`), so behavior validation is mostly manual UI run-through.

## Integration Notes
- Some services still declare explicit `@Header("Authorization")` while interceptor already injects token; follow existing method signature when extending a service.
- Network security currently permits cleartext to emulator hostnames only (`10.0.2.2`, `localhost`).

## Safe Change Checklist for Agents
- Update both API interface and matching DTOs when backend contract changes.
- If adding a new bottom-tab screen, wire it in `NavHelper` and manifest.
- If adding persisted auth/session, replace `TokenManager`/`UserManager` memory assumptions across login/logout paths.
- Do not infer production readiness from sample repositories (`CartRepository`, `OrderRepository`) or TODOs in Activities.
