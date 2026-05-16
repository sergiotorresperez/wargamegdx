package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import es.garrapeta.wargame.ui.MovementSystem.OngoingMovement


class MovementPreviewGhostActor(
    val ongoingMovement: OngoingMovement
) : Actor {

    companion object {
        const val ACTOR_ID = "MovementPreviewGhostActor"
    }
    override val id: String get() = ACTOR_ID

    override fun render(shapeRenderer: ShapeRenderer, delta: Float) {
        paint(shapeRenderer)
    }

    private fun paint(shapeRenderer: ShapeRenderer) {
        shapeRenderer.set(ShapeType.Filled)
        shapeRenderer.color = Color(1f, 1f, 1f, 0.3f)  // white, 30% alpha

        // previews already hold the offset positions — render them directly
        ongoingMovement.previews.forEach { element ->
            val halfW: Float = element.width / 2f
            val halfD: Float = element.depth / 2f
            shapeRenderer.rect(
                element.position.x - halfD,
                element.position.y - halfW,
                halfD, halfW,
                element.depth, element.width,
                1f, 1f,
                element.angleDeg,
            )
        }
    }

}
