package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.ui.MovementSystem
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

        ongoingMovement.selected.forEach { element ->
            val halfW: Float = element.width / 2f
            val halfD: Float = element.depth / 2f

            // apply translation offset to element position
            val fwd = element.forward
            val translatedX: Float = element.position.x + fwd.x * ongoingMovement.translation
            val translatedY: Float = element.position.y + fwd.y * ongoingMovement.translation

            shapeRenderer.rect(
                translatedX - halfD,
                translatedY - halfW,
                halfD, halfW,
                element.depth, element.width,
                1f, 1f,
                element.angleDeg,
            )
        }
    }

}
