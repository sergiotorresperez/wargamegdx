package es.garrapeta.wargame
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils


class MainMenuScreen(
    private val game: DropGame
): Screen {

    override fun resize(width: Int, height: Int) {
        game.viewport.update(width, height, true)
    }

    override fun show() {

    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.BLACK)

        game.viewport.apply()
        game.batch.setProjectionMatrix(game.viewport.camera.combined)

        game.batch.begin()
        //draw text. Remember that x and y are in meters
        game.font.draw(game.batch, "Welcome to Drop!!! ", 1f, 1.5f)
        game.font.draw(game.batch, "Tap anywhere to begin!", 1f, 1f)
        game.batch.end()

        if (Gdx.input.isTouched) {
            game.setScreen(GameScreen(game))
            dispose()
        }
    } // Rest of class still omitted...



    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
    }
}
