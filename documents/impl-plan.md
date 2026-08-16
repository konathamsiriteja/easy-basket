# Implementation Plan: Create Product API (KAN-7)

**Source documents**: [documents/architecture.md](documents/architecture.md), [documents/design-review.md](documents/design-review.md)

## 1. Task Table

| ID | Title | Priority | Depends On | Status |
|---|---|---|---|---|
| T1 | Project/module scaffolding (Maven `product` service: `pom.xml`, Spring Boot 3.x parent, `web`/`validation`/`data-jpa`/`h2`/`springdoc-openapi`/JaCoCo dependencies, base package structure, `Application` main class) | Critical | — | Ready |
| T2 | H2 datasource configuration (`application.yml`/`.properties`: file-based H2 URL, Hibernate DDL mode, dialect; H2 console toggle for dev) | High | T1 | Blocked (waiting on T1) |
| T3 | `Product` JPA entity (`id` `GenerationType.IDENTITY`, `name`, `category` capped at 255, `price` `BigDecimal`, `stock` `Integer`; no uniqueness constraint on `name`) | High | T1 | Blocked (waiting on T1) |
| T4 | `ProductRepository` (`extends JpaRepository<Product, Long>`) | High | T3 | Blocked (waiting on T3) |
| T5 | `ErrorResponse` DTO (`status`, `error`, `message`, `fieldErrors[]`, `timestamp`) | Medium | T1 | Blocked (waiting on T1) |
| T6 | `GlobalExceptionHandler` (`@RestControllerAdvice`, one instance for this microservice): handlers for `MethodArgumentNotValidException` (field-level errors) and generic `Exception` (500 fallback), both returning `ErrorResponse` | High | T5 | Blocked (waiting on T5) |
| T7 | `ProductRequest` DTO with Bean Validation annotations (`@NotBlank @Size(max=255)` on `name`/`category`; `@NotNull @Positive @Digits(integer=..., fraction=2)` on `price`; `@NotNull @PositiveOrZero` on `stock`) | High | T1 | Blocked (waiting on T1) |
| T8 | `ProductResponse` DTO (`id`, `name`, `category`, `price`, `stock`) | Medium | T1 | Blocked (waiting on T1) |
| T9 | `ProductService` interface + `ProductServiceImpl` (`createProduct(ProductRequest) -> ProductResponse`: maps request → entity, calls `ProductRepository.save`, maps saved entity → response) | Critical | T3, T4, T7, T8 | Blocked (waiting on T3, T4, T7, T8) |
| T10 | `ProductController` (`POST /products` only, `@Valid @RequestBody ProductRequest`, delegates to `ProductService`, returns `201 Created` with `ProductResponse` body, no `Location` header) | Critical | T7, T8, T9 | Blocked (waiting on T7, T8, T9) |
| T11 | springdoc-openapi setup (OpenAPI config bean/properties, `@Operation`/`@Schema`/`@ApiResponse` annotations on `ProductController`/DTOs, verify `/v3/api-docs` and `/swagger-ui.html`) | Medium | T10 | Blocked (waiting on T10) |
| T12 | Unit tests: `ProductServiceImplTest` (Mockito, mapping + repository interaction), `ProductRequest` validation unit tests, `GlobalExceptionHandlerTest` | High | T6, T7, T9 | Blocked (waiting on T6, T7, T9) |
| T13 | Controller tests (`@WebMvcTest ProductController`): positive create; negative/boundary — missing `name`/`category`/`price`/`stock`, `price` zero/negative, `price` with >2 decimals, `stock` negative, oversized `name`/`category` (>255 chars) | High | T10, T6 | Blocked (waiting on T10, T6) |
| T14 | Repository/persistence tests (`@DataJpaTest`) for `Product` entity + `ProductRepository`: save/generated-ID behavior, boundary values (`price` 2-decimal precision, `stock = 0`) | Medium | T2, T3, T4 | Blocked (waiting on T2, T3, T4) |
| T15 | Integration test (`@SpringBootTest`, full context + H2): end-to-end `POST /products` happy path (201 + body shape, no `Location` header) and error path (400 + `ErrorResponse` shape) | High | T2, T4, T6, T10 | Blocked (waiting on T2, T4, T6, T10) |
| T16 | JaCoCo coverage verification: bind `report`/`check` goals to Maven `verify`, enforce ≥80% line/branch coverage on the `product` package, fix any gaps surfaced by T12–T15 | Medium | T12, T13, T14, T15 | Blocked (waiting on T12, T13, T14, T15) |

## 2. Dependency Notes

- **T2 → T1**: datasource configuration requires the Maven module, Spring Boot parent, and `h2`/`data-jpa` dependencies to exist first.
- **T3 → T1**: the `Product` entity requires the `data-jpa` dependency and base package structure from scaffolding.
- **T4 → T3**: `ProductRepository` is typed on `Product`, so the entity must exist first.
- **T5 → T1**: `ErrorResponse` is a plain DTO but still needs the module/package structure in place.
- **T6 → T5**: `GlobalExceptionHandler` builds and returns `ErrorResponse` instances, so the DTO shape must be defined first.
- **T7 → T1**, **T8 → T1**: both DTOs only need the scaffolded module (Jakarta Validation dependency for T7); they have no dependency on the entity, keeping the public contract decoupled from persistence per NFR3.
- **T9 → T3, T4, T7, T8**: `ProductServiceImpl` maps `ProductRequest` → `Product` → `ProductResponse` and calls `ProductRepository`, so all four must exist first.
- **T10 → T7, T8, T9**: `ProductController` binds the request body to `ProductRequest`, returns `ProductResponse`, and delegates to `ProductService`.
- **T11 → T10**: springdoc-openapi generates its spec from the controller/DTO annotations, so the controller must exist to annotate.
- **T12 → T6, T7, T9**: unit tests target the exception handler, request validation, and service logic built in those tasks.
- **T13 → T10, T6**: controller tests exercise the endpoint and rely on `GlobalExceptionHandler` translating validation failures into the expected 400/`ErrorResponse` shape.
- **T14 → T2, T3, T4**: repository tests need a real (test) datasource, the entity, and the repository.
- **T15 → T2, T4, T6, T10**: the end-to-end test needs a running datasource, repository, exception handler, and controller all wired together.
- **T16 → T12, T13, T14, T15**: coverage can only be measured and enforced once the test suites that exercise the code exist.

## 3. Blocked Tasks

All tasks except **T1** are currently blocked, since no implementation work has started:

- **T2** — waiting on T1
- **T3** — waiting on T1
- **T4** — waiting on T3
- **T5** — waiting on T1
- **T6** — waiting on T5
- **T7** — waiting on T1
- **T8** — waiting on T1
- **T9** — waiting on T3, T4, T7, T8
- **T10** — waiting on T7, T8, T9
- **T11** — waiting on T10
- **T12** — waiting on T6, T7, T9
- **T13** — waiting on T10, T6
- **T14** — waiting on T2, T3, T4
- **T15** — waiting on T2, T4, T6, T10
- **T16** — waiting on T12, T13, T14, T15

**T1** is the only task with no unmet dependencies and is `Ready` to start immediately.
