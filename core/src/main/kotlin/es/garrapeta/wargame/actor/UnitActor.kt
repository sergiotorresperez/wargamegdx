package es.garrapeta.wargame.actor

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import es.garrapeta.wargame.engine.Actor
import es.garrapeta.wargame.logic.Unit

class UnitActor(val unit: Unit) : Actor {

    override val id: String
        get() = unit.id

    var selected: Boolean = false

    override fun render(shapeRenderer: ShapeRenderer) {
        val hw = unit.size.x / 2f
        val hh = unit.size.y / 2f
        val x = unit.position.x
        val y = unit.position.y
        val angle = unit.facingAngle

        fun rotatePoint(px: Float, py: Float): Pair<Float, Float> {
            val rad = MathUtils.degreesToRadians * angle
            val cos = MathUtils.cos(rad)
            val sin = MathUtils.sin(rad)
            val dx = px - x; val dy = py - y
            return Pair(x + dx * cos - dy * sin, y + dx * sin + dy * cos)
        }

        // Rectángulo rotado
        val color = if (selected) Color.YELLOW else Color.WHITE
        shapeRenderer.color = color
        shapeRenderer.rect(
            x - hw, y - hh,
            hw, hh,
            unit.size.x, unit.size.y,
            1f, 1f,
            angle
        )

        // Chevron rotado
        val (ilX, ilY) = rotatePoint(x - hw, y - hh)  // inferior izquierdo
        val (irX, irY) = rotatePoint(x + hw, y - hh)  // inferior derecho
        val (tmX, tmY) = rotatePoint(x,      y + hh)  // tope medio (facing)

        shapeRenderer.color = Color.WHITE
        shapeRenderer.line(ilX, ilY, irX, irY)   // base
        shapeRenderer.line(irX, irY, tmX, tmY)   // lado derecho
        shapeRenderer.line(tmX, tmY, ilX, ilY)   // lado izquierdo
    }
}
