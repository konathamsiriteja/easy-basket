# Design Review: Get Product API (KAN-15)

## 1. Reviewed Architecture Reference

- **File**: [documents/architecture.md](documents/architecture.md)
- **Review date**: 2026-10-24

## 2. Risks & Gaps Identified

| # | Risk / Gap | Architecture Reference | Assessment |
|---|---|---|---|
| R1 | **`description` backfill is out of scope**, so existing KAN-7-era products will return `description: null` via the new `GET` endpoint. | §7.1 | Low risk. Requirements OQ1 Option (a), as confirmed by the user, scopes KAN-15 to adding the field going forward, not backfilling historical data. No separate backfill story is required unless the user says otherwise. |
| R2 | **No explicit max length for `description` in the contract or Jira issue.** | §7.2 | Low risk, implementation detail. 1000 characters is a reasonable, generous bound for free-text description; does not conflict with any stated requirement. |
| R3 | **`availability` must never be allowed to drift from `stock`** if a future story adds an update/patch endpoint. | §7.3 | Low risk for KAN-15 (no update endpoint in scope), but worth recording as a constraint for future stories: `availability` must remain a derived/computed field, never a directly settable column, to avoid data inconsistency. |
| R4 | **Extending `ProductRequest` (create DTO) with `description`** changes a contract surface that was already shipped in KAN-7. | §7.4 | Low risk — purely additive (optional field, no behavior change for omitted values), so it is backward-compatible and does not require a new API version or deprecation notice. Confirmed non-breaking by inspection of existing `ProductRequest`/`ProductServiceImpl.createProduct` (product-service/src/main/java/com/easybasket/product/dto/ProductRequest.java, .../service/ProductServiceImpl.java). |
| R5 | **Malformed `productId` must not silently fall through to the generic `500` handler.** Spring binds `@PathVariable Long` and throws `MethodArgumentTypeMismatchException` on a non-numeric segment — a distinct exception type from the `MethodArgumentNotValidException` already handled in `GlobalExceptionHandler` for body validation. | §2, §3, §7.5 | Confirmed as a real implementation gap that must be addressed: without a dedicated `@ExceptionHandler(MethodArgumentTypeMismatchException.class)`, FR6 (malformed ID → `4xx`, not `500`) would not be met. Added to architecture.md §3 as a required new handler. |
| R6 | **New `ProductNotFoundException` must stay inside the existing single-`GlobalExceptionHandler` pattern** rather than introducing a second `@RestControllerAdvice` or ad hoc try/catch, to preserve the NFR6/NFR4 pattern established in KAN-7. | §3, §7 | Confirmed as a design constraint, not an open risk — reflected directly in architecture.md's component table (one handler method added to the existing class). |

## 3. Agreed Design Decisions

*All items below are implementation-detail resolutions consistent with the user-confirmed requirements (OQ1 Option (a) and the four confirmed assumptions in requirements.md §7). None required a further round-trip to the user beyond the already-confirmed OQ1 decision; they are recorded here for traceability and are open to revision before the implementation plan is approved.*

| # | Decision | Resolves |
|---|---|---|
| 1 | `description` is not backfilled for existing rows; returns `null` until a product is created/updated with a value. No backfill story is opened unless requested. | R1 |
| 2 | `description` is capped at 1000 characters (`@Column(length = 1000)` on the entity, `@Size(max = 1000)` on the optional `ProductRequest.description`). | R2 |
| 3 | `availability` is computed only, in `ProductServiceImpl`'s entity-to-DTO mapping; it is never a persisted column and never appears as a settable field on `ProductRequest`. Any future update/patch endpoint must preserve this rule. | R3 |
| 4 | `ProductRequest` gains an optional `description` field; omission defaults to `null`. This is additive and backward-compatible with existing KAN-7 clients/tests. | R4 |
| 5 | `GlobalExceptionHandler` gains `@ExceptionHandler(MethodArgumentTypeMismatchException.class)` returning `400 Bad Request`, in addition to the new `@ExceptionHandler(ProductNotFoundException.class)` returning `404 Not Found`. Both are required for FR5/FR6 and are now specified in architecture.md §3. | R5 |
| 6 | Both new error branches live in the single existing `GlobalExceptionHandler` class — no second `@RestControllerAdvice`, no controller/service try/catch. | R6 |

## 4. Action Items

- [x] Confirm `MethodArgumentTypeMismatchException` handling is explicitly designed (not just assumed) — added to architecture.md §2/§3/§5.3. — Done 2026-10-24.
- [x] Confirm `ProductRequest`/create-flow impact of adding `description` is non-breaking — confirmed by inspection, recorded as R4/Decision 4. — Done.
- [ ] None outstanding. If the user wants historical `description` backfill or a future update endpoint, that should be raised as a separate story — not in scope for KAN-15.

## 5. Open Questions

None outstanding for architecture. All items from requirements.md §7 (OQ1 and the four assumptions) were already confirmed by the user before this review began; this review resolved only implementation-level design details that fell out of applying Option (a).
