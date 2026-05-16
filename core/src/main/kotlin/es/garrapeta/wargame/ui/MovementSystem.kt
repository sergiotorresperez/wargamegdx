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
        val consumed = ongoingMovement
            ?.let {
                true
            }
            ?: run {
                false
            }
        return consumed
    }

    data class OngoingMovement(
        private val selected: List<Element>,
        private val translation: Float = 0f
    )
}
