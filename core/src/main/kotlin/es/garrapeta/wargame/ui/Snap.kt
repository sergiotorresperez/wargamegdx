package es.garrapeta.wargame.ui

import com.badlogic.gdx.math.Vector2
import es.garrapeta.wargame.logic.Element

/**
 * Represents a potential snap alignment between a moving element and a target element.
 *
 * A snap candidate is identified by:
 * - Which contact type this is (snapType, which encodes the corner pair and angle requirement)
 * - How close the corners currently are (distance)
 * - Where the moving element would be repositioned to achieve exact alignment (newPosition)
 *
 * No distance threshold is enforced here — all potential snaps are candidates.
 * The caller decides which snap to apply based on distance or other criteria.
 */
data class Snap(
    /**
     * Type of snap (defines which corner pair aligns, and the angle requirement).
     * Encodes the geometric configuration from DBA 2.2 rules.
     */
    val snapType: SnapType,

    /**
     * The element being moved/dragged.
     */
    val element: Element,

    /**
     * The target element this snap would align with.
     */
    val target: Element,

    /**
     * Euclidean distance between the two corners that would be aligned.
     * Represents how far apart they currently are.
     * Used to rank which snap is "best" (smallest distance).
     */
    val distance: Float,

    /**
     * The position where the moving element's center would be placed to achieve this snap.
     * Computed as: targetCornerPos - (myCornerPos - element.position)
     * This ensures the moving element's corner aligns exactly with the target's corner.
     */
    val newPosition: Vector2,
)