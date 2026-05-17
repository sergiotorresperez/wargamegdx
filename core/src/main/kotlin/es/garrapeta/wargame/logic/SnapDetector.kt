package es.garrapeta.wargame.logic

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

    private const val SHOW_THRESHOLD: Float = 1.0f           // inches; snaps beyond this are filtered out
    private const val VERY_CLOSE_THRESHOLD: Float = 0.3f  // inches

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
        val myCornerPos: Vector2 = element.getCorner(snapType.myCorner)
        val theirCornerPos: Vector2 = target.getCorner(snapType.theirCorner)

        // Distance between the corners that would be aligned by this snap
        val distance: Float = myCornerPos.dst(theirCornerPos)

        // New position of element's center to achieve exact corner alignment
        // Formula: newPos = targetCorner - (myCorner - currentPos)
        val offset: Vector2 = Vector2(myCornerPos).sub(element.position)
        val newPosition: Vector2 = Vector2(theirCornerPos).sub(offset)

        if (distance < SHOW_THRESHOLD) {
            return Snap(
                snapType = snapType,
                element = element,
                target = target,
                distance = distance,
                newPosition = newPosition,
                isVeryClose = distance < VERY_CLOSE_THRESHOLD,
            )
        } else {
            return null
        }
    }

}
