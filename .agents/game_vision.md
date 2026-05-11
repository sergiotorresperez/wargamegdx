# Game Vision — Mighty Armies: Ancients (Completo)

## Visión general

Implementación completa de **Mighty Armies: Ancients** — juego de miniaturas de guerras antiguas, 15mm, tablero 2'×3' (36"×24" en unidades de mundo).

Dos ejércitos de 60 AP cada uno se enfrentan en batalla dinámica. Cada turno: tirada de Puntos de Movimiento, movimiento, disparo, combate cuerpo a cuerpo.

---

## Subsistemas del juego completo

### 1. **Motor espacial** (✅ MVP Stage 1)
- Posicionamiento continuo (pulgadas)
- Orientación (facing, rotación libre → futura limitación a 90°)
- Detección de base contact (geometría 2D)
- Movimiento de unidades individuales y grupos
- **MVP cubre**: selección, movimiento libre, rotación libre, grupos

### 2. **Motor de movimiento** (Parcial MVP, expansion post-MVP)
- Puntos de Movimiento (tirada d6 por turno)
- Límites de velocidad por unidad type
- Formación y reagrupación
- Terreno (ralentiza movimiento)
- **MVP cubre**: movimiento libre sin límites de speed, sin terreno
- **Post-MVP**: velocidad, terreno, limpieza de movimiento por turno

### 3. **Combat — Shooting** (Post-MVP)
- Línea de vista (LOS)
- Rango (máx 7")
- Tabla de acierto (Shooting I/II vs Fighting score)
- Destrucción de unidades

### 4. **Combat — Close Combat** (Post-MVP)
- Iniciación automática (base contact)
- Resolución (d6 + Fighting + modifiers)
- Resultados: Driven Back, Destroyed, Draw
- Support (unidades en grupo suman)
- Flanking y Surprise Attacks
- Rearrangement post-combate

### 5. **Sistema de datos de unidades** (MVP base, expansion post-MVP)
- Tipos de unidad (Infantry, Cavalry, Chariot, etc.)
- Stats: Fighting score, Shooting, Speed, Support
- Basing (50×25mm, 50×50mm, large base)
- Habilidades especiales (General, Scout, etc.)
- **MVP cubre**: posición, facing, basing visual
- **Post-MVP**: stats, habilidades, type

### 6. **Construcción de ejército** (Post-MVP)
- Army Point system (60 AP por bando)
- Selección de unidades
- Validación (General único, máx 4 scouts, etc.)
- UI de army building

### 7. **Turno y estado del juego** (Post-MVP)
- Turn sequence: Move Points → Movement → Shooting → Close Combat
- Game state: setup, playing, victory
- Condiciones de victoria: 50% units destroyed OR General destroyed + fewer AP lost

### 8. **Terreno e iluminación** (Post-MVP)
- Colocación de terreno pre-juego
- Efectos: LOS blocking, movimiento ralentizado
- Renderizado de terreno

---

## Relación MVP Stage 1 → Juego Completo

**MVP Stage 1 es la capa fundamental:**
- Establece motor espacial (pulgadas, facing, selección, movimiento)
- Establece grupo (base contact, formación)
- Permite renderizar y interactuar con unidades

**Post-MVP extensions (secuencia lógica):**
1. Stats y tipos de unidad (necesario para combat)
2. Puntos de movimiento y límites de velocidad
3. Combate (shooting + close combat)
4. Terreno y LOS
5. Army building y game state
6. UI completa y victoria conditions

**Nota de arquitectura:** Las decisiones en MVP (modelo de Unit, Group, geometría) deben soportar todos estos subsistemas sin reescritura.

---

## Alcance por subsistema

| Subsistema | MVP | Post-MVP |
|------------|-----|----------|
| Motor espacial | ✅ (posición, facing, base contact) | — |
| Movimiento libre | ✅ | Límites de speed, terreno |
| Selección | ✅ | — |
| Grupos | ✅ (base contact, formación) | — |
| Stats de unidad | — | Fighting, Shooting, Speed, Support |
| Combate | — | Shooting, Close Combat |
| Terreno | — | LOS, ralentización |
| Army building | — | Point system, validación |
| Game state | — | Turnos, victoria |
