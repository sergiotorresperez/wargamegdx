# DBA 2.2 — Technical Summary

> Full rules: [DBA_2_2_Unofficial_Guide.md](DBA_2_2_Unofficial_Guide.md)
> Purpose: Starting point for requirements analysis. Reference full rules for edge cases.

---

## §Entities

| Entity | Key attributes |
|--------|---------------|
| **Element** | Type, subtype, category (Mounted/Foot), base width/depth, combat factor vs Foot, combat factor vs Mounted |
| **General** | One designated element per army; +1 combat factor when fighting; death contributes to loss condition |
| **Army** | Exactly 12 troop elements; optional camp; optional Camp Followers element |
| **Camp** | Placeable terrain piece; can be occupied by one element; captured camp = 2 elements destroyed |

**24 element types** (see Element Table in full rules). Each type has one or more subtypes that differ in base depth only. Key distinctions:

- Mounted vs Foot category drives movement and combat factor selection.
- Bad going penalises most Mounted types and several Foot types in combat.
- "Impetuous" elements (Knights, Warband, Scythed Chariots, Elephants) must pursue automatically.
- Dismounting (Mounted → Foot equivalent) is allowed only at deployment for specific army list entries.
- Scythed Chariots are destroyed on any equal result; they are effectively a one-shot weapon.

