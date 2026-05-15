# Artemis-ODB — ECS Framework Maduro para Juegos

**Artemis-ODB** (continuación de Artemis original) es un framework **Entity Component System (ECS) de producción** para Java/Kotlin, posicionándose como la implementación "high-performance" del patrón en el ecosistema Java. Mantiene independencia de LibGDX (a diferencia de Ashley), permitiendo uso flexible.

**Propósito**: Resolver escalabilidad herencial. ECS invierte POO tradicional: composición sobre herencia.

---

## ¿Qué es Artemis-ODB?

Evolución de Artemis original (2011) → artemis-odb (2013+). Añade:
- Bytecode weaving (procesador de anotaciones)
- Reflection caching (pre-generado en build)
- Fluid API para construcción de entidades
- Serialización nativa (JSON, Kryo, Binary)
- Profiler integrado
- Soporte GWT (web)

Diseñado para proyectos **medianos a grandes** con requisitos de rendimiento.

---

## Conceptos Clave

### Entity
Contenedor de componentes + ID único. Sin lógica.

```kotlin
data class PositionComponent(var x: Float = 0f, var y: Float = 0f) : Component

val player = world.create()
    .add(PositionComponent(100f, 100f))
    .add(VelocityComponent(0f, 0f))
    .add(HealthComponent(100))
```

### Component
Contenedor de datos puro (como JavaBean). Sin lógica. Define propiedades.

```kotlin
data class PositionComponent(var x: Float = 0f, var y: Float = 0f) : Component
data class VelocityComponent(var vx: Float = 0f, var vy: Float = 0f) : Component
data class HealthComponent(var hp: Int = 100) : Component
```

### EntitySystem
Contiene lógica de juego. Procesa entidades que coinciden con un **Aspect** (filtro de componentes).

```kotlin
class MovementSystem : EntitySystem(
    aspectOf(PositionComponent::class, VelocityComponent::class)
) {
    // Dependency injection automática de mappers
    private lateinit var posMapper: ComponentMapper<PositionComponent>
    private lateinit var velMapper: ComponentMapper<VelocityComponent>
    
    override fun processEntity(e: Entity, deltaTime: Float) {
        val pos = posMapper.get(e)
        val vel = velMapper.get(e)
        pos.x += vel.vx * deltaTime
        pos.y += vel.vy * deltaTime
    }
}
```

### World
Contenedor global. Gestiona:
- Todas las entidades
- Todos los sistemas
- Managers (globales)
- Sincronización de procesamiento

```kotlin
val world = World()
world.setSystem(MovementSystem())
world.setSystem(RenderSystem())

// Game loop
world.setDelta(deltaTime)
world.process()
```

### Aspect (Familia)
Define qué combinaciones de componentes debe tener una entidad para que un system la procese.

```kotlin
// Sistema procesa entidades con PositionComponent Y VelocityComponent
aspectOf(PositionComponent::class, VelocityComponent::class)

// Excluye muertas
aspectOf(PositionComponent::class)
    .exclude(DeadComponent::class)
```

### ComponentMapper
Acceso cacheado y tipado a componentes. Optimización crítica (O(1) vs O(n)).

```kotlin
private lateinit var posMapper: ComponentMapper<PositionComponent>

// Injection automática (Artemis ventaja vs Ashley)
val pos = posMapper.get(entity)  // Rápido
```

### Manager
Singleton global en World. Gestiona estado compartido (eventos, recursos, etc.).

```kotlin
class EventManager : Manager() {
    fun emit(event: GameEvent) { /* ... */ }
}

world.setManager(EventManager())
```

### Dependency Injection
**Feature exclusiva de Artemis** vs Ashley. Mappers se inyectan automáticamente.

```kotlin
// Artemis: automático
private lateinit var mapper: ComponentMapper<PositionComponent>

// Ashley: manual cada vez
ComponentMapper.getFor(PositionComponent.class)
```

---

## Patrones de Uso

### 1. Setup de World

```kotlin
val world = World()

// Registrar sistemas en orden
world.setSystem(PhysicsSystem(), true)  // true = activo al inicio
world.setSystem(MovementSystem(), true)
world.setSystem(RenderSystem(), true)

// Registrar managers
world.setManager(EventManager())
world.setManager(InputManager())
```

### 2. Creación de entidades

