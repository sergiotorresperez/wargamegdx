package es.garrapeta.wargame.logic

/**
 * Which element(s) the player currently has selected.
 * UI interaction state — not part of GameState (not serialised with the board).
 */
sealed class SelectionState {
    object None : SelectionState()
    data class Individual(val element: Element) : SelectionState()
    /** One or more elements from the same potential group. May be a full group or a subset. */
    data class GroupSelection(val elements: List<Element>) : SelectionState()
}
