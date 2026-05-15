package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.Vector2

/**
 * Manages element selection: translates click events into SelectionState changes.
 * Owns SelectionState (UI concern, not part of GameState).
 * Reads GameState to resolve hit-tests and group membership.
 */
class ElementSelectionSystem(private val gameState: GameState) {

    var selectionState: SelectionState = SelectionState.None
        private set

    /** Call with the world-space click position and whether Ctrl was held. */
    fun handleClick(worldPos: Vector2, ctrlHeld: Boolean) {
        val hit = gameState.elements.firstOrNull { it.contains(worldPos) }

        if (hit == null) {
            deselect()
            return
        }

        val potentialGroup = GroupDetector.findGroup(hit, gameState.elements)

        if (!ctrlHeld) {
            // plain click: select group or individual
            selectionState = if (potentialGroup != null)
                SelectionState.GroupSelection(potentialGroup)
            else
                SelectionState.Individual(hit)
        } else {
            handleCtrlClick(hit, potentialGroup)
        }
    }

    fun deselect() {
        selectionState = SelectionState.None
    }

    private fun handleCtrlClick(hit: Element, potentialGroup: List<Element>?) {
        // Ctrl+click on isolated element → always ignored
        if (potentialGroup == null) return

        when (val current = selectionState) {
            is SelectionState.None ->
                // no selection + Ctrl+click on group element → select just that element
                selectionState = SelectionState.Individual(hit)

            is SelectionState.Individual -> {
                val currentGroup = GroupDetector.findGroup(current.element, gameState.elements)
                // only act if hit is in the same potential group as the selected element
                if (currentGroup == null || hit !in currentGroup) return
                selectionState = if (hit == current.element)
                    SelectionState.None                          // toggle off
                else
                    SelectionState.GroupSelection(listOf(current.element, hit))  // add
            }

            is SelectionState.GroupSelection -> {
                // only act if hit shares a potential group with at least one selected element
                if (current.elements.none { it in potentialGroup }) return
                val newElements = if (hit in current.elements)
                    current.elements - hit   // remove
                else
                    current.elements + hit   // add
                selectionState = when {
                    newElements.isEmpty()  -> SelectionState.None
                    newElements.size == 1  -> SelectionState.Individual(newElements.first())
                    else                   -> SelectionState.GroupSelection(newElements)
                }
            }
        }
    }
}
