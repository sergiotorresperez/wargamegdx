@file:JvmName("Lwjgl3Launcher")

package es.garrapeta.wargame

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport

class Main : ApplicationListener {

    // Recursos gráficos y de audio
    lateinit var backgroundTexture: Texture
    lateinit var bucketTexture: Texture
    lateinit var dropTexture: Texture
    lateinit var dropSound: Sound
    lateinit var music: Music

    lateinit var spriteBatch: SpriteBatch
    lateinit var viewport: FitViewport

    lateinit var bucketSprite: Sprite
    lateinit var touchPos: Vector2

    lateinit var dropSprites: Array<Sprite>
    var dropTimer = 0f

    // Rectángulos reutilizados para detección de colisiones
    lateinit var bucketRectangle: Rectangle
    lateinit var dropRectangle: Rectangle

    override fun create() {
        backgroundTexture = Texture("background.png")
        bucketTexture = Texture("bucket.png")
        dropTexture = Texture("drop.png")
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"))

        spriteBatch = SpriteBatch()
        viewport = FitViewport(8f, 5f)

        bucketSprite = Sprite(bucketTexture).apply { setSize(1f, 1f) }
        touchPos = Vector2()
        dropSprites = Array()
        bucketRectangle = Rectangle()
        dropRectangle = Rectangle()

        music.apply {
            isLooping = true
            volume = 0.5f
            play()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render() {
        input()
        logic()
        draw()
    }

    private fun input() {
        val speed = 4f
        val delta = Gdx.graphics.deltaTime

        // Movimiento con teclado
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta)
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta)
        }

        // Movimiento con táctil/ratón
        if (Gdx.input.isTouched) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            viewport.unproject(touchPos)
            bucketSprite.setCenterX(touchPos.x)
        }
    }

    private fun logic() {
        val worldWidth = viewport.worldWidth
        val worldHeight = viewport.worldHeight
        val delta = Gdx.graphics.deltaTime

        // Limitar el cubo a los bordes de la pantalla
        bucketSprite.x = MathUtils.clamp(bucketSprite.x, 0f, worldWidth - bucketSprite.width)
        bucketRectangle.set(bucketSprite.x, bucketSprite.y, bucketSprite.width, bucketSprite.height)

        // Iterar en reversa para poder eliminar elementos de forma segura
        for (i in dropSprites.size - 1 downTo 0) {
            val drop = dropSprites[i]
            drop.translateY(-2f * delta)
            dropRectangle.set(drop.x, drop.y, drop.width, drop.height)

            when {
                drop.y < -drop.height -> dropSprites.removeIndex(i) // fuera de pantalla
                bucketRectangle.overlaps(dropRectangle) -> {
                    dropSprites.removeIndex(i)
                    dropSound.play()
                }
            }
        }

        // Generar una gota cada segundo
        dropTimer += delta
        if (dropTimer > 1f) {
            dropTimer = 0f
            createDroplet()
        }
    }

    private fun draw() {
        ScreenUtils.clear(Color.BLACK)
        viewport.apply()
        spriteBatch.projectionMatrix = viewport.camera.combined

        spriteBatch.begin()
        spriteBatch.draw(backgroundTexture, 0f, 0f, viewport.worldWidth, viewport.worldHeight)
        bucketSprite.draw(spriteBatch)
        dropSprites.forEach { it.draw(spriteBatch) }
        spriteBatch.end()
    }

    private fun createDroplet() {
        Sprite(dropTexture).apply {
            setSize(1f, 1f)
            x = MathUtils.random(0f, viewport.worldWidth - width)
            y = viewport.worldHeight // aparece en el borde superior
            dropSprites.add(this)
        }
    }

    override fun pause() {}
    override fun resume() {}
    override fun dispose() {}
}
