package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.Vector2

/**
 * Represents one of the four corners of an Element.
 * Encodes corner type and the geometric parameters (fwdSign, rgtSign) used to compute its position.
 */
data class Corner(
    val type: Type,
    val fwdSign: Float,
    val rgtSign: Float,
) {
    /**
     * The four corners of a rectangular element.
     */
    enum class Type {
        FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT
    }

    companion object {
        val FRONT_LEFT: Corner  = Corner(type = Type.FRONT_LEFT,  fwdSign = +1f, rgtSign = -1f)
        val FRONT_RIGHT: Corner = Corner(type = Type.FRONT_RIGHT, fwdSign = +1f, rgtSign = +1f)
        val REAR_LEFT: Corner   = Corner(type = Type.REAR_LEFT,   fwdSign = -1f, rgtSign = -1f)
        val REAR_RIGHT: Corner  = Corner(type = Type.REAR_RIGHT,  fwdSign = -1f, rgtSign = +1f)
    }
}

/**
 * Get the world position of a specific corner of this element.
 */
fun Element.getCorner(corner: Corner): Vector2 =
    corner(corner.fwdSign, corner.rgtSign)