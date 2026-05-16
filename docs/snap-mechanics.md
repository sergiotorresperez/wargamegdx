# Snap Mechanics — Use Cases from DBA 2.2

## Overview

**Snap** is a digital UX affordance that assists players in achieving precise corner-to-corner alignment during drag-and-drop movement of individual elements. When an element is dragged and a corner comes within **snap radius** (~0.4 inches), the element automatically aligns to the target corner if the angle compatibility requirement is met.

---

## Snap-Triggering Contact Types

All legal contact types defined in DBA 2.2 §5, §6, and §9 can trigger snap. The table below lists each contact type, the corner pair to align, and the angle requirement.

| Contact Type | DBA § | Snap Corner Pair | Angle Requirement | Description |
|---|---|---|---|---|
| **Group formation** (right flank) | §5 | A.frontRight ↔ B.frontLeft | Same (~0°) | A's right flank → B's left flank (side-by-side) |
| **Group formation** (left flank) | §5 | A.frontLeft ↔ B.frontRight | Same (~0°) | A's left flank → B's right flank (side-by-side) |
| **Column formation** (A ahead) | §6 | A.frontLeft ↔ B.rearLeft | Same (~0°) | A's front touches B's rear (column formation) |
| **Column formation** (A behind) | §6 | A.rearLeft ↔ B.frontLeft | Same (~0°) | A's rear touches B's front (column formation) |
| **Front contact** (A left-aligned) | §9.1 | A.frontLeft ↔ B.frontRight | Opposite (~180°) | Front-to-front, A faces opposite to B |
| **Front contact** (A right-aligned) | §9.1 | A.frontRight ↔ B.frontLeft | Opposite (~180°) | Front-to-front, A faces opposite to B |
| **Flank attack** (left flank) | §9.2 | A.frontLeft ↔ B.frontLeft | Perpendicular (~90°) | A's front edge against B's left flank |
| **Flank attack** (right flank) | §9.2 | A.frontRight ↔ B.frontRight | Perpendicular (~90°) | A's front edge against B's right flank |

---

## Angle Compatibility

Snap only activates when the angle between the two elements is compatible. Compatibility is checked using **angle tolerance** of 15°:

- **Same angle** (group & column): `|angleDiff(A, B)| < 15°`
  - Allows flexible formation angles (e.g., 0°, 5°, 12°, all snap to group)
  
- **Opposite angle** (front contact): `|angleDiff(A, B) − 180°| < 15°`
  - A and B facing roughly opposite directions (e.g., 0° ↔ 175°, 0° ↔ 185°)
  
- **Perpendicular angle** (flank attack): `|angleDiff(A, B) − 90°| < 15°`
  - A and B facing roughly perpendicular (e.g., 0° ↔ 90°, 0° ↔ 75°)

---

## Snap Mechanics

### How Snap Works

1. **During drag**: Player drags element A toward element B.
2. **Snap detection**: For each corner pair of A and B, compute distance `dist = A.corner.dst(B.corner)`.
3. **Candidate evaluation**: If `dist < snapRadius` (≈0.4 in) AND angle requirement is met → snap candidate.
4. **Best candidate**: Choose the closest corner pair across all targets and all configurations.
5. **Position adjustment**: A's center is moved so that A's corner aligns exactly with B's corner:
   ```
   A.position = B.corner − (A.corner − A.position)
   ```
   A's angle is **not** changed.

### Snap Radius

Snap activates when a corner is within **0.4 inches** (≈25% of base width). This is comfortable for UX — not too tight (hard to hit), not too loose (unexpected snaps).

---

## Examples

### Group Formation (Same Angle)

Player drags a lone unit A toward a group. When A's right flank corner comes within ~0.4" of the group's left flank corner, and both are facing the same direction (±15°):
- Snap activates.
- A.frontRight aligns exactly with B.frontLeft.
- A's position shifts to achieve this alignment.
- A remains part of the group formation after confirmation.

### Front Contact (Opposite Angle)

Player drags unit A toward an enemy unit B facing the opposite direction (e.g., A facing north, B facing south). When A's front-left corner comes within ~0.4" of B's front-right corner:
- Snap activates.
- A.frontLeft aligns exactly with B.frontRight.
- A enters front contact with B.

### Flank Attack (Perpendicular Angle)

Player drags unit A perpendicular to unit B. When A's front-left corner comes within ~0.4" of B's front-left corner, and angles are ~90° apart:
- Snap activates.
- A.frontLeft aligns exactly with B.frontLeft.
- A attacks B's left flank.

---

## Out of Scope

- **Overlap** (DBA §10): Corner-to-corner contact that is already exact; snap adds no value.
- **Closing the Door** (DBA §11): Special pivoting maneuver; snap is for drag-based movement only.
- **Group movement**: Snap applies only to individual elements being dragged. Groups move rigidly.
- **Movement validation**: Snap does **not** enforce movement budget, ZOC, collision, or gap rules. It is a pure UX affordance.

---

## Implementation Notes

- Snap is implemented in `MovementSystem.kt` in the `touchDragged()` method.
- Snap only applies to `MovementOp.DragAndDrop` (individual element drag).
- Targets for snap are all elements in `gameState` except the element being dragged.
- The snap configuration (8 types) is defined as (myCorner, targetCorner) pairs with angle filters.
