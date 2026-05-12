# Scene2d — UI Toolkit y Scene Graph de LibGDX

**Scene2d** es un **scene graph 2D jerárquico** que proporciona arquitectura para construir interfaces visuales y aplicaciones interactivas en LibGDX. Organiza elementos como árbol de nodos (Actors), donde transformaciones (posición, tamaño, rotación, escala) se aplican en cascada desde padres a hijos.

**Nota clave**: Scene2d es **para UI y presentación**, NO para game logic pesado.

---

## ¿Qué es Scene2d?

Una abstracción por encima del rendering directo con SpriteBatch. Proporciona:
- **Jerarquía visual**: Actors pueden contener Actors (Groups)
- **Transformaciones automáticas**: Posición, escala, rotación, color heredan del padre
- **Event system**: Input routing y propagación automática
- **Actions**: Sistema de animación temporal integrado
- **Widgets predefinidos**: Botones, TextFields, ScrollPanes, Dialogs, etc.

Comparable a frameworks UI modernos (Web DOM, UI frameworks) pero optimizado para juegos 2D.

---

## Conceptos Clave

### Actor
Nodo base en Scene2d. Propiedades:
- `position` (x, y) — relativa al padre
- `size` (width, height)
- `rotation`, `scaleX`, `scaleY`
- `color` (RGBA)
- `origin` — punto de rotación/escala

Un Actor es simultáneamente **estructura de datos** y **presentación visual** (nota: esto es ventaja y desventaja).

### Stage
Contenedor raíz que:
- Gestiona una cámara y viewport
- Mantiene batch de renderizado
- Distribuye eventos de entrada (input processor)
- Ejecuta acciones (actions) de todos los actors

Típicamente hay 1 Stage por pantalla.

### Group
Actor que puede contener hijos. Permite jerarquía:
- Transformaciones de padre se aplican a hijos
- Eventos se propagan hacia abajo
- Útil para UI layouts (panels, windows)

### Action
Sistema de **animación temporal**. Permite:
- Movimiento: `MoveTo`, `MoveBy`
- Escala: `ScaleTo`, `ScaleBy`
- Rotación: `RotateBy`, `RotateTo`
- Fade: `FadeIn`, `FadeOut`
- Custom actions con lógica propia

**Composición de actions**:
- `sequence()` — ejecuta secuencialmente
- `parallel()` — ejecuta en paralelo
- `repeat()`, `forever()` — bucles

**Pooleable**: Actions se reutilizan (no garbage).

```java
// Ejemplo: fade out + move simultáneamente, luego fade in
actor.addAction(
  sequence(
    parallel(
      fadeOut(0.5f),
      moveTo(100, 100, 0.5f)
    ),
    fadeIn(0.5f)
  )
);
```

### Event System
Propagación **bifásica**:
1. **Capture phase**: eventos descienden del root hacia target
2. **Bubbling phase**: eventos suben del target hacia root

Actores pueden:
- Escuchar eventos (listeners)
- Consumir eventos (detener propagación)
- Padres pueden interceptar eventos antes de target

Soporta: touch, mouse, keyboard, custom events.

```java
actor.addListener((event: InputEvent, actor: Actor) -> {
    if (event.getType() == InputEvent.Type.touchDown) {
        actor.addAction(scaleTo(1.2f, 1.2f, 0.2f));
        return true; // Consumir evento
    }
    return false;
});
```

### Skin
Conjunto de recursos para UI widgets:
- Texturas
- Fonts
- Colores
- Estilos predefinidos (Button style, TextField style, etc.)

Permite cambiar look & feel sin código.

### Widgets Predefinidos
Scene2d incluye:
- `TextButton`, `ImageButton` — botones
- `TextField`, `TextArea` — entrada de texto
- `SelectBox`, `CheckBox` — selección
- `Slider`, `ProgressBar` — sliders
- `ScrollPane` — scroll
- `Dialog`, `Window` — ventanas modales
- `Table` — layout grid

---

## Patrones de Uso

### 1. Arquitectura típica

```
Screen.render(deltaTime)
  ↓
stage.act(deltaTime)  // Actualiza posiciones, acciones
  ↓
stage.draw()  // Renderiza todos los actors
  ↓
Pantalla actualizada
```

### 2. Construcción de UI

```java
Stage stage = new Stage(viewport);
Gdx.input.setInputProcessor(stage);

// Crear widgets
TextButton button = new TextButton("Play", skin);
button.addListener((event, actor) -> {
    game.startGame();
    return false;
});

// Agregar a stage (o a un Group/Table para layout)
stage.addActor(button);

// Renderizar
stage.draw();
```

### 3. Animaciones con Actions

```java
actor.addAction(
    sequence(
        moveTo(100, 200, 1f, Interpolation.smooth),
        rotateTo(360f, 0.5f),
        fadeOut(0.5f),
        removeActor()  // Elimina del stage
    )
);
```

### 4. Layouts con Table

```java
Table mainLayout = new Table();
mainLayout.setFillParent(true);  // Llena el stage

mainLayout.add(title).expand().top();
mainLayout.row();
mainLayout.add(playButton).pad(10);
mainLayout.row();
mainLayout.add(settingsButton).pad(10);

stage.addActor(mainLayout);
```

