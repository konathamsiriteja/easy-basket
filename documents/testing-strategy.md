# Testing Strategy & Coverage Verification: Create Product API (KAN-7)

**Verified by**: Independent test verification (Test Agent), not the implementing developer.
**Branch**: `feature/KAN-7-create-product-api`
**Module**: `product-service`
**Verdict**: **PASS** — 45/45 tests pass, 100% line and 100% branch coverage on the JaCoCo-enforced bundle, 98.73% overall instruction coverage across the module (threshold ≥80%). All acceptance-criteria scenarios in [requirements.md](requirements.md) have automated test coverage, including the newly added malformed-JSON negative scenario.

**Re-verification note (this pass)**: Re-run after two fixes were applied on top of the original verification: (1) the H2 console is now disabled by default in `src/main/resources/application.yml` (`spring.h2.console.enabled: false`), closing the OWASP A05 security-misconfiguration finding; (2) a new `@ExceptionHandler(HttpMessageNotReadableException.class)` was added to `GlobalExceptionHandler` returning 400 for malformed/unreadable JSON bodies, backed by a new `ProductControllerTest#createProduct_malformedJson_returns400` test. The developer's claim of **45 tests passing, 98.73% instruction coverage, BUILD SUCCESS** was independently reproduced and matches exactly.

## 1. Testing Approach

- **Unit tests** (JUnit 5 + Mockito + AssertJ):
  - `ProductRequestValidationTest` — Bean Validation (`jakarta.validation`) rules on `ProductRequest` exercised directly via `Validator`, independent of Spring MVC.
  - `ProductServiceImplTest` — service layer logic mocked against `ProductRepository`.
  - `ProductTest`, `ProductResponseTest`, `ErrorResponseTest` — entity/DTO construction, getters/setters, equality.
  - `GlobalExceptionHandlerTest` — handler methods invoked directly with mocked `MethodArgumentNotValidException` / generic `RuntimeException`.
- **Slice/integration tests**:
  - `ProductControllerTest` — `@WebMvcTest` with `ProductService` mocked and `GlobalExceptionHandler` imported; exercises the full HTTP request/response cycle (status codes, JSON body, headers) without a real database.
  - `ProductRepositoryTest` — `@DataJpaTest` against the real H2 datasource, verifying persistence.
- **End-to-end test**:
  - `ProductServiceApplicationIntegrationTest` — `@SpringBootTest` + `@AutoConfigureMockMvc`, full application context, real H2 persistence, happy path and invalid-payload error path.
- **Coverage tool**: JaCoCo Maven plugin `0.8.12`, `prepare-agent` execution during `test`, `report` execution during `test`, `check` execution during `verify` enforcing a `BUNDLE`-level rule of `LINE COVEREDRATIO ≥ 0.80` and `BRANCH COVEREDRATIO ≥ 0.80`, excluding `ProductServiceApplication.class` and the `com/easybasket/product/config/**` package (per `product-service/pom.xml`).

## 2. Commands Run

Command executed exactly as claimed by the developer, from the `product-service` module directory (run via `cmd /c` with output redirected to a log file, since PowerShell's stderr handling of Maven's native JVM warnings otherwise truncates the stream):

```
mvn clean verify
```

**Actual result**: exit code `0` — `BUILD SUCCESS`. The `verify` phase includes the JaCoCo `check` goal, which fails the build if the 80% threshold is not met; a `0` exit code confirms the check passed.

Test summary extracted directly from the console output of this run:

```
Tests run: 13, Failures: 0, Errors: 0, Skipped: 0 -- com.easybasket.product.controller.ProductControllerTest
Tests run: 2,  Failures: 0, Errors: 0, Skipped: 0 -- com.easybasket.product.dto.ErrorResponseTest
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0 -- com.easybasket.product.dto.ProductRequestValidationTest
Tests run: 2,  Failures: 0, Errors: 0, Skipped: 0 -- com.easybasket.product.dto.ProductResponseTest
Tests run: 6,  Failures: 0, Errors: 0, Skipped: 0 -- com.easybasket.product.entity.ProductTest
Tests run: 2,  Failures: 0, Errors: 0, Skipped: 0 -- com.easybasket.product.exception.GlobalExceptionHandlerTest
Tests run: 2,  Failures: 0, Errors: 0, Skipped: 0 -- com.easybasket.product.ProductServiceApplicationIntegrationTest
Tests run: 4,  Failures: 0, Errors: 0, Skipped: 0 -- com.easybasket.product.repository.ProductRepositoryTest
Tests run: 2,  Failures: 0, Errors: 0, Skipped: 0 -- com.easybasket.product.service.ProductServiceImplTest

[INFO] Tests run: 45, Failures: 0, Errors: 0, Skipped: 0
[INFO] All coverage checks have been met.
[INFO] BUILD SUCCESS
```

