package es.garrapeta.wargame.ui

import es.garrapeta.wargame.logic.Corner

/**
 * Types of snap, derived from DBA 2.2 contact types (§5, §6, §9).
 *
 * Each snap type defines:
 * - Which corner of the moving element participates (myCorner)
 * - Which corner of the target element participates (theirCorner)
 * - What angle difference is required for this snap to be valid (requiredAngleDiff in degrees, 0..180)
 */
enum class SnapType(
    /**
     * The corner of the moving element involved in this snap.
     * Example: for SIDE_RIGHT, moving element's front-right corner aligns with target's front-left.
     */
    val myCorner: Corner,

    /**
     * The corner of the target element involved in this snap.
     */
    val theirCorner: Corner,

    /**
     * Required angle difference (in degrees, 0..180) for this snap to be valid.
     * Examples: 0° = same angle (group formation), 90° = perpendicular (flank attack), 180° = opposite (front contact).
     * The actual angle difference between the moving element and target should be within ±15° of this value.
     */
    val requiredAngleDiff: Float,
) {
    // Group formation (flank-to-flank, same angle, DBA §5)
    SIDE_RIGHT(
        myCorner = Corner.FRONT_RIGHT,
        theirCorner = Corner.FRONT_LEFT,
        requiredAngleDiff = 0f,
    ),
    SIDE_LEFT(
        myCorner = Corner.FRONT_LEFT,
        theirCorner = Corner.FRONT_RIGHT,
        requiredAngleDiff = 0f,
    ),

    // Column formation (front-to-rear, same angle, DBA §6)
    COLUMN_AHEAD(
        myCorner = Corner.FRONT_LEFT,
        theirCorner = Corner.REAR_LEFT,
        requiredAngleDiff = 0f,
    ),
    COLUMN_BEHIND(
        myCorner = Corner.REAR_LEFT,
        theirCorner = Corner.FRONT_LEFT,
        requiredAngleDiff = 0f,
    ),

    // Front contact (front-to-front, opposite angles, DBA §9.1)
    FRONT_CONTACT_LEFT(
        myCorner = Corner.FRONT_LEFT,
        theirCorner = Corner.FRONT_RIGHT,
        requiredAngleDiff = 180f,
    ),
    FRONT_CONTACT_RIGHT(
        myCorner = Corner.FRONT_RIGHT,
        theirCorner = Corner.FRONT_LEFT,
        requiredAngleDiff = 180f,
    ),

    // Flank contact (front against flank, perpendicular angles, DBA §9.2)
    FLANK_ATTACK_LEFT(
        myCorner = Corner.FRONT_LEFT,
        theirCorner = Corner.FRONT_LEFT,
        requiredAngleDiff = 90f,
    ),
    FLANK_ATTACK_RIGHT(
        myCorner = Corner.FRONT_RIGHT,
        theirCorner = Corner.FRONT_RIGHT,
        requiredAngleDiff = 90f,
    ),
}