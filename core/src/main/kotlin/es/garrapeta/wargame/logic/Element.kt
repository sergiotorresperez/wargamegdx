package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.Vector2

const val BASE_WIDTH: Float = 1.6f  // 1 BW in inches (40 mm at 15 mm scale)
const val BASE_DEPTH: Float = 0.8f  // 0.5 BW in inches

/** A single unit base on the board, defined purely by its spatial state. */
class Element(
    val id: String,
    var position: Vector2,
    var angleDeg: Float,
    val width: Float = BASE_WIDTH,
    val depth: Float = BASE_DEPTH,
)