```kotlin
// Simple
val enemy = world.create()

// Fluid API
val player = world.create()
    .add(PositionComponent(0f, 0f))
    .add(VelocityComponent(0f, 0f))
    .add(HealthComponent(100))
    .add(SpriteComponent(playerTexture))
```

### 3. Game Loop

```kotlin
override fun render(delta: Float) {
    world.setDelta(delta)
    world.process()  // Ejecuta todos los sistemas
    
    // Datos de componentes disponibles para renderizado
    batch.begin()
    // ... renderizar
    batch.end()
}
```

### 4. Comunicación entre sistemas

Opción A: Event Manager (patrón pub/sub)
```kotlin
class EventManager : Manager() {
    fun emit(event: DamageEvent) { /* ... */ }
}

class DamageSystem : EntitySystem(...) {
    @Transient private lateinit var eventManager: EventManager
    
    fun applyDamage(target: Entity, damage: Int) {
        eventManager.emit(DamageEvent(target, damage))
    }
}
```

Opción B: Acceso directo a componentes de otra entidad
```kotlin
class MovementSystem : EntitySystem(...) {
    private lateinit var posMapper: ComponentMapper<PositionComponent>
    
    fun moveEntity(e: Entity, dx: Float, dy: Float) {
        val pos = posMapper.get(e)
        pos.x += dx
        pos.y += dy
    }
}
```

---

## Ventajas de Artemis-ODB

| Ventaja | Descripción |
|---------|-------------|
| **Escalabilidad** | 1000+ entidades sin lag; composición flexible |
| **Rendimiento extremo** | Bytecode weaving, reflection cache pre-generada |
| **Dependency Injection** | Mappers inyectados automáticamente (ahorra boilerplate) |
| **Serialización nativa** | JSON, Kryo, Binary sin código adicional |
| **Fluid API** | `world.create().add(...).add(...)` elegante |
| **Profiler integrado** | System profiler para debugging |
| **GWT support** | Reflection cache pre-generada (importante para web) |
| **Independencia de LibGDX** | Usa LibGDX opcionalmente; puede integrarse en otros contextos |
| **Extensibilidad** | Sistema de plugins y managers |
| **Testing** | Lógica separada (componentes/sistemas) es fácil testear |

---

## Desventajas de Artemis-ODB

| Desventaja | Descripción |
|------------|-------------|
| **Curva aprendizaje** | Requiere pensar diferente (ECS); DI no es convencional |
| **Setup complejo** | Bytecode weaving en gradle; procesador de anotaciones |
| **Overhead inicial** | Justificado solo con >50 entidades; overkill para simples |
| **Documentación dispersa** | Wiki fragmentada; menos tutoriales que Ashley |
| **Reflexión** | Aunque optimizada, sigue en inicio |
| **Community smaller** | Menos Stack Overflow, menos ejemplos públicos |
| **Configuration gradle** | artemis-odb-processor requiere config adicional |

---

## Curva de Aprendizaje

**Ashley**: 1-2 días (patrón familiar, minimalista).

**Artemis**: 3-5 días (más conceptos, DI no convencional):
1. Entender Entity/Component/System (día 1)
2. Aspectos y filtrado (día 2)
3. ComponentMapper y optimizaciones (día 2.5)
4. Dependency Injection (día 3)
5. Managers y comunicación entre sistemas (día 4)

**Veredictto**: Artemis exige inversión inicial, pero código resultante es más mantenible en proyectos medianos+.

---

## Ashley vs Artemis — Comparación Técnica

| Aspecto | Artemis-ODB | Ashley |
|---------|-------------|--------|
| **Curva aprendizaje** | Alta (3-5 días) | Baja (1-2 días) |
| **Rendimiento base** | Comparable | Comparable |
| **Rendimiento extremo (1000+)** | Superior | Degradación leve |
| **Dependencias** | Ninguna obligatoria (LibGDX opcional) | Requiere libGDX |
| **Dependency Injection** | Nativa (automática) | Manual con mappers |
| **Boilerplate** | Menos | Más (mappers repetitivos) |
| **Serialización** | JSON/Kryo/Binary nativa | Manual |
| **Fluid API** | Sí (`create().add(...)`) | No (boilerplate) |
| **Profiler** | Integrado | No |
| **GWT/Web** | Soportado (reflection cache) | Limitado |
| **Documentación** | Dispersa pero existe | Oficial de LibGDX |
| **Comunidad** | Pequeña, activa | Más grande, más recursos |
| **Recomendación** | Proyectos medianos-grandes, equipos experimentados | Prototipado rápido, equipos nuevas a ECS |

