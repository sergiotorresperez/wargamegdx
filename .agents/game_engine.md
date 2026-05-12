# Game Engine Architecture

## Visión general

El game engine es un sistema **simple y extensible** basado en:
- **Actor pattern** (polimorfismo): cada objeto visual implementa interfaz `Actor`
- **GameEngine**: gestor que colecciona y renderiza actores
- **Hybrid architecture**: objetos con identidad (Unit) + componentes visuales (UnitActor)

```
WargameScreen (orquestación)
    └── GameEngine (gestor de actores)
            ├── UnitActor (Unit → presentación visual)
            ├── UnitActor (Unit → presentación visual)
            ├── UnitActor (Unit → presentación visual)
            └── UnitActor (Unit → presentación visual)
```

---

## Componentes principales

### 1. Actor (interfaz)

**Ubicación:** `engine/Actor.kt`

```kotlin
interface Actor {
    fun render(shapeRenderer: ShapeRenderer)
}
```

**Propósito:** Define el contrato de renderizado. Cualquier objeto visual implementa esta interfaz.

**Por qué polimorfismo:** Permite agregar nuevos tipos de actores (ParticleActor, TerrainActor, etc.) sin modificar GameEngine.

---

### 2. Unit (modelo de datos)

**Ubicación:** `model/Unit.kt`

```kotlin
class Unit(
    val id: String,                          // "A", "B", "C", "U1"
    val position: Vector2,                   // mutable: centro en pulgadas
    var facingAngle: Float = 0f,             // grados: 0°=norte
    val size: Vector2 = Vector2(2f, 1f)      // 2"×1" (inmutable)
)
```

**Propósito:** Almacenar datos espaciales de una unidad (posición, orientación). Sin lógica de renderizado, sin herencia.

**Por qué separado de UnitActor:**
- **Unit** = modelo puro (datos)
- **UnitActor** = presentación (rendering)
- Permite tener múltiples vistas de la misma unidad, testing sin dependencias visuales, etc.

---

### 3. UnitActor (presentación)

**Ubicación:** `actor/UnitActor.kt`

```kotlin
class UnitActor(val unit: Unit) : Actor {
    override fun render(shapeRenderer: ShapeRenderer) {
        // Dibujar rectángulo + chevron basado en datos de unit
    }
}
```

**Propósito:** Renderizar visualmente una `Unit`.

**Responsabilidades:**
- Acceder a datos de Unit (posición, facing, tamaño)
- Dibujar rectángulo (outline)
- Dibujar chevron indicando facing (norte = 0°)

**Nota:** Por ahora, rectángulos axis-aligned. Cuando hay rotación, se calcularán corners rotados.

---

### 4. GameEngine (gestor)

**Ubicación:** `engine/GameEngine.kt`

```kotlin
class GameEngine(private val shapeRenderer: ShapeRenderer) {
    private val actors: MutableList<Actor> = mutableListOf()

    fun addActor(actor: Actor) {
        actors.add(actor)
    }

    fun render() {
        for (actor in actors) {
            actor.render(shapeRenderer)
        }
    }
}
```

**Propósito:** Gestionar colección de actores e iterar para renderizar.

**Por qué centralizado:**
- Separación clara de responsabilidades
- Fácil agregar lógica futura (act/update, culling, etc.)
- Decoupling de WargameScreen

---

### 5. WargameScreen (orquestación)

**Ubicación:** `screen/WargameScreen.kt`

```kotlin
class WargameScreen : KtxScreen {
    private val engine = GameEngine(shapeRenderer)

    override fun show() {
        // Crear unidades
        for (unit in InitialUnitsFactory().createUnits()) {
            engine.addActor(UnitActor(unit))
        }
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        camera.update()
        shapeRenderer.use(ShapeType.Line, camera) {
            engine.render()
        }
    }
}
```

**Propósito:** Orquestar ciclo de vida del juego (setup cámara, llamar engine).

**Responsabilidades:**
- Crear unidades iniciales
- Setup cámara y viewport (36"×24" mundo en pulgadas)
- Llamar a engine.render() en cada frame
- Manejar resize y dispose

---

## Flujo de renderizado

```
WargameScreen.render(delta)
    ↓
ScreenUtils.clear()  // fondo negro
camera.update()
    ↓
shapeRenderer.use(ShapeType.Line, camera) {
    ↓
    engine.render()  // itera sobre actores
        ↓
        for (actor in actors) {
            actor.render(sr)  // polimorfismo
        }
            ↓
            UnitActor.render(sr)
                ↓
                dibuja rectángulo (4 líneas)
                dibuja chevron (3 líneas)
}
    ↓
Pantalla actualizada
```

---

## Extensibilidad

### Agregar nuevo tipo de actor

Ejemplo: ParticleActor que renderiza partículas

```kotlin
// 1. Crear clase que implementa Actor
class ParticleActor : Actor {
    override fun render(shapeRenderer: ShapeRenderer) {
        // dibuja partículas
    }
}

// 2. Agregar al engine
engine.addActor(ParticleActor(...))

// GameEngine renderiza ambos sin cambios
```

### Agregar métodos al engine

Ejemplo: método `act()` para update lógica

```kotlin
class GameEngine {
    fun act(delta: Float) {
        for (actor in actors) {
            actor.act(delta)  // si Actor tiene act()
        }
    }
}
```

---

## Decisiones de diseño

### ¿Por qué Unit y UnitActor separados?

- **Flexibilidad:** Unit es pure data, reutilizable
- **Testing:** Unit se puede testear sin ShapeRenderer
- **Múltiples vistas:** misma Unit, diferentes renderizaciones posibles
- **Separación de concerns:** modelo ≠ presentación

### ¿Por qué polimorfismo con Actor?

- **Extensibilidad:** agregar nuevos actores sin modificar GameEngine
- **Simplicidad:** GameEngine solo itera y llama render()
- **Escalabilidad:** puede crecer sin cambiar estructura

### ¿Por qué GameEngine en lugar de Scene2d Stage?

- **Control:** engine custom, simple y focused
- **Bajo overhead:** solo lo que necesitamos (sin widgets UI)
- **Libertad:** agregar lógica custom fácilmente

---

## Estado actual (Fase 1a)

✅ **Completado:**
- Unit (modelo de datos)
- UnitActor (renderizado simple, axis-aligned)
- GameEngine (gestor de actores)
- WargameScreen (orquestación básica)
- 4 unidades iniciales con chevrones

❌ **Por hacer:**
- Métodos en Unit (rotate, advance, moveTo)
- Input (click, flechas)
- Selección de unidades
- Rotación de unidades (corners rotados)
- Grupos y formaciones
- Combate y stats

---

## Referencias

- `.agents/mvp_stage1_requirements.md` — requisitos del MVP
- `.agents/data_model.md` — modelo de datos conceptual
- `.agents/files.md` — mapa de ficheros (actualizado)
