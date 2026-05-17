package es.garrapeta.wargame.logic

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.ui.Snap
import es.garrapeta.wargame.ui.SnapType

/**
 * Detects potential snap alignments between a moving element and target elements.
 *
 * Analogous to GroupDetector (contact detection), SnapDetector evaluates all 8 snap types
 * for all target elements and returns candidates sorted by proximity (distance).
 *
 * No threshold is enforced — all potential snaps are evaluated. The caller decides
 * which snap to apply based on distance tolerance.
 */
object SnapDetector {

    private const val SHOW_THRESHOLD: Float = 1.3f           // inches; snaps beyond this are filtered out
    private const val VERY_CLOSE_THRESHOLD: Float = 0.6f  // inches
    private const val DEGREE_TOLERANCE: Float = 45f

    /**
     * Find all potential snap alignments between a moving element and a list of targets.
     *
     * For each target and each snap type, compute:
     * - Current distance between the snap corners
     * - Where the moving element would be positioned to achieve exact alignment
     * - Whether the snap is "very close" (distance < 0.5 inches)
     *
     * @param element The element being moved/dragged
     * @param targets List of target elements to snap against
     * @return List of potential snaps, sorted by distance (closest first)
     */
    fun findSnaps(element: Element, targets: List<Element>): List<Snap> {
        val snaps = mutableListOf<Snap>()

        for (target in targets) {
            for (snapType in SnapType.entries) {
                val snap = findSnap(snapType, element, target)
                if (snap != null) {
                    snaps.add(snap)
                }
            }
        }

        return snaps.sortedBy { it.distance }
    }

    private fun findSnap(
        snapType: SnapType,
        element: Element,
        target: Element
    ): Snap? {
        // Validate angle requirement: element's angle diff from target must match requiredAngleDiff
        val angleDiff: Float = element.angleDeg - target.angleDeg
        val requiredDiff: Float = snapType.requiredAngleDiff
        val tolerance: Float = DEGREE_TOLERANCE
        val angleWithinTolerance: Boolean = kotlin.math.abs(angleDiff - requiredDiff) <= tolerance ||
                                             kotlin.math.abs(angleDiff - (requiredDiff - 360f)) <= tolerance ||
                                             kotlin.math.abs(angleDiff - (requiredDiff + 360f)) <= tolerance
        if (!angleWithinTolerance) {
            return null
        }

        // New angle to satisfy snap requirement: target's angle + required difference
        val newAngle: Float = target.angleDeg + requiredDiff

        val myCornerPos: Vector2 = element.getCorner(snapType.myCorner)
        val theirCornerPos: Vector2 = target.getCorner(snapType.theirCorner)

        // Distance between the corners that would be aligned by this snap
        val distance: Float = myCornerPos.dst(theirCornerPos)

        // New position of element's center to achieve exact corner alignment after rotation to newAngle
        // Calculate where myCorner would be if element had newAngle
        val newFwdX: Float = MathUtils.cosDeg(newAngle)
        val newFwdY: Float = MathUtils.sinDeg(newAngle)
        val cornerType: Corner = snapType.myCorner
        val halfD: Float = element.depth / 2f
        val halfW: Float = element.width / 2f
        val myCornerOffsetX: Float = newFwdX * halfD * cornerType.fwdSign + newFwdY * halfW * cornerType.rgtSign
        val myCornerOffsetY: Float = newFwdY * halfD * cornerType.fwdSign - newFwdX * halfW * cornerType.rgtSign
        val newPosition: Vector2 = Vector2(theirCornerPos.x - myCornerOffsetX, theirCornerPos.y - myCornerOffsetY)

        if (distance < SHOW_THRESHOLD) {
            return Snap(
                snapType = snapType,
                element = element,
                target = target,
                distance = distance,
                newPosition = newPosition,
                newAngle = newAngle,
                isVeryClose = distance < VERY_CLOSE_THRESHOLD,
            )
        } else {
            return null
        }
    }

}
