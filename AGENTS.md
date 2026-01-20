# Repository Guidelines

## Project Structure & Module Organization
- Root is a Maven multi-module: `models` (shared entities like `User`, `Order`, `PaymentInfo`), `components` (common config, cache, global exception), `services` (per-domain apps such as `product`, `order`), each with `src/main/java` and `src/main/resources`.
- Controllers live under `edu.nchu.mall.services.<module>.controller`; Swagger config must scan the matching package (order module uses `edu.nchu.mall.services.order.controller`).
- Database DDL lives in `sql/` (e.g., `sql/order.sql`); keep entity fields in sync with table columns and table comments.

## Build, Test, and Development Commands
- `mvn clean install` – full build plus tests; run before pushing.
- `mvn test` – run all unit tests; default local sanity check.
- `mvn -pl services/order -am package` (or replace `order` with another module) – faster scoped build.
- `mvn -pl services/order -am spring-boot:run` – boot the order service locally with dependencies built.

## Coding Style & Naming Conventions
- Java: 4-space indent, UTF-8, braces on the same line; classes PascalCase, methods/fields camelCase, packages lowercase dotted.
- MyBatis-Plus entities require `@TableName/@TableField/@TableId`; add `@Schema` on every field with concise descriptions aligned to DB meaning.
- Controllers return the shared `R`/`RCT` wrapper; REST paths follow domain prefixes (`/orders`, `/order-items`, `/payment-infos`, etc.).
- SQL/migration files should be descriptive (`order-ops-202601.sql`) rather than generic.

## Testing Guidelines
- Tests reside in `src/test/java`; mirror target class names with `*Test` suffix (`OrderServiceImplTest`).
- Prefer JUnit 5; mock external IO (DB/Redis/Nacos) when feasible to keep suites fast and deterministic.
- Document any skipped tests or manual verification in PRs; default expectation is `mvn test` green.

## Commit & Pull Request Guidelines
- Commit message format: `type(scope): subject` (e.g., `feat(order): add refund api`), subject ≤50 chars; wrap body at 72 chars.
- PRs must summarize changes, list test commands/results, and link issues/Jira; include screenshots or curl examples for API/behavior changes.
- Call out schema/config impacts (Nacos, Redis, DB migrations) and rollout/rollback notes to ease review.

## Security & Configuration Tips
- Do not commit secrets; keep local overrides in ignored files like `application-local.yml`.
- When changing cache keys, TTLs, or DB DDL (e.g., `sql/order.sql`), explain compatibility and deployment steps in the PR. 
