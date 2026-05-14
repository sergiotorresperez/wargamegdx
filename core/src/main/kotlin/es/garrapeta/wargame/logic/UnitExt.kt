package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.MathUtils
import es.garrapeta.wargame.geometry.GeometryUtils

fun Unit.canJoinGroup(others: Set<Unit>): Boolean {
    return others.isEmpty() || others.any { that ->
        this.isContiguousWith(that)
    }
}

fun Unit.isContiguousWith(that: Unit): Boolean {
    // Same orientation (within 5° tolerance)
    val orientationDiff = ((that.facingAngle - this.facingAngle + 360) % 360).let {
        if (it > 180) 360 - it else it
    }
    if (orientationDiff > 5f) return false

    val aCorners = GeometryUtils.getCorners(that.center, that.width, that.depth, that.facingAngle)
    val bCorners =
        GeometryUtils.getCorners(this.center, this.width, this.depth, this.facingAngle)

    // A is to the left of B OR to the right OR above OR below
    return isALeftOfB(aCorners, bCorners) ||
        isARightOfB(aCorners, bCorners) ||
        isAAboveB(aCorners, bCorners) ||
        isABelowB(aCorners, bCorners)
}

private fun isALeftOfB(aCorners: List<Vector2>, bCorners: List<Vector2>): Boolean {
    // A's right edge == B's left edge
    val aTopRight = aCorners[1]
    val aBottomRight = aCorners[2]
    val bTopLeft = bCorners[0]
    val bBottomLeft = bCorners[3]

    val tolerance = 0.1f
    return aTopRight.dst(bTopLeft) <= tolerance && aBottomRight.dst(bBottomLeft) <= tolerance
}

private fun isARightOfB(aCorners: List<Vector2>, bCorners: List<Vector2>): Boolean {
    // A's left edge == B's right edge
    val aTopLeft = aCorners[0]
    val aBottomLeft = aCorners[3]
    val bTopRight = bCorners[1]
    val bBottomRight = bCorners[2]

    val tolerance = 0.1f
    return aTopLeft.dst(bTopRight) <= tolerance && aBottomLeft.dst(bBottomRight) <= tolerance
}

private fun isAAboveB(aCorners: List<Vector2>, bCorners: List<Vector2>): Boolean {
    // A's bottom edge == B's top edge
    val aBottomLeft = aCorners[3]
    val aBottomRight = aCorners[2]
    val bTopLeft = bCorners[0]
    val bTopRight = bCorners[1]

    val tolerance = 0.1f
    return aBottomLeft.dst(bTopLeft) <= tolerance && aBottomRight.dst(bTopRight) <= tolerance
}

private fun isABelowB(aCorners: List<Vector2>, bCorners: List<Vector2>): Boolean {
    // A's top edge == B's bottom edge
    val aTopLeft = aCorners[0]
    val aTopRight = aCorners[1]
    val bBottomLeft = bCorners[3]
    val bBottomRight = bCorners[2]

    val tolerance = 0.1f
    return aTopLeft.dst(bBottomLeft) <= tolerance && aTopRight.dst(bBottomRight) <= tolerance
}


