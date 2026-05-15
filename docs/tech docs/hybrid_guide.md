# Hybrid Architecture — OOP + Components (sin ECS puro)

**Hybrid** es un patrón arquitectónico que combina **OOP con identidad de objetos** + **composición de componentes reutilizables**, sin adoptar ECS puro.

También conocido como: **"Unity-style components"** o **"GameObject + Components"** pattern.

**Filosofía**: Mantener entidades con identidad y cerebro propio, pero extraer funcionalidades reutilizables en componentes para evitar clases gigantes.

---

## Problema: OOP Puro

```java
class Player extends Character {
    void jump() { ... }
    void attack() { ... }
    void takeDamage(int dmg) { ... }
    void castSpell(Spell s) { ... }
    void openInventory() { ... }
    void heal() { ... }
    void pickupItem(Item i) { ... }
}

class Enemy extends Character {
    void patrol() { ... }
    void attack() { ... }
    void takeDamage(int dmg) { ... }
    void die() { ... }
}

class Projectile extends MovingEntity {
    void move() { ... }
    void onHit(Entity target) { ... }
}
```

**Problemas**:
- Herencia profunda (`Player → Character → Entity`)
- Métodos duplicados (ambos tienen `attack()`, `takeDamage()`)
- Clases enormes
- Difícil reutilizar comportamiento entre Enemies y Players
- Frágil: cambiar base class afecta todo

---

## Problema: ECS Puro

```kotlin
// Entity = ID vacío
entity.add(PositionComponent(x, y))
entity.add(VelocityComponent(vx, vy))
entity.add(HealthComponent(100))

// Systems controlan TODO
MovementSystem { procesa Position + Velocity }
CombatSystem { procesa Combat + Health }
InventorySystem { procesa Inventory }
AISystem { procesa AI + Position }

// Para mover un jugador:
// - InventorySystem comprueba si puede moverse
// - MovementSystem aplica velocidad
// - AISystem decide destino
// - ...
```

**Problemas**:
- Mucho boilerplate (systems, mappers, aspects)
- Difícil seguir flujo lógico ("¿quién mueve al jugador?")
- Exceso de systems para juegos medianos
- Eventos/comunicación compleja
- Overkill para juegos sencillos

---

## Solución: Hybrid

Mantener objetos con **identidad y cerebro**, pero **componentes reutilizables** internos.

```kotlin
// Actores con identidad
class PlayerActor : EntityActor {
    private val health = HealthComponent()
    private val inventory = InventoryComponent()
    private val weapon = WeaponComponent()
    
    fun attack(target: Actor) {
        weapon.attack(target)  // Delega al componente
    }
    
    fun takeDamage(amount: Int) {
        health.damage(amount)  // Delega al componente
    }
    
    fun move(x: Float, y: Float) {
        this.x = x  // Comportamiento "macro"
        this.y = y
    }
}

class EnemyActor : EntityActor {
    private val health = HealthComponent()
    private val weapon = WeaponComponent()
    private val ai = AIComponent()
    
    fun update(delta: Float) {
        ai.update(delta)  // IA decide qué hacer
        if (ai.shouldAttack) {
            weapon.attack(target)
        }
    }
}

// Componentes reutilizables
class HealthComponent {
    var hp = 100
    var maxHp = 100
    
    fun damage(amount: Int) {
        hp -= amount
    }
    
    fun heal(amount: Int) {
        hp = min(hp + amount, maxHp)
    }
}

class WeaponComponent {
    fun attack(target: Actor) {
        target.takeHit(10)
    }
}
```

**Ventaja**: 
- Actores tienen identidad (`player`, `enemy`)
- Lógica clara y seguible (`player.attack()` → llama `weapon.attack()`)
- Componentes reutilizables (Health, Weapon, AI en múltiples actores)
- Sin boilerplate de ECS
- Código natural y OOP

---

## Arquitectura Typical Hybrid

```
GameScreen
    │
    ├── Stage (LibGDX Scene2d)
    │   │
    │   ├── PlayerActor
    │   │   ├── HealthComponent
    │   │   ├── InventoryComponent
    │   │   ├── WeaponComponent
    │   │   └── AnimationComponent
    │   │
    │   ├── EnemyActor[0]
    │   │   ├── HealthComponent
    │   │   ├── WeaponComponent
    │   │   └── AIComponent
    │   │
    │   ├── EnemyActor[1]
    │   │   ├── HealthComponent
    │   │   ├── WeaponComponent
    │   │   └── AIComponent
    │   │
    │   ├── ItemActor (drop loot)
    │   │   └── PickupComponent
    │   │
    │   └── UI Panel (menus, HUD)
    │
    └── Managers (opcionales, ligeros)
        ├── CombatManager (arbitro de combate)
        ├── LootManager (generación de drops)
        └── QuestManager (progress)
```

**Key**: Cada actor es responsable de sí mismo, PERO usa componentes internos.

