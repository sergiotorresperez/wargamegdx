package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.geometry.mmToInches

/** Creates the fixed initial layout of elements for a new battle. */
class InitialElementsFactory {

    fun createElement(): List<Element> {
        // A: isolated
        val a = Element(
            id = "A",
            position = Vector2(10f, 4f),
            angleDeg = 105f,
            width = 40f.mmToInches(),
            depth = 20f.mmToInches()
        )
        // B, C, D: group formation, flank to flank, facing north
        val b = Element(
            id = "B",
            position = Vector2(15f, 4f),
            angleDeg = 105f,
            width = 40f.mmToInches(),
            depth = 20f.mmToInches()
        )
        val c = Element(
            id = "C",
            position = Vector2(15f + b.width, 4f),
            angleDeg = 105f,
            width = 40f.mmToInches(),
            depth = 20f.mmToInches()
        )
        val d = Element(
            id = "D",
            position = Vector2(15f + c.width * 2f, 4f),
            angleDeg = 105f,
            width = 40f.mmToInches(),
            depth = 20f.mmToInches()
        )
        return listOf(a, b, c, d)
    }

}
