package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

interface Actor {
    fun render(shapeRenderer: ShapeRenderer)
}