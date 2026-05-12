# WargameGDX — Agent Entry Point

Wargame 2D desktop + Android. Kotlin + LibGDX 1.14.0 + KTX. Fase inicial.
Package: `es.garrapeta.wargame`

## Leer siempre al iniciar
- `CLAUDE.md` — workflow Claude-humano y convenciones de código

## Índice lazy — leer solo lo que necesite la tarea

| Necesidad | Leer |
|-----------|------|
| Visión del juego completo y cómo MVP es punto de partida | `.agents/game_vision.md` |
| Modelo de datos (Unit, Group, entidades futuras) | `.agents/data_model.md` |
| Requisitos MVP Stage 1 (selección, movimiento, grupos) | `.agents/mvp_stage1_requirements.md` |
| Arquitectura del game engine implementado | `.agents/game_engine.md` |
| Paradigmas arquitectónicos disponibles | `.agents/ashley_guide.md`, `.agents/artemis_guide.md`, `.agents/hybrid_guide.md`, `.agents/scene2d_guide.md` |
| Decisiones de arquitectura (leer siempre antes de implementar) | `.agents/decisions.md` |
| Librerías disponibles / versiones | `.agents/stack.md` |
| Mapa de ficheros (¿dónde está X?) | `.agents/files.md` |
| Assets (texturas, audio, fuentes) | `.agents/assets.md` |
| Comandos build / run / distribución | `.agents/build.md` |
| Config Android específica | `android/AndroidManifest.xml`, `android/build.gradle` |
| Config Desktop específica | `lwjgl3/build.gradle` |
| Código existente del juego | Ver rutas en `.agents/files.md` → leer el fichero concreto |

## Referencias externas

| Recurso | Dónde |
|---------|-------|
| LibGDX referencia completa (API, patrones, ejemplos) | `libgdx-agent-docs.md` (tiene su propio índice por sección) |
| LibGDX docs web | https://libgdx.com/dev/ |

## No leer sin necesidad
Los ficheros en `.agents/` son autónomos entre sí — cargar solo el relevante.
No leer código fuente hasta conocer qué fichero específico se necesita.
