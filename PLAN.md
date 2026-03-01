# Code Review Plan

This document captures improvement suggestions from a review of the `main` branch of the ZioDemo project — a ZIO-based REST API using Tapir, Http4s, Doobie, and PostgreSQL.

---

## 1. Bugs

### 1.1 `MoviesController` uses `Throwable.getMessage` instead of `AppError.message`

**File:** `src/main/scala/com/github/salamonpavel/zio/controller/MoviesController.scala:37`

```scala
// Current (bug): serviceError.getMessage returns null
.mapError { serviceError: ServiceError =>
  ErrorApiResponse(ApiResponseStatus.InternalServerError, serviceError.getMessage)
}

// Expected: use the custom message field from AppError
.mapError { serviceError: ServiceError =>
  ErrorApiResponse(ApiResponseStatus.InternalServerError, serviceError.message)
}
```

`AppError` extends `Throwable` but neither `DatabaseError` nor `ServiceError` call `super(message)` in their constructors, so `Throwable.getMessage()` returns `null`. The `ActorsController` correctly uses `.message`, but `MoviesController` uses `.getMessage`. This will produce `"null"` in error responses for all movie-related failures.

---

### 1.2 `GetActorById` missing `override val` on implicit parameters

**File:** `src/main/scala/com/github/salamonpavel/zio/database/GetActorById.scala:16`

```scala
// Current (inconsistent — may shadow instead of override abstract members)
class GetActorById(implicit schema: DBSchema, dbEngine: DoobieEngine[Task])

// All other database classes use override val:
class GetMovieById(implicit override val schema: DBSchema, override val dbEngine: DoobieEngine[Task])
class GetActors(implicit override val schema: DBSchema, override val dbEngine: DoobieEngine[Task])
class CreateActor(implicit override val schema: DBSchema, override val dbEngine: DoobieEngine[Task])
```

If `schema` and `dbEngine` are abstract members in the parent class (as they are for `GetMovieById`, `GetActors`, and `CreateActor`), not using `override val` means the implicit parameters create new fields instead of satisfying the abstract definitions. This should be aligned to match the other database function classes.

---

### 1.3 `AppError` extends `Throwable` without forwarding the message to the superclass

**File:** `src/main/scala/com/github/salamonpavel/zio/exception/AppError.scala`

```scala
// Current
case class DatabaseError(message: String) extends AppError
case class ServiceError(message: String) extends AppError

// Suggested
case class DatabaseError(message: String) extends AppError {
  override def getMessage: String = message
}
case class ServiceError(message: String) extends AppError {
  override def getMessage: String = message
}
```

Since `AppError` extends `Throwable`, any code or framework that calls `.getMessage` (including logging frameworks and error handlers that may not know about the custom `.message` field) will get `null`. Overriding `getMessage` makes the hierarchy safe regardless of which method is called.

---

## 2. Incorrect or Misleading Scaladoc

### 2.1 `GetActorById` class doc refers to movies

**File:** `src/main/scala/com/github/salamonpavel/zio/database/GetActorById.scala:13`

```scala
// Current (wrong)
/** A class representing a function to get a movie by ID. */

// Should be
/** A class representing a function to get an actor by ID. */
```

### 2.2 `GetActors` class doc refers to movies

**File:** `src/main/scala/com/github/salamonpavel/zio/database/GetActors.scala:13`

```scala
// Current (wrong)
/** A class representing a function to get a movie by ID. */

// Should be
/** A class representing a function to get actors. */
```

### 2.3 `GetActorById` companion object doc refers to `GetMovieById`

**File:** `src/main/scala/com/github/salamonpavel/zio/database/GetActorById.scala:27`

```scala
// Current (wrong)
/** A ZLayer that provides live implementation of GetMovieById. */

// Should be
/** A ZLayer that provides live implementation of GetActorById. */
```

### 2.4 `GetActors` companion object doc refers to `GetMovieById`

**File:** `src/main/scala/com/github/salamonpavel/zio/database/GetActors.scala:26`

```scala
// Current (wrong)
/** A ZLayer that provides live implementation of GetMovieById. */

// Should be
/** A ZLayer that provides live implementation of GetActors. */
```

### 2.5 `MoviesService` trait doc says "repository" instead of "service"

**File:** `src/main/scala/com/github/salamonpavel/zio/service/MoviesService.scala:12`

