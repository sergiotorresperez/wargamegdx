package es.garrapeta.wargame.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.graphics.use
import es.garrapeta.wargame.engine.GameEngine
import es.garrapeta.wargame.actor.UnitActor

class WargameScreen : KtxScreen {
    private val worldWidth = 36f    // pulgadas
    private val worldHeight = 24f   // pulgadas

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(worldWidth, worldHeight, camera)
    private val shapeRenderer = ShapeRenderer()
    private val engine = GameEngine(shapeRenderer)

    override fun show() {
        // Agregar actores al engine
        for (unit in InitialUnitsFactory().createUnits()) {
            engine.addActor(UnitActor(unit))
        }

        // Setup cámara y viewport
        camera.position.set(worldWidth / 2f, worldHeight / 2f, 0f)
        camera.update()
    }

    override fun render(delta: Float) {
        // Limpiar pantalla
        ScreenUtils.clear(0f, 0f, 0f, 1f)

        // Update cámara
        camera.update()

        // Renderizar
        shapeRenderer.use(ShapeType.Line, camera) {
            shapeRenderer.color = Color.WHITE
            engine.render()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }
}
