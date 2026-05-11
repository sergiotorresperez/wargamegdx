# MVP Stage 1 — Requisitos

**NOTA: Este es el MVP inicial. El diseño debe permitir evolución hacia el juego completo (ver `.agents/game_vision.md`).**

## Objetivo
Implementar los fundamentos de selección, movimiento y agrupación de unidades en el tablero. Sin combate, sin stats, sin terreno. Enfoque: entender la mecánica de selección y movimiento libre.

---

## Mecánica de selección

### Unidad aislada
- **Click en unidad** → selecciona esa unidad
- Unidad seleccionada se visualiza de forma diferenciada

### Grupo de unidades
- **Grupo**: dos o más unidades en **base contact** (lados tocándose)
- Si click recae en una unidad que está en grupo legal → **selecciona el grupo entero**
- Grupo seleccionado se visualiza con outline envolvente

### Desacoplamiento de grupo
- Si usuario selecciona una unidad específica dentro de un grupo seleccionado → esa unidad se selecciona aisladamente
- Al mover esa unidad fuera del grupo → se desacopla
- Las unidades restantes pueden volver a agruparse si permanecen en base contact

---

## Mecánica de movimiento

### Rotación (unidad y grupo)
- **Tecla izquierda/derecha** → rota unidad/grupo seleccionado
- Rotación **libre** (sin límites de ángulo por ahora)
- **Unidad aislada**: cambia facing, se redibuja rotada
- **Grupo**: las unidades rotan en torno al **centro del grupo** + se reposicionan manteniendo formación y base contact

### Traslación (unidad y grupo)
- **Tecla arriba** → avanza (trasladar siguiendo facing)
- **Unidad aislada**: se traslada en dirección de su facing
- **Grupo**: todas las unidades se trasladan juntas, manteniendo formación y facing individual

### Sin limitaciones (MVP)
- Sin límites de velocidad
- Sin colisión con otras unidades
- Sin límites de rango de movimiento
- Sin terreno

---

## Setup inicial

**4 unidades en mundo:**
- 1 unidad aislada (ubicación arbitraria)
- 3 unidades en formación A-B-C (base contact, dispuestas horizontalmente como el ejemplo de requisitos)

**Propósito:** testear selección individual, selección de grupo, desacoplamiento y reagrupación.

---

## Visualización

### Unidades
- Rectángulo simple (2" × 1")
- Chevron (triángulo) indicando facing
- Color diferenciado cuando está seleccionada
- Outline adicional cuando es parte de un grupo seleccionado

### Grupo
- Outline envolvente (rectángulo o polígono) que contiene todas las unidades del grupo

---

## Plataforma & Escala

- **Plataforma**: Desktop only (para MVP)
- **Escala**: Mundo en inches (unidades LibGDX)
  - Unidades físicas: 50mm × 25mm = ~2" × 1"
  - Campo estándar: 36" × 24" (2' × 3' del juego físico)

---

## Input Method (MVP)

- **Click en unidad** → selecciona unidad o grupo
- **Flecha izquierda/derecha** → rotar (±ángulo, sin límite)
- **Flecha arriba** → avanzar (trasladar siguiendo facing)
- **TBD**: método alternativo para seleccionar grupo de múltiples unidades en formación legal

---

## Nota: Decisiones pendientes

- Modelo de grupo: ¿entidad persistente en memoria vs propiedad derivada de posición? (Decisión de diseño, no en requisitos)
- Input method alternativo para seleccionar múltiples unidades (TBD)
- Limitaciones futuras: velocidad, rango, colisiones, terreno, etc. (post-MVP)