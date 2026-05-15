package es.garrapeta.wargame.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import es.garrapeta.wargame.engine.ElementActor
import es.garrapeta.wargame.engine.GameEngine
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.ElementSelectionSystem
import es.garrapeta.wargame.logic.GameState
import es.garrapeta.wargame.logic.InitialElementsFactory
import es.garrapeta.wargame.logic.SelectionState
import ktx.app.KtxScreen

private const val BATTLEFIELD_WIDTH: Float = 36f
private const val BATTLEFIELD_HEIGHT: Float = 24f
private const val GROUP_BOX_MARGIN: Float = 0.15f   // inches of padding around group bounding box

/** Game screen: owns the render loop and routes input to the selection system. */
class WargameScreen : KtxScreen {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(BATTLEFIELD_WIDTH, BATTLEFIELD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()

    // logic
    private val gameState = GameState(InitialElementsFactory().createElement())
    private val selectionSystem = ElementSelectionSystem(gameState)

    // rendering
    private val engine = GameEngine()

    override fun show() {
        camera.position.set(BATTLEFIELD_WIDTH / 2f, BATTLEFIELD_HEIGHT / 2f, 0f)
        camera.update()

        // one ElementActor per Element — shares the same Element instance
        gameState.elements.forEach { element ->
            engine.addActor(ElementActor(element = element))
        }

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (button != Input.Buttons.LEFT) return false
                val worldPos = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                val ctrlHeld = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                               Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)
                selectionSystem.handleClick(worldPos, ctrlHeld)
                syncSelectionToActors()
                return true
            }

            override fun keyDown(keycode: Int): Boolean {
                if (keycode == Input.Keys.ESCAPE) {
                    selectionSystem.deselect()
                    syncSelectionToActors()
                }
                return false
            }
        }
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined

        // element bodies, borders, chevrons and per-element selection outline — via GameEngine
        engine.render(shapeRenderer = shapeRenderer, delta = delta)

        // group bounding box spans multiple actors, rendered here separately
        val selection = selectionSystem.selectionState
        if (selection is SelectionState.GroupSelection) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            shapeRenderer.begin(ShapeType.Filled)
            shapeRenderer.color = Color(1f, 1f, 0f, 0.15f)
            drawGroupBoundingBox(selection.elements)
            shapeRenderer.end()
            Gdx.gl.glDisable(GL20.GL_BLEND)

            shapeRenderer.begin(ShapeType.Line)
            shapeRenderer.color = Color.YELLOW
            drawGroupBoundingBox(selection.elements)
            shapeRenderer.end()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

    /** Pushes current SelectionState into each ElementActor's isSelected flag. */
    private fun syncSelectionToActors() {
        val selection = selectionSystem.selectionState
        engine.actors.filterIsInstance<ElementActor>().forEach { actor ->
            actor.isSelected = when (selection) {
                is SelectionState.None           -> false
                is SelectionState.Individual     -> actor.element == selection.element
                is SelectionState.GroupSelection -> actor.element in selection.elements
            }
        }
    }

    private fun drawGroupBoundingBox(elements: List<Element>) {
        val b = groupBounds(elements)
        shapeRenderer.rect(
            b[0] - GROUP_BOX_MARGIN,
            b[1] - GROUP_BOX_MARGIN,
            b[2] - b[0] + GROUP_BOX_MARGIN * 2f,
            b[3] - b[1] + GROUP_BOX_MARGIN * 2f,
        )
    }

    /** Returns [minX, minY, maxX, maxY] enclosing all corners of the given elements. */
    private fun groupBounds(elements: List<Element>): FloatArray {
        var minX = Float.MAX_VALUE;  var minY = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE; var maxY = -Float.MAX_VALUE
        elements.forEach { el ->
            listOf(el.frontLeft, el.frontRight, el.rearLeft, el.rearRight).forEach { c ->
                if (c.x < minX) minX = c.x;  if (c.y < minY) minY = c.y
                if (c.x > maxX) maxX = c.x;  if (c.y > maxY) maxY = c.y
            }
        }
        return floatArrayOf(minX, minY, maxX, maxY)
    }
}
