# Decisiones de arquitectura

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
