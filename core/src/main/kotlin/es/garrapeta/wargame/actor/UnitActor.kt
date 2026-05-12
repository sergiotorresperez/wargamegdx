package es.garrapeta.wargame.actor

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
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


        // Dibujar rectángulo
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(x - hw, y - hh, unit.size.x, unit.size.y)

        // Dibujar chevron indicando facing
        val infIzqX = x - hw
        val infIzqY = y - hh
        val infDerX = x + hw
        val infDerY = y - hh
        val facingMidX = x
        val facingMidY = y + hh

        shapeRenderer.color = Color.WHITE
        shapeRenderer.line(infIzqX, infIzqY, infDerX, infDerY)          // base
        shapeRenderer.line(infDerX, infDerY, facingMidX, facingMidY)    // lado derecho
        shapeRenderer.line(facingMidX, facingMidY, infIzqX, infIzqY)    // lado izquierdo

        // Outline seleccion
        shapeRenderer.color = Color.YELLOW

        // Dibujar rectángulo (outline)
        if (selected) {
            shapeRenderer.rect(x - hw, y - hh, unit.size.x, unit.size.y)
        }
    }
}