---

## Componentes vs Sistemas

### En Hybrid

**Componentes** = datos + lógica específica y reutilizable

```kotlin
class HealthComponent {
    var hp: Int
    var maxHp: Int
    
    fun damage(amount: Int) { ... }
    fun heal(amount: Int) { ... }
    fun isDead(): Boolean = hp <= 0
}

class WeaponComponent {
    var damage: Int
    var cooldown: Float
    
    fun attack(target: Actor) { ... }
    fun canAttack(): Boolean = cooldown <= 0
}

class AIComponent {
    fun update(delta: Float) { ... }
    fun think(): Action { ... }
}
```

**Actores** = identidad, comportamiento "macro", coordinación

```kotlin
class EnemyActor : EntityActor {
    private val health = HealthComponent(100)
    private val weapon = WeaponComponent(15)
    private val ai = AIComponent(this)
    
    override fun act(delta: Float) {
        super.act(delta)
        ai.update(delta)
        
        // Coordina componentes
        if (ai.shouldAttack && weapon.canAttack()) {
            weapon.attack(target)
        }
        
        if (health.isDead()) {
            remove()
        }
    }
}
```

**No hay Systems globales** que controlen todo. Los actores coordinan sus propios componentes.

---

## Diferencia: Hybrid vs ECS

| Aspecto | Hybrid | ECS Puro |
|--------|--------|----------|
| **Identidad de entidad** | ✅ Sí, objetos con identidad | ❌ IDs vacíos |
| **Lógica dónde** | Actor + Componentes | Systems globales |
| **Flujo de control** | Claro (`player.attack()`) | Distribuido (systems) |
| **Boilerplate** | Bajo | Alto (systems, mappers, aspects) |
| **Reutilización** | Componentes compartidos | Lógica en systems |
| **Rendimiento** | Excelente | Mejor en extremos (1000+) |
| **Debugging** | Fácil (flujo lineal) | Difícil (lógica distribuida) |
| **Escalabilidad** | Buena hasta ~500 entidades | Excelente en miles |
| **Acoplamiento** | Bajo (componentes independientes) | Cero (ortogonal) |
| **Aprendizaje** | Fácil (OOP conocido) | Moderado (mental shift) |

---

## Integración con Scene2d

**Scene2d es perfecto para Hybrid**:

```kotlin
// Scene2d proporciona Stage + Actors
// Hybrid añade componentes internos

abstract class EntityActor : Actor() {
    // Comportamiento base de entidad
    override fun act(delta: Float) {
        // Los actores hijos implementan
    }
    
    override fun draw(batch: Batch, parentAlpha: Float) {
        // Renderizar
    }
}

class PlayerActor : EntityActor {
    private val health = HealthComponent()
    private val inventory = InventoryComponent()
    
    override fun act(delta: Float) {
        // Lógica del jugador
        health.update(delta)
        inventory.update(delta)
    }
}

// En Screen:
val stage = Stage()
stage.addActor(PlayerActor())
stage.addActor(EnemyActor())
```

**Ventaja de Scene2d + Hybrid**:
- Stage gestiona jerarquía visual
- Actores tienen update (`act()`) y render (`draw()`)
- Componentes manejan lógica específica
- Input automático vía Stage
- Actions para animaciones

---

## Patrones de Comunicación

### 1. Actor → Componente (directo)

```kotlin
class PlayerActor : EntityActor {
    private val health = HealthComponent()
    
    fun takeDamage(amount: Int) {
        health.damage(amount)
    }
}

// Desde afuera:
player.takeDamage(10)
```

### 2. Componente → Actor (callback)

```kotlin
class HealthComponent(val actor: EntityActor) {
    fun damage(amount: Int) {
        hp -= amount
        if (isDead()) {
            actor.onDeath()  // Callback
        }
    }
}

class EnemyActor : EntityActor {
    override fun onDeath() {
        // Reaccionar a muerte
        this.remove()
    }
}
```

### 3. Actor ↔ Actor (directo o via Manager)

```kotlin
// Directo (si referencias existen)
player.attack(enemy)

// Via Manager (si no hay referencia directa)
CombatManager.attack(player, enemy)
```

### 4. Componente ↔ Componente (dentro del actor)

```kotlin
class PlayerActor : EntityActor {
    private val health = HealthComponent()
    private val armor = ArmorComponent()
    
    fun takeDamage(amount: Int) {
        val reducedDamage = armor.reduce(amount)
        health.damage(reducedDamage)
    }
}
```

---

## Ventajas de Hybrid

