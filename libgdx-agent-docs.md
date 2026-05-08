# libGDX Agent Reference — 2D Game Development
> Javadocs: https://javadoc.io/doc/com.badlogicgames.gdx  
> Wiki: https://libgdx.com/wiki/  
> Version: 1.12.x (LWJGL3 default backend)

---

## INDEX — Jump to section by task

| Task | Section |
|------|---------|
| Bootstrap / launcher | [A. App Framework](#a-application-framework) |
| Game loop / lifecycle | [A2. Lifecycle](#a2-lifecycle) |
| Draw sprites/textures | [B1. SpriteBatch](#b1-spritebatch--texture--sprite) |
| Camera & world units | [B2. OrthographicCamera](#b2-orthographiccamera) |
| Screen scaling strategy | [B3. Viewports](#b3-viewports) |
| Sprite animation | [B4. Animation](#b4-animation) |
| Tile maps (Tiled) | [B5. Tile Maps](#b5-tile-maps) |
| HUD / menus / UI | [B6. Scene2D & UI](#b6-scene2d--ui) |
| Bitmap fonts | [B7. Fonts](#b7-fonts) |
| Particle effects | [B8. Particles](#b8-particles) |
| Keyboard/mouse/touch | [C. Input](#c-input-handling) |
| Sound effects / music | [D. Audio](#d-audio) |
| 2D physics collisions | [E. Box2D Physics](#e-box2d-physics) |
| Load assets async | [F. AssetManager](#f-assetmanager) |
| Read/write files | [G. File Handling](#g-file-handling) |
| Save game / settings | [G2. Preferences](#g2-preferences) |
| JSON serialization | [G3. JSON](#g3-json) |
| Vectors, shapes, math | [H. Math Utilities](#h-math-utilities) |
| Memory / dispose | [I. Memory Management](#i-memory-management) |
| Gradle dependencies | [J. Dependency Setup](#j-dependency-setup) |

---

## A. Application Framework

### A1. Starter Class (Desktop — LWJGL3)

```java
// lwjgl3/src/.../Lwjgl3Launcher.java
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("MyGame");
        config.useVsync(true);
        config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        config.setWindowedMode(1280, 720);
        new Lwjgl3Application(new MyGame(), config);
    }
}
```

Other backends: `AndroidApplication`, `IOSApplication`, `GwtApplication`.  
Android entry point: override `Activity.onCreate()`, call `initialize(new MyGame(), config)`.

### A2. Lifecycle

Implement `ApplicationListener` (or extend `ApplicationAdapter`/`Game`):

| Method | When called |
|--------|------------|
| `create()` | Once at start — init resources |
| `resize(w, h)` | On window resize and once after `create()` |
| `render()` | Every frame — update + draw here |
| `pause()` | App loses focus / minimised |
| `resume()` | App regains focus |
| `dispose()` | On exit — release all Disposables |

**No explicit main loop.** `render()` is the loop body. Use `Gdx.graphics.getDeltaTime()` for frame-independent updates.

### A3. Screen Management (`Game` + `Screen`)

```java
public class MyGame extends Game {
    @Override public void create() { setScreen(new MainMenuScreen(this)); }
}

public class GameScreen implements Screen {
    // show(), hide(), render(delta), resize(), pause(), resume(), dispose()
}
```

Use `game.setScreen(screen)` to transition. Previous screen's `hide()` is called; new screen's `show()` is called.

### A4. Global Access (`Gdx`)

```java
Gdx.app       // Application — log, exit, type
Gdx.graphics  // Graphics — getDeltaTime(), getWidth/Height(), FPS
Gdx.input     // Input — keyboard, mouse, touch
Gdx.audio     // Audio — newSound(), newMusic()
Gdx.files     // Files — internal(), external(), local()
Gdx.gl        // GL20 — raw OpenGL calls
```

---

## B. Graphics (2D)

### B1. SpriteBatch / Texture / Sprite

**Pattern:** one `SpriteBatch` per app (expensive to create). All draw calls between `begin()`/`end()`.

```java
SpriteBatch batch = new SpriteBatch(); // create once

// In render():
batch.setProjectionMatrix(camera.combined); // required for world-space drawing
batch.begin();
    batch.draw(texture, x, y);
    batch.draw(texture, x, y, width, height);
    // full signature: draw(tex, x, y, originX, originY, w, h, scaleX, scaleY, rotation, srcX, srcY, srcW, srcH, flipX, flipY)
    sprite.draw(batch);
batch.end();
```

**Texture** — decode PNG/JPG and upload to GPU:
```java
Texture tex = new Texture(Gdx.files.internal("player.png"));
// Dimensions should be powers of 2 for compatibility
```

**TextureRegion** — sub-region of a texture (use for spritesheets):
```java
TextureRegion region = new TextureRegion(texture, x, y, width, height);
// Split full sheet into 2D array:
TextureRegion[][] frames = TextureRegion.split(sheet, frameW, frameH);
```

**Sprite** — bundles TextureRegion + position/rotation/scale:
```java
Sprite sprite = new Sprite(texture, srcX, srcY, srcW, srcH);
sprite.setPosition(x, y);   // REQUIRED — default is 0,0
sprite.setRotation(degrees);
sprite.setScale(sx, sy);
sprite.setOriginCenter();
sprite.draw(batch);
```

**TextureAtlas** — packed atlas via TexturePacker (recommended for production):
```java
TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("game.atlas"));
TextureRegion region = atlas.findRegion("player_idle");
Array<AtlasRegion> regions = atlas.findRegions("walk"); // for animation
```

**Tinting:**
```java
batch.setColor(r, g, b, a); // tint all subsequent draws
batch.setColor(Color.WHITE); // reset
```

### B2. OrthographicCamera

**Key concept:** work in world units, NOT pixels. A camera defines how much of the world is visible.

```java
float WORLD_W = 20f, WORLD_H = 20f * (Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth());
OrthographicCamera camera = new OrthographicCamera(WORLD_W, WORLD_H);
camera.position.set(WORLD_W / 2, WORLD_H / 2, 0); // center on world origin
camera.update(); // MUST call after any change

// In render():
camera.update();
batch.setProjectionMatrix(camera.combined);

// Move camera (e.g. follow player):
camera.position.set(player.x, player.y, 0);

// Zoom:
camera.zoom = 1.5f; // > 1 = zoom out

// World ↔ screen coordinate conversion:
Vector3 worldPos = new Vector3(screenX, screenY, 0);
camera.unproject(worldPos); // screen → world
```

### B3. Viewports

Viewport manages camera viewport dimensions when window is resized. Call `viewport.update(w, h)` in `resize()`.

| Viewport | Behaviour |
|---------|-----------|
| `StretchViewport(vw, vh)` | Stretches to fill — may distort aspect ratio |
| `FitViewport(vw, vh)` | Maintains ratio, adds black bars |
| `FillViewport(vw, vh)` | Maintains ratio, crops edges |
| `ExtendViewport(vw, vh)` | Maintains ratio, shows more world on wider screens |
| `ScreenViewport` | 1 unit = 1 pixel, no scaling |

```java
Viewport viewport = new FitViewport(800, 480, camera);

// In resize():
viewport.update(width, height);

// For UI stage (center camera at 0,0 bottom-left):
viewport.update(width, height, true);

// When using multiple viewports:
viewport1.apply(); // sets glViewport
// draw scene 1
viewport2.apply();
// draw scene 2 (e.g. HUD)
```

### B4. Animation

```java
// From TextureAtlas (recommended):
Animation<TextureRegion> anim = new Animation<>(0.1f, atlas.findRegions("walk"), PlayMode.LOOP);

// From spritesheet manually:
TextureRegion[][] tmp = TextureRegion.split(sheet, frameW, frameH);
TextureRegion[] frames = new TextureRegion[COLS * ROWS];
// flatten tmp into frames array...
Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);

// In render():
stateTime += Gdx.graphics.getDeltaTime();
TextureRegion frame = anim.getKeyFrame(stateTime, /*looping=*/true);
batch.draw(frame, x, y);
```

**PlayMode options:** `NORMAL`, `REVERSED`, `LOOP`, `LOOP_REVERSED`, `LOOP_PINGPONG`, `LOOP_RANDOM`

Check completion: `anim.isAnimationFinished(stateTime)` (only valid for non-looping).

### B5. Tile Maps

Load Tiled `.tmx` maps. Supported loaders: `TmxMapLoader` (default), `AtlasTmxMapLoader`.

```java
// Load:
TiledMap map = new TmxMapLoader().load("level1.tmx");

// Render (choose renderer based on map orientation):
OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map, unitScale);
// unitScale: meters-per-tile, e.g. 1/32f if tiles are 32px and you want 1m tiles

// In render():
renderer.setView(camera);          // links camera to renderer
renderer.render();                 // all layers

// Selective layer rendering (e.g. insert sprites between layers):
int[] bgLayers = {0, 1};
int[] fgLayers = {2};
renderer.render(bgLayers);
drawSprites();
renderer.render(fgLayers);
```

**Accessing map data:**
```java
MapLayer layer = map.getLayers().get("Collision");  // by name
TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(0);
TiledMapTileLayer.Cell cell = tileLayer.getCell(col, row); // null = empty

// Object layers (spawn points, trigger zones, etc.):
MapObjects objects = map.getLayers().get("Objects").getObjects();
for (MapObject obj : objects) {
    // RectangleMapObject, PolygonMapObject, CircleMapObject, etc.
    RectangleMapObject rect = (RectangleMapObject) obj;
    Rectangle r = rect.getRectangle();
}

// Custom properties:
map.getProperties().get("key", String.class);
layer.getProperties().get("key", Float.class);
```

**Dispose:** `map.dispose()` — also disposes textures.

### B6. Scene2D & UI

Scene2D is a 2D scene graph. Use for: HUDs, menus, inventory screens.  
**Do NOT use Scene2D for gameplay entities** if you need MVC separation — it couples model+view.

```java
// Core setup:
Stage stage = new Stage(new FitViewport(800, 480));
Gdx.input.setInputProcessor(stage); // or InputMultiplexer

// In render():
stage.act(Gdx.graphics.getDeltaTime()); // update actions/timers
stage.draw();

// In resize():
stage.getViewport().update(w, h, true); // true = center camera (for UIs)

// In dispose():
stage.dispose();
```

**Adding actors:**
```java
Image image = new Image(texture);
image.setPosition(100, 100);
stage.addActor(image);

Label label = new Label("Score: 0", skin);
TextButton button = new TextButton("Play", skin);
button.addListener(new ChangeListener() {
    public void changed(ChangeEvent e, Actor actor) { /* click */ }
});
```

**Layout with Table (recommended for UIs):**
```java
Table table = new Table();
table.setFillParent(true); // expand to stage
table.center();
table.add(label).padBottom(20).row();
table.add(button).width(200).height(60);
stage.addActor(table);
```

**Skin** — themes for UI widgets. Load from `.json` + atlas:
```java
Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
// Free skins: https://github.com/czyzby/gdx-skins
```

**InputMultiplexer** — route input to both Stage and game:
```java
InputMultiplexer multiplexer = new InputMultiplexer();
multiplexer.addProcessor(stage);      // UI gets first chance
multiplexer.addProcessor(gameInput);  // game gets remainder
Gdx.input.setInputProcessor(multiplexer);
```

**Actions (tweening):**
```java
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
actor.addAction(sequence(
    fadeIn(0.5f),
    delay(1f),
    fadeOut(0.5f),
    run(() -> stage.getRoot().removeActor(actor))
));
```

### B7. Fonts

**BitmapFont** (pre-rasterised, zero deps):
```java
BitmapFont font = new BitmapFont(); // default Arial 15px
BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/myfont.fnt"));
// Generate .fnt files with Hiero tool (libgdx.com/wiki/tools/hiero)

// Draw (outside SpriteBatch, needs its own or use GlyphLayout):
font.draw(batch, "Hello World", x, y);

GlyphLayout layout = new GlyphLayout(font, "text"); // for measuring
float textW = layout.width;
```

**FreeType fonts** (TTF, requires gdx-freetype extension):
```java
FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
params.size = 24;
BitmapFont font = generator.generateFont(params);
generator.dispose(); // dispose generator after generating
```

### B8. Particles

```java
// Edit effects with the 2D Particle Editor tool (libgdx.com/wiki/tools/2d-particle-editor)
ParticleEffect effect = new ParticleEffect();
effect.load(Gdx.files.internal("fire.p"), Gdx.files.internal(""));
effect.start();

// In render():
effect.update(Gdx.graphics.getDeltaTime());
batch.begin();
effect.draw(batch);
batch.end();
if (effect.isComplete()) effect.reset();

// In dispose():
effect.dispose();
```

---

## C. Input Handling

Two modes: **polling** (check state each frame) or **event** (listener callbacks).

### Polling (recommended for movement/actions)

```java
// Keyboard:
if (Gdx.input.isKeyPressed(Keys.W)) { /* held */ }
if (Gdx.input.isKeyJustPressed(Keys.SPACE)) { /* just pressed this frame */ }

// Mouse / touch:
if (Gdx.input.isTouched()) { /* finger/button down */ }
if (Gdx.input.justTouched()) { /* just touched */ }
int x = Gdx.input.getX(); // screen coords (y=0 at top on most backends)
int y = Gdx.input.getY();
boolean left = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

// Mouse → world coordinates:
Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
camera.unproject(pos);
float worldX = pos.x, worldY = pos.y;

// Multi-touch (pointer index):
Gdx.input.isTouched(0); // first finger
Gdx.input.getX(1);      // second finger x
```

### Event Handling

```java
Gdx.input.setInputProcessor(new InputAdapter() {
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    // return true = event consumed
});
```

**Key codes:** `Input.Keys.A`…`Z`, `UP`, `DOWN`, `LEFT`, `RIGHT`, `SPACE`, `ENTER`, `ESCAPE`, `BACK`, etc.

### Cursor (Desktop)

```java
Gdx.input.setCursorCatched(true);  // hide & lock cursor (FPS-style)
Gdx.input.setCursorPosition(x, y);
```

---

## D. Audio

Two audio types: `Sound` (short effects, loaded into RAM) and `Music` (streamed from disk).

```java
// Sound effects (< ~1 MB, loaded fully):
Sound sound = Gdx.audio.newSound(Gdx.files.internal("sfx/shoot.wav"));
long id = sound.play(volume);           // returns instance id
sound.play(volume, pitch, pan);
sound.stop(id);
sound.setLooping(id, true);
sound.dispose();

// Music (streaming, long tracks):
Music music = Gdx.audio.newMusic(Gdx.files.internal("music/theme.ogg"));
music.setLooping(true);
music.setVolume(0.7f);
music.play();
music.pause();
music.stop();
music.dispose();
```

**libGDX auto-pauses/resumes audio on app pause/resume.**

**Android note:** default SoundPool has latency. For rhythm games use `AsynchronousAndroidAudio` or third-party `gdx-miniaudio`.

**Supported formats:** MP3, OGG, WAV (WAV recommended for effects, OGG for music).

---

## E. Box2D Physics

Box2D is an **extension** — must add to gradle (see [J](#j-dependency-setup)).  
**1 Box2D unit = 1 metre.** Never use pixels as units.

### Setup

```java
Box2D.init(); // call once before creating World

World world = new World(new Vector2(0, -9.8f), true); // gravity, doSleep
Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer(); // dev only

// Fixed timestep (in render()):
private float accumulator = 0;
void doPhysicsStep(float delta) {
    float frameTime = Math.min(delta, 0.25f); // prevent spiral of death
    accumulator += frameTime;
    while (accumulator >= 1/60f) {
        world.step(1/60f, 6, 2); // timestep, velocityIter, positionIter
        accumulator -= 1/60f;
    }
}

// Debug render (after game render):
debugRenderer.render(world, camera.combined);
```

### Creating Bodies

```java
BodyDef bodyDef = new BodyDef();
bodyDef.type = BodyDef.BodyType.DynamicBody;  // or StaticBody, KinematicBody
bodyDef.position.set(x, y);

Body body = world.createBody(bodyDef);

PolygonShape shape = new PolygonShape();
shape.setAsBox(halfW, halfH); // half-extents

FixtureDef fixtureDef = new FixtureDef();
fixtureDef.shape = shape;
fixtureDef.density = 1.0f;
fixtureDef.friction = 0.5f;
fixtureDef.restitution = 0.1f; // bounciness

body.createFixture(fixtureDef);
shape.dispose(); // shapes MUST be disposed after fixture creation

// Attach game object reference:
body.setUserData(myGameObject);
```

**Shape types:** `PolygonShape` (box/polygon), `CircleShape`, `ChainShape`, `EdgeShape`.

### Forces / Movement

```java
body.applyLinearImpulse(new Vector2(0, 10), body.getWorldCenter(), true);
body.applyForceToCenter(new Vector2(5, 0), true);
body.setLinearVelocity(new Vector2(3, 0)); // direct velocity set
body.setTransform(x, y, angleRad);        // teleport
```

### Sync Sprites with Bodies

```java
// In render(), after physics step:
sprite.setPosition(body.getPosition().x - sprite.getWidth()/2,
                   body.getPosition().y - sprite.getHeight()/2);
sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
```

### Sensors & Collision

```java
fixtureDef.isSensor = true; // detects overlap, no physics response

world.setContactListener(new ContactListener() {
    @Override public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        // cast a.getBody().getUserData() to your game type
    }
    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact c, Manifold m) {}
    @Override public void postSolve(Contact c, ContactImpulse i) {}
});
```

**Important:** never create/destroy bodies inside a ContactListener. Queue changes and apply after `world.step()`.

### Cleanup

```java
world.destroyBody(body);
world.dispose();
debugRenderer.dispose();
```

---

## F. AssetManager

Use for async loading with progress screen. Provides reference counting and a single asset store.

```java
AssetManager assets = new AssetManager();

// Queue assets (do NOT make static):
assets.load("player.png", Texture.class);
assets.load("game.atlas", TextureAtlas.class);
assets.load("theme.ogg", Music.class);
assets.load("shoot.wav", Sound.class);
assets.load("ui/skin.json", Skin.class);
assets.load("level1.tmx", TiledMap.class,
    new TmxMapLoader.Parameters()); // optional params

// Loading screen update():
if (assets.update()) {
    // loading complete
    goToGameScreen();
} else {
    float progress = assets.getProgress(); // 0.0–1.0
}

// Or load synchronously (blocks):
assets.finishLoading();

// Retrieve:
Texture tex = assets.get("player.png", Texture.class);
TextureAtlas atlas = assets.get("game.atlas");

// Release one asset:
assets.unload("player.png");

// Dispose all:
assets.dispose();
```

**TextureParameter example:**
```java
TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
param.minFilter = TextureFilter.Linear;
param.magFilter = TextureFilter.Nearest;
assets.load("sprite.png", Texture.class, param);
```

---

## G. File Handling

```java
// Read-only, bundled with app (use for assets):
FileHandle internal = Gdx.files.internal("data/config.json");

// Read/write, app-local persistent storage:
FileHandle local = Gdx.files.local("saves/slot1.json");

// Read/write, external/SD (avoid — Android restrictions since API 30):
FileHandle external = Gdx.files.external("MyGame/export.csv");

// Reading:
String text = internal.readString();
byte[] bytes = internal.readBytes();
InputStream is = internal.read();

// Writing (local/external only):
local.writeString(jsonString, false); // false = overwrite
local.writeBytes(data, true);         // true = append

// Checks:
internal.exists();
local.isDirectory();
for (FileHandle child : dir.list()) { ... }

// Copy / move / delete:
src.copyTo(dst);
src.moveTo(dst);
local.delete();
```

### G2. Preferences

Simple key-value persistent storage (cross-platform). Best for settings, high scores.

```java
Preferences prefs = Gdx.app.getPreferences("MyGame");

prefs.putString("playerName", "Hero");
prefs.putInteger("highscore", 9999);
prefs.putBoolean("soundEnabled", true);
prefs.putFloat("volume", 0.8f);

String name = prefs.getString("playerName", "Unknown"); // default if missing
int score = prefs.getInteger("highscore", 0);

prefs.flush(); // REQUIRED to persist changes
```

### G3. JSON

```java
Json json = new Json();

// Serialize object → JSON string:
String jsonStr = json.toJson(myObject);

// Deserialize JSON string → object:
MyClass obj = json.fromJson(MyClass.class, jsonStr);

// Pretty print:
String pretty = json.prettyPrint(myObject);

// Read/write file:
json.toJson(myObject, Gdx.files.local("save.json"));
MyClass loaded = json.fromJson(MyClass.class, Gdx.files.local("save.json"));
```

Custom serializer: implement `Json.Serializable` or register `json.setSerializer(MyClass.class, customSerializer)`.

---

## H. Math Utilities

Package: `com.badlogic.gdx.math`

```java
// MathUtils — common operations:
MathUtils.sin(radians)   // uses lookup table, fast
MathUtils.cos(radians)
MathUtils.atan2(y, x)
MathUtils.radiansToDegrees  // constant
MathUtils.degreesToRadians
MathUtils.clamp(value, min, max)
MathUtils.lerp(from, to, t)
MathUtils.random.nextFloat()  // shared Random instance
MathUtils.random(min, max)    // random float in range

// Vector2 (2D vector, mutable — re-use to avoid GC):
Vector2 v = new Vector2(x, y);
v.add(other); v.sub(other); v.scl(scalar);
v.len();         // magnitude
v.nor();         // normalise in-place
v.dst(other);    // distance to other
v.angleDeg();    // angle in degrees
v.rotate(deg);
v.set(x, y);     // reuse instance

// Rectangle:
Rectangle r = new Rectangle(x, y, width, height);
r.overlaps(other);
r.contains(x, y);

// Circle:
Circle c = new Circle(x, y, radius);
c.contains(x, y);
c.overlaps(rect);

// Interpolation (easing):
float t = Interpolation.elasticOut.apply(0f, 1f, progress); // 0..1
// Other: linear, smooth, smoothstep, bounce, swing, pow2, exp10, fade, etc.
```

---

## I. Memory Management

**Critical:** libGDX manages native (non-JVM) memory. `Disposable` objects **must** be manually disposed. GC will NOT free them.

**Must dispose:**
`Texture`, `TextureAtlas`, `SpriteBatch`, `BitmapFont`, `Skin`, `Stage`, `Sound`, `Music`, `AssetManager`, `ParticleEffect`, `Pixmap`, `FrameBuffer`, `ShaderProgram`, `Mesh`, `Box2D.World`, all Bullet classes.

**Pattern:**
```java
@Override public void dispose() {
    batch.dispose();
    atlas.dispose();
    music.dispose();
    stage.dispose();
    world.dispose();
    // If using AssetManager, assets.dispose() handles most of the above
}
```

**Object Pooling** (avoid GC spikes for frequent spawning — bullets, particles):
```java
Pool<Bullet> bulletPool = new Pool<Bullet>() {
    @Override protected Bullet newObject() { return new Bullet(); }
};

// Obtain:
Bullet b = bulletPool.obtain();
b.init(x, y, direction);
activeBullets.add(b);

// Free (when bullet dies):
bulletPool.free(b); // calls b.reset() if Bullet implements Pool.Poolable
activeBullets.removeValue(b, true);
```

**Array vs java.util.ArrayList:** Use `com.badlogic.gdx.utils.Array<T>` — avoids boxing, iterator GC allocation. Also: `IntArray`, `FloatArray`, `ObjectMap`, `OrderedMap`.

---

## J. Dependency Setup

**Tool:** use [gdx-liftoff](https://github.com/libgdx/gdx-liftoff) to generate projects (replaces old gdx-setup).

`build.gradle` (core module) — key dependency strings:

```groovy
// Core (always included):
api "com.badlogicgames.gdx:gdx:$gdxVersion"

// Box2D extension:
api "com.badlogicgames.gdx:gdx-box2d:$gdxVersion"
// Desktop native:
api "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop"

// FreeType fonts:
api "com.badlogicgames.gdx:gdx-freetype:$gdxVersion"
// Desktop native:
api "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop"

// Controllers:
api "com.badlogicgames.gdx-controllers:gdx-controllers-core:$controllersVersion"

// Ashley ECS (optional entity-component system):
api "com.badlogicgames.ashley:ashley:$ashleyVersion"

// AI library:
api "com.badlogicgames.gdx-ai:gdx-ai:$aiVersion"
```

Current stable version: check https://libgdx.com/dev/versions/

---

## Quick Patterns

### Game loop with fixed physics + interpolated render

```java
float accumulator = 0;
static final float STEP = 1/60f;

@Override public void render() {
    float delta = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
    accumulator += delta;
    while (accumulator >= STEP) {
        updateGame(STEP);     // game logic + box2d
        accumulator -= STEP;
    }
    float alpha = accumulator / STEP; // interpolation factor (optional)
    draw(alpha);
}
```

### Camera follow player (smooth)

```java
float lerp = 5f;
camera.position.x += (player.x - camera.position.x) * lerp * delta;
camera.position.y += (player.y - camera.position.y) * lerp * delta;
camera.update();
```

### Screen-to-world mouse position

```java
Vector3 mouseWorld = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
camera.unproject(mouseWorld);
// mouseWorld.x, mouseWorld.y are now in world space
```

### Minimal game structure (no Screen)

```java
public class MyGame extends ApplicationAdapter {
    SpriteBatch batch;
    OrthographicCamera camera;
    Viewport viewport;
    AssetManager assets;

    @Override public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(20, 12, camera); // 20x12 world units
        assets = new AssetManager();
        assets.load("game.atlas", TextureAtlas.class);
        assets.finishLoading();
    }
    @Override public void resize(int w, int h) { viewport.update(w, h); }
    @Override public void render() {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // draw here
        batch.end();
    }
    @Override public void dispose() { batch.dispose(); assets.dispose(); }
}
```

---

## External Resources

- **Javadocs:** https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest
- **Wiki:** https://libgdx.com/wiki/
- **gdx-liftoff (project generator):** https://github.com/libgdx/gdx-liftoff
- **Tiled map editor:** https://www.mapeditor.org
- **TexturePacker:** https://libgdx.com/wiki/tools/texture-packer
- **Hiero (bitmap font tool):** https://libgdx.com/wiki/tools/hiero
- **Awesome-libGDX (libraries list):** https://github.com/rafaskb/awesome-libgdx
- **Free UI skins:** https://github.com/czyzby/gdx-skins
- **Discord:** https://libgdx.com/community/discord/
