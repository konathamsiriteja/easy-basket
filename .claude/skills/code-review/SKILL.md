---
name: code-review
description: "Use when: reviewing implemented code changes before a pull request, merge, or hand-off, checking correctness, security, error handling, test coverage, code clarity, DRY, and dependency safety."
argument-hint: "Changed files or a pull request/branch to review"
---

# Code Review

Review the actual diff against `documents/requirements.md` (when present) and the repository's conventions. Report findings per checklist item; do not silently fix code unless the user explicitly asks for fixes.

## Checklist

1. **Correctness** — Does each changed component behave as specified in `documents/requirements.md`? Trace each acceptance criterion to the code that implements it and flag any criterion left unmet.
2. **Security** — Are secrets, tokens, or credentials excluded from source, logs, and command output? Is all external and user input validated before use (type, range, length, format)?
3. **Error Handling** — Are API/network failures, missing files, empty repositories, and empty/malformed responses handled gracefully, with clear errors rather than crashes or silent failures?
4. **Test Coverage** — Do tests cover the happy path AND "Not Found" / missing-field / boundary edge cases? Reject coverage that only exercises the success path.
5. **Code Clarity** — Are function and variable names self-explanatory? Is the logic easy to follow without relying on comments to explain intent?
6. **DRY Principle** — Is there duplicated logic across files or functions that should be refactored into a shared function or module?
7. **Dependency Safety** — Do any added or updated dependencies have known-vulnerable versions? Run the repository's dependency audit command (for example `npm audit`, `pip-audit`, or the equivalent for the detected stack) when available.

## Procedure

1. Identify the changed files (diff, branch, or file list supplied by the caller).
2. Read `documents/requirements.md` and `documents/architecture.md` if present to ground the Correctness check.
3. Walk the checklist in order above. For each item, record: **Pass**, **Issue** (with file/line and concrete fix suggestion), or **Not Applicable** (with reason).
4. Run an available dependency audit command for the Dependency Safety check; report tool output, not assumptions.
5. Summarize with an overall verdict: **Approve**, **Approve with suggestions**, or **Changes requested**, and list blocking issues separately from nice-to-have suggestions.

## Guardrails

- Do not approve when a checklist item has a blocking, unresolved **Issue** (missing input validation, exposed secret, unhandled failure path, missing edge-case test, or a known-vulnerable dependency).
- Do not invent line numbers or file paths; only cite what was actually read.
- Do not rewrite code as part of the review unless explicitly asked; propose the fix instead.
