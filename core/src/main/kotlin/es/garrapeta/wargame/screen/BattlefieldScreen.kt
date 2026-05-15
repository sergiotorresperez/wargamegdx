package es.garrapeta.wargame.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import es.garrapeta.wargame.engine.ElementActor
import es.garrapeta.wargame.engine.GameEngine
import es.garrapeta.wargame.logic.InitialElementsFactory
import ktx.app.KtxScreen

private const val BATTLEFIELD_WIDTH: Float = 36f    // inches
private const val BATTLEFIELD_HEIGHT: Float = 24f   // inches

/** Game screen where the battle takes place; delegates rendering to GameEngine. */
class WargameScreen : KtxScreen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val viewport: FitViewport = FitViewport(BATTLEFIELD_WIDTH, BATTLEFIELD_HEIGHT, camera)
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()
    private val engine: GameEngine = GameEngine()

    override fun show() {
        camera.position.set(BATTLEFIELD_WIDTH / 2f, BATTLEFIELD_HEIGHT / 2f, 0f)
        camera.update()

        InitialElementsFactory().createElement().forEach { element ->
            engine.addActor(actor = ElementActor(element = element))
        }

        engine.getActorById<ElementActor>(id = "B")?.isSelected = true
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        engine.render(shapeRenderer = shapeRenderer, delta = delta)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }
}