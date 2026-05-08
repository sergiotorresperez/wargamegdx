# WargameGDX — CLAUDE.md

<!-- ==================== LAZY READING PROTOCOL ====================
AGENT: Lee SOLO este fichero al iniciar sesión. NO preleas otros ficheros.
Carga ficheros lazily: solo cuando el prompt del usuario lo requiera explícitamente.

Para leer una sección dentro de un fichero:
  1. Grep del §NombreSección → obtener número de línea
  2. Leer ~40 líneas desde ahí (ajustar si la sección es mayor)
  Nunca leer un fichero entero salvo que el usuario pida revisión completa.

REGLA DE ÍNDICE VIVO: Si añades o renombras una §Sección en cualquier fichero de doc,
actualiza la fila correspondiente en la tabla índice de este fichero en la misma sesión.
================================================================ -->

Wargame 2D desktop + Android. Kotlin + LibGDX + KTX. Detalles técnicos en `.agents/`.

## Índice de proyecto

| Leer cuando... | Fichero |
|----------------|---------|
| Inicio de sesión (siempre) | Este fichero |
| Antes de implementar cualquier cosa | `.agents/decisions.md` |
| Punto de entrada lazy del proyecto | `AGENTS.md` |
| Libs disponibles / versiones / módulos KTX | `.agents/stack.md` |
| Mapa de ficheros (¿dónde está X?) | `.agents/files.md` |
| Assets disponibles (texturas, audio, fuentes) | `.agents/assets.md` |
| Comandos build / run / distribución | `.agents/build.md` |
| Reglas de Mighty Armies (resumen) | `rulesets/mighty_armies_summary.md` |
| Reglas de Mighty Armies (detalle) | `rulesets/mighty_armies_ancients_core_rules.md` |
| Reglas de DBA 2.2 (resumen) | `rulesets/dba_summary.md` |
| API LibGDX (tiene índice propio por sección) | `libgdx-agent-docs.md` |

## Estado actual
- `Main.kt` — demo drop-game (`ApplicationListener` directo, no usar como patrón)
- `Main2.kt` — demo `KtxGame`/`KtxScreen` — **patrón a seguir**

## Workflow Claude-Humano
- **Claude escribe el código; el usuario toma todas las decisiones.**
- Antes de implementar algo no trivial, Claude propone el enfoque en 2-3 líneas y espera aprobación.
- El usuario dirige la arquitectura; Claude ejecuta.
- Lenguaje de comunicación: **español**.

## Convenciones de código
- Kotlin idiomático — usar KTX siempre que exista extensión equivalente.
- Preferir `KtxGame`/`KtxScreen` sobre `ApplicationListener` directo.
- Sin comentarios salvo que el WHY no sea obvio.
- No añadir abstracciones anticipadas; implementar solo lo pedido.
