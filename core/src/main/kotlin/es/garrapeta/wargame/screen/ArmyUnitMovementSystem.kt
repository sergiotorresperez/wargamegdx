package es.garrapeta.wargame.screen

import com.badlogic.gdx.math.MathUtils
import es.garrapeta.wargame.logic.GameLogicState
import es.garrapeta.wargame.logic.Unit
import es.garrapeta.wargame.logic.rotateAroundGroupCenter

class ArmyUnitMovementSystem(val gameLogicState: GameLogicState) {

    private val speedInchesPerSecond = 6f

    fun onMoveForward(delta: Float, selectedUnits: Set<Unit>) {
        selectedUnits.forEach { unit ->
            val rad = MathUtils.degreesToRadians * unit.facingAngle
            unit.position.x += -MathUtils.sin(rad) * speedInchesPerSecond * delta
            unit.position.y += MathUtils.cos(rad) * speedInchesPerSecond * delta
        }
    }

    fun onRotateGroup(angleDegrees: Float, selectedUnits: Set<Unit>) {
        selectedUnits.rotateAroundGroupCenter(angleDegrees)
    }
}