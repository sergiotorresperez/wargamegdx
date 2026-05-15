package es.garrapeta.wargame.logic

import kotlin.math.abs

/**
 * Determines which elements form a legal group.
 * A group requires same facing (within tolerance) and base contact on a valid edge pair:
 *   - right flank of A ↔ left flank of B  (line formation)
 *   - front of A ↔ rear of B              (column formation)
 */
object GroupDetector {

    private const val CONTACT_EPSILON = 0.05f  // inches — ~3% of BW
    private const val ANGLE_EPSILON = 1f        // degrees

    /**
     * Returns all elements transitively connected to [element] via legal group contact,
     * including [element] itself. Returns null if [element] is isolated (no neighbours).
     */
    fun findGroup(element: Element, allElements: List<Element>): List<Element>? {
        val visited = mutableSetOf(element)
        val queue = ArrayDeque<Element>().also { it.add(element) }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            allElements
                .filter { it !in visited && inGroupContact(current, it) }
                .forEach { visited.add(it); queue.add(it) }
        }

        return if (visited.size > 1) visited.toList() else null
    }

    /** True if [a] and [b] face the same direction and touch on a valid edge pair. */
    private fun inGroupContact(a: Element, b: Element): Boolean =
        angleDiff(a.angleDeg, b.angleDeg) <= ANGLE_EPSILON && (
            a.rightFlankCenter.dst(b.leftFlankCenter) < CONTACT_EPSILON ||
            a.leftFlankCenter.dst(b.rightFlankCenter) < CONTACT_EPSILON ||
            a.frontCenter.dst(b.rearCenter) < CONTACT_EPSILON ||
            a.rearCenter.dst(b.frontCenter) < CONTACT_EPSILON
        )

    /** Shortest angular distance between two angles, handling 0°/360° wraparound. */
    private fun angleDiff(a: Float, b: Float): Float {
        val d = abs(a - b) % 360f
        return if (d > 180f) 360f - d else d
    }
}
