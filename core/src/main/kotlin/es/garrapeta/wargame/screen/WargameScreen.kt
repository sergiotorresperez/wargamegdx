package es.garrapeta.wargame.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.graphics.use
import es.garrapeta.wargame.model.Unit

class WargameScreen : KtxScreen {
    private val worldWidth = 36f    // pulgadas
    private val worldHeight = 24f   // pulgadas

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(worldWidth, worldHeight, camera)
    private val shapeRenderer = ShapeRenderer()

    private val units: MutableList<Unit> = mutableListOf()
    private val selectedUnits: MutableList<Unit> = mutableListOf()

    override fun show() {
        // Crear unidades iniciales
        val factory = InitialUnitsFactory()
        units.addAll(factory.createUnits())

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
        shapeRenderer.use(ShapeType.Line, camera) { sr ->
            sr.color = Color.WHITE
            for (unit in units) {
                val hw = unit.size.x / 2f
                val hh = unit.size.y / 2f
                val x = unit.position.x
                val y = unit.position.y

                // Dibujar rectángulo (outline)
                sr.rect(x - hw, y - hh, unit.size.x, unit.size.y)

                // Dibujar chevron indicando facing
                // Triángulo: esquina inf izq, esquina inf der, punto medio del lado del facing
                val infIzqX = x - hw
                val infIzqY = y - hh
                val infDerX = x + hw
                val infDerY = y - hh

                // Punto medio del lado del facing (facing=0° = norte/+Y)
                val facingMidX = x
                val facingMidY = y + hh

                // Dibujar las 3 líneas del chevron
                sr.line(infIzqX, infIzqY, infDerX, infDerY)      // base (inf izq a inf der)
                sr.line(infDerX, infDerY, facingMidX, facingMidY)  // lado derecho (inf der a punta)
                sr.line(facingMidX, facingMidY, infIzqX, infIzqY)  // lado izquierdo (punta a inf izq)
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }
}