```scala
// Current (wrong)
/** A trait representing the repository for movies. */

// Should be
/** A trait representing the service for movies. */
```

---

## 3. Security Concerns

### 3.1 Database credentials hardcoded in `reference.conf`

**Files:** `src/main/resources/reference.conf`, `src/test/resources/reference.conf`

Both config files contain:
```
user=postgres
password=postgres
```

Credentials should be injected via environment variables rather than committed to source control. Even for a demo, it establishes a bad pattern.

**Suggested approach:**
```hocon
postgres {
  user = ${?POSTGRES_USER}
  password = ${?POSTGRES_PASSWORD}
  # ...
}
```

### 3.2 Flyway credentials hardcoded in `build.sbt`

**File:** `build.sbt:13-15`

```scala
flywayUrl := "jdbc:postgresql://localhost:5433/testdb"
flywayUser := "postgres"
flywayPassword := "postgres"
```

These should be read from environment variables:
```scala
flywayUrl      := sys.env.getOrElse("FLYWAY_URL", "jdbc:postgresql://localhost:5433/testdb")
flywayUser     := sys.env.getOrElse("FLYWAY_USER", "postgres")
flywayPassword := sys.env.getOrElse("FLYWAY_PASSWORD", "postgres")
```

### 3.3 `ServerHost` hardcoded to `"localhost"`

**File:** `src/main/scala/com/github/salamonpavel/zio/Constants.scala:11`

The server binds to `localhost` unconditionally, which prevents the application from being deployed in a container or behind a load balancer without code changes. This should be made configurable (e.g., read from config or environment).

---

## 4. Architecture & Design

### 4.1 Test-only layer exposed in production source tree

**File:** `src/main/scala/com/github/salamonpavel/zio/database/TransactorProvider.scala:34`

```scala
val testLayer: ZLayer[Any, Config.Error, Transactor[Task]] = ZLayer { ... }
```

`testLayer` lives inside `TransactorProvider` which is in `src/main/scala`. Test utilities should either be in `src/test/scala` or separated into a dedicated test-support module. Having it in production code risks it being accidentally used in production or being included in production artifacts.

### 4.2 Unused `AppConfig` object

**File:** `src/main/scala/com/github/salamonpavel/zio/config/AppConfig.scala:9`

```scala
object AppConfig { // not needed at the moment
```

Dead code should be removed. If it is kept for future use, a tracking issue is a better record than a comment in source code.

### 4.3 Duplicate `reference.conf` files

**Files:** `src/main/resources/reference.conf`, `src/test/resources/reference.conf`

Both files are identical. The test config should only override values that differ from the main config (e.g., the database port or credentials). Duplicating the entire file means changes to one must be manually mirrored in the other, which is error-prone.

### 4.4 Inconsistent error handling style in `ActorsController`

**File:** `src/main/scala/com/github/salamonpavel/zio/controller/ActorsController.scala`

`findActorById` uses `mapError` followed by `flatMap`, while `findActors` and `createActor` use `foldZIO`. Both approaches are valid, but consistent style across all methods in the same class is easier to read and maintain. Choose one idiom and apply it uniformly.

### 4.5 Missing CRUD operations for movies

The application only implements `getMovieById` for movies. There are no endpoints or database functions for creating, listing, updating, or deleting movies. For consistency with the actors domain, consider adding at minimum `createMovie` and `getMovies` operations to match the actors feature set.

---

## 5. Test Coverage Gaps

### 5.1 No tests for `MoviesRepository`, `MoviesService`, or `MoviesController`

Tests only cover the actors domain. The entire movies domain has zero unit test coverage:

- `MoviesRepository` — no spec
- `MoviesService` — no spec
- `MoviesController` — no spec

Add corresponding `*Spec` files following the same patterns as `ActorsRepositorySpec` and `ActorsServiceSpec`.

### 5.2 No tests for `ActorsController`

**File:** `src/test/scala/com/github/salamonpavel/zio/` (missing)

`ActorsController` translates service results to API responses and handles `NotFound` logic. This logic is not directly tested.

### 5.3 Integration tests mixed with unit tests in `ActorsRepositorySpec`

**File:** `src/test/scala/com/github/salamonpavel/zio/repository/ActorsRepositorySpec.scala`

