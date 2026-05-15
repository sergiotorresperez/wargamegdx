package es.garrapeta.wargame.logic

/**
 * Serialisable state of the board: the elements and their spatial data.
 * No rendering, no input, no UI concerns — this is the tabletop itself.
 */
class GameState(val elements: List<Element>)
