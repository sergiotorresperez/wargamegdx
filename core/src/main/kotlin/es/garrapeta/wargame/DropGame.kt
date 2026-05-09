package es.garrapeta.wargame

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport

class DropGame : Game() {
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont
    lateinit var viewport: FitViewport


    override fun create() {
        batch = SpriteBatch()
        // use libGDX's default font
        font = BitmapFont()
        viewport = FitViewport(8f, 5f)


        //font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false)
        font.getData().setScale(viewport.worldHeight / Gdx.graphics.height)

        this.setScreen(MainMenuScreen(this))
    }

    override fun render() {
        super.render() // important!
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