Some tests provide real database layers (`TransactorProvider.testLayer`, `PostgresDatabaseProvider.layer`) and require a running PostgreSQL instance. Others use mocks. These should be separated:

- Unit tests (mocks only) — always run in CI
- Integration tests — run only when a database is available (e.g., tagged or in a separate sbt config)

### 5.4 Integration test assertion may be fragile

**File:** `src/test/scala/com/github/salamonpavel/zio/repository/ActorsRepositorySpec.scala:47-50`

```scala
test("returns an expected instance of Actor from postgres in docker container") {
  val expectedActor = Actor(1, "John", "Doe")
  ...
  } yield assertTrue(actor.contains(expectedActor))
```

The test hardcodes `actor_id = 1` and expects specific name values. If the seed data order changes or migrations are re-run, this test will fail spuriously. Tests depending on specific auto-generated IDs should query by name and verify the returned data, or use a controlled fixture mechanism.

---

## 6. CI/CD Improvements

### 6.1 GitHub Actions workflow only triggered manually

**File:** `.github/workflows/build.yml:3-4`

```yaml
on:
  workflow_dispatch
```

The workflow only runs on manual trigger. Add automatic triggers on push and pull requests:

```yaml
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  workflow_dispatch:
```

### 6.2 Outdated GitHub Actions versions

**File:** `.github/workflows/build.yml`

```yaml
uses: actions/checkout@v2          # outdated; use @v4
uses: olafurpg/setup-scala@v10     # unmaintained; use actions/setup-java or coursier/setup-action
```

- `actions/checkout@v2` should be updated to `@v4`.
- `olafurpg/setup-scala` is no longer actively maintained. The recommended replacement is [`coursier/setup-action`](https://github.com/coursier/setup-action).

### 6.3 Non-deterministic `postgres:latest` Docker image in CI

**File:** `.github/workflows/build.yml:15`

```yaml
image: postgres:latest
```

Using `latest` makes builds non-reproducible. Pin to a specific version:

```yaml
image: postgres:16.2
```

---

## 7. Dependency & Build Hygiene

### 7.1 Mixed tapir versions

**File:** `build.sbt:43-51`

```scala
"com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % "1.9.4"
"com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.9.5"
"com.softwaremill.sttp.tapir" %% "tapir-json-play"         % "1.9.5"
```

Two different tapir versions (`1.9.4` and `1.9.5`) are used. Extract to a shared version variable:

```scala
lazy val tapirVersion = "1.9.5"
```

### 7.2 Scala 2.13 with macro annotations dependency

**File:** `build.sbt:11`

```scala
scalacOptions += "-Ymacro-annotations"
```

This flag is required by `zio-macros` for the `@accessible` annotation in Scala 2.13. Consider migrating to Scala 3 where this pattern is handled differently and the macro infrastructure is more stable. ZIO 2.x has full Scala 3 support.

---

## Summary Table

| # | Category | Severity | File(s) |
|---|----------|----------|---------|
| 1.1 | Bug | High | `MoviesController.scala` |
| 1.2 | Bug | Medium | `GetActorById.scala` |
| 1.3 | Bug | Medium | `AppError.scala` |
| 2.1–2.5 | Docs | Low | `GetActorById.scala`, `GetActors.scala`, `MoviesService.scala` |
| 3.1 | Security | High | `reference.conf` (main & test) |
| 3.2 | Security | High | `build.sbt` |
| 3.3 | Security | Medium | `Constants.scala` |
| 4.1 | Architecture | Medium | `TransactorProvider.scala` |
| 4.2 | Architecture | Low | `AppConfig.scala` |
| 4.3 | Architecture | Low | `reference.conf` (duplicate) |
| 4.4 | Architecture | Low | `ActorsController.scala` |
| 4.5 | Architecture | Low | (missing endpoints) |
| 5.1 | Testing | High | (missing specs) |
| 5.2 | Testing | Medium | (missing `ActorsControllerSpec`) |
| 5.3 | Testing | Medium | `ActorsRepositorySpec.scala` |
| 5.4 | Testing | Low | `ActorsRepositorySpec.scala` |
| 6.1 | CI/CD | Medium | `build.yml` |
| 6.2 | CI/CD | Medium | `build.yml` |
| 6.3 | CI/CD | Low | `build.yml` |
| 7.1 | Build | Low | `build.sbt` |
| 7.2 | Build | Low | `build.sbt` |
