package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.Vector2

class InitialUnitsFactory {
    fun createUnits(): List<Unit> {
        // Unidad aislada: U1
        val u1 = Unit(
            id = "U1",
            position = Vector2(6f, 18f),
            facingAngle = 0f
        )

        // Formación A-B-C: fila horizontal, short-edge contact
        // Separación 2" en X (= width de unidad)
        val unitA = Unit(
            id = "A",
            position = Vector2(14f, 12f),
            facingAngle = 0f
        )
        val unitB = Unit(
            id = "B",
            position = Vector2(16f, 12f),
            facingAngle = 0f
        )
        val unitC = Unit(
            id = "C",
            position = Vector2(18f, 12f),
            facingAngle = 0f
        )

        return listOf(u1, unitA, unitB, unitC)
    }
}
