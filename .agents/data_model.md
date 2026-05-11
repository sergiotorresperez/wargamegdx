# Data Model — Entidades y relaciones

Este documento describe el modelo de datos **conceptual** del juego, diseñado para ser extensible desde MVP hacia el juego completo.

---

## Entidad: Unit

Representa una unidad individual en el tablero.

### Propiedades esenciales (MVP Stage 1)
- `id: String` — identificador único
- `position: Vector2` — posición en pulgadas (x, y)
- `facing: Float` — orientación en grados (0-360, ó radianes)
- `baseSize: Rect` — tamaño base (50×25mm ≈ 2"×1")

### Propiedades futuras (Post-MVP)
- `type: UnitType` — Infantry, Cavalry, Chariot, etc.
- `fightingScore: Int` — stat para close combat
- `shootingLevel: Int` — Shooting I, II (o Artillery I, II)
- `speed: Int` — MU por movimiento
- `supportScore: Int` — valor de soporte en grupo
- `isGeneral: Boolean` — tiene habilidad General
- `isScout: Boolean` — cuenta como scout (máx 4 por ejército)
- `healthStatus: UnitStatus` — Healthy, DrivenBack, Destroyed (post-MVP)

### Relaciones
- `groupId: String?` — ID del grupo al que pertenece (null si aislada)
  - **MVP**: se calcula dinámicamente (base contact con otras)
  - **Post-MVP**: podría ser persistente

---

## Entidad: Group

Representa un conjunto de unidades en base contact.

### Propiedades (MVP Stage 1)
- `id: String` — identificador único
- `units: List<Unit>` — unidades que componen el grupo
- `centerPosition: Vector2` — centro geométrico (calculado)
- `facing: Float` — facing promedio o del "primary fighter"
- `bounds: Polygon` — envolvente visual

### Propiedades futuras (Post-MVP)
- `primaryFighter: Unit` — unidad que dirige el combate (en close combat)
- `supportTotal: Int` — suma de support scores (calculado)
- `status: GroupStatus` — Healthy, DrivenBack, PartiallyDestroyed

### Relaciones
- `units` ↔ `Unit.groupId` — relación bidireccional

### Comportamiento MVP
- Grupo **existe** si hay 2+ unidades en base contact
- Grupo **se desforma** automáticamente si unidad se mueve fuera de base contact
- Grupo **se puede formar** dinámicamente si unidades están en base contact
- **No persiste**: es derivado de posiciones, no una entidad guardada

---

## Conceptos derivados del MVP (calculados, no persistidos)

### Base Contact
Dos unidades están en base contact si sus rectángulos base se tocan a lo largo de una arista completa (long edge to long edge preferentemente).

```
Pseudocódigo:
isInBaseContact(unit1, unit2) -> bool:
  if overlaps 1" of long edges OR
     overlaps 1" of short edge (flank):
    return true
```

### Group Formation
Un grupo mantiene su formación relativa durante rotación/traslación. 

```
Propiedades relativas que se preservan:
- offset de cada unit respecto al centro del grupo
- relative facing (si no es grupo "facing alignment")
```

---

## Entidades futuras (Post-MVP)

### Terrain
- `position: Vector2`
- `bounds: Polygon`
- `type: TerrainType` — Woods, Hill, Ruined, etc.
- `losBlocks: Boolean`
- `slowsMovement: Boolean`

### Army
- `id: String` — "Player 1", "Player 2"
- `units: List<Unit>`
- `totalAP: Int` — suma de AP de unidades
- `general: Unit?` — unidad con General
- `scouts: List<Unit>` — máx 4

### GameState
- `turnNumber: Int`
- `currentPlayer: Army`
- `movePointsRemaining: Int`
- `victoryCondition: VictoryType`
- `unitsDestroyedByPlayer: Map<Army, Int>`

---

## Notas de diseño

### Por qué Group no persiste en MVP
- **Simplicidad**: es derivado de posiciones, no estado adicional
- **Escalabilidad**: si later necesitamos grupos persistentes (ej: en post-MVP con rearrangement), es un refactor local
- **MVP clarity**: el código es más simple si pensamos "grupos son patrones de base contact"

### Extensibilidad
- `Unit` puede crecer con stats sin afectar motor espacial
- `Group` puede ganar persistencia sin cambiar lógica de formación
- Nuevas entidades (Terrain, Army) son independientes del core Unit/Group

### Convenciones
- Posiciones en **pulgadas** (inches) — unidades LibGDX
- Ángulos en **grados** (0-360) o radianes (TBD)
- Tamaños en pulgadas (50×25mm = ~2"×1")
