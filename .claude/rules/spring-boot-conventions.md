---
paths:
  - "product-service/src/main/java/**/*.java"
---

# Spring Boot Conventions (product-service)

Follow the layering and style already established by the Create Product API (`com.easybasket.product`).

## Package layout

Each capability (e.g. `product`) keeps these sub-packages, mirrored by any new capability added later:

- `controller/` — thin `@RestController`s; no business logic, no direct repository access.
- `service/` — a `*Service` interface plus a `*ServiceImpl` implementation (`@Service`). Controllers depend on the interface, not the impl.
- `repository/` — Spring Data JPA interfaces: `extends JpaRepository<Entity, ID>`.
- `entity/` — JPA entities. Never returned directly from a controller or service return type.
- `dto/` — request/response DTOs, distinct from entities; the service layer maps entity ⇄ DTO.
- `exception/` — exactly one `@RestControllerAdvice` `GlobalExceptionHandler` per microservice (not shared across services).
- `config/` — Spring configuration (e.g. `OpenApiConfig`). Excluded from JaCoCo coverage in `pom.xml`; don't rely on this package to hit the coverage threshold.

## Style

- Constructor injection only — no field `@Autowired` (see `ProductController`, `ProductServiceImpl`).
- Validate inbound DTOs with Jakarta Bean Validation annotations (`@NotBlank`, `@NotNull`, `@Positive`, `@PositiveOrZero`, `@Digits`, `@Size(max=255)`); keep validation declarative on the DTO, not in controller/service code.
- Validation messages are short and explicit: `"<field> is mandatory"`, `"<field> must <constraint>"`.
- Annotate every new endpoint with `@Operation` and the relevant `@ApiResponse` entries (success + validation-failure at minimum) so the generated OpenAPI spec (`/v3/api-docs`, `/swagger-ui.html`) stays accurate — treat the spec as a reviewable contract, not an afterthought.

## Error handling

- All errors funnel through the single `GlobalExceptionHandler` into the standard `ErrorResponse` shape: `status`, `error`, `message`, `fieldErrors[]`, `timestamp`. Don't add ad hoc try/catch blocks in controllers/services to shape error responses.
- Never leak stack traces or internal exception messages to the client; log unexpected exceptions via SLF4J (`log.error(...)`) before returning the generic 500 response.

## Persistence

- New entities default to `GenerationType.IDENTITY` for IDs unless a confirmed design decision in `documents/architecture.md` says otherwise.
- Keep each business capability's classes under its own package boundary — no compile-time dependency on another capability's classes (independent deployability).
