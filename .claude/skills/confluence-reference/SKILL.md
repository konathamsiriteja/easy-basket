---
name: confluence-reference
description: "Use when: reading Confluence pages linked from a Jira issue, or referenced directly by URL/page ID/title, to gather supporting requirements, design notes, or specifications through the mcp-atlassian MCP server."
argument-hint: "Confluence page URL, page ID, or a Jira issue key that links to one"
---

# Confluence Reference

Use the `mcp-atlassian` MCP server as the system of record for Confluence content. Treat it as read-only reference material unless the user explicitly asks for a Confluence edit.

## Procedure

1. **Find the reference.** If a Confluence URL or page ID was given directly, use it. Otherwise, locate it from a Jira issue: check the issue's description, comments, and remote/web links (retrieved via the Jira skill) for Confluence URLs.
2. **Resolve the page.** If only a URL is available, extract the space key and page title/ID from it. Fetch the page with `mcp_atlassian_mcp_confluence_get_page`, or use `mcp_atlassian_mcp_confluence_search` when only a title or keyword is known.
3. **Expand when needed.** If the page is a parent/index page, use `mcp_atlassian_mcp_confluence_get_page_children` to find the specific child page relevant to the requested scope.
4. **Summarize, do not dump.** Convert the retrieved content into the relevant structured points (functional behavior, non-functional constraints, diagrams described in text, open decisions) rather than pasting raw markup.
5. **Cite the source.** Record the page title, URL, and space for every fact pulled from Confluence so it can be traced back later.

## Guardrails

- Do not fabricate Confluence content. If a linked page cannot be found or access is restricted, report the broken/inaccessible link instead of guessing its content.
- Do not modify, comment on, or restrict a Confluence page unless the user explicitly asks for that action.
- Do not treat Confluence content as higher priority than the Jira issue itself — Confluence supplements the Jira requirement, it does not replace it.
