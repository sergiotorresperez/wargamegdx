package es.garrapeta.wargame.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.GameState

class MovementSystem(
    private val gameState: GameState,
    private val onMovementStarted: ((OngoingMovement) -> Unit),
    private val onMovementFinished: (() -> Unit)
) {

    companion object {
        private const val TRANSLATION_INCREMENT: Float = 0.2f  // inches per keypress
    }

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

        if (hitInsideSelection) {
            onMovementConfirmed(movement)
        }  else {
            onMovementCanceled()
        }

        return true
    }

    fun keyDown(keycode: Int): Boolean {
        val translationDelta = when (keycode) {
            Input.Keys.UP -> TRANSLATION_INCREMENT
            Input.Keys.DOWN -> -TRANSLATION_INCREMENT
            else -> null
        }

        val movement = ongoingMovement

        return if (movement != null && translationDelta != null) {
            onTranslate(movement, translationDelta)
            true
        } else {
            false
        }
    }

    private fun onTranslate(movement: OngoingMovement, delta: Float) {
        // TODO: this is wrong if the facing has changed
        movement.translation += delta
    }

    private fun onMovementConfirmed(movement: OngoingMovement) {
        movement.selected.forEach { element ->
            element.position.y += movement.translation
        }
        onMovementFinished()
    }

    private fun onMovementCanceled() {
        onMovementFinished()
        ongoingMovement = null
    }

    data class OngoingMovement(
        val selected: List<Element>,
        var translation: Float = 0f
    )
}
