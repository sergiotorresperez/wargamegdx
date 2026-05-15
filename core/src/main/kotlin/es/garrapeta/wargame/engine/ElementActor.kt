package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import es.garrapeta.wargame.logic.Element

private const val SELECTION_OUTLINE_PADDING: Float = 0.1f  // inches of padding around selected element

/** Renders a single Element as a filled blue rectangle with white border and facing chevron. */
class ElementActor(val element: Element) : Actor {

    override val id: String get() = element.id
    var isSelected: Boolean = false

    override fun render(shapeRenderer: ShapeRenderer, delta: Float) {
        val halfW: Float = element.width / 2f
        val halfD: Float = element.depth / 2f

        if (isSelected) drawSelectionOutline(shapeRenderer = shapeRenderer, halfW = halfW, halfD = halfD)

        // filled blue body
        shapeRenderer.set(ShapeType.Filled)
        shapeRenderer.color = Color.BLUE
        shapeRenderer.rect(
            element.position.x - halfD,
            element.position.y - halfW,
            halfD, halfW,
            element.depth, element.width,
            1f, 1f,
            element.angleDeg,
        )

        // white border + chevron
        shapeRenderer.set(ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(
            element.position.x - halfD,
            element.position.y - halfW,
            halfD, halfW,
            element.depth, element.width,
            1f, 1f,
            element.angleDeg,
        )

        val fwdX: Float = MathUtils.cosDeg(element.angleDeg)
        val fwdY: Float = MathUtils.sinDeg(element.angleDeg)
        val rgtX: Float = MathUtils.sinDeg(element.angleDeg)
        val rgtY: Float = -MathUtils.cosDeg(element.angleDeg)

        shapeRenderer.triangle(
            element.position.x - fwdX * halfD - rgtX * halfW,  // back-left
            element.position.y - fwdY * halfD - rgtY * halfW,
            element.position.x + fwdX * halfD,                  // front-middle
            element.position.y + fwdY * halfD,
            element.position.x - fwdX * halfD + rgtX * halfW,  // back-right
            element.position.y - fwdY * halfD + rgtY * halfW,
        )
    }

    private fun drawSelectionOutline(shapeRenderer: ShapeRenderer, halfW: Float, halfD: Float) {
        val outlineHalfW: Float = halfW + SELECTION_OUTLINE_PADDING
        val outlineHalfD: Float = halfD + SELECTION_OUTLINE_PADDING
        shapeRenderer.set(ShapeType.Line)
        shapeRenderer.color = Color.GREEN
        shapeRenderer.rect(
            element.position.x - outlineHalfD,
            element.position.y - outlineHalfW,
            outlineHalfD, outlineHalfW,
            element.depth + SELECTION_OUTLINE_PADDING * 2f,
            element.width + SELECTION_OUTLINE_PADDING * 2f,
            1f, 1f,
            element.angleDeg,
        )
    }
}