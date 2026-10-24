# Implementation Plan: Get Product API (KAN-15)

**Source documents**: [documents/architecture.md](documents/architecture.md), [documents/design-review.md](documents/design-review.md)

## 1. Task Table

| ID | Title | Priority | Depends On | Status |
|---|---|---|---|---|
| T1 | Extend `Product` JPA entity: add nullable `description` column (`@Column(length = 1000)`), getter/setter, update constructor(s) and `equals`/`hashCode` as needed (`product-service/src/main/java/com/easybasket/product/entity/Product.java`) | Critical | — | Ready |
| T2 | Extend `ProductResponse` DTO: add `description: String` and `availability: String` fields, getters/setters, `@Schema` annotations, update constructor (`.../dto/ProductResponse.java`) | Critical | — | Ready |
| T3 | Extend `ProductRequest` DTO: add optional `description: String` field (`@Size(max = 1000)`, no `@NotBlank`), getter/setter, `@Schema` annotation (`.../dto/ProductRequest.java`) | High | — | Ready |
| T4 | New `ProductNotFoundException` (unchecked `RuntimeException`, carries `productId`) (`.../exception/ProductNotFoundException.java`) | Critical | — | Ready |
| T5 | Extend `GlobalExceptionHandler`: add `@ExceptionHandler(ProductNotFoundException.class)` → `404` `ErrorResponse`; add `@ExceptionHandler(MethodArgumentTypeMismatchException.class)` → `400` `ErrorResponse` (`.../exception/GlobalExceptionHandler.java`) | Critical | T4 | Blocked (waiting on T4) |
| T6 | Extend `ProductServiceImpl.createProduct` to pass `description` from `ProductRequest` into the new `Product` constructor/setter arg (keeps create flow consistent with the extended entity) | High | T1, T3 | Blocked (waiting on T1, T3) |
| T7 | Add `ProductService.getProductById(Long)` to the interface, implement in `ProductServiceImpl`: `productRepository.findById(id)`, throw `ProductNotFoundException` if empty, else map entity → `ProductResponse` deriving `availability` (`stock > 0 ? "IN_STOCK" : "OUT_OF_STOCK"`) (`.../service/ProductService.java`, `.../service/ProductServiceImpl.java`) | Critical | T1, T2, T4 | Blocked (waiting on T1, T2, T4) |
| T8 | Add `GET /products/{productId}` to `ProductController`: `@PathVariable Long productId`, delegate to `ProductService.getProductById`, return `200 OK` with `ProductResponse`; annotate with `@Operation` + `@ApiResponse` for `200` and `404` (`.../controller/ProductController.java`) | Critical | T2, T5, T7 | Blocked (waiting on T2, T5, T7) |
| T9 | Verify generated OpenAPI spec (`/v3/api-docs`, `/swagger-ui.html`) reflects the new endpoint and the extended `Product`/`ProductResponse` schema (`description`, `availability`) | Medium | T8 | Blocked (waiting on T8) |
| T10 | Unit tests: `ProductServiceImplTest` — `getProductById` found (maps `availability` correctly for both `IN_STOCK`/`OUT_OF_STOCK` boundary, i.e. `stock = 0` and `stock > 0`) and not-found (throws `ProductNotFoundException`); update existing `createProduct` tests for the new `description` field | High | T6, T7 | Blocked (waiting on T6, T7) |
| T11 | Unit tests: `GlobalExceptionHandlerTest` — new `ProductNotFoundException` → `404` branch, new `MethodArgumentTypeMismatchException` → `400` branch | High | T5 | Blocked (waiting on T5) |
| T12 | Controller tests (`@WebMvcTest ProductController`): positive `GET /products/{productId}` (200 + full `ProductResponse` body shape incl. `description`/`availability`); negative — non-existent ID (404 + `ErrorResponse`), non-numeric path value e.g. `/products/abc` (400 + `ErrorResponse`) | High | T8, T5 | Blocked (waiting on T8, T5) |
| T13 | Repository/persistence tests (`@DataJpaTest`): save a `Product` with `description` set and with `description` left `null`; `findById` returns both correctly; non-existent ID returns empty `Optional` | Medium | T1 | Blocked (waiting on T1) |
| T14 | Integration test (`@SpringBootTest`, full context + H2): end-to-end `GET /products/{productId}` happy path (200 + body shape), not-found path (404 + `ErrorResponse`), malformed-ID path (400 + `ErrorResponse`) | High | T8, T5 | Blocked (waiting on T8, T5) |
| T15 | JaCoCo coverage verification: re-run `verify`, confirm ≥80% line/branch coverage still holds across the `product` package including all new/changed classes; fix any gaps surfaced by T10–T14 | Medium | T10, T11, T12, T13, T14 | Blocked (waiting on T10, T11, T12, T13, T14) |

## 2. Dependency Notes

- **T5 → T4**: the new exception handler method signature depends on `ProductNotFoundException` existing first.
- **T6 → T1, T3**: wiring `description` through `createProduct` needs both the entity column (T1) and the request DTO field (T3).
- **T7 → T1, T2, T4**: `getProductById` needs the entity's `description` field, the response DTO's `description`/`availability` fields, and the not-found exception type.
- **T8 → T2, T5, T7**: the new controller endpoint returns `ProductResponse` (T2), relies on `GlobalExceptionHandler` for both new error branches (T5), and delegates to the new service method (T7).
- **T9 → T8**: the OpenAPI spec is generated from the controller's annotations, so the endpoint must exist first.
- **T10 → T6, T7**: service-layer unit tests exercise both the updated `createProduct` (T6) and the new `getProductById` (T7).
- **T11 → T5**: exception-handler unit tests target the two new handler methods directly.
- **T12 → T8, T5**: controller tests exercise the new endpoint and depend on the exception handler translating both error cases correctly.
- **T13 → T1**: repository tests need the extended entity to assert `description` persistence.
- **T14 → T8, T5**: the end-to-end test needs the full request path (controller → service → repository) and both new error branches wired together.
- **T15 → T10, T11, T12, T13, T14**: coverage can only be measured once all new/updated test suites exist.

## 3. Blocked Tasks

**T1**, **T2**, **T3**, and **T4** have no unmet dependencies and are `Ready` to start immediately (they can proceed in parallel). All other tasks are currently blocked:

- **T5** — waiting on T4
- **T6** — waiting on T1, T3
- **T7** — waiting on T1, T2, T4
- **T8** — waiting on T2, T5, T7
- **T9** — waiting on T8
- **T10** — waiting on T6, T7
- **T11** — waiting on T5
- **T12** — waiting on T8, T5
- **T13** — waiting on T1
- **T14** — waiting on T8, T5
- **T15** — waiting on T10, T11, T12, T13, T14

## 4. Out of Scope (per requirements.md / architecture.md)

- `description` backfill for existing/historical product rows (returns `null` until explicitly set).
- Any update/patch endpoint — `availability` must remain derived-only if one is added later (architecture.md §7.3).
- `GET /products` (list), `POST /products` changes beyond the additive `description` field, `DELETE /products/{productId}`, and all `/orders*` endpoints.
- Authentication/authorization (confirmed out of scope, requirements.md §7.4).
