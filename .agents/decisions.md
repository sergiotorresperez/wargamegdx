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
