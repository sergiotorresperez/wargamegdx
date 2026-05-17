package es.garrapeta.wargame.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import ktx.actors.onClick

/** HUD overlay with Stage and Undo button. */
class HudOverlay(
    private var onUndoMovementClicked: (() -> Unit)
): Stage(ScreenViewport()) {

    private val rootTable = VisTable()
    private val undoButton: VisTextButton = VisTextButton("Undo")

    init {
//        rootTable.debug()
        rootTable.setFillParent(true)
        addActor(rootTable)
        undoButton.onClick { onUndoMovementClicked.invoke() }
        rootTable.add(undoButton).expand().bottom().right()
        setUndoMovementIsVisible(false)
    }

    fun setUndoMovementIsVisible(isVisible: Boolean) {
        undoButton.isVisible = isVisible
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
}
