# Ashley — ECS Framework para LibGDX

**Ashley** es un framework **Entity-Component-System (ECS) minimalista** escrito en Java, diseñado específicamente para juegos LibGDX. Enfoque en alto rendimiento, API transparente y bajo overhead.

---

## ¿Qué es ECS?

ECS es un patrón arquitectónico que separa **datos** de **lógica**:

- **Entities** (Entidades): contenedores sin lógica (un jugador, un enemigo, un proyectil)
- **Components** (Componentes): datos puros que describen propiedades (posición, velocidad, salud, sprite)
- **Systems** (Sistemas): lógica que procesa entidades basada en sus componentes

**Ventaja clave**: composición sobre herencia — comportamiento emerge de combinar componentes.

---

## Conceptos Clave de Ashley

### Entity
Contenedor ligero que:
- Tiene un ID único
- Almacena referencias a componentes
- No contiene lógica
- Representa cualquier objeto del juego

### Component
Estructura de datos pura que:
- Define un atributo de la entidad (posición, velocidad, sprite, salud)
- No contiene lógica
- Extrae de `Component` (interfaz marcadora de Ashley)

```java
// Ejemplo (pseudocódigo):
class PositionComponent extends Component {
  public float x, y;
}

class VelocityComponent extends Component {
  public float vx, vy;
}

class SpriteComponent extends Component {
  public TextureRegion texture;
}
```

### System
Contiene la **lógica del juego** y procesa entidades:
- Itera sobre entidades que cumplen criterios específicos
- Modifica componentes basándose en reglas
- Se ejecuta cada frame en orden definido

```java
// Ejemplo (pseudocódigo):
class MovementSystem extends IteratingSystem {
  // Procesa solo entidades que tengan Position Y Velocity
  
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    PositionComponent pos = mapper.get(entity);
    VelocityComponent vel = mapper.get(entity);
    pos.x += vel.vx * deltaTime;
    pos.y += vel.vy * deltaTime;
  }
}
```

### Family (Familia)
Define qué combinación de componentes debe tener una entidad para que un system la procese:
- `Family.all(PositionComponent.class, VelocityComponent.class)` — procesa entidades que tengan AMBOS
- `Family.all(...).exclude(DeadComponent.class)` — pero excluye muertas
- Optimización interna: Ashley cachea familias

### ComponentMapper
Herramienta para acceso rápido a componentes (O(1)):
```java
ComponentMapper<PositionComponent> posMapper = 
  ComponentMapper.getFor(PositionComponent.class);
PositionComponent pos = posMapper.get(entity); // Rápido
```

Mejor que `entity.getComponent(PositionComponent.class)` (más lento).

### Engine
Orquestador central que:
- Crea/destruye entidades
- Registra sistemas
- Ejecuta sistemas en orden cada frame
- Gestiona familias

```java
Engine engine = new Engine();
engine.addSystem(new MovementSystem());
engine.addSystem(new RenderSystem());

// En render() cada frame:
engine.update(deltaTime);
```

---

## Patrones de Uso

### 1. Arquitectura típica

```
GameScreen.render(deltaTime)
  ↓
engine.update(deltaTime)
  ↓
[MovementSystem] procesa entidades con Posición + Velocidad
  ↓
[RenderSystem] procesa entidades con Posición + Sprite
  ↓
Renderizado en pantalla
```

### 2. Creación de entidades

```java
Entity player = new Entity();
player.add(new PositionComponent(100, 100));
player.add(new VelocityComponent(0, 0));
player.add(new SpriteComponent(playerTexture));
engine.addEntity(player);
```

### 3. Sistemas especializados

- **IteratingSystem**: procesa cada entidad iterativamente (más común)
- **SortedIteratingSystem**: procesa en orden específico (ej: renderizar por profundidad Z)
- **IntervalSystem**: ejecuta cada N segundos, no cada frame

---

## Ventajas de Ashley

| Ventaja | Descripción |
|---------|-------------|
| **Alto rendimiento** | Acceso O(1) a componentes, caché de familias, sin garbage en hot paths |
| **Escalabilidad** | Ideal para 100+ entidades sin lag |
| **Flexibilidad** | Composición sin herencia; fácil añadir/remover comportamiento |
| **Transparencia** | API simple, sin "black magic", predecible |
| **Bajo overhead** | ~50KB, no requiere dependencies |
| **Compatible** | Java 6+, GWT, licencia Apache 2.0 |
| **LibGDX-native** | Diseñado para LibGDX, integración directa |

