<!--
Sync Impact Report
Version change: 1.1.0 → 1.2.0
Modified principles:
- None (existing titles retained, content translated to English)
Added sections:
- Principle V. Language Governance
Removed sections:
- None
Templates requiring updates:
- ✅ .specify/templates/plan-template.md (reinforce zh-TW output requirement after constitution language switch)
- ✅ .specify/templates/spec-template.md (reinforce zh-TW output requirement after constitution language switch)
- ✅ .specify/templates/tasks-template.md (reinforce zh-TW output requirement after constitution language switch)
- ⚠ .specify/templates/commands (directory still missing; needed for language policy propagation)
Follow-up TODOs:
- TODO(COMMAND_TEMPLATES): Create `.specify/templates/commands` entries so command docs enforce bilingual governance.
-->

# Business Card Constitution

## Core Principles

### I. Code Quality Stewardship
- Every change MUST leave the codebase healthier: reduce lint/static-analysis debt, eliminate dead code, and document architectural intent inside the impacted modules.
- Owners MUST publish module boundaries, dependencies, and contracts in `/specs/.../plan.md` or companion docs before implementation begins; code merged without synchronized docs is non-compliant.
- Code reviews MUST apply the craftsmanship checklist (readability, failure handling, security, logging); failure on any item blocks the merge.
- Continuous integration MUST run formatters, linters, and dependency vulnerability scans; failures halt the pipeline until resolved.
*Rationale: Quality is the cheapest place to catch defects and protects Business Card's lightweight maintenance model.*

### II. Tests Define Reality
- Feature work starts with executable acceptance criteria: at least one failing integration or contract test per user story plus unit coverage for critical branches.
- Core modules MUST maintain >=90% statement coverage and >=75% branch coverage; waivers require explicit justification in the plan checklist.
- Regression, accessibility, and performance suites MUST run in CI, and any failure blocks release until fixed or jointly waived by product and engineering leads.
- Test artifacts (fixtures, datasets, goldens) MUST be versioned with the feature to guarantee reproducibility.
*Rationale: Business Card is trustworthy only when behavior is continuously proven; untested code is unscheduled rework.*

### III. Unified Experience Guarantees
- All user experiences MUST implement the shared design language (typography, spacing, color tokens, motion) captured in the latest UX kits; deviations require a recorded design decision in the spec.
- Every screen or interaction MUST log accessibility evidence demonstrating WCAG 2.1 AA conformance, preferred input methods, and localization behavior.
- Cross-device validation (desktop, tablet, mobile widths) MUST be automated via visual regression or snapshot tests for any user-visible change.
- Copy, iconography, and interaction patterns MUST stay consistent; reuse shared components before introducing variants and record rationale if a new pattern is unavoidable.
*Rationale: Consistency sustains user trust and keeps the Business Card brand recognizable everywhere.*

### IV. Performance Predictability
- Each feature spec MUST declare budgets (e.g., <=200 ms input-to-render, <=1 MB incremental payload, <=5% CPU overhead). Missing budgets are blockers.
- Instrumentation for those budgets (profilers, RUM hooks, synthetic monitors) MUST ship with the feature; dashboards MUST alert on >=5% regression.
- Performance tests MUST run in CI for hot paths; failing tests stop merges unless an approved exception with a remediation date exists.
- Build artifacts MUST remain deterministic and cacheable; optimization flags default to the safest, fastest setting.
*Rationale: Business Card succeeds only when it feels instant on commodity hardware; performance debt compounds faster than functional debt.*

### V. Language Governance
- This constitution MUST remain in English to guarantee universal interpretation across teams and tooling.
- Specifications, implementation plans, and all user-facing documentation (README excerpts, quickstarts, help centers, marketing copy) MUST be authored and maintained in Traditional Chinese (zh-TW); other languages MAY exist only as appendices referencing the zh-TW source of truth.
- `/speckit.*` command outputs MUST verify that their generated artifacts are zh-TW compliant before handing off for review.
- Transliteration or machine-generated zh-TW content MUST be reviewed by a native or fluent speaker before publication.
*Rationale: English governance keeps the rules stable across jurisdictions, while zh-TW delivery ensures regional alignment with Business Card's audience.*

## Delivery Standards & Constraints
- Specs, plans, and task docs MUST spell out how each principle is satisfied; missing evidence blocks `/speckit.*` commands.
- Specs, plans, and user-facing docs MUST be written in zh-TW; if supplemental languages exist they are subordinate to the zh-TW canonical copy.
- Documentation (README excerpts, quickstarts, API descriptions) MUST be updated with the code; documentation debt counts as code debt.
- Production telemetry, logging, and dashboards MUST be provisioned before serving traffic.
- Third-party dependencies MUST be evaluated for license compatibility and performance footprint; upgrades require recorded benchmarks.

## Development Workflow & Quality Gates
- `/speckit.plan`, `/speckit.specify`, and `/speckit.tasks` outputs MUST embed the Constitution Check, summarizing compliance evidence across all principles.
- Pull requests MUST link to their plan/spec/tasks artifacts, note which constitutional gates they satisfy, and confirm zh-TW coverage for user-facing deliverables.
- Releases MUST include a signed changelog entry summarizing quality, testing, experience, performance, and language impacts plus links to passing test runs.
- Quarterly audits review lint debt, coverage, UX regressions, performance budgets, and zh-TW documentation health, then schedule remediation whenever drift occurs.

## Governance
The Business Card Constitution supersedes bespoke practices. Amendments require (1) a written proposal referencing this document, (2) review by engineering, design, and product leads, (3) consensus recorded in version control, and (4) synchronized updates to dependent templates or tools. Version numbers follow semantic rules: MAJOR for breaking governance/principle removals, MINOR for new principles/sections or materially stronger mandates, PATCH for clarifications. Ratified versions stay in force until superseded, and every release cycle includes compliance checks that ensure specs, plans, tasks, and runtime documentation uphold the constitution.

**Version**: 1.2.0 | **Ratified**: 2025-11-21 | **Last Amended**: 2025-11-21
