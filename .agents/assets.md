# Assets — Índice de recursos

Directorio raíz: `assets/`
Lista generada automáticamente en: `assets/assets.txt` (regenerar con `./gradlew generateAssetList`)

## Texturas

| Fichero | Uso actual |
|---------|------------|
| `background.png` | Fondo del drop-game demo (`Main.kt`) |
| `bucket.png` | Sprite del cubo (`Main.kt`) |
| `drop.png` | Sprite de gota (`Main.kt`) |
| `logo.png` | Logo splash (`Main2.kt`) |
| `ui/uiskin.png` | Spritesheet del skin de UI (VisUI) |

## Audio

| Fichero | Tipo | Uso actual |
|---------|------|------------|
| `music.mp3` | Music (stream) | Música de fondo (`Main.kt`) |
| `drop.mp3` | Sound (effect) | Sonido al coger gota (`Main.kt`) |

## Fuentes (BitmapFont)

| Fichero | Uso |
|---------|-----|
| `ui/font.fnt` + `.png` | Fuente general UI |
| `ui/font-list.fnt` | Fuente para listas |
| `ui/font-subtitle.fnt` | Subtítulos |
| `ui/font-window.fnt` | Títulos de ventana |
| `com/badlogic/gdx/utils/lsans-15.fnt` | Fuente default LibGDX |

## UI Skin (VisUI)

| Fichero | Propósito |
|---------|-----------|
| `ui/uiskin.json` | Definición del skin (colores, estilos) |
| `ui/uiskin.atlas` | Atlas descriptor para `uiskin.png` |

## Notas
- LibGDX distingue `Sound` (carga en memoria, efectos) vs `Music` (stream, música).
- Los assets de demo (`Main.kt`) son placeholder; se reemplazarán por assets del wargame.
