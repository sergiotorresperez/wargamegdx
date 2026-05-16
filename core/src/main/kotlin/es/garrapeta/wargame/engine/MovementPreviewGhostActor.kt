package es.garrapeta.wargame.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import es.garrapeta.wargame.logic.getCorner
import es.garrapeta.wargame.ui.MovementSystem.OngoingMovement


class MovementPreviewGhostActor(
    val ongoingMovement: OngoingMovement
) : Actor {

    companion object {
        const val ACTOR_ID = "MovementPreviewGhostActor"
    }
    override val id: String get() = ACTOR_ID

    override fun render(shapeRenderer: ShapeRenderer, delta: Float) {
        paint(shapeRenderer)
    }

    private fun paint(shapeRenderer: ShapeRenderer) {
        shapeRenderer.set(ShapeType.Filled)
        shapeRenderer.color = Color(1f, 1f, 1f, 0.3f)  // white, 30% alpha

        // previews already hold the offset positions — render them directly
        ongoingMovement.previews.forEach { element ->
            val halfW: Float = element.width / 2f
            val halfD: Float = element.depth / 2f
            shapeRenderer.rect(
                element.position.x - halfD,
                element.position.y - halfW,
                halfD, halfW,
                element.depth, element.width,
                1f, 1f,
                element.angleDeg,
            )
        }

        // visualize all potential snaps
        paintSnapRectangles(shapeRenderer)
        paintSnapLines(shapeRenderer)
        paintSnapPoints(shapeRenderer)
    }

    private fun paintSnapRectangles(shapeRenderer: ShapeRenderer) {
        if (ongoingMovement.snaps.isEmpty()) return

        ongoingMovement.snaps.forEachIndexed { index, snap ->
            shapeRenderer.set(ShapeType.Filled)
            // closest snap (index 0) in cyan; others in yellow
            val isClosest: Boolean = index == 0
            shapeRenderer.color = if (isClosest) {
                Color(0f, 1f, 1f, 0.2f)  // cyan, 20% alpha
            } else {
                Color(1f, 1f, 0f, 0.1f)  // yellow, 10% alpha
            }

            val halfW: Float = snap.element.width / 2f
            val halfD: Float = snap.element.depth / 2f
            shapeRenderer.rect(
                snap.newPosition.x - halfD,
                snap.newPosition.y - halfW,
                halfD, halfW,
                snap.element.depth, snap.element.width,
                1f, 1f,
                snap.element.angleDeg,
            )
        }
    }

    private fun paintSnapLines(shapeRenderer: ShapeRenderer) {
        if (ongoingMovement.snaps.isEmpty()) return

        shapeRenderer.set(ShapeType.Line)

        ongoingMovement.snaps.forEachIndexed { index, snap ->
            val isClosest: Boolean = index == 0
            shapeRenderer.color = if (isClosest) {
                Color(0f, 1f, 1f, 0.6f)  // cyan, 60% alpha
            } else {
                Color(1f, 1f, 0f, 0.3f)  // yellow, 30% alpha
            }

            val myCornerPos = snap.element.getCorner(snap.snapType.myCorner)
            val theirCornerPos = snap.target.getCorner(snap.snapType.theirCorner)

            shapeRenderer.line(myCornerPos.x, myCornerPos.y, theirCornerPos.x, theirCornerPos.y)
        }
    }

    private fun paintSnapPoints(shapeRenderer: ShapeRenderer) {
        if (ongoingMovement.snaps.isEmpty()) return

        shapeRenderer.set(ShapeType.Filled)
        val pointRadius: Float = 0.1f  // small circle

        ongoingMovement.snaps.forEachIndexed { index, snap ->
            val isClosest: Boolean = index == 0
            shapeRenderer.color = if (isClosest) {
                Color(0f, 1f, 1f, 0.8f)  // cyan, 80% alpha
            } else {
                Color(1f, 1f, 0f, 0.5f)  // yellow, 50% alpha
            }

            val myCornerPos = snap.element.getCorner(snap.snapType.myCorner)
            val theirCornerPos = snap.target.getCorner(snap.snapType.theirCorner)

            // draw circles at both corners
            shapeRenderer.circle(myCornerPos.x, myCornerPos.y, pointRadius)
            shapeRenderer.circle(theirCornerPos.x, theirCornerPos.y, pointRadius)
        }
    }

}
