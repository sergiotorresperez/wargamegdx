package es.garrapeta.wargame.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.GameState
import es.garrapeta.wargame.logic.GroupDetector

/**
 * Manages element selection: translates click events into a list of selected elements.
 * Empty list = no selection. Size 1 = individual. Size > 1 = group.
 */
class ElementSelectionSystem(
    private val gameState: GameState,
    private val onSelectionChanged: (selected: List<Element>) -> Unit
) {

    companion object {
        private const val INPUT_SELECTION_MODIFIER    = Input.Keys.CONTROL_LEFT
    }

    private var _selectedElements: List<Element> = emptyList()
    var selectedElements: List<Element>
        get() = _selectedElements
        set(value) {
            _selectedElements = value
            onSelectionChanged(value)
        }

    fun touchDown(worldPos: Vector2, button: Int): Boolean {
        if (button != Input.Buttons.LEFT) return false
        val modifierHeld = Gdx.input.isKeyPressed(INPUT_SELECTION_MODIFIER)
        handleClick(worldPos, modifierHeld)
        return true
    }

    fun keyDown(keycode: Int): Boolean {
        return false
    }

    /** Call with the world-space click position and whether Ctrl was held. */
    private fun handleClick(worldPos: Vector2, modifierHeld: Boolean) {
        val hit: Element? = gameState.elements.firstOrNull { it.contains(worldPos) }
        if (hit == null) { deselect(); return }

        if (modifierHeld) {
            toggleSelection(hit = hit)
        } else {
            selectedElements = GroupDetector.findGroup(hit, gameState.elements) ?: listOf(hit)
        }
    }

    /** Adds or removes [hit] from the selection, provided the result is a valid connected subgroup. */
    private fun toggleSelection(hit: Element) {
        val newSelected: List<Element> = if (hit in selectedElements)
            selectedElements - hit
        else
            selectedElements + hit

        // reject if removing an element disconnects the remaining selection (e.g. remove B from A-B-C)
        if (newSelected.size > 1 && !GroupDetector.isConnectedSubgroup(newSelected)) return

        selectedElements = newSelected
    }

    private fun deselect() { selectedElements = emptyList() }

}
