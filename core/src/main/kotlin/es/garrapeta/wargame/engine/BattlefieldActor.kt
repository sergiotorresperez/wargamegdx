package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

/** Renders the battlefield: border + 1"×1" grid. */
class BattlefieldActor(
    val width: Float,
    val height: Float
) : Actor {

    override val id: String = "battlefield"

    override fun render(shapeRenderer: ShapeRenderer, delta: Float) {
        drawGrid(shapeRenderer)
        drawBorder(shapeRenderer)
    }

    private fun drawGrid(shapeRenderer: ShapeRenderer) {
        shapeRenderer.set(ShapeType.Line)
        shapeRenderer.color = Color(0f, 0.4f, 0f, 1f)

        // Vertical lines (columns)
        for (x in 1 until width.toInt()) {
            shapeRenderer.line(x.toFloat(), 0f, x.toFloat(), height)
        }

        // Horizontal lines (rows)
        for (y in 1 until height.toInt()) {
            shapeRenderer.line(0f, y.toFloat(), width, y.toFloat())
        }
    }

    private fun drawBorder(shapeRenderer: ShapeRenderer) {
        shapeRenderer.set(ShapeType.Line)
        shapeRenderer.color = Color.GREEN
        val thickness: Float = 0.1f

        // Bottom
        shapeRenderer.rectLine(0f, 0f, width, 0f, thickness)

        // Top
        shapeRenderer.rectLine(0f, height, width, height, thickness)

        // Left
        shapeRenderer.rectLine(0f, 0f, 0f, height, thickness)

        // Right
        shapeRenderer.rectLine(width, 0f, width, height, thickness)
    }
}
