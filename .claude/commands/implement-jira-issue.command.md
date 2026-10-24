---
name: "Implement Jira Issue"
description: "Start the gated end-to-end SDLC workflow for a Jira issue, from requirements through pull request and In Review transition."
agent: "SDLC Orchestrator"
argument-hint: "Jira issue key, optionally followed by a feature slug"
---

Run the complete SDLC workflow for the Jira issue key and optional feature slug supplied as this prompt's argument.

Use the Jira issue key as the source of truth. Follow every gate in the SDLC Orchestrator instructions and stop with a precise report if any required system, transition, validation command, or coverage threshold is unavailable.