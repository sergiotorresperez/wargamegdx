package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/** Renderable object in the world scene. Called once per frame inside a ShapeRenderer begin/end block with autoShapeType enabled. */
interface Actor {
    fun render(shapeRenderer: ShapeRenderer, delta: Float)
}