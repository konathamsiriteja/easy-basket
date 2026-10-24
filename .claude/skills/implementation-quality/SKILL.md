---
name: implementation-quality
description: "Use when: implementing a Jira feature, adding positive and negative test journeys, selecting the repository test tooling, and verifying at least 80 percent coverage."
argument-hint: "Jira requirements and acceptance criteria"
---

# Implementation Quality

Deliver the Jira acceptance criteria as maintainable production code with focused automated tests for successful and failing behavior.

## Procedure

1. Inspect the repository's language, package manager, application structure, linting, testing framework, and existing test conventions before editing.
2. Convert each acceptance criterion into observable positive and negative scenarios. Include validation failures, authorization or boundary failures when applicable, error handling, and edge cases.
3. Implement the smallest coherent production change that satisfies the requirement. Preserve existing public APIs and project conventions.
4. Add or update automated tests beside the relevant implementation using the repository's established test framework.
5. Run the narrow affected tests first, then the project test suite with coverage enabled. Prefer the repository's existing coverage command; otherwise use the framework-supported coverage mode.
6. Capture the measured line or statement coverage. The changed feature must be covered by tests, and overall reported coverage must be at least 80%.
7. Run the applicable lint, typecheck, build, or formatter checks. Report exact commands and results.

## Completion Contract

Do not claim completion until all of these are true:

- Positive and negative journeys have automated tests.
- The relevant test suite passes.
- Coverage is measured and is at least 80%.
- Static checks required by the repository pass.
- The final response identifies changed files, test evidence, coverage, and residual risks.

## Guardrails

- Do not fabricate coverage. If coverage cannot run, diagnose the repository setup and stop before pull-request creation.
- Do not lower coverage thresholds, skip tests, or weaken assertions solely to satisfy the threshold.
- Do not change unrelated code to inflate coverage.