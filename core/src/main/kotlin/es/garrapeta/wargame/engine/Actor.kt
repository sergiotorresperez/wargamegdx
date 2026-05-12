package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

interface Actor {
    val id: String
    fun render(shapeRenderer: ShapeRenderer)
}
