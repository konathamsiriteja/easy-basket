# Design Review: Create Product API (KAN-7)

## 1. Reviewed Architecture Reference

- **File**: [documents/architecture.md](documents/architecture.md)
- **Review date**: 2026-08-15

## 2. Risks & Gaps Identified

### 2.1 Risks flagged by the Solution Architect (architecture.md §7)

*Status: all items below were resolved with the user on 2026-08-15 — see §3 Agreed Design Decisions for the final, confirmed outcome of each.*

| # | Risk / Gap | Architecture Reference | Resolution (Agreed — see §3) |
|---|---|---|---|
| R1 | **No authentication/authorization on `POST /products`.** Endpoint is fully open. | §6 Security, §7.1 | Proceed with no auth for KAN-7 as an explicit, documented scope exclusion (not a silent gap), and open a follow-up story/ticket to add security once the full Confluence page or a security-specific requirement is confirmed. Confirm this is acceptable for a first-pass internal/dev deployment. |
| R2 | **Unconfirmed product ID generation strategy** (JPA auto-increment/IDENTITY vs. UUID). | §2 Technology Choices, §7.2 | Default to `GenerationType.IDENTITY` (numeric auto-increment `Long id`) as the lowest-friction Spring Data JPA option, and treat it as provisional until the authoritative Swagger contract is confirmed. If UUID is later required, this is a breaking, versioned contract change. |
| R3 | **`price = 0` boundary treatment unconfirmed** — only "reject negative price" is explicit in requirements; architecture assumes `@PositiveOrZero`. | §7.4 | Keep `@PositiveOrZero` for both `price` and `stock` (0 is valid) for consistency, since FR5 only says "reject negative values." Confirm explicitly with the user/product owner before implementation, since switching to `@Positive` is a one-line change but affects test cases either way. |
| R4 | **No product-name uniqueness constraint.** Duplicate product names are accepted; no unique index or `409 Conflict` path exists. | §7.5 | Do not add a uniqueness constraint for this story (defer to a follow-up story), since it is not in the FR/AC list. If the user wants uniqueness enforced now, this adds: a unique DB constraint on `Product.name`, a new `409 Conflict` branch in `GlobalExceptionHandler`, and additional negative test cases — scope/effort impact should be confirmed before adding. |
| R5 | **Full product schema unconfirmed** beyond `name`, `category`, `price`, `stock` (e.g., description, SKU, image URL, whether `category` is free text or a referenced entity). | §7.6 | Implement only the four confirmed fields + generated `id` for this story. Explicitly confirm with the user that no additional fields (SKU, description, image, category-as-entity) are required before implementation starts, since adding fields later is a breaking contract change. |
| R6 | **H2 as a long-term, multi-instance datastore** conflicts with NFR5 (horizontal scaling) — H2 file-based storage is not safely shared across replicas. | §6 Scalability, §7.7 | Accept H2 for this story only (FR7 explicitly mandates it), and log a follow-up architectural action item to evaluate a shared, durable datastore (e.g., PostgreSQL/MySQL) before any horizontally-scaled production deployment. Confirm this phased approach is acceptable. |
| R7 | **Confluence source page was truncated during retrieval** — sections beyond "Scalability" (security, detailed data model, persistence/testing standards) are unconfirmed. | §7.8 | Treat this design as provisional pending either (a) a successful re-fetch of the full Confluence page, or (b) explicit user confirmation that no further constraints exist beyond what's captured. Recommend re-attempting retrieval or requesting a manual export/paste of the remaining sections before finalizing implementation. |

### 2.2 Additional gaps identified during this review

*Status: all items below were resolved with the user on 2026-08-15 — see §3 Agreed Design Decisions for the final, confirmed outcome of each.*

