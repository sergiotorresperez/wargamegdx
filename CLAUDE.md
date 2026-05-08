# WargameGDX — CLAUDE.md

Wargame 2D desktop + Android. Kotlin + LibGDX + KTX. Detalles técnicos en `.agents/`.

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
