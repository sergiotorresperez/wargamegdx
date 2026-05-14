package es.garrapeta.wargame.geometry

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.MathUtils

object GeometryUtils {

    /**
     * Comprueba si un punto está dentro de un rectángulo rotado en 2D.
     *
     * @param point Punto a testear (world space)
     * @param rectCenter Centro del rectángulo
     * @param width Ancho del rectángulo
     * @param depth Profundidad del rectángulo
     * @param facingAngleDegrees Ángulo de rotación en grados
     * @return true si el punto está dentro del rectángulo, false en caso contrario
     */
    fun pointInRotatedRect(
        point: Vector2,
        rectCenter: Vector2,
        width: Float,
        depth: Float,
        facingAngleDegrees: Float
    ): Boolean {
        // Transformar punto a local space del rectángulo
        val dx = point.x - rectCenter.x
        val dy = point.y - rectCenter.y

        // Rotación inversa: girar el punto -facingAngle alrededor del origen
        val radians = MathUtils.degreesToRadians * -facingAngleDegrees
        val cos = MathUtils.cos(radians)
        val sin = MathUtils.sin(radians)

        val localX = dx * cos - dy * sin
        val localY = dx * sin + dy * cos

        // Comprobar si está dentro del rectángulo en local space
        val halfWidth = width / 2f
        val halfHeight = depth / 2f

        return localX >= -halfWidth && localX <= halfWidth &&
               localY >= -halfHeight && localY <= halfHeight
    }

    /**
     * Calcula las 4 esquinas de un rectángulo rotado.
     *
     * @param position Centro del rectángulo
     * @param width Ancho del rectángulo
     * @param depth Profundidad del rectángulo
     * @param facingAngleDegrees Ángulo de rotación en grados
     * @return Lista de 4 Vector2 en orden: top-left, top-right, bottom-right, bottom-left
     */
    fun getCorners(position: Vector2, width: Float, depth: Float, facingAngleDegrees: Float): List<Vector2> {
        val radians = MathUtils.degreesToRadians * facingAngleDegrees
        val cos = MathUtils.cos(radians)
        val sin = MathUtils.sin(radians)

        val hw = width / 2f
        val hh = depth / 2f

        val corners = listOf(
            Vector2(-hw, hh),   // top-left
            Vector2(hw, hh),    // top-right
            Vector2(hw, -hh),   // bottom-right
            Vector2(-hw, -hh)   // bottom-left
        )

        return corners.map { corner ->
            val rotated = Vector2(
                corner.x * cos - corner.y * sin,
                corner.x * sin + corner.y * cos
            )
            rotated.add(position)
        }
    }
}
