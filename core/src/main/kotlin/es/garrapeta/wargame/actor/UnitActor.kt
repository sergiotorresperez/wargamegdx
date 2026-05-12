package es.garrapeta.wargame.actor

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import es.garrapeta.wargame.engine.Actor
import es.garrapeta.wargame.model.Unit

class UnitActor(val unit: Unit) : Actor {
    override fun render(shapeRenderer: ShapeRenderer) {
        val hw = unit.size.x / 2f
        val hh = unit.size.y / 2f
        val x = unit.position.x
        val y = unit.position.y

        // Dibujar rectángulo (outline)
        shapeRenderer.rect(x - hw, y - hh, unit.size.x, unit.size.y)

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
        shapeRenderer.line(infIzqX, infIzqY, infDerX, infDerY)          // base
        shapeRenderer.line(infDerX, infDerY, facingMidX, facingMidY)    // lado derecho
        shapeRenderer.line(facingMidX, facingMidY, infIzqX, infIzqY)    // lado izquierdo
    }
}
