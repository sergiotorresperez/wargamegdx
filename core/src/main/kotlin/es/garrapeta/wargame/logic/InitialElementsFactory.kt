package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.Vector2

/** Creates the fixed initial layout of elements for a new battle. */
class InitialElementsFactory {

    fun createElement(): List<Element> = listOf(
        // A: isolated
        Element(
            id = "A",
            position = Vector2(9f, 12f),
            angleDeg = 90f, // north
        ),
        // B, C, D: group formation, flank to flank, facing north
        Element(
            id = "B",
            position = Vector2(18f, 12f),
            angleDeg = 90f,
        ),
        Element(
            id = "C",
            position = Vector2(18f + BASE_WIDTH, 12f),
            angleDeg = 90f,
        ),
        Element(
            id = "D",
            position = Vector2(18f + BASE_WIDTH * 2f, 12f),
            angleDeg = 90f,
        ),
    )
}