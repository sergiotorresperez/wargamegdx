package es.garrapeta.wargame.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import es.garrapeta.wargame.engine.ElementActor
import es.garrapeta.wargame.engine.GameEngine
import es.garrapeta.wargame.engine.MovementPreviewGhostActor
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.GameState
import es.garrapeta.wargame.logic.InitialElementsFactory
import ktx.app.KtxScreen

private const val BATTLEFIELD_WIDTH: Float = 36f
private const val BATTLEFIELD_HEIGHT: Float = 24f

/** Game screen: owns the render loop and routes input to the selection system. */
class WargameScreen : KtxScreen {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(BATTLEFIELD_WIDTH, BATTLEFIELD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()

    // logic
    private val gameState = GameState(
        elements = InitialElementsFactory().createElement()
    )
    private val selectionSystem = ElementSelectionSystem(
        gameState = gameState,
        onSelectionChanged = ::onSelectionChanged,
    )
    private val movementSystem = MovementSystem(
        gameState = gameState,
        onMovementStarted = ::onMovementStarted,
        onMovementFinished = ::onMovementFinished
    )

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
                return onTouchDown(screenX, screenY, pointer, button)
            }

            override fun keyDown(keycode: Int): Boolean {
                return onKeyDown(keycode = keycode)
            }
        }
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined

        // element bodies, borders, chevrons and per-element selection outline — via GameEngine
        engine.render(shapeRenderer = shapeRenderer, delta = delta)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

    private fun onTouchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val worldPos = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
        return if (movementSystem.touchDown(worldPos, button)) {
            true
        } else {
            selectionSystem.touchDown(worldPos = worldPos, button = button)
        }
    }

    private fun onKeyDown(keycode: Int): Boolean {
        return if (movementSystem.keyDown(keycode)) {
            true
        } else {
            selectionSystem.keyDown(keyDown = keycode)
        }
    }

    /** Pushes selectedElements into each ElementActor's isSelected flag. */
    private fun onSelectionChanged(selected: List<Element>) {
        engine.actors.filterIsInstance<ElementActor>().forEach { actor ->
            actor.isSelected = actor.element in selected
        }
        movementSystem.startMovement(selected)
    }

    private fun onMovementStarted(ongoingMovement: MovementSystem.OngoingMovement) {
        engine.addActor(MovementPreviewGhostActor(ongoingMovement = ongoingMovement))
    }

    private fun onMovementFinished() {
        engine.removeActorById(MovementPreviewGhostActor.ACTOR_ID)
    }
}
