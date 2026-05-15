package es.garrapeta.wargame.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.InitialElementsFactory
import ktx.app.KtxScreen

private const val BATTLEFIELD_WIDTH: Float = 36f    // inches
private const val BATTLEFIELD_HEIGHT: Float = 24f   // inches

/** Game screen where the battle takes place; renders all elements each frame. */
class WargameScreen : KtxScreen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val viewport: FitViewport = FitViewport(BATTLEFIELD_WIDTH, BATTLEFIELD_HEIGHT, camera)
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()
    private val elements: List<Element> = InitialElementsFactory().createElement()

    override fun show() {
        camera.position.set(BATTLEFIELD_WIDTH / 2f, BATTLEFIELD_HEIGHT / 2f, 0f)
        camera.update()
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined

        // filled blue bodies
        shapeRenderer.begin(ShapeType.Filled)
        shapeRenderer.color = Color.BLUE
        elements.forEach { element: Element -> drawBody(element) }
        shapeRenderer.end()

        // white borders and chevrons
        shapeRenderer.begin(ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        elements.forEach { element: Element -> drawBorderAndChevron(element) }
        shapeRenderer.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

    private fun drawBody(element: Element) {
        val halfW: Float = element.width / 2f
        val halfD: Float = element.depth / 2f
        shapeRenderer.rect(
            element.position.x - halfW, // bottom-left x of un-rotated rect
            element.position.y - halfD, // bottom-left y of un-rotated rect
            halfW,                      // originX: pivot at center
            halfD,                      // originY: pivot at center
            element.width,
            element.depth,
            1f, 1f,
            element.angleDeg,
        )
    }

    private fun drawBorderAndChevron(element: Element) {
        val halfW: Float = element.width / 2f
        val halfD: Float = element.depth / 2f

        // border (same rect as body, line mode)
        shapeRenderer.rect(
            element.position.x - halfW,
            element.position.y - halfD,
            halfW, halfD,
            element.width, element.depth,
            1f, 1f,
            element.angleDeg,
        )

        // chevron: back-left → front-middle → back-right
        val fwdX: Float = MathUtils.cosDeg(element.angleDeg)
        val fwdY: Float = MathUtils.sinDeg(element.angleDeg)
        val rgtX: Float = MathUtils.sinDeg(element.angleDeg)
        val rgtY: Float = -MathUtils.cosDeg(element.angleDeg)

        shapeRenderer.triangle(
            element.position.x - fwdX * halfD - rgtX * halfW,  // back-left x
            element.position.y - fwdY * halfD - rgtY * halfW,  // back-left y
            element.position.x + fwdX * halfD,                 // front-middle x
            element.position.y + fwdY * halfD,                 // front-middle y
            element.position.x - fwdX * halfD + rgtX * halfW,  // back-right x
            element.position.y - fwdY * halfD + rgtY * halfW,  // back-right y
        )
    }
}