# DBA 2.2 — Geometry Rules Reference

Source: *De Bellis Antiquitatis 2.2 — Unofficial Spanish Guide (Wadbag / La Armada)*
Scope: only rules with geometric / spatial implications. Combat, army lists, terrain types, and non-movement special rules are excluded.

---

## Index

1. [Scale and Units](#1-scale-and-units)
2. [Element Shape and Anatomy](#2-element-shape-and-anatomy)
3. [Measuring Distances](#3-measuring-distances)
4. [Single-Element Movement](#4-single-element-movement)
5. [Group Movement](#5-group-movement)
6. [Column Movement](#6-column-movement)
7. [Reducing Front to Pass a Gap](#7-reducing-front-to-pass-a-gap)
8. [Zone of Control (ZOC)](#8-zone-of-control-zoc)
9. [Contact Types](#9-contact-types)
10. [Overlap (Solapamiento)](#10-overlap-solapamiento)
11. [Closing the Door (Cerrar la Puerta)](#11-closing-the-door-cerrar-la-puerta)
12. [Cutting the Corner (Cortar la Esquina)](#12-cutting-the-corner-cortar-la-esquina)
13. [Separating from Frontal Contact](#13-separating-from-frontal-contact)
14. [Facing Adjustment (Encaramiento)](#14-facing-adjustment-encaramiento)
15. [Gap Rule](#15-gap-rule)
16. [Passing Through Friendly Elements](#16-passing-through-friendly-elements)

---

## 1. Scale and Units

| Scale | 1 inch = | Base width (BW) | Base width in inches |
|-------|----------|-----------------|----------------------|
| 15 mm | 100 pasos | 40 mm | ≈ 1.6 in |
| 25 mm | 100 pasos | 60 mm | ≈ 2.4 in |

- All distances in the rules are given in **pasos**.
- 1 BW (base width) is the standard reference unit for ranges, ZOC, gaps, etc.
- **All world coordinates in this implementation are in inches.**
- Conversion: 100 pasos = 1 inch → movement of 200 pasos = 2.0 inches.

### Movement speed table (15 mm, open terrain, placeholder — all types same for prototype)

| Element type | Open terrain | Difficult terrain | Road |
|---|---|---|---|
| (prototype — all) | 2.0 in | — | 4.0 in |

---

## 2. Element Shape and Anatomy

An element is a **rectangular base**.

```
         FRONT EDGE
   FL ─────────────── FR
   │                   │
   │   (element body)  │
   │                   │
   RL ─────────────── RR
         REAR EDGE
```

- **FL** = front-left corner
- **FR** = front-right corner
- **RL** = rear-left corner
- **RR** = rear-right corner
- Left/right are from the **element's own perspective** (as if a soldier standing in the unit looks forward).
- The element also has:
  - A **front edge** (FL → FR)
  - A **rear edge** (RL → RR)
  - A **left flank edge** (FL → RL)
  - A **right flank edge** (FR → RR)
  - A **front-center** point (midpoint of front edge)
  - A **center** point (geometric center of the base)
- **Width** (front edge length) = 1 BW for all standard elements.
- **Depth** (flank edge length) = varies by element type; for the prototype, 0.5 BW.
- The **facing direction** is perpendicular to the front edge, pointing outward from the front.

---

## 3. Measuring Distances

### Movement cost
> *"Distance is measured from the corner that travels the most."*

The cost of any move is the **maximum straight-line distance** traveled by any single corner between its start position and its end position.

```
cost = max(dst(FL_start, FL_end),
           dst(FR_start, FR_end),
           dst(RL_start, RL_end),
           dst(RR_start, RR_end))
```

- Distances are **chord lengths** (straight-line), not arc lengths.
- This applies to both translation and rotation moves.
- A pivot in place costs the arc-chord distance of the outermost corner.
- Measuring may be done **at any time**, including before moving.

### Group cost
Same formula: the cost is the maximum corner distance across **all elements in the group**.

---

## 4. Single-Element Movement

### What is allowed
A single-element move is **extremely flexible**:
- The element may be placed **anywhere** within its movement budget.
- It may translate, rotate, sidestep, advance, or retreat freely.
- Any combination of rotations and translations is permitted, provided the total corner cost does not exceed the movement cap.
- The element does **not** need to end facing any particular direction.

### Restrictions
1. **Movement cap**: max corner distance ≤ element's movement allowance for the terrain type.
2. **Gap rule**: element cannot pass through a gap narrower than 1 BW at any point along its path (see §15).
3. **ZOC restrictions**: if the element starts or enters a ZOC during its move (see §8).
4. **No overlap**: element cannot end its move overlapping another element (friendly or enemy).
5. **Cannot pass through enemy elements** at any point.

---

## 5. Group Movement

### Forming a group

Two or more elements form a valid group if **all** of the following hold:
- All elements face **the same direction** (same angleDeg).
- All elements are in **side-to-side contact**: each element's flank edge is flush against an adjacent element's flank edge, corner to corner.
- The group is **contiguous**: no gap between any two members.

### Allowed manoeuvres
A group may, in a single move:
- **Translate directly forward** (perpendicular to the front edge).
- **Pivot** around the front-left corner of the leftmost element, or the front-right corner of the rightmost element.
- Any **combination** of the above, including multiple pivots, in sequence.
- **Slide sideways** up to ½ BW left or right — but only to align the group's front with a nearby enemy element that is less than 1 BW away from at least one group member.

### Restrictions
- No element may change its **relative position** within the group.
- No element may **enter difficult terrain** at any point.
- No element may **exceed its individual movement cap** (cost = max corner distance across all elements).
- The group must **start and end** in valid group formation.

### Pivot mechanics
- The pivot corner is **fixed** (zero travel distance during the pivot).
- All other elements rotate rigidly around it.
- The farther from the pivot, the more movement budget the outer elements consume.
- Both left-pivot (around FL of leftmost) and right-pivot (around FR of rightmost) are valid in one move.

---

## 6. Column Movement

### What is a column
Two or more elements form a valid **column** if:
- The column is **exactly 1 element wide**.
- Each element's **rear edge** is in contact with the **front edge** of the next element (or one rear corner touches the front edge of the next when the column is curved after a pivot).
- The column is **contiguous**: no gap between members.

### Allowed manoeuvres
- Translate directly **forward**.
- The **lead element** pivots on either of its front corners; all following elements move to the same position the element ahead of them just vacated (follow-the-leader).
- Any combination of the above.

### Forming a column from a group
Elements already in group formation may form a column at any point during a group move. This is equivalent to reducing the front to 1 element wide (see §7).

### Restrictions
- No element may change its **relative order** in the column.
- No element may **exceed its movement cap**.
- No element that joins the column may **retreat** to do so.
- All elements must **end** the move in valid column formation.

---

## 7. Reducing Front to Pass a Gap

When a group encounters a gap (see §15), it may reduce its front to pass through:

1. **Identify the gap**: the space between two obstacles wide enough for at least 1 element but narrower than the full group width.
2. **Elements that fit** through the gap execute a normal group move forward.
3. **Elements that don't fit** queue up directly **behind** the rearmost element that did fit, in the same left-to-right order as before.
4. The group may reduce its front by **no more than the minimum necessary** to pass.
5. Elements shifting to the rear do so as part of the same move; it does not cost extra.

---

## 8. Zone of Control (ZOC)

### Shape
Each element projects a ZOC **directly in front** of itself:
- A rectangle of width = 1 BW and depth = 1 BW, flush against the element's front edge.

```
   ┌──────────────────┐  ← ZOC front edge (1 BW ahead)
   │                  │
   │    ZOC area      │  depth = 1 BW
   │   (1BW × 1BW)    │
   ├──────────────────┤  ← element front edge (FL — FR)
   │   element body   │
   └──────────────────┘
```

- The ZOC **includes** its front boundary (an element exactly 1 BW away is inside).
- The ZOC does **not** include its lateral boundaries or front corners.

### Blocking (rolling-carpet rule)
The ZOC extends forward until it hits something that **spans its full width**. A partial blocker does not stop it.

> *Simplified for prototype: ZOC is always the full 1 BW × 1 BW rectangle; blocking is not modelled yet.*

### Effect on an element that STARTS its move inside an enemy ZOC
The element may only:
1. **Retreat**: move directly backward (same facing direction), no direction change, minimum 2.0 in, not ending in contact with any enemy.
2. **Advance to front contact** with the ZOC-owning enemy.
3. **Face** the ZOC-owning enemy (rotate toward it, following the rules for facing).

### Effect on an element that ENTERS a ZOC during its move
Once the element enters a ZOC it may only:
1. **Advance to front contact** with the ZOC-owning enemy.
2. **Face** the ZOC-owning enemy.
It cannot continue moving in any other direction after entering the ZOC.

### ZOC does not affect
- Elements moving as part of a **group or column** that will make legal contact with the ZOC-owning enemy (they may pass through to contact).

---

## 9. Contact Types

Contact is only **legal** when the two elements' edges are **flush and their corners are aligned**. Edges merely touching at an angle or partially overlapping do not constitute contact.

### 9.1 Front Contact (Contacto Frontal)
- Element A's **front edge** is flush against element B's **front edge**.
- They face **toward each other** (roughly opposite directions).
- Corner alignment: A's FL touches B's FR, and A's FR touches B's FL simultaneously.

```
  ▼ A        ▲ B        (arrows = facing direction)
┌─────┐    ┌─────┐
│     │    │     │
└─────┘────└─────┘
 FR·FL alignment
```

### 9.2 Flank Contact (Contacto de Flanco)
- Element A's **front edge** is flush against element B's **left or right flank edge**.
- Corner alignment: A's FL meets B's FL (left-flank attack), or A's FR meets B's FR (right-flank attack).
- A's full front edge lies along B's full flank edge.

```
       ▲ B
    ┌──┤
    │  │←── A's front edge flush against B's left flank
    └──┤
```

### 9.3 Rear Contact (Contacto de Retaguardia)
- Element A's **front edge** is flush against element B's **rear edge**.
- Corner alignment: A's FL meets B's RL, and A's FR meets B's RR.

### 9.4 Legal contact requires alignment
An element that merely touches another's edge at an angle is **not in legal contact**. Before moving into contact, elements (especially groups) must **pivot or align** until their edges are flush.

### 9.5 Making contact with a group
A group may slide ≤ ½ BW sideways to achieve alignment with a nearby enemy (see §5). This counts as part of the move.

### 9.6 Automatic facing at end of movement phase (Encaramiento)
At the **end of the movement phase**, any element that has an enemy in contact with its flank or rear — but no enemy in front contact — immediately rotates to face the **first enemy that established contact** with it, ending in front contact with that element.

---

## 10. Overlap (Solapamiento)

An element is **overlapping** an enemy when:
- It is in **corner-to-corner** contact with the enemy (one of A's front corners touches one of the enemy's front corners).
- It is **not** in flank contact with the enemy.
- Its front corners match with the enemy's front corners on the same side (right-right or left-left).
- A **friendly element** is in **front contact** with the enemy.
- The overlapping element is **not** in its own front contact with the enemy.

```
     F (friendly, front contact)
   ┌─────┐
   │     │← front contact
   └─────┘
       ▲       ← enemy E facing up
    ┌─────┐
    │  E  │
    └──┬──┘
       │ (FR of E = FL of A)
    ┌──┴──┐
    │  A  │← A overlaps E (A's FL touches E's FR)
    └─────┘
```

An element can overlap the enemy on the **left** (A's FL meets E's FR) or on the **right** (A's FR meets E's FL).

---

## 11. Closing the Door (Cerrar la Puerta)

### What it is
A special movement allowing an element to swing from an overlap position to a flank contact position on the same enemy. This exceeds the normal movement cap.

### Pre-conditions
- The moving element **A** is currently in **overlap** with enemy **E** (see §10).
- A **friendly element F** is in **front contact** with E.

### The move
A rotates around the shared corner (E's front corner) until A's front edge is **flush against E's flank edge** — i.e., A ends in legal flank contact with E.

The destination is **geometrically fixed**: there is only one valid end position (A's front flush against E's left or right flank, depending on which side A started on).

### Movement cost
- This move **may exceed** A's normal movement cap.
- If F is already in front contact with E, the move may be executed **without measuring** (distance is irrelevant).
- A cannot do anything else in this move.

### Diagram
```
Before:              After:
                        ┌──┐ A (flank contact)
 F ── E                 │  │
      └─ A (overlap)  F ── E
```

---

## 12. Cutting the Corner (Cortar la Esquina)

### What it is
A single-element move that passes through a **friendly** element is normally illegal. Exception: if **exactly one** of the moving element's four corner paths crosses the friendly element's polygon, the move is allowed.

### Rule
- Trace the straight-line path of each of the 4 corners from start to end position.
- Count how many of these 4 paths cross (intersect) the friendly element's boundary.
- **Exactly 1 crossing** → move is legal ("cutting the corner").
- 0 crossings → the element doesn't pass through the friendly (normal move, no exception needed).
- 2 or more crossings → illegal (element would fully pass through the friendly).

### Typical use case
An element starts **behind** a friendly and ends up **directly beside** it. The path of one rear corner clips the friendly's polygon while the other three corners go around.

---

## 13. Separating from Frontal Contact

An element in **front contact** with an enemy may disengage, but only as a **single-element move** with these restrictions:
- Must move **directly backward** (opposite of facing direction). No direction changes.
- Must remain **facing the enemy** (same angleDeg throughout).
- Must move **at least 2.0 inches** (200 pasos).
- Must not start the move **also in flank or rear contact** with an enemy.
- Must not **end** the move in contact with any enemy, or in a ZOC, or at a BUA/camp occupied by the enemy.

---

## 14. Facing Adjustment (Encaramiento)

Happens **automatically at the end of the movement phase**, not during:
- If an element has an enemy in contact with its **flank or rear**, and no enemy in **front contact**, it rotates to face the first enemy that established that contact.
- It ends in **front contact** with that enemy.
- If two or more enemies established flank/rear contact simultaneously, the element faces the first one to have done so.

This is a free adjustment (no movement cost, no PIPs spent).

---

## 15. Gap Rule

An element (or group) **cannot pass through a gap** narrower than **1 BW** at any point during its move.

### What counts as an obstacle forming a gap
- Any element (friendly or enemy)
- A camp
- A BUA (Built-Up Area)
- A terrain feature
- *(Not applicable for prototype: no board edges or terrain)*

### How it applies
- For a **single element**: the swept path of the element through space must not pass through any gap < 1 BW.
- For a **group**: the group's full width must be able to pass; if the gap is narrower, the group must reduce its front (see §7).
- Gaps of exactly 1 BW are passable.

---

## 16. Passing Through Friendly Elements

An element can pass through a friendly only if:
- The moving element is **mounted** (cavalry) and the friendly is **Psiloi**, OR
- The moving element is **Psiloi** and the friendly is any element.
- All friendlies in the path must face **the same direction as or opposite to** the mover.
- There must be a **free space at least 1 BW wide** immediately behind the last friendly in the path.
- The mover must have enough movement budget to reach that free space.

> *For prototype: this rule is deferred. Only the "cutting the corner" exception (§12) is modelled. Full pass-through is not in scope.*
