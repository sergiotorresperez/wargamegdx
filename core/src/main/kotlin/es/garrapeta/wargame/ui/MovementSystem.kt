package es.garrapeta.wargame.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.GameState
import es.garrapeta.wargame.logic.GroupDetector

class MovementSystem(
    private val gameState: GameState,
    private val onMovementStarted: ((OngoingMovement) -> Unit),
    private val onMovementFinished: (() -> Unit)
) {

    private var ongoingMovement: OngoingMovement? = null

    fun startMovement(selected: List<Element>) {
        assert(ongoingMovement == null)
        ongoingMovement = OngoingMovement(selected).also {
            onMovementStarted(it)
        }
    }

    fun touchDown(worldPos: Vector2, button: Int): Boolean {
        val movement = ongoingMovement ?: return false

        val hitInsideSelection = movement.selected.any { element ->
            val fwd = element.forward
            val offsetX = fwd.x * movement.translation
            val offsetY = fwd.y * movement.translation
            val offsetPos = Vector2(element.position.x + offsetX, element.position.y + offsetY)

            val tempElement = Element(
                id = element.id,
                position = offsetPos,
                angleDeg = element.angleDeg,
                width = element.width,
                depth = element.depth
            )
            tempElement.contains(worldPos)
        }

        if (!hitInsideSelection) {
            ongoingMovement = null
            onMovementFinished()
            return false
        }

        return true
    }

    data class OngoingMovement(
        val selected: List<Element>,
        val translation: Float = 0f
    )
}
