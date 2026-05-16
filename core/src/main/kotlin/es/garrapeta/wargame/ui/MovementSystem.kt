package es.garrapeta.wargame.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.GameState
import es.garrapeta.wargame.logic.SnapDetector

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
        private const val SNAP_THRESHOLD: Float = 1f           // inches; snaps beyond this are filtered out
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
        if (button != Input.Buttons.LEFT) return false
        val movement = ongoingMovement ?: return false
        if (movement.originals.size > 1) return false
        if (!movement.previews.first().contains(worldPos)) return false
        movement.activeOp = MovementOp.DragAndDrop(lastWorldPos = worldPos)
        return true
    }

    fun touchDragged(worldPos: Vector2): Boolean {
        val movement = ongoingMovement ?: return false
        val drag = movement.activeOp as? MovementOp.DragAndDrop ?: return false
        val dx: Float = worldPos.x - drag.lastWorldPos.x
        val dy: Float = worldPos.y - drag.lastWorldPos.y
        movement.previews.forEach { element ->
            element.position.x += dx
            element.position.y += dy
        }
        updateSnaps(movement = movement)
        movement.activeOp = MovementOp.DragAndDrop(lastWorldPos = worldPos)
        return true
    }

    fun touchUp(worldPos: Vector2, button: Int): Boolean {
        val movement = ongoingMovement ?: return false
        if (movement.activeOp !is MovementOp.DragAndDrop) return false
        movement.activeOp = MovementOp.None
        return true
    }

    fun keyDown(keycode: Int): Boolean {
        val movement = ongoingMovement ?: return false
        val isGroupMovement = movement.originals.size > 1

        return when (keycode) {
            INPUT_FORWARD -> {
                movement.activeOp = MovementOp.Translate
                translate(movement = movement, delta = +TRANSLATION_INCREMENT_IN)
                true
            }

            INPUT_LEFT -> {
                if (!isGroupMovement) {
                    movement.activeOp = MovementOp.Rotate
                    onRotate(movement = movement, deltaDeg = -ROTATION_INCREMENT_DEG)
                } else {
                    if (movement.activeOp != MovementOp.PivotRight) {
                        movement.activeOp = MovementOp.PivotLeft
                        onPivotLeft(movement = movement, deltaDeg = +ROTATION_INCREMENT_DEG)
                    } else {
                        onPivotRight(movement = movement, deltaDeg = +ROTATION_INCREMENT_DEG)
                    }
                }
                true
            }

            INPUT_RIGHT -> {
                if (!isGroupMovement) {
                    movement.activeOp = MovementOp.Rotate
                    onRotate(movement = movement, deltaDeg = +ROTATION_INCREMENT_DEG)
                } else {
                    if (movement.activeOp != MovementOp.PivotLeft) {
                        movement.activeOp = MovementOp.PivotRight
                        onPivotRight(movement = movement, deltaDeg = -ROTATION_INCREMENT_DEG)
                    } else {
                        onPivotLeft(movement = movement, deltaDeg = -ROTATION_INCREMENT_DEG)
                    }
                }
                true
            }

            INPUT_CONFIRM -> {
                movement.activeOp = MovementOp.None
                onMovementConfirmed(movement)
                true
            }

            INPUT_CANCEL -> {
                movement.activeOp = MovementOp.None
                onMovementCanceled(movement)
                true
            }

            else -> {
                movement.activeOp = MovementOp.None
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
        updateSnaps(movement = movement)
    }

    private fun onRotate(movement: OngoingMovement, deltaDeg: Float) {
        movement.previews.forEach { element ->
            element.angleDeg += deltaDeg
        }
        updateSnaps(movement = movement)
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
        updateSnaps(movement = movement)
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
        updateSnaps(movement = movement)
    }

    private fun updateSnaps(movement: OngoingMovement) {
        // detect snaps for single-element movements only
        if (movement.originals.size == 1) {
            val targets: List<Element> = gameState.elements.filter { it.id != movement.originals.first().id }
            val allSnaps: List<Snap> = SnapDetector.findSnaps(movement.previews.first(), targets)
            movement.snaps = allSnaps.filter { it.distance < SNAP_THRESHOLD }
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

    sealed class MovementOp {
        object None       : MovementOp()
        object Translate  : MovementOp()
        object Rotate     : MovementOp()
        object PivotLeft  : MovementOp()
        object PivotRight : MovementOp()
        data class DragAndDrop(val lastWorldPos: Vector2) : MovementOp()
    }

    data class OngoingMovement(
        val originals: List<Element>,  // original references, mutated only on confirm
        val previews: List<Element>,   // deep copies, updated freely during movement
        var activeOp: MovementOp = MovementOp.None,
        var snaps: List<Snap> = emptyList(),  // potential snaps detected during movement
    )

    private fun Element.deepCopy(): Element = Element(
        id = id,
        position = Vector2(position),
        angleDeg = angleDeg,
        width = width,
        depth = depth,
    )
}
