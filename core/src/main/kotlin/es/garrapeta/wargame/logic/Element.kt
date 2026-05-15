package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

const val BASE_WIDTH: Float = 1.6f  // 1 BW in inches (40 mm at 15 mm scale)
const val BASE_DEPTH: Float = 0.8f  // 0.5 BW in inches

/**
 * A single unit base on the board.
 * Spatial state only: position (center), facing angle, and base dimensions.
 * All geometry (corners, edge centres, hit-test) derives from these four values.
 */
class Element(
    val id: String,
    var position: Vector2,
    var angleDeg: Float,
    val width: Float = BASE_WIDTH,  // front/rear edge length (perpendicular to facing)
    val depth: Float = BASE_DEPTH,  // flank edge length (parallel to facing)
) {
    // --- primitive direction vectors (new instances, safe to mutate by caller) ---

    /** Unit vector pointing in the facing direction. */
    val forward: Vector2
        get() = Vector2(MathUtils.cosDeg(angleDeg), MathUtils.sinDeg(angleDeg))

    /** Unit vector pointing right relative to facing (90° clockwise from forward). */
    val right: Vector2
        get() = Vector2(MathUtils.sinDeg(angleDeg), -MathUtils.cosDeg(angleDeg))

    // --- 4 corners ---

    val frontLeft: Vector2  get() = corner(fwdSign = +1f, rgtSign = -1f)
    val frontRight: Vector2 get() = corner(fwdSign = +1f, rgtSign = +1f)
    val rearLeft: Vector2   get() = corner(fwdSign = -1f, rgtSign = -1f)
    val rearRight: Vector2  get() = corner(fwdSign = -1f, rgtSign = +1f)

    // --- 4 edge centres (used by GroupDetector for contact detection) ---

    val frontCenter: Vector2      get() = midpoint(frontLeft, frontRight)
    val rearCenter: Vector2       get() = midpoint(rearLeft, rearRight)
    val leftFlankCenter: Vector2  get() = midpoint(frontLeft, rearLeft)
    val rightFlankCenter: Vector2 get() = midpoint(frontRight, rearRight)

    // --- hit detection ---

    /** True if [point] falls inside the element's rotated rectangle. */
    fun contains(point: Vector2): Boolean {
        val fl = frontLeft; val fr = frontRight
        val rr = rearRight; val rl = rearLeft
        return Intersector.isPointInPolygon(
            floatArrayOf(fl.x, fl.y, fr.x, fr.y, rr.x, rr.y, rl.x, rl.y),
            0, 4, point.x, point.y,
        )
    }

    // --- private helpers ---

    private fun corner(fwdSign: Float, rgtSign: Float): Vector2 {
        val halfD = depth / 2f
        val halfW = width / 2f
        // right = (sinDeg, -cosDeg) = (fwdY, -fwdX) — avoids a second trig call
        val fwdX = MathUtils.cosDeg(angleDeg)
        val fwdY = MathUtils.sinDeg(angleDeg)
        return Vector2(
            position.x + fwdX * halfD * fwdSign + fwdY * halfW * rgtSign,
            position.y + fwdY * halfD * fwdSign - fwdX * halfW * rgtSign,
        )
    }

    private fun midpoint(a: Vector2, b: Vector2): Vector2 =
        Vector2((a.x + b.x) / 2f, (a.y + b.y) / 2f)
}
