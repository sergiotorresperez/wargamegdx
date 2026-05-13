package es.garrapeta.wargame.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.app.KtxInputAdapter
import ktx.graphics.use
import es.garrapeta.wargame.engine.GameEngine
import es.garrapeta.wargame.actor.UnitActor
import es.garrapeta.wargame.logic.Unit
import es.garrapeta.wargame.logic.GameLogicState
import es.garrapeta.wargame.logic.InitialUnitsFactory

class WargameScreen : KtxScreen, KtxInputAdapter {
    private val worldWidth = 36f    // pulgadas
    private val worldHeight = 24f   // pulgadas

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(worldWidth, worldHeight, camera)
    private val shapeRenderer = ShapeRenderer()
    private val engine = GameEngine(shapeRenderer)

    private val gameLogicState = GameLogicState()

    private val selectionSystem = ArmyUnitSelectionSystem(
        gameLogicState = gameLogicState,
        onUnitsSelected = ::onArmyUnistSelected
    )

    private val movementSystem = ArmyUnitMovementSystem(gameLogicState)

    override fun show() {
        // Crear actores y mapear por unit id
        for (armyUnit in InitialUnitsFactory().createUnits()) {
            gameLogicState.addArmyUnit(armyUnit)
            engine.addActor(UnitActor(armyUnit))
        }

        // Setup cámara y viewport
        camera.position.set(worldWidth / 2f, worldHeight / 2f, 0f)
        camera.update()

        // Registrar input handler
        Gdx.input.inputProcessor = this
    }

    override fun render(delta: Float) {
        // Update movimiento
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            movementSystem.onMoveForward(delta, selectionSystem.selectedUnits)
        }

        // Limpiar pantalla
        ScreenUtils.clear(0f, 0f, 0f, 1f)

        // Update cámara
        camera.update()

        // Renderizar
        shapeRenderer.use(ShapeType.Line, camera) {
            shapeRenderer.color = Color.WHITE
            engine.render()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

    private fun onArmyUnistSelected(selectedUnits: Set<Unit>) {
        engine.getActors(UnitActor::class.java).forEach { actor ->
            actor.selected = actor.unit in selectedUnits
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button != Input.Buttons.LEFT) return false

        val worldCoords = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val isGroupSelection = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
        selectionSystem.onUnitSelectionClick(worldCoords.x, worldCoords.y, isGroupSelection)

        return true
    }
}
