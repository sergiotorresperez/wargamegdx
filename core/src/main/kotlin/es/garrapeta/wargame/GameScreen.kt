package es.garrapeta.wargame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils

class GameScreen(val game: DropGame) : Screen {
    var backgroundTexture: Texture
    var bucketTexture: Texture
    var dropTexture: Texture
    var dropSound: Sound
    var music: Music
    var bucketSprite: Sprite
    var touchPos: Vector2
    var dropSprites: Array<Sprite>
    var dropTimer: Float = 0f
    var bucketRectangle: Rectangle
    var dropRectangle: Rectangle
    var dropsGathered: Int = 0

    init {
        // load the images for the background, bucket and droplet
        backgroundTexture = Texture("background.png")
        bucketTexture = Texture("bucket.png")
        dropTexture = Texture("drop.png")

        // load the drop sound effect and background music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"))
        music.setLooping(true)
        music.setVolume(0.5f)

        bucketSprite = Sprite(bucketTexture)
        bucketSprite.setSize(1f, 1f)

        touchPos = Vector2()

        bucketRectangle = Rectangle()
        dropRectangle = Rectangle()

        dropSprites = Array<Sprite>()
    }

    override fun show() {
        // start the playback of the background music
        // when the screen is shown
        music.play()
    }

    override fun render(delta: Float) {
        input()
        logic()
        draw()
    }

    private fun input() {
        val speed = 4f
        val delta = Gdx.graphics.getDeltaTime()

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta)
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta)
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX().toFloat(), Gdx.input.getY().toFloat())
            game.viewport!!.unproject(touchPos)
            bucketSprite.setCenterX(touchPos.x)
        }
    }

    private fun logic() {
        val worldWidth = game.viewport!!.getWorldWidth()
        val worldHeight = game.viewport!!.getWorldHeight()
        val bucketWidth = bucketSprite.getWidth()
        val bucketHeight = bucketSprite.getHeight()
        val delta = Gdx.graphics.getDeltaTime()

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0f, worldWidth - bucketWidth))
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight)

        for (i in dropSprites.size - 1 downTo 0) {
            val dropSprite = dropSprites.get(i)
            val dropWidth = dropSprite.getWidth()
            val dropHeight = dropSprite.getHeight()

            dropSprite.translateY(-2f * delta)
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight)

            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i)
            else if (bucketRectangle.overlaps(dropRectangle)) {
                dropsGathered++
                dropSprites.removeIndex(i)
                dropSound.play()
            }
        }

        dropTimer += delta
        if (dropTimer > 1f) {
            dropTimer = 0f
            createDroplet()
        }
    }

    private fun draw() {
        ScreenUtils.clear(Color.BLACK)
        game.viewport!!.apply()
        game.batch!!.setProjectionMatrix(game.viewport!!.getCamera().combined)
        game.batch!!.begin()

        val worldWidth = game.viewport!!.getWorldWidth()
        val worldHeight = game.viewport!!.getWorldHeight()

        game.batch!!.draw(backgroundTexture, 0f, 0f, worldWidth, worldHeight)
        bucketSprite.draw(game.batch)

        game.font!!.draw(game.batch, "Drops collected: " + dropsGathered, 0f, worldHeight)

        for (dropSprite in dropSprites) {
            dropSprite.draw(game.batch)
        }

        game.batch!!.end()
    }

    private fun createDroplet() {
        val dropWidth = 1f
        val dropHeight = 1f
        val worldWidth = game.viewport!!.getWorldWidth()
        val worldHeight = game.viewport!!.getWorldHeight()

        val dropSprite = Sprite(dropTexture)
        dropSprite.setSize(dropWidth, dropHeight)
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth))
        dropSprite.setY(worldHeight)
        dropSprites.add(dropSprite)
    }

    override fun resize(width: Int, height: Int) {
        game.viewport!!.update(width, height, true)
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        backgroundTexture.dispose()
        dropSound.dispose()
        music.dispose()
        dropTexture.dispose()
        bucketTexture.dispose()
    }
}