---

## OOP vs ECS: La perspectiva de Artemis

Artemis no es "más OOP-friendly", sino **usa patrones OOP** para resolver **problemas que OOP no resuelve bien**.

**Analógico**:
- **OOP clásico**: Herencia profunda → explosión de subclases (Player extends Character extends Entity)
- **ECS (Artemis)**: Composición → comportamiento emerge de combinación de componentes

**Artemis "parece OOP"** porque usa:
- Dependency Injection (patrón OOP clásico)
- Interfaces y abstracción (EntitySystem, Manager)
- Reflexión y anotaciones (patrón OOP avanzado)

Pero invierte la filosofía: datos separados de lógica, comportamiento compuesto, no heredado.

---

## Integración con LibGDX

**Relación**: Artemis-ODB **independiente** de LibGDX. Coexisten ortogonalmente:

```kotlin
class GameScreen : KtxScreen {
    private val world = World()
    
    init {
        world.setSystem(MovementSystem())
        world.setSystem(RenderSystem())
    }
    
    override fun render(delta: Float) {
        world.setDelta(delta)
        world.process()
    }
}
```

**KTX support**: `ktx-artemis` proporciona builders y extensiones Kotlin.

---

## Casos de Uso Ideales

✅ **Artemis brilla en:**
- Juegos medianos-grandes (RPG, estrategia, acción con AI complejo)
- Aplicaciones con 100+ entidades
- Proyectos que requieren serialización persistente
- Equipos con experiencia Java/Gradle
- Requisitos de rendimiento críticos

❌ **Artemis NO es buena para:**
- Prototipos de 1 semana (overhead setup)
- Juegos muy simples (<50 entidades)
- Equipos nuevas a ECS (curva aprendizaje)
- Proyectos que necesitan resultado rápido

---

## Ejemplo Completo

```kotlin
// 1. Componentes
data class PositionComponent(var x: Float = 0f, var y: Float = 0f) : Component
data class VelocityComponent(var vx: Float = 0f, var vy: Float = 0f) : Component
data class HealthComponent(var hp: Int = 100) : Component

// 2. Sistemas
class MovementSystem : EntitySystem(
    aspectOf(PositionComponent::class, VelocityComponent::class)
) {
    private lateinit var posMapper: ComponentMapper<PositionComponent>
    private lateinit var velMapper: ComponentMapper<VelocityComponent>
    
    override fun processEntity(e: Entity, deltaTime: Float) {
        val pos = posMapper.get(e)
        val vel = velMapper.get(e)
        pos.x += vel.vx * deltaTime
        pos.y += vel.vy * deltaTime
    }
}

class HealthSystem : EntitySystem(aspectOf(HealthComponent::class)) {
    private lateinit var healthMapper: ComponentMapper<HealthComponent>
    
    override fun processEntity(e: Entity, deltaTime: Float) {
        val health = healthMapper.get(e)
        if (health.hp <= 0) {
            e.deleteFromWorld()
        }
    }
}

// 3. Crear y ejecutar
val world = World()
world.setSystem(MovementSystem(), true)
world.setSystem(HealthSystem(), true)

// Crear entidad
val player = world.create()
    .add(PositionComponent(100f, 100f))
    .add(VelocityComponent(50f, 0f))
    .add(HealthComponent(100))

// Game loop
fun gameLoop(deltaTime: Float) {
    world.setDelta(deltaTime)
    world.process()
}
```

---

## Referencias

- [Artemis-ODB GitHub](https://github.com/junkdog/artemis-odb)
- [Artemis-ODB Wiki](https://github.com/junkdog/artemis-odb/wiki)
- [LibGDX Artemis Quickstart](https://github.com/DaanVanYperen/libgdx-artemis-quickstart)
- [Artemis vs Ashley Discussion](https://github.com/junkdog/artemis-odb/wiki/Artemis-vs-Ashley:-(Don't)-Fight!)
- [KTX Artemis Support](https://libktx.github.io/)
- [Strategy RPG Tutorial with Artemis](https://jvm-gaming.org/t/strategy-rpg-beginners-tutorial-using-libgdx-artemis/41779)
