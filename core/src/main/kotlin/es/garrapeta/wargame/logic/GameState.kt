package es.garrapeta.wargame.logic

/**
 * State of the board: the elements and their spatial data.
 * No rendering, no input, no UI concerns — this is the tabletop itself.
 */
class GameState(val elements: List<Element>) {

    companion object {
        const val BATTLEFIELD_WIDTH: Float = 36f
        const val BATTLEFIELD_HEIGHT: Float = 24f
    }
}