Total: **45 tests, 0 failures, 0 errors, 0 skipped** — matches the developer's claim exactly. `ProductControllerTest` grew from 12 to 13 tests (the new `createProduct_malformedJson_returns400` case), accounting for the increase from the previously-verified 44 to the current 45.

Note: one `ERROR`-level log line appears mid-run from `GlobalExceptionHandler : Unhandled exception while processing request` — this is expected application logging triggered intentionally by `GlobalExceptionHandlerTest#handleGeneric_returnsInternalServerError`, not a test or build failure.

## 3. Coverage Summary

Measured from `product-service/target/site/jacoco/jacoco.csv` generated by the same `mvn clean verify` run (raw counters, not rounded from an external source):

| Class (bundle scope: excludes `ProductServiceApplication` and `config/**`) | Instructions Missed/Covered | Lines Missed/Covered | Branches Missed/Covered |
|---|---|---|---|
| `entity.Product` | 0 / 82 | 0 / 29 | 0 / 4 |
| `exception.GlobalExceptionHandler` | 0 / 74 | 0 / 24 | 0 / 0 |
| `service.ProductServiceImpl` | 0 / 38 | 0 / 15 | 0 / 0 |
| `controller.ProductController` | 0 / 16 | 0 / 5 | 0 / 0 |
| `dto.ProductResponse` | 0 / 56 | 0 / 24 | 0 / 0 |
| `dto.ErrorResponse` | 0 / 56 | 0 / 24 | 0 / 0 |
| `dto.ProductRequest` | 0 / 46 | 0 / 20 | 0 / 0 |
| **Enforced bundle total** | **0 / 368 → 100%** | **0 / 141 → 100% line** | **0 / 4 → 100% branch** |

Both the LINE and BRANCH `COVEREDRATIO` are 1.00 on the enforced bundle, well above the 0.80 (80%) minimum enforced by the `jacoco-maven-plugin` `check` goal in `pom.xml`. `GlobalExceptionHandler` grew from 18 to 24 covered lines (74 instructions), reflecting the new `handleMalformedJson` handler, and remains at 100%.

Overall module-wide **instruction coverage** (including the two classes excluded from the enforced rule) computed from the full `jacoco.csv`:

```
Instruction coverage: 98.73% (388/393)
Branch coverage:      100.00% (4/4)
```

This matches the developer's claim of **98.73% instruction coverage** exactly. The 5 missed instructions all belong to `ProductServiceApplication` (the `main()` bootstrap method), which is explicitly excluded from the enforced `check` rule:
- `ProductServiceApplication` (main entry point): 5 instructions / 2 lines missed, 3 instructions / 1 line covered.
- `config.OpenApiConfig`: fully covered (0 missed / 17 instructions, 6 lines) — excluded regardless per the pom rule.

This exclusion is a reasonable and disclosed choice (main-method bootstrap and static Swagger config carry little business-logic risk) and does not inflate the reported percentage in a misleading way — it's visible directly in `pom.xml` and reproducible.

## 4. Scenarios Covered

