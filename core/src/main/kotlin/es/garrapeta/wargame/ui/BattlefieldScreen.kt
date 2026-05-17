package es.garrapeta.wargame.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import es.garrapeta.wargame.engine.BattlefieldActor
import es.garrapeta.wargame.engine.ElementActor
import es.garrapeta.wargame.engine.GameEngine
import es.garrapeta.wargame.engine.MovementPreviewGhostActor
import es.garrapeta.wargame.logic.Element
import es.garrapeta.wargame.logic.GameState
import es.garrapeta.wargame.logic.InitialElementsFactory
import ktx.app.KtxScreen

/** Game screen: owns the render loop and routes input to the selection system. */
class WargameScreen : KtxScreen {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameState.BATTLEFIELD_WIDTH, GameState.BATTLEFIELD_HEIGHT, camera)
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
        onMovementStopped = ::onMovementFinished
    )

    // rendering
    private val engine = GameEngine()

    override fun show() {
        camera.position.set(GameState.BATTLEFIELD_WIDTH / 2f, GameState.BATTLEFIELD_HEIGHT / 2f, 0f)
        camera.update()

        engine.addActor(BattlefieldActor(
            width = GameState.BATTLEFIELD_WIDTH,
            height = GameState.BATTLEFIELD_HEIGHT
        ))

        // one ElementActor per Element — shares the same Element instance
        gameState.elements.forEach { element ->
            engine.addActor(ElementActor(element = element))
        }

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                return onTouchDown(screenX = screenX, screenY = screenY, pointer = pointer, button = button)
            }

            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                return onTouchDragged(screenX = screenX, screenY = screenY)
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                return onTouchUp(screenX = screenX, screenY = screenY, button = button)
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
        return if (selectionSystem.touchDown(worldPos = worldPos, button = button)) {
            true
        } else {
            movementSystem.touchDown(worldPos = worldPos, button = button)
        }
    }

    private fun onTouchDragged(screenX: Int, screenY: Int): Boolean {
        val worldPos: Vector2 = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
        return movementSystem.touchDragged(worldPos = worldPos)
    }

    private fun onTouchUp(screenX: Int, screenY: Int, button: Int): Boolean {
        val worldPos: Vector2 = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
        return movementSystem.touchUp(worldPos = worldPos, button = button)
    }

    private fun onKeyDown(keycode: Int): Boolean {
        return if (selectionSystem.keyDown(keycode = keycode)) {
            true
        } else {
            movementSystem.keyDown(keycode)
        }
    }

    /** Pushes selectedElements into each ElementActor's isSelected flag. */
    private fun onSelectionChanged(selected: List<Element>) {
        engine.actors.filterIsInstance<ElementActor>().forEach { actor ->
            actor.isSelected = actor.element in selected
        }
        movementSystem.stopMovement()
        if (selected.isNotEmpty()) {
            movementSystem.startMovement(selected)
        }
    }

    private fun onMovementStarted(ongoingMovement: MovementSystem.OngoingMovement) {
        engine.addActor(MovementPreviewGhostActor(ongoingMovement = ongoingMovement))
    }

    private fun onMovementFinished() {
        engine.removeActorById(MovementPreviewGhostActor.ACTOR_ID)
    }
}
