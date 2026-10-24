---
description: "Start the gated end-to-end SDLC workflow for a Jira issue, from requirements through pull request and In Review transition."
argument-hint: "[Jira issue key] [optional feature slug]"
---

Delegate this entire workflow to the `SDLC Orchestrator` subagent (via the Agent tool), passing it the Jira issue key and feature slug: $ARGUMENTS

Wait for it to report back, including any point where it stops to ask for human approval, and relay that to the user.
