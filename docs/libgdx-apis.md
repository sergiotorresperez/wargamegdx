# libGDX API Reference for This Project

Version: **libGDX 1.12.1**, desktop backend LWJGL3, JVM target Java 11+.

This document covers only the APIs actually used or likely to be used in this project. It is not exhaustive — consult the [libGDX javadoc](https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest/) for the full reference.

---

## Index

1. [Math — Vector2](#1-math--vector2)
2. [Math — MathUtils](#2-math--mathutils)
3. [Math — Polygon and Intersector](#3-math--polygon-and-intersector)
4. [Math — Rectangle and Circle](#4-math--rectangle-and-circle)
5. [Math — Interpolation](#5-math--interpolation)
6. [Graphics — Color](#6-graphics--color)
7. [Graphics — OrthographicCamera](#7-graphics--orthographiccamera)
8. [Graphics — ShapeRenderer](#8-graphics--shaperenderer)
9. [Graphics — SpriteBatch and BitmapFont](#9-graphics--spritebatch-and-bitmapfont)
10. [Scene2D — Stage, Actor, Table](#10-scene2d--stage-actor-table)
11. [Viewport](#11-viewport)
12. [Input](#12-input)
13. [Application lifecycle](#13-application-lifecycle)
14. [Critical patterns and gotchas](#14-critical-patterns-and-gotchas)

---

## 1. Math — Vector2

`com.badlogic.gdx.math.Vector2`

**⚠ Vector2 is mutable.** Most methods modify the instance in place and return `this` for chaining. Use `.cpy()` when you need a separate copy.

```kotlin
val v = Vector2(x, y)

// Basic arithmetic (all modify in place, return this)
v.add(other)          // v += other
v.add(dx, dy)
v.sub(other)          // v -= other
v.scl(scalar)         // v *= scalar
v.scl(sx, sy)

// Geometry
v.len()               // magnitude
v.len2()              // magnitude squared (cheaper, avoids sqrt)
v.nor()               // normalize in place; returns this
v.dst(other)          // distance to other (sqrt)
v.dst2(other)         // distance squared (cheaper)
v.dot(other)          // dot product
v.crs(other)          // 2D cross product (returns Float, = v.x*other.y - v.y*other.x)

// Rotation
v.rotate(degrees)     // rotate counter-clockwise in place
v.rotateDeg(degrees)  // same (alias in newer versions)
v.rotateRad(radians)
v.rotateAround(reference: Vector2, degrees: Float)  // rotate around a point

// Angle
v.angleDeg()          // angle of this vector in degrees (0=east, 90=north)
v.angleRad()

// Utilities
v.set(x, y)           // set components
v.set(other)
v.cpy()               // returns a NEW Vector2 (copy)
v.isZero()
v.isZero(margin)
v.lerp(target, alpha) // linear interpolation toward target, modifies in place

// Static helpers
Vector2.Zero          // (0,0) — READ ONLY, do not modify
Vector2.X             // (1,0)
Vector2.Y             // (0,1)
```

---

## 2. Math — MathUtils

`com.badlogic.gdx.math.MathUtils` — all static / companion object style in Kotlin.

```kotlin
// Constants
MathUtils.PI            // 3.14159...
MathUtils.PI2           // 2 * PI
MathUtils.degreesToRadians  // PI / 180
MathUtils.radiansToDegrees  // 180 / PI

// Trigonometry (table-based — FAST, use these instead of Math.cos/sin)
MathUtils.cosDeg(degrees: Float): Float
MathUtils.sinDeg(degrees: Float): Float
MathUtils.cos(radians: Float): Float
MathUtils.sin(radians: Float): Float
MathUtils.atan2(y: Float, x: Float): Float          // returns radians
MathUtils.atan2Deg(y: Float, x: Float): Float       // returns degrees

// Clamping and interpolation
MathUtils.clamp(value: Float, min: Float, max: Float): Float
MathUtils.clamp(value: Int, min: Int, max: Int): Int
MathUtils.lerp(fromValue: Float, target: Float, alpha: Float): Float
MathUtils.lerpAngle(fromDeg: Float, toDeg: Float, alpha: Float): Float  // shortest path

// Rounding
MathUtils.round(value: Float): Int
MathUtils.floor(value: Float): Int
MathUtils.ceil(value: Float): Int

// Random (uses RandomXS128 internally)
MathUtils.random()                  // [0, 1)
MathUtils.random(range: Float)      // [0, range]
MathUtils.random(start: Float, end: Float)
MathUtils.randomBoolean()
MathUtils.randomSign()              // -1 or 1
```

---

## 3. Math — Polygon and Intersector

### Polygon
`com.badlogic.gdx.math.Polygon`

Used to represent element bases as rotated rectangles.

```kotlin
// Construct from flat float array: [x0,y0, x1,y1, x2,y2, ...]
// Vertices must be in consistent winding order (CCW or CW).
// For element bases: FL, FR, RR, RL (counter-clockwise)
val polygon = Polygon(floatArrayOf(
    fl.x, fl.y,
    fr.x, fr.y,
    rr.x, rr.y,
    rl.x, rl.y
))

// If you prefer to build from a local-space polygon and apply transform:
val local = Polygon(localVertices)
local.setPosition(centerX, centerY)
local.setRotation(angleDeg)         // rotates around the origin before translation
local.setScale(sx, sy)

// Read vertices
polygon.vertices           // local (untransformed) float array
polygon.transformedVertices  // world-space float array (applies position/rotation/scale)

// Point-in-polygon
polygon.contains(x: Float, y: Float): Boolean

// Bounding box
polygon.boundingRectangle  // returns Rectangle (world-space)
```

**Tip for this project**: compute element corners directly in world space (using our own math) and pass them to `Polygon()`. Do **not** use `setPosition`/`setRotation` — it adds an extra transform on top that is easy to get wrong.

### Intersector
`com.badlogic.gdx.math.Intersector` — all static.

```kotlin
// Convex polygon overlap (returns true if they intersect or touch)
Intersector.overlapConvexPolygons(p1: Polygon, p2: Polygon): Boolean

// With minimum translation vector (how far to push apart)
val mtv = Intersector.MinimumTranslationVector()
Intersector.overlapConvexPolygons(p1, p2, mtv): Boolean
// mtv.normal — push direction; mtv.depth — push distance

// Segment–segment intersection
val intersection = Vector2()
Intersector.intersectSegments(
    p1: Vector2, p2: Vector2,   // segment 1
    p3: Vector2, p4: Vector2,   // segment 2
    intersection: Vector2?      // output point (nullable)
): Boolean

// Point in triangle
Intersector.isPointInTriangle(point: Vector2, a: Vector2, b: Vector2, c: Vector2): Boolean

// Segment crosses a polygon (any edge of the polygon)
Intersector.intersectSegmentPolygon(p1: Vector2, p2: Vector2, polygon: Polygon): Boolean

// Distance from a point to a line segment
Intersector.distanceSegmentPoint(
    startX: Float, startY: Float,
    endX: Float, endY: Float,
    pointX: Float, pointY: Float
): Float

// Circle–rectangle overlap
Intersector.overlaps(circle: Circle, rect: Rectangle): Boolean

// Rectangle–rectangle overlap
Intersector.overlaps(r1: Rectangle, r2: Rectangle): Boolean
```

---

## 4. Math — Rectangle and Circle

### Rectangle
`com.badlogic.gdx.math.Rectangle`

```kotlin
val r = Rectangle(x, y, width, height)   // x,y = bottom-left corner

r.x; r.y; r.width; r.height
r.contains(x: Float, y: Float): Boolean
r.contains(other: Rectangle): Boolean
r.overlaps(other: Rectangle): Boolean
r.merge(other: Rectangle)               // expands this to include other, in place
r.getCenter(out: Vector2): Vector2      // writes center to out, returns out
r.set(x, y, w, h)
r.setPosition(x, y)
r.setSize(w, h)
```

### Circle
`com.badlogic.gdx.math.Circle`

```kotlin
val c = Circle(x, y, radius)
c.contains(x: Float, y: Float): Boolean
c.overlaps(other: Circle): Boolean
c.overlaps(rect: Rectangle): Boolean  // via Intersector
```

---

## 5. Math — Interpolation

`com.badlogic.gdx.math.Interpolation`

Used for smooth animations (move preview easing, selection highlight, etc.).

```kotlin
// Apply an interpolation: returns a value in [0,1] for input alpha in [0,1]
Interpolation.linear.apply(alpha: Float): Float
Interpolation.smooth.apply(alpha: Float): Float     // smoothstep
Interpolation.smooth2.apply(alpha: Float)           // smoother
Interpolation.fade.apply(alpha: Float)              // Ken Perlin's fade
Interpolation.pow2.apply(alpha: Float)              // ease in
Interpolation.pow2Out.apply(alpha: Float)           // ease out
Interpolation.pow2InInverse.apply(alpha)
Interpolation.elastic.apply(alpha: Float)
Interpolation.bounce.apply(alpha: Float)
Interpolation.swing.apply(alpha: Float)

// Interpolate between two values
Interpolation.smooth.apply(start: Float, end: Float, alpha: Float): Float
```

---

## 6. Graphics — Color

`com.badlogic.gdx.graphics.Color`

```kotlin
val c = Color(r, g, b, a)   // floats 0–1

// Predefined constants (do not modify these)
Color.WHITE; Color.BLACK; Color.CLEAR   // a=0
Color.RED; Color.GREEN; Color.BLUE
Color.CYAN; Color.MAGENTA; Color.YELLOW
Color.ORANGE; Color.PINK; Color.GRAY; Color.DARK_GRAY; Color.LIGHT_GRAY

// Operations
c.set(r, g, b, a)
c.set(other: Color)
c.cpy(): Color
c.lerp(target: Color, t: Float)   // in place
c.mul(other: Color)               // component multiply

// Convert to/from RGBA8888 int
Color.rgba8888(color): Int
Color.toFloatBits(r, g, b, a): Float  // packed float for SpriteBatch

// Tip: to create a semi-transparent version without modifying the original:
val semiTransparent = Color(base).apply { a = 0.3f }
```

---

## 7. Graphics — OrthographicCamera

`com.badlogic.gdx.graphics.OrthographicCamera`

```kotlin
val camera = OrthographicCamera()

// Setup: world-space dimensions visible in the viewport
// yDown=false → Y increases upward (standard math convention)
camera.setToOrtho(yDown = false, viewportWidth = 24f, viewportHeight = 24f)

// Must call every frame before rendering
camera.update()

// The combined projection-view matrix — pass to ShapeRenderer and SpriteBatch
camera.combined: Matrix4

// Camera position (center of view)
camera.position.set(x, y, 0f)

// Zoom (1 = normal, 2 = zoomed out 2×)
camera.zoom = 1f

// Convert screen coordinates to world coordinates
// screenX, screenY are in pixels (origin top-left, y increases downward)
val worldCoords = Vector3(screenX.toFloat(), screenY.toFloat(), 0f)
camera.unproject(worldCoords)  // modifies in place
// worldCoords.x, worldCoords.y now contain world-space position

// Convert world → screen
val screenCoords = Vector3(worldX, worldY, 0f)
camera.project(screenCoords)
```

---

## 8. Graphics — ShapeRenderer

`com.badlogic.gdx.graphics.glutils.ShapeRenderer`

Draws primitive shapes in world space. **Must not be active at the same time as SpriteBatch.**

```kotlin
val sr = ShapeRenderer()

// Always set projection matrix before rendering
sr.setProjectionMatrix(camera.combined)

// --- Filled shapes ---
sr.begin(ShapeRenderer.ShapeType.Filled)
sr.setColor(r, g, b, a)           // or sr.setColor(Color.RED)
sr.rect(x, y, width, height)      // x,y = bottom-left
sr.rect(x, y, originX, originY, width, height, scaleX, scaleY, degrees)  // with rotation
sr.circle(x, y, radius)           // default 12 segments
sr.circle(x, y, radius, segments) // more segments = smoother
sr.ellipse(x, y, width, height)
sr.triangle(x1, y1, x2, y2, x3, y3)
sr.arc(x, y, radius, start, degrees)
sr.polygon(vertices: FloatArray)   // flat [x0,y0,x1,y1,...], auto-closed
sr.end()

// --- Outlines (lines) ---
sr.begin(ShapeRenderer.ShapeType.Line)
sr.setColor(...)
sr.rect(x, y, width, height)      // outline only
sr.circle(x, y, radius, segments)
sr.line(x1, y1, x2, y2)
sr.line(p1: Vector2, p2: Vector2)
sr.rectLine(x1, y1, x2, y2, width)  // thick line as filled rectangle
sr.polygon(vertices: FloatArray)
sr.end()

// --- Points ---
sr.begin(ShapeRenderer.ShapeType.Point)
sr.point(x, y, z)
sr.end()

// Alpha blending (required for any a < 1.0)
Gdx.gl.glEnable(GL20.GL_BLEND)
Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
// ... render semi-transparent shapes ...
Gdx.gl.glDisable(GL20.GL_BLEND)

// Dispose when done (e.g., in Screen.dispose())
sr.dispose()
```

**Important**: you can change `ShapeType` mid-render without calling `end()` by using `sr.set(ShapeType.Line)`, but only when transitioning between compatible types. Safest: always call `end()` before `begin()` with a different type.

---

## 9. Graphics — SpriteBatch and BitmapFont

Used for rendering text and textures (UI overlay, debug labels).

```kotlin
// SpriteBatch
val batch = SpriteBatch()
batch.setProjectionMatrix(camera.combined)  // or stage camera for UI
batch.begin()
batch.draw(texture, x, y)
batch.draw(texture, x, y, width, height)
batch.end()
batch.dispose()

// BitmapFont — libGDX's built-in 15pt Arial font (no file needed)
val font = BitmapFont()
font.getData().setScale(2f)        // scale up
font.setColor(Color.WHITE)

// Draw text — must be inside batch.begin()/end()
font.draw(batch, "Hello", x, y)   // y = baseline

// Measure text before drawing
val layout = GlyphLayout(font, "Hello")
layout.width   // text width in world units
layout.height  // text height

// Reuse layout:
layout.setText(font, "New text")

font.dispose()
```

---

## 10. Scene2D — Stage, Actor, Table

Used for the UI layer (End Turn button, turn label, etc.).

```kotlin
// Stage manages a tree of Actors; update + render each frame
val viewport = StretchViewport(1280f, 720f)  // or FitViewport, etc.
val stage = Stage(viewport)

// Add actors
stage.addActor(myActor)

// Each frame
stage.act(delta)  // update all actors (Actions, listeners)
stage.draw()      // render all actors

// Route input to stage (before game input)
val mux = InputMultiplexer(stage, gameInputProcessor)
Gdx.input.setInputProcessor(mux)

stage.dispose()
```

### Table (layout)
```kotlin
val table = Table()
table.setFillParent(true)   // fills the stage
table.top().left()          // align contents

// Add widgets
table.add(label).expandX().left().pad(8f)
table.add(button).right().padRight(8f)
table.row()                 // next row
table.add(anotherWidget).colspan(2).center()

stage.addActor(table)
```

### Label and TextButton (minimal skin-free setup)
```kotlin
val font = BitmapFont()
val labelStyle = Label.LabelStyle(font, Color.WHITE)
val label = Label("Blue Player's Turn", labelStyle)

val buttonStyle = TextButton.TextButtonStyle()
buttonStyle.font = font
buttonStyle.fontColor = Color.WHITE
buttonStyle.overFontColor = Color.YELLOW
val button = TextButton("End Turn", buttonStyle)

button.addListener(object : ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) {
        // handle click
    }
})
```

### Custom WorldActor base class
For game-world actors (not Scene2D):
```kotlin
abstract class WorldActor {
    var visible: Boolean = true
    var zIndex: Int = 0
    open fun update(delta: Float) {}
    abstract fun render(sr: ShapeRenderer)
    open fun dispose() {}
}
```

---

## 11. Viewport

`com.badlogic.gdx.utils.viewport`

```kotlin
// FitViewport: maintains aspect ratio, adds black bars if needed
val viewport = FitViewport(worldWidth, worldHeight)

// StretchViewport: stretches to fill screen (may distort)
val viewport = StretchViewport(worldWidth, worldHeight)

// ExtendViewport: shows more of the world rather than stretching
val viewport = ExtendViewport(minWorldWidth, minWorldHeight)

// Must call in Screen.resize()
viewport.update(screenWidth, screenHeight, true)  // true = center camera

// Access the viewport's camera
val cam = viewport.camera as OrthographicCamera

// Apply viewport (sets GL viewport scissor) — usually done by Stage automatically
viewport.apply()
```

---

## 12. Input

```kotlin
// Set processor (call once, typically in show())
Gdx.input.setInputProcessor(processor)

// Multiple processors in priority order — first one to return true wins
val mux = InputMultiplexer(stageFirst, gameInputSecond)
Gdx.input.setInputProcessor(mux)

// InputAdapter — override only what you need
val handler = object : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // button: Input.Buttons.LEFT / RIGHT / MIDDLE
        return true   // consume event; false = pass to next processor
    }
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { ... }
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean { ... }
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean { ... }
    override fun keyDown(keycode: Int): Boolean { ... }  // keycode: Input.Keys.R etc.
    override fun keyUp(keycode: Int): Boolean { ... }
    override fun scrolled(amountX: Float, amountY: Float): Boolean { ... }
}

// Polling (inside render loop)
Gdx.input.isKeyPressed(Input.Keys.SPACE)
Gdx.input.isKeyJustPressed(Input.Keys.R)  // true only the first frame
Gdx.input.isTouched()
Gdx.input.getX(); Gdx.input.getY()        // screen coords, y=0 at top
```

---

## 13. Application lifecycle

### Game and Screen
```kotlin
class MyGame : Game() {
    override fun create() {
        setScreen(MenuScreen(this))
    }
}

class MyScreen(val game: MyGame) : Screen {
    override fun show()   {}                     // called when screen becomes active
    override fun render(delta: Float) {}         // called every frame
    override fun resize(width: Int, height: Int) {}
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}                     // called before switching screens
    override fun dispose() {}                    // called when screen is disposed
}

// Switch screen (disposes nothing automatically — dispose the old screen yourself if needed)
game.setScreen(GameScreen(game))
```

### Globals
```kotlin
Gdx.app          // ApplicationListener access
Gdx.graphics     // rendering info
Gdx.graphics.deltaTime        // seconds since last frame
Gdx.graphics.width            // screen width in pixels
Gdx.graphics.height           // screen height in pixels
Gdx.graphics.framesPerSecond
Gdx.input        // input state
Gdx.files        // file access (internal, external, local, absolute)
Gdx.gl           // raw OpenGL ES 2.0 access (glEnable, glClear, etc.)
```

### Desktop launcher (LWJGL3)
```kotlin
object DesktopLauncher {
    @JvmStatic fun main(args: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration()
        config.setTitle("Wargame")
        config.setWindowedMode(1280, 720)
        config.setForegroundFPS(60)
        config.setResizable(true)
        Lwjgl3Application(MyGame(), config)
    }
}
```

---

## 14. Critical patterns and gotchas

### Begin/end pairing
ShapeRenderer and SpriteBatch use a begin/end pattern. **They cannot be active simultaneously.** Always `end()` one before `begin()`-ing the other. Forgetting this causes an `IllegalStateException` or silent corruption.

```kotlin
// Correct order in a frame:
shapeRenderer.begin(Filled); ...; shapeRenderer.end()
spriteBatch.begin();         ...; spriteBatch.end()
stage.draw()   // stage manages its own batch internally
```

### Projection matrix
Set `setProjectionMatrix(camera.combined)` on ShapeRenderer and SpriteBatch **every frame after** `camera.update()`. The combined matrix changes when the camera moves or zooms.

### Alpha blending
ShapeRenderer does not enable alpha blending by default. For any semi-transparent shape:
```kotlin
Gdx.gl.glEnable(GL20.GL_BLEND)
Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
shapeRenderer.begin(Filled)
shapeRenderer.setColor(r, g, b, 0.3f)   // a < 1
// draw shapes
shapeRenderer.end()
Gdx.gl.glDisable(GL20.GL_BLEND)
```

### Vector2 mutability
```kotlin
val a = Vector2(1f, 0f)
val b = a.add(Vector2(0f, 1f))   // a is now (1,1), b == a (same object!)

// Safe: copy first
val b = a.cpy().add(Vector2(0f, 1f))  // a unchanged, b = (1,1)
```

### Dispose everything
Anything that implements `Disposable` must be disposed when no longer needed. Failure to do so leaks GPU memory. Common disposables: `ShapeRenderer`, `SpriteBatch`, `BitmapFont`, `Texture`, `Pixmap`, `Stage`, `Sound`, `Music`.

### Screen coordinate system
libGDX screen coordinates have **y=0 at the top** (opposite of world space if using `setToOrtho(false)`). Always unproject through the camera before comparing to world positions:
```kotlin
val tmp = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
camera.unproject(tmp)
// tmp.x, tmp.y are now in world space
```

### Polygon vertices must be consistent
`Intersector.overlapConvexPolygons` requires both polygons to have vertices in the **same winding order** (both CW or both CCW). Mixed winding produces incorrect results silently. This project uses **CCW** (counter-clockwise): FL → FR → RR → RL.

### ShapeRenderer line width
`Gdx.gl.glLineWidth(pixels)` sets the line width for `ShapeType.Line`. Default is 1px. Note: this is a hint on some drivers and may be ignored; OpenGL Core Profile clamps to 1px. For thick lines use `sr.rectLine(...)` (filled rectangle) instead.
