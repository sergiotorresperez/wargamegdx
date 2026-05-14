package es.garrapeta.wargame.model

import com.badlogic.gdx.math.Vector2

class Unit(
    val id: String,
    val center: Vector2,
    var facingAngle: Float = 0f,
    val width: Float = 2f,
    val depth: Float = 1f
)
