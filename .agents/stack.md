# Stack — Dependencias y versiones

## Core frameworks

| Librería | Versión | Propósito |
|----------|---------|-----------|
| LibGDX (`gdx`) | 1.14.0 | Framework de juegos (render, input, audio, math) |
| KTX | 1.13.1-rc1 | Extensiones Kotlin para LibGDX (ver módulos abajo) |
| Kotlin | 2.3.21 | Lenguaje |
| Kotlinx Coroutines | 1.10.2 | Coroutines (via ktx-async) |

## ECS — Entity Component System

| Librería | Versión | Cuándo usar |
|----------|---------|-------------|
| Ashley (`com.badlogicgames.ashley`) | 1.7.4 | ECS oficial de LibGDX |
| Artemis ODB (`net.onedaybeard.artemis`) | 2.3.0 | ECS alternativo, más rendimiento |

**Decisión pendiente:** elegir uno de los dos antes de implementar entidades.
KTX tiene módulos para ambos: `ktx-ashley` y `ktx-artemis`.

## Física y AI

| Librería | Versión | Propósito |
|----------|---------|-----------|
| Box2D (`gdx-box2d`) | 1.14.0 | Motor de física 2D |
| gdx-ai | 1.8.2 | Steering behaviors, pathfinding, BT |

## UI

| Librería | Versión | Propósito |
|----------|---------|-----------|
| VisUI | 1f8b37a24b | UI toolkit avanzado para LibGDX |
| gdx-freetype | 1.14.0 | Carga fuentes TrueType en runtime |

## Módulos KTX disponibles

`ktx-app` · `ktx-actors` · `ktx-ai` · `ktx-artemis` · `ktx-ashley` ·
`ktx-assets` · `ktx-assets-async` · `ktx-async` · `ktx-box2d` ·
`ktx-collections` · `ktx-freetype` · `ktx-freetype-async` · `ktx-graphics` ·
`ktx-i18n` · `ktx-inject` · `ktx-json` · `ktx-log` · `ktx-math` ·
`ktx-preferences` · `ktx-reflect` · `ktx-scene2d` · `ktx-style` ·
`ktx-tiled` · `ktx-vis` · `ktx-vis-style`

Versiones declaradas en: `gradle.properties`

## Referencia de API LibGDX
Documentación completa en `libgdx-agent-docs.md`. Índice rápido:

| Necesidad | Sección |
|-----------|---------|
| SpriteBatch, Texture, Sprite | §B1 |
| Cámara y unidades de mundo | §B2 |
| Viewports (escalado de pantalla) | §B3 |
| Animación | §B4 |
| Tile maps | §B5 |
| UI / HUD (Scene2D) | §B6 |
| Input (teclado, ratón, touch) | §C |
| Audio (Sound, Music) | §D |
| Box2D física | §E |
| AssetManager (carga async) | §F |
| Persistencia (Preferences, JSON) | §G2, §G3 |
| Math utilities (Vector2, Rectangle) | §H |
| Memory management / dispose | §I |
