# Project Scope and Technology

---

## What we are building

A 2D top-down wargame faithful to the geometry and movement rules of **DBA 2.2** (De Bellis Antiquitatis), implemented as a digital game using **libGDX**.

The goal of the current phase is to get the **spatial and movement mechanics exactly right** before adding any game-state or combat logic.

---

## In scope (current phase)

| Topic | Detail |
|---|---|
| Element modelling | Rectangular bases with position, facing, width, depth |
| Single-element movement | Flexible move with corner-distance cost, ZOC restrictions |
| Group movement | Formation detection, pivot, forward translate, sideways slide |
| Column movement | Follow-the-leader movement, 1-element-wide formation |
| Reducing front | Pass through a gap by queuing trailing elements behind |
| Zone of Control | ZOC rectangle in front; movement restrictions inside ZOC |
| Contact detection | Front, flank, rear — corner-aligned only |
| Overlap detection | Corner-to-corner with a friendly in front contact |
| Closing the door | Overlap → flank contact, may exceed movement cap |
| Cutting the corner | Single-corner path crossing a friendly = allowed |
| Separating from contact | Disengage rules (straight back, min distance) |
| Facing adjustment | Auto-rotation at end of movement phase |
| Gap rule | Cannot pass through gaps < 1 BW; swept-path check |
| Rendering | Visual display of elements, ZOC, contact state, move preview |
| Input | Click/drag to select and move elements; turn management |

---

## Out of scope (this phase)

| Topic | Reason |
|---|---|
| Combat (melee, ranged, resolution) | Deferred — geometry first |
| Element types (Knight, Spear, Psiloi, etc.) | All elements use the same stats for now |
| Different move speeds per type | Deferred with element types |
| Army building / lists | Deferred |
| Terrain effects on movement | No terrain in prototype |
| Board edges | No board boundary in prototype |
| Terrain pieces (hills, forests, rivers, BUAs) | Deferred |
| ZOC rolling-carpet blocking | Simplified to fixed 1BW×1BW rectangle for now |
| Full pass-through rules (cavalry through Psiloi) | Deferred; only cutting-the-corner modelled |
| Camps and BUAs as obstacles | Deferred |
| Dismounting | Deferred |
| Coastal landings | Deferred |
| Second moves | Deferred |
| Multiplayer / network | Not planned |

---

## Technology

| Layer | Choice |
|---|---|
| Language | **Kotlin** |
| Game framework | **libGDX 1.12.1** |
| Build tool | **Gradle 8.x** |
| JVM | **Java 21** (OpenJDK) |
| Desktop backend | libGDX LWJGL3 |
| Rendering | libGDX `ShapeRenderer` (no sprites needed for prototype) |
| UI | libGDX `Scene2D` (Stage, Table, TextButton) |

---

## World coordinate system

| Item | Value |
|---|---|
| World unit | **inch** |
| Base width (BW) | **1.6 inches** (= 40 mm at 15 mm scale) |
| Base depth (prototype) | **0.8 inches** (= 0.5 BW) |
| Movement allowance (prototype, all elements) | **2.0 inches** open terrain / **4.0 inches** on road |
| Map size | **24 × 24 inches** (≈ 60 cm × 60 cm, standard DBA map) |
| Coordinate origin | Bottom-left (libGDX Y-up) |
| Angle 0° | Facing east (+X); 90° = north (+Y) |

---

## Source rules reference

*De Bellis Antiquitatis 2.2 — Unofficial Spanish Guide*
Original work: Wadbag — Translation: La Armada
www.wadbag.com / www.laarmada.org

Geometry rules are documented in `docs/rules-geometry.md`.