---

## Desventajas de Ashley

| Desventaja | Descripción |
|------------|-------------|
| **Curva de aprendizaje** | Requiere pensar diferente (ECS ≠ OOP) |
| **Documentación limitada** | Menos recursos que Unity/Unreal |
| **Community pequeña** | Menos tutoriales, ejemplos disponibles |
| **Sin serialización** | No hay save/load automático de estado |
| **Debugging** | Más difícil debuggear lógica distribuida en systems |

---

## Curva de Aprendizaje

**Fácil**. Los conceptos clave se entienden en 1-2 horas:
1. Entidades = contenedores
2. Componentes = datos
3. Sistemas = lógica que procesa componentes

Los primeros sistemas funcionales toman ~30 minutos. La dificultad real está en **diseño arquitectónico** (qué es componente vs sistema), no en la API.

---

## Casos de Uso Ideales

✅ **Ashley es buena para:**
- Juegos 2D con muchas entidades (side-scrollers, roguelikes, estrategia)
- Prototipos rápidos (baja fricción para añadir features)
- Juegos con física Box2D compleja
- Proyectos que valoren rendimiento y escalabilidad

❌ **Ashley NO es buena para:**
- Juegos muy simples (overkill arquitectónico)
- Proyectos con poco tiempo de learning curve (ej: game jam de 1 semana)
- Apps UI-heavy sin lógica compleja (usa Scene2d directamente)

---

## Integración con LibGDX

Ashley vive en el ecosistema LibGDX pero **no es dependencia obligatoria**:

```java
// En tu Screen:
@Override
public void render(float deltaTime) {
  Gdx.gl.glClearColor(0, 0, 0, 1);
  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
  
  engine.update(deltaTime);  // Procesa todos los sistemas
  
  // Los sistemas llaman a Gdx.graphics, batch, etc. directamente
}
```

ComponentMapper acelera acceso a componentes de render (ej: TextureRegion, posición).

---

## Ejemplo completo (pseudocódigo)

```
COMPONENTES:
  PositionComponent(x, y)
  VelocityComponent(vx, vy)
  SpriteComponent(texture)

ENTIDAD JUGADOR:
  + PositionComponent(100, 100)
  + VelocityComponent(0, 0)
  + SpriteComponent(playerTexture)

MOVEMENTSYSTEM:
  Procesa Family[Position, Velocity]
  Cada frame: position += velocity * deltaTime

RENDERSYSTEM:
  Procesa Family[Position, Sprite]
  Cada frame: dibuja sprite en posición

RESULTADO:
  Jugador se mueve sin escribir lógica de "movimiento" en la entidad
  Lógica emerge de la composición de componentes + sistemas
```

---

## Comparación: Ashley vs OOP puro

| Aspecto | Ashley (ECS) | OOP puro |
|---------|-------------|----------|
| **Estructura** | Composición (componentes) | Herencia (clases) |
| **Escalabilidad** | ✅ Fácil (añade componentes/systems) | ❌ Difícil (jerarquías profundas) |
| **Performance** | ✅ Predecible (datos contiguos) | ⚠️ Puede fragmentarse |
| **Reusabilidad** | ✅ Componentes compartibles | ⚠️ Acoplamiento fuerte |
| **Curva aprendizaje** | ⚠️ Mental shift requerido | ✅ Familiar para devs Java |
| **Flexibilidad runtime** | ✅ Fácil cambiar comportamiento | ❌ Difícil sin refactor |

---

## Referencias

- [Ashley GitHub Wiki](https://github.com/libgdx/ashley/wiki)
- [Ashley Javadoc](https://javadoc.io/doc/com.badlogicgames.ashley/ashley/latest/index.html)
- [Full LibGDX Game Tutorial – Entities using Ashley](https://www.gamedevelopment.blog/full-libgdx-game-tutorial-entities-ashley/)
- [LibGDX Overlap2D Survival Guide](https://libgdx.com/wiki/misc/overlap2d-survival-guide-for-libgdxjam)