# Docs Index

Read this file first. It tells you what every doc contains and where to write new knowledge.

---

## Project knowledge

| File | What it contains | Write here when… |
|------|-----------------|------------------|
| `docs/scope-and-tech.md` | What's in/out of scope, tech stack, world units (inches, BW, map size) | Scope changes, new tech choices |
| `docs/decisions.md` | Every design/architecture decision with rationale; open questions | Any decision is made or reversed |
| `docs/game rules/dba2.2 Wadbag rules.md` | DBA 2.2 geometry rules reference (spatial rules only, indexed, ~400 lines) | — read-only reference |
| `docs/game rules/DBA_2_2_Unofficial_Guide.md` | Complete DBA 2.2 rules (full rulebook, all topics) | — read-only reference |
| `docs/game rules/dba_summary.md` | DBA 2.2 condensed: entities, turn, movement, combat, win conditions | — read-only reference |
| `docs/game rules/mighty_armies_ancients_core_rules.md` | Complete Mighty Armies: Ancients rules | — read-only reference |
| `docs/game rules/mighty_armies_summary.md` | Mighty Armies condensed: entities, turn, movement, combat, win conditions | — read-only reference |

## Tech docs

| File | What it contains | Write here when… |
|------|-----------------|------------------|
| `docs/tech docs/build.md` | Gradle commands (run, build, APK, JARs), module layout, JVM config | Build commands change |
| `docs/tech docs/libgdx-apis.md` | libGDX API reference: Vector2, MathUtils, Polygon, Intersector, ShapeRenderer, SpriteBatch, Scene2D, Viewport, Input, lifecycle, gotchas | New libGDX API knowledge |
| `docs/tech docs/scene2d_guide.md` | Scene2D guide: Actor, Stage, Group, Action, Event, Skin, Widgets (Spanish) | — reference |
| `docs/tech docs/ashley_guide.md` | Ashley ECS guide: Entity, Component, System, Engine (Spanish) | — reference |
| `docs/tech docs/artemis_guide.md` | Artemis-ODB ECS guide: more advanced than Ashley; Aspect, Manager, DI (Spanish) | — reference |
| `docs/tech docs/hybrid_guide.md` | Hybrid OOP+components architecture guide; comparison of all paradigms (Spanish) | — reference |

---

## Note on duplicates

`rulesets/` mirrors `docs/game rules/` exactly. The canonical copies are in `docs/game rules/`. Ignore `rulesets/`.

---

## Where to add entirely new knowledge

- New game rule interpretation → `docs/decisions.md` (as a decision or open question)
- New tech guide for a library → new file in `docs/tech docs/`
- New ruleset → new file in `docs/game rules/`
- Scope or stack change → `docs/scope-and-tech.md`
