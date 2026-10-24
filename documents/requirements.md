# Requirements: Get Product API (get-product-api)

## 1. Jira Reference

- **Key**: [KAN-15](https://konathamsiriteja78-1786267857296.atlassian.net/browse/KAN-15)
- **Title**: Implement GET API for get product functionality
- **Type**: Story (parent Epic: KAN-6 — Implement Easy Basket backend system)
- **Status**: To Do
- **Priority**: Medium
- **Assignee**: None
- **Labels**: None
- **Comments / Attachments / Issue links**: None

## 2. Confluence References

- **Title**: Easy Basket - Ecommerce application
- **Space**: projectalpha (project-alpha)
- **URL**: https://konathamsiriteja78.atlassian.net/wiki/spaces/projectalpha/pages/15171585/Easy+Basket+-+Ecommerce+application
- **Summary**: Full page retrieved this time (no truncation). Covers backend-wide standards plus the embedded OpenAPI 3.0.3 contract:
  - **API Contract Compliance** — implement all APIs/data models per the approved contract; endpoints must adhere to specified HTTP methods, request/response structures, status codes, and validation rules; contract changes must be version-controlled and documented.
  - **Microservices Architecture** — business capabilities (Product Management, Order Management, etc.) are separated into independent, minimally-coupled, independently deployable/maintainable services.
  - **Scalability** — support increasing traffic/transaction volumes via horizontal scaling; avoid single points of failure where practical.
  - **Technology Stack** — Java (Maven/Gradle), Spring Boot, RESTful APIs with standard controller/service/repository/data-model layering, deployed on AWS (APIs hosted on AWS ECS).
  - **Performance and Latency** — low response latency; efficient algorithms/queries; optimize DB access (indexing, query optimization, connection management); performance testing to find bottlenecks.
  - **Maintainability and Extensibility** — clean, modular code; new functionality added with minimal impact on existing services; each microservice owns its own business logic and data.
  - **Error Handling and Validation** — validate incoming request data; return appropriate HTTP status codes; consistent error response structure across APIs; no leakage of internal implementation details.
  - **Logging and Monitoring** — meaningful logs for troubleshooting (API requests, failures, business transactions); sufficient monitoring to identify failures/performance issues.
  - **Testing** — unit tests for business/service logic, integration tests for component and external-dependency interactions, API-level contract tests, supporting automated testing in the dev lifecycle.
  - **Embedded OpenAPI 3.0.3 contract** — defines, among other endpoints, `GET /products/{productId}` (the scope of this story):
    - Path param `productId` (integer, required).
    - `200` response: body conforms to the shared `Product` schema.
    - `404` response: product not found (no body schema specified beyond the description).
    - Shared `Product` schema fields: `id` (integer), `name` (string), `description` (string), `category` (string), `price` (number/double), `stock` (integer), `availability` (string enum: `IN_STOCK`, `OUT_OF_STOCK`).
  - The contract also defines `POST /products`, `GET /products` (list), `DELETE /products/{productId}`, and `/orders*` endpoints — explicitly **out of scope** for KAN-15; only `GET /products/{productId}` applies here.

## 3. Summary

KAN-15 requires implementing the `GET /products/{productId}` REST endpoint for the Product Management capability already established by KAN-7 (`com.easybasket.product`). The endpoint must retrieve a single product from the H2 database by its ID, returning `200 OK` with the product's details when found, and `404 Not Found` when no product with that ID exists. The implementation must handle invalid/malformed IDs gracefully, comply with the approved OpenAPI contract's response shape, and follow the clean-code, layering, and error-handling conventions already in place in the codebase (thin controller, `ProductService`/`ProductServiceImpl`, `ProductRepository`, single `GlobalExceptionHandler`, `ErrorResponse` shape). Unit and integration tests must cover positive, negative, invalid-ID, and non-existent-product scenarios with ≥80% coverage.

A gap existed between the contract's `Product` schema and the current implementation: the contract defines `description` and a derived `availability` enum (`IN_STOCK`/`OUT_OF_STOCK`) that did not exist on either the `Product` JPA entity or the `ProductResponse` DTO created in KAN-7 (both previously exposed only `id`, `name`, `category`, `price`, `stock`). **Decision (confirmed by user): Option (a)** — the `Product` entity and `ProductResponse` DTO are extended as part of KAN-15 itself to add a persisted `description` column and a derived `availability` field (`stock > 0 ? IN_STOCK : OUT_OF_STOCK`). See OQ1 resolution in Section 7.

## 4. Functional Requirements

- FR1: Implement `GET /products/{productId}` exactly as defined in the approved API/Swagger contract (path, method, response structures, status codes).
- FR2: Accept a `productId` path parameter identifying the product to retrieve.
- FR3: Retrieve the product details from the H2 database using the supplied product ID via `ProductRepository` (existing `JpaRepository<Product, Long>`).
- FR4: When a product with the given ID exists, return `HTTP 200 OK` with the product's details in the response body, shaped as the contract-aligned `ProductResponse` (now including `description` and `availability` per OQ1 Option (a)).
- FR5: When no product with the given ID exists, return `HTTP 404 Not Found` with a response via the existing `GlobalExceptionHandler` (`ErrorResponse` shape: `status`, `error`, `message`, `fieldErrors[]`, `timestamp`), consistent with how KAN-7 handles other error cases.
- FR6: Handle invalid/malformed `productId` values (e.g., non-numeric, since the entity ID is `Long`) with a `400 Bad Request` response via the existing `GlobalExceptionHandler`, rather than an unhandled exception or `500`.
- FR7: Response payload must comply with the defined API contract's `Product` schema for this endpoint (field names, types, structure), including `description` (string, persisted column) and `availability` (string enum, derived from `stock`: `IN_STOCK` when `stock > 0`, otherwise `OUT_OF_STOCK`).
- FR11: Extend the `Product` JPA entity with a new persisted `description` column (nullable, `String`), and extend `ProductResponse` with `description` and a derived `availability` field; existing KAN-7 seed/sample data and the `createProduct` flow must continue to function (e.g., accept an optional `description` on create, or default it to `null`/empty — implementation detail for the Solution Architect).
- FR8: New code must follow the existing layering established by KAN-7: thin `ProductController` method delegating to `ProductService`/`ProductServiceImpl`, which uses `ProductRepository`; no direct repository access from the controller, no entity returned directly from controller/service.
- FR9: Add a new method to the `ProductService` interface (e.g., `getProductById(Long productId)`) implemented in `ProductServiceImpl`, mapping the found `Product` entity to the response DTO, consistent with the existing `createProduct` pattern.
- FR10: Add a `@GetMapping("/products/{productId}")` endpoint method to `ProductController`, annotated with `@Operation` and `@ApiResponse` entries for both `200` and `404` outcomes, so the generated OpenAPI spec stays accurate.

## 5. Non-Functional Requirements

- NFR1 (Code Quality): Implementation must follow clean code principles and the established Java/Spring Boot coding standards already used in `product-service` (constructor injection, interface-based service layer, declarative validation, single global exception handler).
- NFR2 (Test Coverage): Unit and integration tests must cover positive (product found), negative (product not found → 404), invalid-ID, and non-existent-product scenarios, achieving a minimum of 80% test coverage for the implemented functionality; all automated tests must pass.
- NFR3 (Contract Compliance): The API must adhere to the approved OpenAPI contract's path, method, status codes (`200`, `404`), and response schema for `GET /products/{productId}` (per Confluence); any contract deviation must be documented and version-controlled.
- NFR4 (Architecture): The endpoint is delivered within the existing, independently deployable Product Management microservice (`product-service`) with no compile-time dependency on other capabilities (per Confluence and the project's Spring Boot conventions).
- NFR5 (Performance): The lookup should use an efficient, indexed primary-key query (default JPA `findById` behavior is acceptable) to minimize response latency (per Confluence's Performance and Latency guidance).
- NFR6 (Error Handling): Errors (not-found, invalid ID, unexpected failures) must be handled centrally via the existing single `GlobalExceptionHandler`, producing the standard `ErrorResponse` shape without leaking internal implementation details or stack traces to the client.
- NFR7 (Logging/Monitoring): Meaningful log entries should be emitted for failure scenarios (e.g., not-found, invalid ID) consistent with the existing logging pattern (SLF4J `log.error(...)` for unexpected exceptions) to support troubleshooting (per Confluence).
- NFR8 (Maintainability): The new retrieval logic should reuse existing DTOs/mapping conventions where possible and avoid duplicating logic already present in the `create` flow.

## 6. Acceptance Criteria (Testable Checklist)

- [ ] `GET /products/{productId}` is implemented per the approved API/Swagger contract (path, method, status codes).
- [ ] API retrieves product details using a valid product ID from the H2 database.
- [ ] When the product exists, the API returns `HTTP 200 OK` with the requested product's details in the response body.
- [ ] When the product does not exist, the API returns `HTTP 404 Not Found`.
- [ ] Invalid product IDs (e.g., non-numeric path value) are handled appropriately, returning a `4xx` response rather than an unhandled error.
- [ ] Response payload complies with the defined API contract's `Product` schema for this endpoint, including `description` and `availability`.
- [ ] Appropriate HTTP status codes are returned for both successful and unsuccessful scenarios.
- [ ] Implementation follows clean code principles and established Java/Spring Boot coding standards (existing `product-service` layering and conventions).
- [ ] Unit and integration tests cover positive, negative, invalid-ID, and non-existent-product scenarios.
- [ ] Minimum 80% test coverage is achieved.
- [ ] All applicable automated tests pass successfully.

## 7. Assumptions & Decisions (Confirmed by User, 2026-10-24)

1. **OQ1 — Resolved: Option (a)**. The embedded OpenAPI contract's `Product` schema includes `description` (string) and a derived `availability` enum (`IN_STOCK`/`OUT_OF_STOCK`), neither of which existed on the `Product` JPA entity (`product-service/src/main/java/com/easybasket/product/entity/Product.java`) or the `ProductResponse` DTO (`.../dto/ProductResponse.java`) delivered in KAN-7. **Decision**: extend the `Product` entity with a persisted `description` column and derive `availability` from `stock` (`stock > 0 ? IN_STOCK : OUT_OF_STOCK`) as part of KAN-15 itself. This is now a confirmed requirement (see FR4, FR7, FR11) for the Solution Architect to design against.
2. **Confirmed**: `productId` is validated as a `Long` consistent with the existing `Product` entity's `@Id` type; a non-numeric path value is handled as `400 Bad Request` via the existing `GlobalExceptionHandler`.
3. **Confirmed**: The `404` response body uses the same `ErrorResponse` shape (`status`, `error`, `message`, `fieldErrors[]`, `timestamp`) already used for other error cases in `GlobalExceptionHandler`, for consistency across the service.
4. **Confirmed**: No authentication/authorization requirement applies to this read endpoint for KAN-15.
5. **Confirmed**: Any product no longer present in the `products` table (regardless of reason — never existed or previously deleted) results in a plain `404`; no distinct "deleted" status is required.
