---
name: workflow-approval
description: "Mandatory workflow for all changes: requires prior approval report before executing any change, and a completion report after. Never switch from plan to build mode without explicit user permission. Use on every project interaction involving file modifications, builds, or game launches."
---

# Workflow & Approval Skill

## Rules

1. **Plan vs Build** — Never switch from `plan` mode to `build` mode without asking for explicit user permission first.

2. **Pre-Change Report** — Before making any modification (editing files, writing new files, running builds, launching the game, or installing tools), generate a report explaining:
   - What will be changed
   - Why it needs to change
   - A summary of the diff or new content
   - Any risks or side effects
   Then **ask for permission** before proceeding.

3. **Post-Change Report** — After each change is executed, generate a report of:
   - What was actually done (files modified/created/deleted)
   - The outcome (success, errors, warnings)
   - Any follow-up steps needed

4. **Scope** — These rules apply to all project interactions: editing source code, resource files, build scripts, configuration files, running gradle tasks, launching the game, or installing dependencies.
