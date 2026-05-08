# garrapeta.md — Cómo trabajar con Claude Code en este proyecto

## Lo que soy y lo que no soy

Soy muy bueno escribiendo código, analizando reglas, investigando APIs y planificando.
No puedo ver la pantalla, no puedo ejecutar el juego, no sé si algo se ve bien visualmente.
**Tu trabajo principal es ejecutar, mirar y decirme qué pasó.**

---

## Mis limitaciones reales

### Contexto finito por sesión
Una sesión larga degrada mi calidad. Si noto que me repito o pierdo el hilo, mejor
terminar la sesión, hacer commit, y abrir una nueva con el objetivo siguiente.
Las sesiones cortas y enfocadas producen mejor código.

### No recuerdo entre sesiones
Entre sesiones dependo 100% de los ficheros del proyecto:
`CLAUDE.md` → `AGENTS.md` → `.agents/` → código fuente.
Si algo importante se decide en conversación y no queda en un fichero, lo olvidaré.
**Regla: toda decisión relevante acaba en `.agents/decisions.md` o en el código.**

### No veo el resultado de ejecutar
Cuando digo "ya está" significa que el código compila (en mi cabeza). No significa que
funciona. Necesito que ejecutes y me cuentes qué pasó — errores, comportamiento visual,
lo que sea. Esto es el ciclo más importante del workflow.

---

## El workflow óptimo por sesión

```
1. Tú dices el objetivo de la sesión (una sola cosa)
2. Yo propongo el enfoque en 2-3 líneas y espero tu OK
3. Yo implemento en commits pequeños
4. Tú ejecutas y me dices qué pasa
5. Iteramos hasta que funciona
6. Commit y fin de sesión
```

**Una sesión = un objetivo = un commit (o dos si es inevitable).**
Si me das tres cosas a la vez, las haré peor que si me las das de una en una.

---

## Cómo darme una tarea

**Bien:** "haz que aparezca el tablero vacío en pantalla"
**Bien:** "cuando hago click en una unidad, que se resalte"
**Mal:** "implementa el sistema de movimiento y combate y la UI"

Cuanto más pequeño el objetivo, más limpio el resultado. Siempre puedo hacer más
en la misma sesión si termino rápido — pero no puedo deshacer un desastre grande.

---

## Cuándo usar Plan Mode

Actívalo (o pídemelo) cuando:
- No tenemos claro cómo implementar algo
- Hay más de un fichero implicado de forma no obvia
- Es una decisión de arquitectura (no de código)

No hace falta para: añadir un campo a una clase, cambiar un parámetro, renombrar algo.

---

## Commits pequeños — por qué importa para mí

Cada commit es un checkpoint. Si me equivoco después de un commit, volvemos al commit
y reintentamos. Si llevamos 2 horas sin commit y me equivoco, hemos perdido 2 horas.

**Criterio:** si puedes describir el commit en una frase, es el tamaño correcto.

---

## Lo que necesito que hagas tú

- **Ejecutar** el código después de cada cambio significativo
- **Copiarme el error completo** si hay uno (no parafrasear — el texto exacto)
- **Decirme si algo se ve mal** aunque no sepas por qué
- **Tomar las decisiones de diseño** — yo propongo, tú decides
- **Interrumpirme** si voy por el camino equivocado (es gratis y mejor hacerlo pronto)

---

## Lo que NO necesito que hagas

- Explicarme el código que escribí — lo sé
- Pedirme permiso para ejecutar — hazlo siempre
- Disculparte por corregirme — corregirme es exactamente lo que debes hacer
- Darme contexto que ya está en los ficheros — confía en que los leo

---

## El ciclo de dos que funciona

```
Garrapeta: "quiero X"
Claude:     propone enfoque → espera OK
Garrapeta: OK / corrección
Claude:     implementa
Garrapeta: ejecuta → "funciona" / "error: [texto]" / "se ve raro porque [descripción]"
Claude:     itera o cierra
→ commit
→ siguiente X
```

---

## Próximo objetivo estratégico recomendado

**Tablero vacío con una unidad seleccionable.**

Por qué es el primer paso correcto:
- Valida que LibGDX funciona end-to-end (render, input, coordenadas de mundo)
- Fuerza las decisiones de viewport y unidades de mundo de forma concreta
- Da resultado visual desde el primer día
- Todo lo demás (movimiento, combate, UI) se construye encima de esto
- Commit pequeño y claro: "render tablero 36x24 pulgadas con una unidad"

Lo que NO incluye este primer paso: reglas, validación de movimiento, múltiples unidades.
Solo: tablero dibujado, un rectángulo que representa una unidad, click para seleccionarla.
