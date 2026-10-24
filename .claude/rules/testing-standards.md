---
paths:
  - "product-service/src/test/java/**/*.java"
---

# Testing Standards (product-service)

- Minimum 80% line AND branch coverage, enforced by JaCoCo at the Maven `verify` phase (`product-service/pom.xml`). A feature is not done until `mvn verify` passes locally — don't claim coverage without running it.
- Use the test layer that matches what's under test, mirroring the existing suite:
  - `@WebMvcTest` — controller + validation behavior.
  - `@DataJpaTest` — repository/persistence behavior.
  - Plain JUnit 5 + Mockito unit tests — service-layer logic.
  - One `@SpringBootTest` integration test per feature — full happy path plus a representative error path through the real H2 instance.
- Every new endpoint needs at minimum: a happy-path test, a missing-mandatory-field test, boundary tests (valid edge, e.g. `stock=0`, and invalid edge, e.g. `price=0`), and a malformed-JSON-body test — mirror `ProductControllerTest` and `GlobalExceptionHandlerTest`.
- Test classes live in the same package path as the class under test, under `src/test/java`, named `<ClassUnderTest>Test` (e.g. `ProductServiceImplTest` for `ProductServiceImpl`).
- Never weaken assertions, delete tests, or change unrelated production code solely to hit the 80% threshold — fix the real gap instead.
