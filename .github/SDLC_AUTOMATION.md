# SDLC Automation

Use `/Implement Jira Issue JIRA-123 product-api` to start the end-to-end workflow.

The workspace customizations are organized by responsibility:

- `skills/jira-work-item`: requirements gathering and Jira workflow transitions through `mcp-atlassian`.
- `skills/confluence-reference`: reads Confluence pages linked from a Jira issue through `mcp-atlassian`.
- `skills/github-delivery`: feature branches and pull requests through GitHub MCP.
- `skills/implementation-quality`: code, positive and negative tests, and 80% coverage.
- `skills/code-review`: correctness, security, error handling, test coverage, clarity, DRY, and dependency-safety checklist.
- `agents/requirement-analyst.agent.md`: reads the Jira issue (and any linked Confluence page) and writes `documents/requirements.md`.
- `agents/solution-architect.agent.md`: reads `documents/requirements.md` and writes `documents/architecture.md`.
- `agents/design-review-agent.agent.md`: reviews `documents/architecture.md` for risks/gaps, agrees decisions with the user, writes `documents/design-review.md`.
- `agents/implementation-planning-agent.agent.md`: breaks `documents/architecture.md` into a dependency-ordered `documents/impl-plan.md`.
- `agents/backend-developer.agent.md`: isolated implementation and test authoring work, driven only by `documents/impl-plan.md` after human approval.
- `agents/test-agent.agent.md`: independent coverage verification and `documents/testing-strategy.md`.
- `agents/code-review-agent.agent.md`: applies the `code-review` skill checklist and returns a verdict.
- `agents/sdlc-orchestrator.agent.md`: ordered orchestration and delivery gates across all of the above.

All agent-generated documentation is centralized under the `documents/` folder at the repository root, so requirements, architecture, design review, implementation plan, and testing artifacts stay in one predictable, git-tracked location across every run of the workflow.

## Prerequisites

- Configure `JIRA_URL`, `JIRA_USERNAME`, `JIRA_API_TOKEN`, `CONFLUENCE_URL`, `CONFLUENCE_USERNAME`, and `CONFLUENCE_API_TOKEN` for the `mcp-atlassian` server in `.vscode/mcp.json`.
- Authenticate the GitHub MCP server with an account that can create branches and pull requests in the target repository.
- Ensure the repository has a repeatable test command that can generate coverage before starting a delivery.

## Workflow Gates

1. Jira issue is readable (with any linked Confluence page summarized) and `documents/requirements.md` is produced; issue transitions to `In Progress`.
2. `documents/architecture.md` is produced from `documents/requirements.md`.
3. Design risks/gaps are reviewed and agreed with the user in `documents/design-review.md`; `documents/architecture.md` is updated if issues are confirmed.
4. `documents/impl-plan.md` is produced from the reviewed architecture, dependency-ordered with blocked tasks flagged.
5. The user explicitly approves `documents/impl-plan.md` (human-in-the-loop gate) before any branch or code is created.
6. A non-conflicting `feature/<JIRA-KEY>-<feature>` branch is created from the default branch.
7. Acceptance criteria have positive and negative automated tests, independently verified with at least 80% measured coverage in `documents/testing-strategy.md`.
8. Code review verdict is Approve or Approve with suggestions (not Changes requested).
9. Feature changes are pushed and a pull request is created.
10. Jira transitions to `In Review` with the pull request URL recorded.

The automation intentionally stops at the first failed gate to keep Jira, source control, and review status truthful.