| Ventaja | Descripción |
|---------|------------|
| **Claridad mental** | Flujo obvio: `player.attack()` → `weapon.attack()` |
| **Familiaridad OOP** | Devs de Unity/Godot se sienten cómodos |
| **Bajo boilerplate** | Sin systems, mappers, aspects, families |
| **Reutilización** | Componentes compartidos sin duplicar |
| **Rendimiento** | Suficiente para juegos medianos (10-500 entidades) |
| **Debugging** | Fácil de seguir y debuggear |
| **Escalabilidad** | Suena rápido hasta ~500 entidades |
| **Flexibilidad** | Fácil agregar managers/systems ligeros después |

---

## Desventajas de Hybrid

| Desventaja | Descripción |
|------------|------------|
| **Sin aislamiento** | Actores entienden componentes (acoplamiento) |
| **Menos escalable** | Difícil con 1000+ entidades dinámicas |
| **Sin profiler** | No hay tooling integrada (vs Artemis) |
| **Menos separación** | Datos + lógica viven juntos (vs ECS puro) |
| **Comunicación** | Callbacks/referencias pueden volverse complejos |
| **No serializable** | Sin soporte automático (vs ECS/Artemis) |

---

## Cuándo Usar Hybrid

✅ **Hybrid es perfecto para:**
- Juegos 2D medianos (10-500 entidades activas)
- Roguelikes, Tactics, Metroidvania
- Action RPG, Tower Defense, Deckbuilders
- Equipos con experiencia OOP (no ECS)
- Prototipado rápido con claridad mental
- Proyectos con Scene2d como base
- Cuando rendimiento "suficiente" es aceptable

❌ **NO usar Hybrid si:**
- Necesitas 1000+ entidades simultáneas
- Simulación RTS/estrategia masiva
- Optimización hardcore
- Requiere serialización automática
- Bullet hell con parículas extremas

---

## Ejemplo Completo: MVP Wargame

```kotlin
// Componentes reutilizables
class PositionComponent(var x: Float = 0f, var y: Float = 0f)

class SelectableComponent {
    var isSelected = false
}

class MovementComponent {
    fun move(x: Float, y: Float, targetX: Float, targetY: Float) {
        x = targetX
        y = targetY
    }
}

// Actors con identidad
abstract class UnitActor : EntityActor {
    protected val position = PositionComponent()
    protected val selectable = SelectableComponent()
    protected val movement = MovementComponent()
    
    override fun act(delta: Float) {
        // Update lógica
    }
    
    fun select() {
        selectable.isSelected = true
    }
    
    fun moveTo(x: Float, y: Float) {
        movement.move(position.x, position.y, x, y)
    }
}

class WargameUnit : UnitActor {
    var facingAngle = 0f
    
    override fun draw(batch: Batch, parentAlpha: Float) {
        // Dibujar rectángulo + chevron
        val rect = Rectangle(position.x, position.y, 2f, 1f)
        if (selectable.isSelected) {
            // Highlight
        }
        // Dibujar chevron según facingAngle
    }
    
    fun rotate(degrees: Float) {
        facingAngle += degrees
    }
    
    override fun act(delta: Float) {
        // Actualizar estado
    }
}

// En Screen:
class WargameScreen : KtxScreen {
    val stage = Stage()
    val units = mutableListOf<WargameUnit>()
    
    init {
        // Crear 4 unidades (MVP)
        for (i in 0..3) {
            val unit = WargameUnit()
            stage.addActor(unit)
            units.add(unit)
        }
    }
    
    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }
}
```

---

## Comparación: Todos los paradigmas

| Paradigma | OOP Puro | Hybrid | ECS Puro | Artemis |
|-----------|----------|--------|----------|---------|
| **Claridad** | ✅ Muy claro | ✅ Claro | ❌ Distribuido | ❌ Distribuido |
| **Reutilización** | ❌ Pobre | ✅ Buena | ✅ Excelente | ✅ Excelente |
| **Boilerplate** | ✅ Bajo | ✅ Bajo | ❌ Alto | ⚠️ Medio |
| **Escalabilidad** | ❌ Pobre | ⚠️ Hasta 500 | ✅ Buena | ✅ Excelente |
| **Learning curve** | ✅ Bajo | ✅ Bajo | ⚠️ Moderado | ❌ Alto |
| **Rendimiento extremo** | ❌ No | ⚠️ No | ✅ Sí | ✅ Sí |
| **Debugging** | ✅ Fácil | ✅ Fácil | ❌ Difícil | ⚠️ Complejo |
| **Recomendación** | Juegos simples | Juegos medianos | Juegos complejos | Proyectos grandes |

---

## Conclusión

**Hybrid es el "punto dulce"** para muchos juegos 2D:
- Familiaridad OOP
- Claridad mental
- Componentes reutilizables
- Sin overhead ECS
- Suficiente rendimiento

Es lo que usan internamente muchos frameworks (Unity, Godot) y lo que muchos devs de LibGDX terminan adoptando después de probar ECS puro.

Para proyectos grandes o necesidades extremas, ECS puro (Ashley/Artemis) es mejor. Pero para juegos medianos, **Hybrid es probablemente la mejor opción**.
