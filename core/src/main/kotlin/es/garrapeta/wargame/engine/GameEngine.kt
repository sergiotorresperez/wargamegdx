package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/** Holds and renders all world actors. The screen owns the ShapeRenderer and sets the projection matrix before calling render(). */
class GameEngine {

    @PublishedApi internal val actors: MutableList<Actor> = mutableListOf()

    fun addActor(actor: Actor) { actors.add(actor) }


    inline fun <reified T : Actor> getActorById(id: String): T? =
        actors.filterIsInstance<T>().find { it.id == id }

    fun removeActor(actor: Actor) { actors.remove(actor) }

    fun removeActorById(id: String) { actors.removeIf { id == it.id } }

    fun render(shapeRenderer: ShapeRenderer, delta: Float) {
        shapeRenderer.setAutoShapeType(true)
        shapeRenderer.begin()
        actors.forEach { it.render(shapeRenderer, delta) }
        shapeRenderer.end()
    }
}
