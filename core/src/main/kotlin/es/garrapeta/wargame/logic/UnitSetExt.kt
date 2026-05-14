package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.MathUtils

fun Set<Unit>.isValidGroup(): Boolean {
    return this.all { unit ->
        val others = this.minus(unit)
        return others.isEmpty() || others.any { other ->
            unit.isContiguousWith(other)
        }
    }
}

fun Set<Unit>.rotateAroundGroupCenter(angleDegrees: Float) {
    if (this.size <= 1) return

    // Calculate group centroid
    var centerX = 0f
    var centerY = 0f
    this.forEach { unit ->
        centerX += unit.center.x
        centerY += unit.center.y
    }
    centerX /= this.size
    centerY /= this.size

    val radians = MathUtils.degreesToRadians * angleDegrees
    val cos = MathUtils.cos(radians)
    val sin = MathUtils.sin(radians)

    // Rotate each unit around group center
    this.forEach { unit ->
        // Translate to centroid-relative coordinates
        val relX = unit.center.x - centerX
        val relY = unit.center.y - centerY

        // Apply 2D rotation
        val rotatedX = relX * cos - relY * sin
        val rotatedY = relX * sin + relY * cos

        // Translate back to world space
        unit.center.x = centerX + rotatedX
        unit.center.y = centerY + rotatedY

        // Rotate unit's facing angle
        unit.facingAngle = (unit.facingAngle + angleDegrees) % 360f
    }
}
