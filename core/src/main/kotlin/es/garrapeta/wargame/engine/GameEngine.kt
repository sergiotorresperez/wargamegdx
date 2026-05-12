package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class GameEngine(private val shapeRenderer: ShapeRenderer) {
    private val actors: MutableMap<String, Actor> = mutableMapOf()

    fun addActor(actor: Actor) {
        actors[actor.id] = actor
    }

    fun getActor(id: String): Actor? = actors[id]

    fun <T : Actor> getActors(clazz: Class<T>): List<T> =
        actors.values.filter { clazz.isInstance(it) }.map { clazz.cast(it) }

    fun render() {
        actors.values.forEach { it.render(shapeRenderer) }
    }
}
