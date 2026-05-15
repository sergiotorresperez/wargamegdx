package es.garrapeta.wargame

import es.garrapeta.wargame.ui.WargameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

/** Application entry point; creates and launches the main game screen. */
class WarGame : KtxGame<KtxScreen>() {

    override fun create() {
        addScreen(WargameScreen())
        setScreen<WargameScreen>()
    }
}
