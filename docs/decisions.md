# Design Decisions and Element Modelling

This file records decisions that have been made, the reasoning behind them, and open questions yet to be resolved.

---

## Index

1. [Language and framework](#1-language-and-framework)
2. [World unit](#2-world-unit)
3. [Element representation](#3-element-representation)
4. [Coordinate system and facing](#4-coordinate-system-and-facing)
5. [Corner computation](#5-corner-computation)
6. [Movement cost formula](#6-movement-cost-formula)
7. [Collision / swept-path strategy](#7-collision--swept-path-strategy)
8. [ZOC simplification](#8-zoc-simplification)
9. [Close-door destination](#9-close-door-destination)
10. [Contact tolerance](#10-contact-tolerance)
11. [Facing adjustment timing](#11-facing-adjustment-timing)
12. [Architecture: game logic vs rendering](#12-architecture-game-logic-vs-rendering)
13. [ElementActor wraps GameElement](#13-elementactor-wraps-gameelement)
14. [Rendering an element with ShapeRenderer](#14-rendering-an-element-with-shaperenderer)
15. [Architecture: input systems](#15-architecture-input-systems)
16. [Open questions](#16-open-questions)

---

## 1. Language and framework

**Decision**: Kotlin + libGDX 1.12.1, desktop via LWJGL3.

**Reason**: Kotlin is more concise than Java for geometric data classes and extension functions. libGDX provides `Polygon`, `Intersector`, `Vector2`, `MathUtils`, and `ShapeRenderer` — everything needed for 2D geometry without additional dependencies.

---

## 2. World unit

**Decision**: 1 world unit = 1 inch.

**Reason**: DBA 2.2 gives all distances in pasos, where 100 pasos = 1 inch (15 mm scale). Using inches keeps the numbers human-readable (movement = 2.0 in, BW = 1.6 in) and maps directly to the rule descriptions without a conversion factor at every call site.

Conversion reference:
- 1 BW = 1.6 inches (160 pasos)
- 100 pasos = 1.0 inch

---

## 3. Element representation

**Decision**: an element is represented by:
- `position: Vector2` — the **geometric center** of the base, in inches
- `angleDeg: Float` — the **facing direction** in degrees (see §4)
- `width: Float` — length of the front/rear edge (default = 1.6 in = 1 BW)
- `depth: Float` — length of the flank edge (default = 0.8 in = 0.5 BW)
- `faction: Faction` — which side it belongs to

**Reason for center as origin**: libGDX's `Polygon` and `ShapeRenderer` work most naturally with a center point. Rotation math around the center is clean. All 4 corners are derived from center + angle. Alternative considered: front-left corner as origin (maps directly to pivot math) — rejected because center is more symmetric and easier to reason about for collision detection.

**The 4 corners** are computed on demand (not stored), because the element can be rotated freely and caching would require invalidation logic.

---

## 4. Coordinate system and facing

**Decision**: libGDX Y-up coordinate system. `angleDeg = 0` means facing **east** (+X direction); angles increase counter-clockwise.

| angleDeg | Facing |
|---|---|
| 0° | East (+X) |
| 90° | North (+Y) |
| 180° | West (−X) |
| 270° | South (−Y) |

**Forward direction vector**:
```
forward = Vector2(cos(angleDeg), sin(angleDeg))
```
Using `MathUtils.cosDeg` / `MathUtils.sinDeg` (libGDX, table-based, faster than `Math.cos`).

**Right direction vector** (from the element's own perspective, 90° clockwise from forward):
```
right = Vector2(sin(angleDeg), -cos(angleDeg))
```

This gives: facing north (90°) → right = east (+X). Facing east (0°) → right = south (−Y). Correct for a top-down map where "right" from a northward-facing unit is east.

---

## 5. Corner computation

All 4 corners derived from `(position, angleDeg, width, depth)`:

```
halfW = width / 2
halfD = depth / 2

FL = position + forward * halfD - right * halfW   // front-left
FR = position + forward * halfD + right * halfW   // front-right
RL = position - forward * halfD - right * halfW   // rear-left
RR = position - forward * halfD + right * halfW   // rear-right
```

**Polygon vertex order** for libGDX `Intersector.overlapConvexPolygons`: vertices must be in order (clockwise or counter-clockwise, consistent). We use **counter-clockwise**: FL → FR → RR → RL.

Stored as a flat float array: `[FL.x, FL.y, FR.x, FR.y, RR.x, RR.y, RL.x, RL.y]`.

---

## 6. Movement cost formula

**Decision**: cost = maximum straight-line (chord) distance traveled by any single corner.

```
cost = max(FL0.dst(FL1), FR0.dst(FR1), RL0.dst(RL1), RR0.dst(RR1))
```

where subscript 0 = start state, subscript 1 = end state.

**Reason**: the rules explicitly state "distance is measured from the corner that travels the most." The chord (not arc) interpretation is consistent with physical tape measurement across a table.

For a **group**, the cost is the maximum across all corner distances of all elements in the group.

---

## 7. Collision / swept-path strategy

**Decision**: validate movement using a **swept-path check** — sample the element's polygon at multiple intermediate positions along the path from start to end, not just at the destination.

**Reason**: a pure start+end check misses cases where the element passes through another element or a narrow gap during a rotation. The rules prohibit this ("cannot pass through a gap < 1 BW at any point").

**Implementation approach**: linearly interpolate `(position, angleDeg)` between start and end states at N steps, check `Intersector.overlapConvexPolygons` at each step. N is tunable (start with 10; increase if precision issues arise).

**For gap checking**: at each interpolation step, for each pair of nearby obstacles, compute the perpendicular clearance between them in the direction of movement and check if it is ≥ 1 BW.

---

## 8. ZOC simplification

**Decision**: ZOC is modelled as a **fixed 1 BW × 1 BW rectangle** directly in front of the element. The rolling-carpet blocking rule is **not implemented** in the prototype.

**Reason**: the rolling-carpet rule requires computing whether an obstacle spans the full ZOC width, which adds significant complexity. The fixed rectangle is correct for most tactical situations and is much simpler to implement.

**Future work**: add blocking by checking whether any obstacle projects across the full width of the ZOC box along its depth axis.

---

## 9. Close-door destination

**Decision**: the destination of a close-door move is **geometrically fixed** — not chosen by the player.

The destination is uniquely determined by:
- Which front corner of the enemy **A** was sharing with **E** (A's FL at E's FR → A swings to E's right flank; A's FR at E's FL → A swings to E's left flank).
- The final position: A's front-center = E's right-flank-center (or left), and A's facing direction = E's facing direction rotated 90° toward A.

Concretely:
- If A was overlapping on E's **right** (A's FL touches E's FR):
  - A's new facing = E's facing rotated −90° (A faces left from E's perspective, i.e., toward E's right flank)
  - A's new front-center = E's right-flank-center
- If A was overlapping on E's **left** (A's FR touches E's FL):
  - A's new facing = E's facing rotated +90°
  - A's new front-center = E's left-flank-center

---

## 10. Contact tolerance

**Decision**: contact detection uses a **small epsilon tolerance** (e.g., 0.05 in = 5% of BW) for corner proximity checks, not exact floating-point equality.

**Reason**: floating-point arithmetic will not produce exact corner coincidence after rotation and translation. A small epsilon prevents false negatives.

**Open question**: should contact snapping be implemented (auto-align when the player drops an element close to contact)? This would greatly improve usability. Not yet decided.

---

## 11. Facing adjustment timing

**Decision**: the automatic facing adjustment (encaramiento, §14 of rules) happens **at the end of the movement phase**, applied as a post-processing step after all moves are resolved.

It is **not** part of the movement cost calculation. It is a free rotation that consumes no PIPs and no movement budget.

---

## 12. Architecture: game logic vs rendering

**Decision**: game logic, rules, and geometry are strictly separated from rendering and game-loop concerns. Neither layer depends on the other.

### The boundary

```
┌─────────────────────────────────────────────────────────────┐
│  LOGIC LAYER  (pure Kotlin, zero libGDX rendering deps)     │
│                                                             │
│  GameElement      — data: position, angle, width, depth     │
│  GameWorld        — state: element list, turn, selection    │
│  ContactDetector  — front / flank / rear / overlap checks   │
│  CollisionDetector— swept-path and gap checks               │
│  ZoneOfControl    — ZOC shape and containment               │
│  SingleMoveValidator — validates individual moves           │
│  GroupFormation   — group / column detection and pivot math │
│  CloseDoorMove    — cerrar la puerta detection + dest.      │
│  CutCornerDetector— one-corner path crossing check          │
└─────────────────────────────────────────────────────────────┘
           ▲ reads from / calls into
┌─────────────────────────────────────────────────────────────┐
│  RENDERING / ENGINE LAYER  (libGDX, ShapeRenderer, Stage)   │
│                                                             │
│  ElementActor     — wraps GameElement; knows how to draw it │
│  WorldScene       — list of WorldActors; update + render    │
│  GameScreen       — libGDX Screen; orchestrates the loop    │
│  Stage            — Scene2D UI (buttons, labels)            │
└─────────────────────────────────────────────────────────────┘
```

### Rules
- The logic layer has **no imports from `com.badlogic.gdx.graphics`** or any rendering package. It may use `com.badlogic.gdx.math` (Vector2, Polygon, Intersector, MathUtils) because those are pure math utilities with no rendering dependency.
- The rendering layer reads from the logic layer but **never writes game state directly**. Mutations go through `GameWorld` methods.
- Game rules (movement validation, contact detection, etc.) are never computed inside `render()` or `act()` calls. They are computed in response to **input events** and their results cached in game state.

### Render loop shape

```kotlin
// GameScreen.render(delta)
camera.update()
worldScene.update(delta)           // actors update their visual state
worldScene.render(shapeRenderer, camera)  // actors draw themselves
uiStage.act(delta)
uiStage.draw()
```

---

## 13. ElementActor wraps GameElement

**Decision**: `ElementActor` is the primary game-object class. It **owns** and **wraps** a `GameElement`.

```kotlin
class ElementActor(val element: GameElement) : WorldActor() {
    // visual-only state (animations, highlight, etc.)
    override fun update(delta: Float) { /* update visual state */ }
    override fun render(sr: ShapeRenderer) { /* draw element */ }
}
```

**Rationale**:
- `ElementActor` is the live object in the scene. Adding an element to the game means creating an `ElementActor` and adding it to `WorldScene`.
- `GameElement` is pure data + geometry. It doesn't know it's being rendered.
- Other actors (ZocActor, SelectionActor, GhostActor) may hold a **reference** to an `ElementActor` (or its inner `GameElement`) to derive their position/shape, but they do not own it.
- `GameWorld` holds references to `GameElement` objects (the model). `WorldScene` holds `ElementActor` objects (the view). They stay in sync because `ElementActor.element` is the same object instance that `GameWorld` holds — not a copy.

### Actor catalogue (current scope)

| Actor | Owns | Draws |
|---|---|---|
| `ElementActor` | `GameElement` | element base, facing arrow, faction colour |
| `ZocActor` | ref to `ElementActor` | ZOC rectangle (semi-transparent) |
| `SelectionActor` | ref to selected `ElementActor` | move-range circle, selection outline |
| `GhostActor` | proposed `GameElement` state | ghost (translucent) preview of move destination |
| `ContactIndicatorActor` | refs to two `ElementActor`s | colour-coded contact line |

---

## 14. Rendering an element with ShapeRenderer

**Decision**: use `ShapeRenderer.rect(x, y, originX, originY, width, height, scaleX, scaleY, degrees)` to draw the element body and border, with the **depth dimension along the rect's local X axis** and the **width dimension along the local Y axis**, then rotate by `angleDeg`.

### Why this mapping

`ShapeRenderer.rect` defines the unrotated rectangle in local space where local +X is horizontal and local +Y is vertical. The rotation is applied counter-clockwise around the origin point.

In the element model:
- `depth` (0.8 in) = the **forward/backward** dimension — along the facing direction
- `width` (1.6 in) = the **side** dimension — perpendicular to facing

When `angleDeg = 0` (facing east, +X), the element extends 0.8 in along +X and 1.6 in along ±Y. So in local (unrotated) space the rect must be **0.8 wide (local X) × 1.6 tall (local Y)**. Rotating by `angleDeg` then correctly aligns it with the facing direction.

### Correct call pattern

```kotlin
val halfW = element.width / 2f   // 0.8
val halfD = element.depth / 2f   // 0.4

shapeRenderer.rect(
    element.position.x - halfD,  // bottom-left x  (depth is the local-X dimension)
    element.position.y - halfW,  // bottom-left y  (width is the local-Y dimension)
    halfD,                       // originX — pivot at center
    halfW,                       // originY — pivot at center
    element.depth,               // local-X size = depth
    element.width,               // local-Y size = width
    1f, 1f,
    element.angleDeg,
)
```

### Common mistake

Passing `element.width` as the `width` parameter and `element.depth` as `height` feels natural but is **wrong**. It draws a 1.6×0.8 rect in local space and after rotating 90° you get a 0.8×1.6 result — taller than wide for a north-facing element, which is the opposite of what the model specifies.

### Chevron / corners

Chevron and corner computations use the decisions §5 formula directly (forward vector × halfD, right vector × halfW) and are **not** affected by this rect-axis convention — they work in world space and are always correct regardless of angle.

---

## 15. Architecture: input systems

Input follows a **chain-of-responsibility** pattern: `BattlefieldScreen` is the sole dispatcher; each system declares its interest by returning `true` (consumed) or `false` (pass through).

### Dispatch flow

```
libGDX InputAdapter
    └─ touchDown(screenX, screenY, pointer, button)
           │  convert screen → world coords
           ▼
    BattlefieldScreen.onTouchDown(worldPos, button)
           ├─ selectionSystem.touchDown(worldPos, button)  ← priority 1
           └─ movementSystem.touchDown(worldPos, button)   ← priority 2 (only if 1 returns false)

    └─ keyDown(keycode)
    BattlefieldScreen.onKeyDown(keycode)
           ├─ selectionSystem.keyDown(keycode)   ← priority 1
           └─ movementSystem.keyDown(keycode)    ← priority 2
```

`BattlefieldScreen` performs screen→world coordinate conversion before dispatching `touchDown`. Systems receive world coordinates only.

### Contract for every input system

```kotlin
fun touchDown(worldPos: Vector2, button: Int): Boolean
fun keyDown(keycode: Int): Boolean
```

- **`true`** = event consumed; dispatcher stops the chain.
- **`false`** = not handled; dispatcher continues to next system.
- Systems must filter on `button` internally (e.g. `if (button != Input.Buttons.LEFT) return false`).

### Conventions

| Convention | Rule |
|---|---|
| Input key constants | Declared in `companion object` with `INPUT_` prefix (e.g. `INPUT_CONFIRM`, `INPUT_TRANSLATE_UP`) |
| Tuning constants | Declared in `companion object` with unit suffix (e.g. `TRANSLATION_INCREMENT_IN`, `ROTATION_INCREMENT_DEG`) |
| External communication | Callbacks only — injected at construction, never polling |
| Parameter names | `worldPos: Vector2`, `button: Int`, `keycode: Int` — must be identical across all systems |

### Adding a new input system

1. Implement `fun touchDown(worldPos: Vector2, button: Int): Boolean` and `fun keyDown(keycode: Int): Boolean`.
2. Add it to `BattlefieldScreen` at the appropriate priority position in `onTouchDown` / `onKeyDown`.
3. Inject its callbacks from `BattlefieldScreen`.

---

## 16. Open questions

| # | Question | Status |
|---|---|---|
| Q1 | Should contact snapping be implemented? (auto-align element when dropped near legal contact position) | Open |
| Q2 | Does the close-door move require a friendly in front contact, or can it happen any time an overlap exists? | Resolved: friendly in front contact is required. Without it, move can still happen but must be measured normally (cannot exceed cap). |
| Q3 | Can A move into overlap position AND close the door in the same turn? | Resolved: No. Close-door starts from an overlap that exists at the **beginning** of the move. |
| Q4 | Should group pivot support mid-move pivots (e.g., advance → pivot → advance in one order)? | Resolved: yes, the rules allow any combination of forward movement and pivots in a single group move. |
| Q5 | Left/right order of elements in group after reducing front through a gap | Open — rules say maintain left-to-right order, but the exact interpretation needs a diagram reference. |
| Q6 | ZOC rolling-carpet blocking | Deferred (simplified to fixed rectangle for now). |
| Q7 | Full pass-through rules (cavalry through Psiloi) | Deferred. |
