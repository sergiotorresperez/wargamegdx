package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class GameEngine(private val shapeRenderer: ShapeRenderer) {
    private val actors: MutableList<Actor> = mutableListOf()

    fun addActor(actor: Actor) {
        actors.add(actor)
    }

    fun render() {
        for (actor in actors) {
            actor.render(shapeRenderer)
        }
    }
}
