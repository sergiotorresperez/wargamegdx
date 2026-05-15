package es.garrapeta.wargame.ui

import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.GameState
import es.garrapeta.wargame.logic.GroupDetector

/**
 * Manages element selection: translates click events into a list of selected elements.
 * Empty list = no selection. Size 1 = individual. Size > 1 = group.
 */
class ElementSelectionSystem(private val gameState: GameState) {

    var selectedElements: List<Element> = emptyList()
        private set

    /** Call with the world-space click position and whether Ctrl was held. */
    fun handleClick(worldPos: Vector2, ctrlHeld: Boolean) {
        val hit: Element? = gameState.elements.firstOrNull { it.contains(worldPos) }
        if (hit == null) { deselect(); return }

        if (ctrlHeld) handleCtrlClick(hit = hit)
        else selectedElements = GroupDetector.findGroup(hit, gameState.elements) ?: listOf(hit)
    }

    fun deselect() { selectedElements = emptyList() }

    /** Adds or removes [hit] from the selection, provided the result is a valid connected subgroup. */
    private fun handleCtrlClick(hit: Element) {
        val newSelected: List<Element> = if (hit in selectedElements)
            selectedElements - hit
        else
            selectedElements + hit

        // reject if removing an element disconnects the remaining selection (e.g. remove B from A-B-C)
        if (newSelected.size > 1 && !GroupDetector.isConnectedSubgroup(newSelected)) return

        selectedElements = newSelected
    }
}