Cross-checked against the Acceptance Criteria in [requirements.md](requirements.md#6-acceptance-criteria-testable-checklist):

| Acceptance Criteria | Covered? | Test(s) |
|---|---|---|
| `POST /products` implemented per contract, returns 201 + body, no `Location` header | ✅ | `ProductControllerTest#createProduct_validRequest_returns201WithBodyAndNoLocationHeader`, `ProductServiceApplicationIntegrationTest#createProduct_happyPath_persistsAndReturns201WithoutLocationHeader` |
| Missing `name` → 4xx | ✅ | `ProductControllerTest#createProduct_missingName_returns400`, `ProductRequestValidationTest#blankName_isRejected` |
| Missing `category` → 4xx | ✅ | `ProductControllerTest#createProduct_missingCategory_returns400`, `ProductRequestValidationTest#blankCategory_isRejected` |
| Missing `price` → 4xx | ✅ | `ProductControllerTest#createProduct_missingPrice_returns400`, `ProductRequestValidationTest#nullPrice_isRejected` |
| Missing `stock` → 4xx | ✅ | `ProductControllerTest#createProduct_missingStock_returns400`, `ProductRequestValidationTest#nullStock_isRejected` |
| Negative `price` rejected | ✅ | `ProductControllerTest#createProduct_negativePrice_returns400`, `ProductRequestValidationTest#negativePrice_isRejected` |
| `price = 0` rejected (must be > 0) | ✅ | `ProductControllerTest#createProduct_zeroPrice_returns400`, `ProductRequestValidationTest#zeroPrice_isRejected` |
| Negative `stock` rejected | ✅ | `ProductControllerTest#createProduct_negativeStock_returns400`, `ProductRequestValidationTest#negativeStock_isRejected` |
| `stock = 0` boundary is **valid** (per requirements assumption #4) | ✅ | `ProductControllerTest#createProduct_zeroStock_isValidBoundaryAndReturns201`, `ProductRequestValidationTest#zeroStock_isValidBoundary`, `ProductServiceImplTest#createProduct_stockZero_isPersistedAsValidBoundary` |
| Oversized `name` (>255 chars) rejected | ✅ | `ProductControllerTest#createProduct_oversizedName_returns400`, `ProductRequestValidationTest#oversizedName_isRejected` |
| Oversized `category` (>255 chars) rejected | ✅ | `ProductControllerTest#createProduct_oversizedCategory_returns400`, `ProductRequestValidationTest#oversizedCategory_isRejected` |
| `price` with more than 2 decimal places rejected | ✅ | `ProductControllerTest#createProduct_priceWithMoreThanTwoDecimals_returns400`, `ProductRequestValidationTest#priceWithTooManyDecimals_isRejected` |
| Unique product ID generated per creation | ✅ | `ProductServiceImplTest#createProduct_mapsRequestToEntity_savesAndReturnsResponse` (asserts unsaved entity has null ID, response has DB-assigned ID), `ProductRepositoryTest` |
| Product persisted in H2 | ✅ | `ProductRepositoryTest` (`@DataJpaTest`), `ProductServiceApplicationIntegrationTest` (real H2, full context) |
| Failures return consistent error response via global exception handler | ✅ | `GlobalExceptionHandlerTest#handleValidation_returnsBadRequestWithFieldErrors`, `GlobalExceptionHandlerTest#handleGeneric_returnsInternalServerError`, `ProductServiceApplicationIntegrationTest#createProduct_invalidPayload_returns400WithErrorResponseShape` |
| Request/response payload shape matches contract | ✅ | `ProductControllerTest` JSON-path assertions, `ProductResponseTest`, `ErrorResponseTest` |
| Malformed/unreadable JSON request body returns a consistent 400 error (not 500) | ✅ | `ProductControllerTest#createProduct_malformedJson_returns400` (asserts `400` status, `status: 400` in body, and `verifyNoInteractions(productService)`), backed by `GlobalExceptionHandler#handleMalformedJson` |

All positive, negative, validation, and boundary scenarios named in the user's cross-check list (missing fields, `price` = 0/negative, negative stock, oversized name/category, price with >2 decimals, `stock` = 0 boundary, malformed JSON body) have direct automated test coverage at both the unit-validation level and the HTTP-controller level, with the happy path additionally re-verified end-to-end.

## 5. Gaps & Follow-ups

- **Resolved since last verification**: malformed/non-JSON request bodies are now explicitly covered by `ProductControllerTest#createProduct_malformedJson_returns400`, and the previously-generic 500 response is now a proper 400 via `GlobalExceptionHandler#handleMalformedJson`. This also closes an implicit information-disclosure/robustness concern (unhandled parse errors no longer surface a 500 with a stack-trace-adjacent generic handler).
- **Resolved since last verification**: the H2 console is now disabled by default (`spring.h2.console.enabled: false` in `src/main/resources/application.yml`), closing the OWASP A05 (Security Misconfiguration) finding — no test asserts this directly, but it is a configuration-only change verifiable by inspection and does not require a dedicated test case.
- No negative test exists for a **duplicate product name** — but per requirements.md open question #6, this is explicitly assumed out of scope (no uniqueness constraint required), so this is not a gap against current acceptance criteria.
- Coverage is measured and enforced only on the `product-service` module; there is no cross-module or contract-test verification against the actual Swagger/OpenAPI document (requirements.md flags the authoritative contract location as an open question). Recommend confirming/adding an OpenAPI schema diff check once the contract source is available.
- The 80% JaCoCo threshold excludes `ProductServiceApplication` and `config/**`; this is a reasonable, standard exclusion but should remain visible/documented in the pom (it currently is) so it isn't mistaken for gaming the metric.

No coverage or scenario gaps were found that block acceptance of KAN-7 against the current [requirements.md](requirements.md) acceptance criteria.
