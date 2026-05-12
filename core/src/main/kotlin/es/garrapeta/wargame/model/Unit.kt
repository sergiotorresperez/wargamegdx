package es.garrapeta.wargame.model

import com.badlogic.gdx.math.Vector2

class Unit(
    val id: String,
    val position: Vector2,
    var facingAngle: Float = 0f,
    val size: Vector2 = Vector2(2f, 1f)
)
