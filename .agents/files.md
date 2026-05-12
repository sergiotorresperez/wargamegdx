# Files — Mapa del proyecto

## Código fuente (Kotlin)

### Core game (`core/src/main/kotlin/es/garrapeta/wargame/`)

| Fichero | Paquete | Descripción |
|---------|---------|-------------|
| `WarGame.kt` | `es.garrapeta.wargame` | Entry point: `KtxGame`, registra `WargameScreen` |

### Model (`model/`)

| Fichero | Descripción |
|---------|-------------|
| `Unit.kt` | POJO con datos espaciales: posición, orientación (facing), tamaño. Sin lógica, sin herencia. |

### Engine (`engine/`)

| Fichero | Descripción |
|---------|-------------|
| `Actor.kt` | Interfaz: contrato de renderizado (`render(shapeRenderer)`) |
| `GameEngine.kt` | Gestor de actores: lista, agregar actores, iterar y renderizar |

### Rendering (`rendering/`)

| Fichero | Descripción |
|---------|-------------|
| `UnitActor.kt` | Implementa `Actor`. Encapsula `Unit`, dibuja rectángulo + chevron |

### Screen (`screen/`)

| Fichero | Descripción |
|---------|-------------|
| `WargameScreen.kt` | `KtxScreen` principal: cámara, viewport, engine, renderizado |
| `InitialUnitsFactory.kt` | Factory: crea las 4 unidades iniciales (U1 aislada, A-B-C grupo) |

## Configuración de build

| Fichero | Descripción |
|---------|-------------|
| `build.gradle` | Root: plugins Kotlin/Android, repos, tarea `generateAssetList` |
| `gradle.properties` | Versiones de todas las dependencias |
| `settings.gradle` | Subproyectos: `lwjgl3`, `android`, `core` |
| `core/build.gradle` | Dependencias del juego (LibGDX, KTX, Ashley, Artemis, Box2D…) |
| `lwjgl3/build.gradle` | Desktop: plugin Construo, natives, JARs de distribución |
| `android/build.gradle` | Android: compileSdk, minSdk, natives ABI, desugaring |
| `lwjgl3/nativeimage.gradle` | Soporte opcional Graal Native Image |

## Configuración del proyecto

| Fichero | Descripción |
|---------|-------------|
| `AGENTS.md` | Punto de entrada para agentes (este sistema) |
| `CLAUDE.md` | Workflow Claude-humano + convenciones de código |
| `android/AndroidManifest.xml` | Permisos, orientación landscape, OpenGL ES 2.0 |
| `local.properties` | Ruta SDK Android local (no commitear) |

## Assets
Ver `.agents/assets.md` para índice completo de texturas, audio y fuentes.
Directorio: `assets/`

## Notas de arquitectura
- Todo código de juego va en `core/` (independiente de plataforma).
- Los launchers (`lwjgl3/`, `android/`) solo inicializan la app — no contienen lógica de juego.
- Package base: `es.garrapeta.wargame`
