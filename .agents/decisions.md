# Decisiones de arquitectura

## Stack y enfoque — LibGDX puro

No hay separación en capas, no hay StateFlow, no hay Koin, no hay interfaces de capas.
El juego se implementa directamente con KtxGame / KtxScreen (patrón en `Main2.kt`).
Target principal: **Desktop**. Android ya está configurado en el proyecto — usar si no añade trabajo significativo.

Existe un proyecto previo en `/home/garrapeta/development/git/wargame/docs/` con análisis
de reglas de juego útil (subsistemas, modelo de datos, modificadores de combate).
La arquitectura de software de ese proyecto es **irrelevante** — era para Compose.



## Motor espacial — Movimiento libre (pulgadas)

El tablero es espacio continuo 2D. No hay cuadrícula.

- Las posiciones y distancias se expresan en **pulgadas** (unidades de mundo).
- La detección de contacto de base, LOS, arcos frontales y terreno se implementa
  con geometría 2D: `Vector2`, `Rectangle`, `Polygon` (LibGDX math).
- El campo de juego estándar es **36" × 24"** (2' × 3' del tabletop físico).

**Implicaciones de implementación:**
- Un agente que toque el motor espacial NO debe introducir lógica de celda/tile.
- Las reglas de Mighty Armies hablan de "pulgadas" directamente — mapear 1 pulgada = 1 unidad de mundo.
- Esta decisión escala a DBA 2.2 sin reescritura (DBA también usa MU libres).

## Selección y agrupación de unidades

**Concepto de grupo:** múltiples unidades que cumplen condiciones de formación.
- Misma orientación (within 5° tolerance)
- Contiguas: compartiendo borde (top-right == top-left, etc.)

**Validación:** `Unit.canJoinGroup(others)` devuelve true si:
- El conjunto es vacío (siempre se puede seleccionar una)
- O hay al menos una unidad en `others` contigua a esta unit

**Selección en UI:**
- **Click sin grupo (single):** toggle individual o reemplazar selección
- **Click con grupo (Ctrl/Cmd):** agregar si es válido, remover si el resto sigue siendo grupo válido

**Implementación:**
- `UnitExt.kt`: extensiones `canJoinGroup()`, `isContiguousWith()`, funciones de geometría
- `ArmyUnitSelectionSystem`: máquina de selección, valida y aplica cambios
- `GeometryUtils.getCorners()`: cálculo de esquinas para rectángulos rotados