| # | Risk / Gap | Resolution (Agreed — see §3) |
|---|---|---|
| R8 | **`Location` header on `201 Created` (§5.1 step 8) references a resource-retrieval endpoint (e.g., `GET /products/{id}`) that does not exist yet** in this or any prior story. Clients following the header will get a `404`. | Confirm whether KAN-7 should still set the `Location` header (pointing to a not-yet-implemented URI, per HTTP spec that only requires the resource identifier) or omit it until a `GET /products/{id}` story exists. Recommend keeping the header (spec-compliant) and noting the dependent endpoint as a known follow-on gap. |
| R9 | **Scope/ownership of `GlobalExceptionHandler` is ambiguous** given NFR4 (independently deployable, minimally-coupled microservices). Architecture describes it as a single "application-wide" component (open question 7 in requirements.md), but each microservice is its own deployable unit — a literal single shared instance would introduce coupling across services. | Confirm whether "application-wide" means *shared per individual microservice deployment* (each service has its own copy/instance of the same pattern, no cross-service coupling) rather than one physically shared component across services. Recommend the former to stay consistent with NFR4; update architecture.md wording if confirmed. |
| R10 | **No explicit precision/scale constraint on `price` (`BigDecimal`)** — e.g., currency rounding to 2 decimal places is not specified anywhere in requirements or architecture. | Confirm whether `price` should be constrained to 2 decimal places (typical currency handling) via `@Digits(integer=..., fraction=2)` or left unconstrained for this story. Low effort either way but affects validation test cases. |
| R11 | **No payload size limits or input-length constraints** on `name`/`category` (only `@NotBlank`), which is a minor hardening gap (OWASP: unrestricted resource consumption / injection surface via oversized or malformed input), independent of the already-flagged auth gap. | Confirm whether a reasonable max length (e.g., `@Size(max=255)`) should be added to `name`/`category` for this story, or deferred as out-of-scope hardening alongside the auth follow-up. |

## 3. Agreed Design Decisions

*Confirmed by the user on 2026-08-15. All 11 items below are final and have been reflected in `documents/architecture.md`.*

| # | Decision | Resolves |
|---|---|---|
| 1 | No authentication/authorization is required for `POST /products`. Proceed with the endpoint fully open for this story. | R1 |
| 2 | Product ID generation uses JPA `GenerationType.IDENTITY` (numeric auto-increment `Long id`). | R2 |
| 3 | `price` must be **strictly positive** — `price = 0` is **not** valid. Use `@Positive` (not `@PositiveOrZero`) on `price`. `stock` remains `@PositiveOrZero` (`stock = 0` is valid) since only `price` was called out. This corrects the architecture's earlier `@PositiveOrZero` assumption for `price`. | R3 |
| 4 | No product-name uniqueness constraint. Duplicate names are accepted; no unique index and no `409 Conflict` path for this story. | R4 |
| 5 | Implement only the fields needed to store the POST request's product information: `name`, `category`, `price`, `stock`, and the generated `id`. No additional schema/fields (no SKU, description, image, category-as-entity). | R5 |
| 6 | Proceed with H2 for this story. No follow-up ticket is needed for a shared/durable datastore migration. | R6 |
| 7 | No further Confluence re-fetch is needed. `documents/requirements.md`'s functional/non-functional/acceptance criteria are sufficient as-is. | R7 |
| 8 | Omit the `Location` header on `201 Created` responses, since `GET /products/{id}` does not exist. Only `POST /products` is in scope for this story. | R8 |
| 9 | `GlobalExceptionHandler` is one instance **per microservice** (not a single cross-service shared component). Include exactly one `@RestControllerAdvice` in the current codebase. | R9 |
| 10 | `price` precision is enforced to 2 decimal places via `@Digits(integer=..., fraction=2)`; USD currency is assumed. | R10 |
| 11 | `name` and `category` have a max length of 255 characters, enforced via `@Size(max=255)`. | R11 |

## 4. Action Items

- [x] Obtain explicit user confirmation on each proposed resolution (R1–R11) above. — Done 2026-08-15.
- [x] Update `documents/architecture.md` to reflect all 11 agreed decisions (price `@Positive` + `@Digits`, no `Location` header, per-microservice exception handler scope, `@Size(max=255)`, confirmed ID strategy/schema/H2/auth). — Done.
- [ ] None outstanding. No follow-up tickets were requested by the user (H2 migration and auth hardening were explicitly declined as follow-ups per decisions #1 and #6).

## 5. Open Questions

None. All previously open questions (R2–R5, R7, R9) were resolved by the agreed decisions in §3.
