package es.garrapeta.wargame.screen

import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.geometry.GeometryUtils
import es.garrapeta.wargame.logic.Unit
import es.garrapeta.wargame.logic.GameLogicState
import es.garrapeta.wargame.logic.canJoinGroup
import es.garrapeta.wargame.logic.isValidGroup

class ArmyUnitSelectionSystem(
    val gameLogicState: GameLogicState,
    val onUnitsSelected: (Set<Unit>) -> kotlin.Unit
) {

    val selectedUnits = mutableSetOf<Unit>()

    fun onUnitSelectionClick(worldX: Float, worldY: Float, isGroupSelection: Boolean) {
        val armyUnits = gameLogicState.units
        val clickPoint = Vector2(worldX, worldY)
        val hitUnit = armyUnits.find { unit ->
            GeometryUtils.pointInRotatedRect(
                point = clickPoint,
                rectCenter = unit.center,
                width = unit.width,
                depth = unit.depth,
                facingAngleDegrees = unit.facingAngle
            )
        }
        if (hitUnit == null) {
            // si fuera de unidad, clear
            selectedUnits.clear()
        } else {
            val selected = selectedUnits.contains(hitUnit)
            if (!isGroupSelection) {
                if (selected) {
                    // si estaba seleccionada, deseleccionar
                    selectedUnits.remove(hitUnit)
                } else {
                    // si no isAddingToGroup a grupo, seleccionar en aislamiento
                    selectedUnits.clear()
                    selectedUnits.add(hitUnit)
                }
            } else {
                if (selected && selectedUnits.minus(hitUnit).isValidGroup()) {
                    // si estaba seleccionada y grupo valido, deseleccionar
                    selectedUnits.remove(hitUnit)
                } else if (hitUnit.canJoinGroup(selectedUnits)) {
                    // si no estaba seleccionada y grupo valido, seleccionar
                    selectedUnits.add(hitUnit)
                }
            }
        }
        onUnitsSelected(selectedUnits.toSet())
    }
}
