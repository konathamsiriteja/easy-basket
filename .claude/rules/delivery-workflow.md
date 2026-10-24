# Jira / Confluence / GitHub Delivery Workflow

This project delivers features through Jira + Confluence + GitHub MCP servers (see `.vscode/mcp.json`), using the ported skills in `.claude/skills/`.

- **Jira** (`mcp-atlassian` MCP server) is the system of record for requirements and status. Use the `jira-work-item` skill to read an issue, transition it (`In Progress` → `In Review`), and report delivery status. Never invent field values or transition IDs — always fetch them first.
- **Confluence** (same MCP server) supplements Jira with linked design/spec detail. Use the `confluence-reference` skill. Treat it as read-only reference; cite page title/URL/space for anything pulled from it. If a linked page is missing or inaccessible, report that rather than guessing its content.
- **GitHub** (GitHub MCP server) handles all remote repo operations — branch creation, pushing, and opening the pull request ("MR"). Use the `github-delivery` skill. Branch naming: `feature/<JIRA-KEY>-<kebab-case-feature>`. Local git handles staging/committing; the MCP server handles the remote branch/PR actions.
- **Documentation flow** for every new API/feature mirrors KAN-7: `documents/requirements.md` → `documents/architecture.md` (+ `documents/design-review.md` if risks are found) → `documents/impl-plan.md` → implementation → `documents/testing-strategy.md`. Update these existing docs for the new feature rather than creating parallel ones elsewhere.
- **Implementation quality gate**: use the `implementation-quality` skill — positive and negative test journeys, ≥80% measured coverage, applicable lint/build checks — before considering a feature done.
- A pull request is not opened until tests pass and coverage is ≥80%, and its description includes: Summary, Changes Made, Test Evidence, Known Limitations, and a Reviewer Checklist.
- Don't transition a Jira issue to `In Review` without a pull request URL and passing validation evidence.
