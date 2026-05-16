package es.garrapeta.wargame.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.GameState

class MovementSystem(
    private val gameState: GameState,
    private val onMovementStarted: ((OngoingMovement) -> Unit),
    private val onMovementStopped: (() -> Unit)
) {

    companion object {
        private const val INPUT_FORWARD = Input.Keys.UP
        private const val INPUT_LEFT = Input.Keys.LEFT
        private const val INPUT_RIGHT = Input.Keys.RIGHT
        private const val INPUT_CONFIRM = Input.Keys.ENTER
        private const val INPUT_CANCEL = Input.Keys.ESCAPE

        private const val TRANSLATION_INCREMENT_IN: Float = 0.2f  // inches per keypress
        private const val ROTATION_INCREMENT_DEG: Float = 5f       // degrees per keypress
    }

    private var ongoingMovement: OngoingMovement? = null

    fun startMovement(selected: List<Element>) {
        ongoingMovement = OngoingMovement(
            originals = selected,
            previews = selected.map { it.deepCopy() },
        ).also { onMovementStarted(it) }
    }

    fun stopMovement() {
        if (ongoingMovement != null) {
            ongoingMovement = null
            onMovementStopped()
        }
    }

    fun touchDown(worldPos: Vector2, button: Int): Boolean {
        return false
    }

    fun keyDown(keycode: Int): Boolean {
        val movement = ongoingMovement ?: return false
        val isGroupMovement = movement.originals.size > 1

        return when (keycode) {
            INPUT_FORWARD -> {
                movement.activeOp = MovementOp.TRANSLATE
                translate(movement = movement, delta = +TRANSLATION_INCREMENT_IN)
                true
            }

            INPUT_LEFT -> {
                if (!isGroupMovement) {
                    movement.activeOp = MovementOp.ROTATE
                    onRotate(movement = movement, deltaDeg = -ROTATION_INCREMENT_DEG)
                } else {
                    if (movement.activeOp != MovementOp.PIVOT_RIGHT) {
                        movement.activeOp = MovementOp.PIVOT_LEFT
                        onPivotLeft(movement = movement, deltaDeg = +ROTATION_INCREMENT_DEG)
                    } else {
                        onPivotRight(movement = movement, deltaDeg = +ROTATION_INCREMENT_DEG)
                    }
                }
                true
            }

            INPUT_RIGHT -> {
                if (!isGroupMovement) {
                    movement.activeOp = MovementOp.ROTATE
                    onRotate(movement = movement, deltaDeg = +ROTATION_INCREMENT_DEG)
                } else {
                    if (movement.activeOp != MovementOp.PIVOT_LEFT) {
                        movement.activeOp = MovementOp.PIVOT_RIGHT
                        onPivotRight(movement = movement, deltaDeg = -ROTATION_INCREMENT_DEG)
                    } else {
                        onPivotLeft(movement = movement, deltaDeg = -ROTATION_INCREMENT_DEG)
                    }
                }
                true
            }

            INPUT_CONFIRM -> {
                movement.activeOp = MovementOp.NONE
                onMovementConfirmed(movement)
                true
            }

            INPUT_CANCEL -> {
                movement.activeOp = MovementOp.NONE
                onMovementCanceled(movement)
                true
            }

            else -> {
                movement.activeOp = MovementOp.NONE
                false
            }
        }
    }

    private fun translate(movement: OngoingMovement, delta: Float) {
        movement.previews.forEach { element ->
            val fwd = element.forward
            element.position.x += fwd.x * delta
            element.position.y += fwd.y * delta
        }
    }

    private fun onRotate(movement: OngoingMovement, deltaDeg: Float) {
        movement.previews.forEach { element ->
            element.angleDeg += deltaDeg
        }
    }

    private fun onPivotLeft(movement: OngoingMovement, deltaDeg: Float) {
        // pivot point = the frontLeft corner most to the left across all previews
        val right: Vector2 = movement.previews.first().right
        val pivot: Vector2 = movement.previews.minBy { it.frontLeft.dot(right) }.frontLeft

        val cos: Float = MathUtils.cosDeg(deltaDeg)
        val sin: Float = MathUtils.sinDeg(deltaDeg)
        movement.previews.forEach { element ->
            val dx: Float = element.position.x - pivot.x
            val dy: Float = element.position.y - pivot.y
            element.position.x = pivot.x + dx * cos - dy * sin
            element.position.y = pivot.y + dx * sin + dy * cos
            element.angleDeg += deltaDeg
        }
    }

    private fun onPivotRight(movement: OngoingMovement, deltaDeg: Float) {
        // pivot point = the frontRight corner most to the right across all previews
        val right: Vector2 = movement.previews.first().right
        val pivot: Vector2 = movement.previews.maxBy { it.position.dot(right) }.frontRight

        val cos: Float = MathUtils.cosDeg(deltaDeg)
        val sin: Float = MathUtils.sinDeg(deltaDeg)
        movement.previews.forEach { element ->
            val dx: Float = element.position.x - pivot.x
            val dy: Float = element.position.y - pivot.y
            element.position.x = pivot.x + dx * cos - dy * sin
            element.position.y = pivot.y + dx * sin + dy * cos
            element.angleDeg += deltaDeg
        }
    }

    private fun onMovementConfirmed(movement: OngoingMovement) {
        // apply preview positions/facing to original elements
        movement.originals.zip(movement.previews).forEach { (original, preview) ->
            original.position.set(preview.position)
            original.angleDeg = preview.angleDeg
        }
        stopMovement()
    }

    private fun onMovementCanceled(movement: OngoingMovement) {
        stopMovement()
        startMovement(movement.originals)
    }

    enum class MovementOp { NONE, TRANSLATE, ROTATE, PIVOT_LEFT, PIVOT_RIGHT }

    data class OngoingMovement(
        val originals: List<Element>,  // original references, mutated only on confirm
        val previews: List<Element>,   // deep copies, updated freely during movement
        var activeOp: MovementOp = MovementOp.NONE,
    )

    private fun Element.deepCopy(): Element = Element(
        id = id,
        position = Vector2(position),
        angleDeg = angleDeg,
        width = width,
        depth = depth,
    )
}
