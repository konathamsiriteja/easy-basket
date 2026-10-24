---
name: github-delivery
description: "Use when: creating a Jira feature branch, checking GitHub repository access, pushing implementation changes, or opening a pull request through the GitHub MCP server."
argument-hint: "Jira key and feature name, for example JIRA-123 product-api"
---

# GitHub Delivery

Use the GitHub MCP server for remote GitHub operations. Use local Git commands only to edit, stage, commit, and push the workspace when the remote operation is not provided by MCP.

## Branch Procedure

1. Call `mcp_github_mcp_se_get_me` before any other GitHub MCP operation to confirm the authenticated account.
2. Confirm the repository and read its branch list with `mcp_github_mcp_se_list_branches`.
3. Create the branch name as `feature/<JIRA-KEY>-<kebab-case-feature>`. For example: `feature/JIRA-123-product-api`.
4. Ensure the branch does not already exist, then call `mcp_github_mcp_se_create_branch` from the repository default branch. Record the source commit SHA.
5. Check out the remote branch locally before implementation. Keep commits scoped to the Jira requirement and do not include credentials or unrelated worktree changes.

## Pull Request Procedure

1. Confirm the final local branch, changed files, test command, coverage result, and remote URL.
2. Push the feature branch to the remote repository.
3. Search for pull-request templates at `.github/pull_request_template.md` and `.github/PULL_REQUEST_TEMPLATE/`; use one as the base structure if found, but still include every required section below.
4. Create a pull request with a title beginning with the Jira key and a body containing exactly these sections, in order:
   - **Summary** — a 2-3 sentence overview of what was built and why.
   - **Changes Made** — a bulleted list of every file added/modified and the reason for each change.
   - **Test Evidence** — the actual test run output (or a link to the CI run) showing the command and result, including measured coverage.
   - **Known Limitations** — anything left "Not Found", deferred, or explicitly out of scope for this change.
   - **Reviewer Checklist** — a Markdown tick-list (`- [ ] ...`) of items the reviewer must verify before approving (e.g. acceptance criteria met, tests pass, no secrets committed, docs updated).
5. Return the pull request URL, branch name, commit SHA, and validation evidence to the orchestrator.

## Guardrails

- Never create a branch that collides with an existing branch.
- Never push secrets, generated credentials, or unrelated files.
- Never publish a pull request missing any of the five required description sections above.
- Never create a pull request unless tests pass and measured coverage is at least 80%, unless the user explicitly authorizes an exception that is documented in the pull request.