→ Full type descriptions: [Elements](DBA_2_2_Unofficial_Guide.md#elements)

---

## §TurnStructure

Turns alternate between players (invader moves first overall). Each turn has 4 phases, executed strictly in order:

```
1. PIP Phase          — Roll 1d6 for Player Initiative Points
2. Tactical Movement  — Active player moves elements (spend PIPs)
3. Distant Shooting   — Both players shoot (Bows, Pavises, War Wagons, Artillery)
4. Close Combat       — All mutual full-front-contact pairs fight
```

Win condition is checked **at the end of a turn**, not mid-turn.

→ Full details: [Sequence of Play](DBA_2_2_Unofficial_Guide.md#sequence-of-play)

---

## §ActionPoints

- Roll **1d6** at turn start → result = PIPs for the turn.
- **1 PIP minimum** per tactical move (single element or group).
- PIPs are a **consumable resource** — unspent PIPs are lost at turn end.
- Extra PIPs required (stacking, one per applicable category):
  - **+1** if the move includes Elephants, Hordes, War Wagons, or Artillery.
  - **+1** if all moving elements are outside the general's command distance (16 MU with line-of-sight; 8 MU if blocked) OR the general has been destroyed.
  - **+1** if the general's element is entirely inside a camp, woods, oasis, or marsh (not when moving the general itself).
- Extra PIPs **not** required on the active player's first turn.

→ Full details: [PIPs](DBA_2_2_Unofficial_Guide.md#pips)

---

## §Movement

### Move types

| Type | Rules |
|------|-------|
| Single element | Full flexibility — any direction, any facing change, intermediate moves allowed |
| Group | All elements same facing, in edge/corner contact; forward, wheel, or lateral slide ≤ ½ base width; no bad going unless column |
| Column | One element wide; lead element wheels; used to enter bad going or rivers |

### Distances (Movement Units)

| Element Types | Good Going (MU) | Bad Going (MU) |
|--------------|-----------------|----------------|
| Light Horse, Light Camelry | 8 | 3 |
| Cavalry, Light Chariots, Scythed Chariots | 6 | 3 |
| Camelry | 5 | 3 |
| Knights, Cataphracts, Elephants | 4 | 3 |
| Auxilia | 5 | 5 |
| Psiloi, Light Spears | 4 | 4 |
| Raiders, Blades, Bows, Hordes, Spears, Pavises, Pikes, Warband | 3 | 3 |
| Artillery, War Wagons | 3 | No bad going move |

Road move: 8 MU for Light Horse/Light Camelry; 6 MU for all others. River crossing: max 2 MU.

### Key constraints

- An element entering bad going at any point in its move treats the **entire** move as bad going.
- Zone of Control (ZOC): extends 1 base width forward from each element; entering a ZOC restricts movement to retiring, moving to contact, or squaring with one enemy.
- Artillery and War Wagons may not move into contact with enemy.
- Elements in full front contact may only leave by "breaking off" (must be faster than enemy; moves directly rear; min 3 MU).
- Second/subsequent moves cost PIPs normally; Light Horse may make unlimited subsequent moves if they stay away from enemy; Warband may make one extra move to reach contact.

→ Full details: [Tactical Movement](DBA_2_2_Unofficial_Guide.md#tactical-movement), [Movement Distance](DBA_2_2_Unofficial_Guide.md#movement-distance)

---

## §Combat

### Distant Shooting

Eligible shooters: Bows, Pavises, War Wagons (360° arc), Artillery. Bows/Pavises/War Wagons shoot in both turns; Artillery only in own turn (unless return fire).

| Shooter | Max Range |
|---------|-----------|
| Bows, Pavises, War Wagons | 3 MU |
| Artillery | 8 MU |

- Up to 3 shooters may combine against one target (each extra shooter gives target −1 factor).
- A shooter not being shot at ignores any adverse result against itself.
- Elements eligible to shoot **must** do so.

### Close Combat

All mutual full-front-contact pairs fight. Resolution is identical for both combat types:

1. Sum: basic combat factor (type vs Foot or Mounted) + tactical modifiers + rear support factors.
2. Each side rolls 1d6 and adds their total factor → **combat score**.
3. Compare scores → look up result on Combat Results Table.

### Key combat factors (modifiers)

| Situation | Factor |
|-----------|--------|
| General's element | +1 |
| Uphill or defending river bank | +1 |
| Occupying own camp | +2 |
| Each enemy overlapping or in flank/rear contact | −1 |
| Mounted in bad going, or in CC with enemy in bad going | −2 |
| Art/Bd/Hd/Pav/Pk/4Sp/WWg in bad going | −2 |
| Raiders in bad going | −1 |
| Bl/El/Rd shot at by any | −1 |

### Rear support bonuses

| Supported | Supporter | Bonus |
|-----------|-----------|-------|
| Pikes | Pikes (directly behind) | +3 vs Foot (exc. Ps/Bw); +2 vs Kn/Cat/El |
| Spears, Light Spears, Warband | Same type (directly behind) | +1 vs Foot (exc. Ps/Bw); +1 vs Kn/Cat/El |
| Ax/Bd/3Sp/Rd/4Sp | Psiloi (directly behind) | +1 vs Mounted, Warband, or camp |

### Combat results (summary)

- **Equal**: no effect (Scythed Chariots destroyed).
- **Beaten** (loser > half winner): mostly Recoil; many type-specific quick-kills (e.g. Bows vs Mounted → destroyed, Blades vs Knights → destroyed).
- **Doubled** (loser ≤ half winner): mostly Destroyed; some types Flee instead.

**Recoil**: move directly rear by own base depth (or 1 base width, whichever less); destroyed if blocked by enemy, impassable terrain, or board edge.

**Flee**: recoil + turn 180° + full tactical move away; destroyed if it hits the board edge or a river.

**Pursuit** (impetuous types — Knights, Scythed Chariots, Elephants, Warband): automatically advance 1 base depth/width when enemy recoils, flees, or is destroyed in front contact.

→ Full tables: [Combat Resolution Factors](DBA_2_2_Unofficial_Guide.md#combat-resolution-factors), [Combat Results Table](DBA_2_2_Unofficial_Guide.md#combat-results-table)

---

## §WinConditions

A player wins **at the end of a turn** when **both** of the following are true:

1. The opponent has lost **either** their general's element **or** at least **4** troop elements.
2. The opponent has lost **more elements** than the winning player.

Special counting rules:
- Scythed Chariot elements and Camp Follower elements are **not** counted as destroyed.
- A captured enemy camp counts as **2 elements** destroyed.

Victory is checked end-of-turn only (not mid-turn); both conditions must hold simultaneously.

→ Full details: [Winning and Losing](DBA_2_2_Unofficial_Guide.md#winning-and-losing)

---

## §ImplementationNotes

Key technical concepts to model:

- **State machine per turn**: PIP roll → tactical movement phase (PIP pool) → distant shooting phase (both players) → close combat phase → win check at turn end.
- **Spatial engine**: base contact detection (full front, flank, rear, overlap, corner), ZOC projection (rolling-carpet method), line-of-sight for command distance and shooting eligibility, arc-of-fire checking, terrain intersection (good/bad going classification per move).
- **Element type registry**: 24 types × subtypes; each entry carries combat factors vs Foot/Mounted, movement distances (good/bad going, road), base dimensions, impetuous flag, shooter flag, bad-going penalty flag.
- **PIP cost calculator**: base cost 1 per move + up to 3 conditional extra PIPs (heavy/slow units, out-of-command, general in terrain).
- **Command distance checker**: 16 MU with LOS, 8 MU without; must account for hills, woods, oasis, dunes blocking LOS.
- **Group management**: group contact validation (same facing, edge/corner contiguous); column formation; frontage reduction through gaps; no bad-going group moves (except Psiloi-only groups and columns).
- **Combat resolver**: factor summation (base + modifiers + rear support) + d6 per side → score comparison → result classification (Equal / Beaten / Doubled) → type-specific outcome lookup (Recoil / Flee / Destroyed / No Effect).
- **Outcome movement**: recoil path-clear check with pass-through/push-back cascades; flee path routing (terrain avoidance per type); pursuit for impetuous types.
- **Element destruction counter**: separate count per side, general destroyed flag, camp captured flag (worth 2); win condition evaluated end-of-turn.
- **Distant shooting mutual obligation**: any eligible shooter must shoot; shooting order matters (resolve each fully before next); return fire for Artillery.
