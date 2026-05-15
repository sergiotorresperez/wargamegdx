# Mighty Armies: Ancients — Technical Summary

> Full rules: [mighty_armies_ancients_core_rules.md](mighty_armies_ancients_core_rules.md)
> Purpose: Starting point for requirements analysis. Reference full rules for edge cases.

---

## §Entities

| Entity | Key attributes |
|--------|---------------|
| **Unit** | Fighting score, Speed, Support score, Shooting rating (optional), special abilities (General, Scout) |
| **Group** | 2–6 units in base contact, same facing, max 3 abreast × 2 deep |
| **Army** | Up to 60 Army Points; 1 General max; 4 Scouts max |
| **General** | A unit with the General special ability; death is a loss condition |

Large-base units (chariots, huge animals) count as 2 normal bases for Group composition.

---

## §TurnStructure

Turns alternate between players. Each turn has 4 phases, executed strictly in order:

```
1. Roll Move Points (d6)
2. Move phase   — spend MP to move units/groups or issue commands
3. Shoot phase  — units with Shooting ability fire (no MP cost)
4. Close Combat — all units in base contact fight automatically
```

→ Full details: [Turn Sequence (p.5)](mighty_armies_ancients_core_rules.md#turn-sequence-p5)

---

## §ActionPoints

- Roll **1d6** at turn start → result = Move Points (MP) for the turn.
- **1 MP** = move one unit or Group, OR use one Command.
- MP are a **consumable resource** — unused MP are lost.
- Each unit moves **at most once** per turn.
- A General may attempt a Command on a given unit **only once** per turn.
- Groups of 5–6 units cost **2 MP** to move.

---

## §Movement

- A unit/Group moves up to its Speed (inches) in a straight line.
- Up to **90° turn before** movement + **90° turn after** movement (pivot around centre).
- Entering or passing through terrain **halves remaining Speed** that turn.
- Units **cannot pass through** other units (friendly or enemy).
- **Reform action** (1 MP): micro-adjustments — 1" nudge, 180° turn, reorder group, pull nearby units in. Unit cannot move normally afterwards.

→ Full details: [Movement (p.6)](mighty_armies_ancients_core_rules.md#movement-p6)

---

## §Combat

### Shooting
- Declared after all movement; **no MP cost**.
- Requires: target in front arc, within 7", line of sight unobstructed, target not in close combat.
- Resolution: **Shooting rating vs target's Fighting score** → look up target number on table → roll d6 → meet or beat = target **destroyed**.

| Target Fighting | Shooting I | Shooting II | Artillery I | Artillery II |
|----------------|-----------|------------|------------|-------------|
| 0–2            | 5+        | 4+         | 3+         | 2+          |
| 3–4            | 6+        | 5+         | 4+         | 3+          |
| 5+             | —         | 6+         | 5+         | 4+          |

### Close Combat
- Automatic for every unit in base contact at the start of the CC phase.
- Both roll **d6 + Fighting score + modifiers**.

| Modifier | Value |
|----------|-------|
| Charged (moved into CC this turn) | +1 |
| Difficult Ground (moved through terrain this turn) | −1 |
| Flanked (enemy on short/rear edge) | −1 |
| Surprise Attack (enemy on rear long edge) | +1 |

**Results:**
- **Victory**: higher but < double opponent's total → enemy **Driven Back**.
- **Massacre**: double or more → enemy **destroyed**.
- **Draw**: same total → both stay locked, fight again next turn automatically.

**Groups in CC:** one unit is primary fighter; all others add their Support score. Victory destroys 1 enemy unit of loser's choice + drives rest back. Massacre: roll d6 — that many units destroyed.

**Driven Back:** loser turns 180° and retreats its slowest unit's Speed. If retreat is blocked → **destroyed**.

→ Full details: [Combat (p.7–9)](mighty_armies_ancients_core_rules.md#combat-p7)

---

## §WinConditions

1. Destroy **≥ half** of opponent's units (round up) before opponent does the same.
2. Destroy the **enemy General** AND have lost fewer AP worth of units.

Victory is **immediate** when either condition is met mid-turn.

→ Full details: [Victory (p.10)](mighty_armies_ancients_core_rules.md#victory-p10)

---

## §ImplementationNotes

Key technical concepts to model:

- **State machine per turn**: roll MP → move phase (MP pool) → shoot phase → CC phase.
- **Spatial engine**: base contact detection, line-of-sight, arc checking, terrain intersection.
- **Group management**: dynamic grouping/ungrouping, slowest-unit speed propagation, support score aggregation.
- **Combat resolver**: table lookups for shooting; dice + modifier sums + result classification for CC.
- **Army point tracker**: AP lost per side for win condition #2.
- **Driven Back resolver**: path-clear check; destroy if blocked.