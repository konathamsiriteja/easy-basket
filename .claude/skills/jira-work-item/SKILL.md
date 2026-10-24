---
name: jira-work-item
description: "Use when: gathering requirements from a Jira issue, moving an issue to In Progress or In Review, or reporting Jira delivery status through the mcp-atlassian MCP server."
argument-hint: "Jira issue key, for example JIRA-123"
---

# Jira Work Item

Use the `mcp-atlassian` MCP server as the system of record for delivery requirements and status. Do not infer Jira fields or workflow transition identifiers.

## Procedure

1. Read the issue with `mcp_atlassian_mcp_jira_get_issue` using the supplied issue key.
2. Extract the summary, description, acceptance criteria, priority, linked issues, attachments, comments, and current status. Report gaps or contradictions before changing code.
3. Retrieve available transitions with `mcp_atlassian_mcp_jira_get_transitions`.
4. Find the transition whose destination status is exactly `In Progress`, then call `mcp_atlassian_mcp_jira_transition_issue` with its returned transition ID. Never assume a numeric transition ID.
5. Before handing work to review, retrieve transitions again. Find the destination status matching `In Review` (case-insensitive); transition only after the pull request is created and its URL is available.
6. Add a concise Jira comment with the branch name, pull request URL, test command, coverage result, and any known limitation when comments are enabled for the project.

## Guardrails

- Stop and report if the issue cannot be read, is already closed, or has no matching workflow transition.
- Do not transition a Jira issue to `In Review` without a pull request URL and passing validation evidence.
- Keep the issue key unchanged in branch names, commits, pull-request titles, and comments.