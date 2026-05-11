# Files — Mapa del proyecto

## Código fuente (Kotlin)

empty for now

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