---

## Ventajas

| Ventaja | Descripción |
|---------|-------------|
| **UI Development** | Excelente para menus, HUDs, diálogos |
| **Jerárquico y composable** | Código legible, fácil extensión |
| **Hit detection automática** | Colisiones respetan transformaciones (rotación, escala) |
| **Event routing flexible** | Capture/bubbling, fácil manejar input |
| **Actions integradas** | Animaciones sin código frame-by-frame manual |
| **Widgets predefinidos** | Botones, inputs, scrolls listos para usar |
| **Skins y estilos** | Cambiar look & feel sin código |

---

## Desventajas

| Desventaja | Descripción |
|------------|-------------|
| **Actor acopla datos + presentación** | No hay separación clara MVC |
| **NO para game logic pesado** | Overhead innecesario para lógica de juego compleja |
| **Serialización difícil** | Estado de UI acoplado a estructura visual |
| **Transformaciones en cascada** | Costo de recalcular transformaciones anidadas profundas |
| **Performance en muchos actors** | Si tienes 1000+ actors dinámicos, mejor ECS |

### Crítica arquitectónica: "Cuando NO usar Scene2d"

**Nate (autor) documentó**: Al reconstruir un prototype de **juego de acción** sin Scene2d (usando POJOs puros + rendering manual), el código fue más manejable.

**Razón**: Actor acopla modelo (datos) con vista (rendering). En juegos de acción con lógica compleja, esto causa:
- Dificultad para probar lógica (ligada a Stage)
- Overhead de transformaciones innecesarias
- Jerarquía visual no mapea a lógica de juego

---

## Casos de Uso Ideales

✅ **Scene2d brilla en:**
- Menus, HUDs, interfaces de usuario
- Juegos basados en tablero (tactical RPGs, strategy)
- Apps interactivas (editor, simulador, herramientas)
- Cualquier cosa UI-heavy con lógica simple

❌ **Scene2d NO es buena para:**
- Juegos de acción densos (flying enemies, combat, physics)
- Lógica de juego separada de presentación
- Aplicaciones que requieren strict MVC
- Sistemas con muchas entidades dinámicas

---

## Integración con Lógica de Juego

**Recomendación arquitectónica**: Scene2d como **capa de presentación**, NO como motor de game logic.

**Patrón correcto**:
```
Game Logic (POJOs puros o ECS)
        ↓
    Listeners/Callbacks
        ↓
Scene2d (presentación)
        ↓
Renderizado + Input
```

**Ejemplo**: Tactical RPG
- **Game logic**: Turnos, AI, pathfinding (código puro, testeable)
- **Presentación**: Scene2d menús, HUD, selección de unidades
- **Comunicación**: Lógica dispara eventos → UI escucha y anima

**Antipatrón**: Meter toda la lógica dentro de Actors.

---

## Comparación: Scene2d vs Alternativas

| Aspecto | Scene2d | Canvas Manual | ECS |
|---------|---------|---------------|----|
| **UI Development** | Excelente | Tedioso | N/A |
| **Jerarquía visual** | ✅ Nativa | Manual | N/A |
| **Model-View Separation** | Acoplada | Control total | Limpia |
| **Actions/Animaciones** | Built-in | Manual | Manual |
| **Learning Curve** | Baja | Alta | Moderada |
| **Game Logic Pesado** | ❌ Malo | ✅ Mejor | ✅ Mejor |
| **Performance many actors** | ⚠️ Overhead | ✅ Eficiente | ✅ Eficiente |

---

## Ejemplo Completo: Botón Interactivo

```java
// Crear Stage
Stage stage = new Stage(viewport);

// Crear botón
TextButton button = new TextButton("Click Me", skin);
button.setPosition(100, 100);

// Listener
button.addListener(new ClickListener() {
    @Override
    public void clicked(InputEvent event, float x, float y) {
        // Animación al hacer click
        button.addAction(
            sequence(
                scaleTo(1.1f, 1.1f, 0.1f),
                scaleTo(1f, 1f, 0.1f)
            )
        );
        
        // Callback a lógica del juego
        gameLogic.onButtonClicked();
    }
});

stage.addActor(button);

// En render()
stage.act(delta);
stage.draw();
```

---

## Curva de Aprendizaje

**Baja a moderada**. Conceptos fundamentales (Stage, Actor, Action, Event) son intuitivos para devs con experiencia en UI frameworks. La curva se incrementa al entender event propagation avanzada y Skins, pero documentación es accesible.

---

## Referencias

- [LibGDX Scene2d Wiki](https://libgdx.com/wiki/graphics/2d/scene2d/scene2d)
- [Scene2d Actions and Event Handling](https://web.archive.org/web/20200805185955/http://www.netthreads.co.uk/2012/01/31/libgdx-example-of-using-scene2d-actions-and-event-handling/)
- [When to Use Actors (JVM Gaming Discussion)](https://jvm-gaming.org/t/libgdx-actor-to-use-or-not-to-use-or-when-to-use/41938/7)
- [Street Race Swipe — Scene2d Example](https://theinvader360.blogspot.com/2013/05/street-race-swipe-libgdx-scene2d.html)
