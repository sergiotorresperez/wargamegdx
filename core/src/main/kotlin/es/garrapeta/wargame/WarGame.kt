package es.garrapeta.wargame

import ktx.app.KtxGame
import ktx.app.KtxScreen
import es.garrapeta.wargame.screen.WargameScreen

class WarGame : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(WargameScreen())
        setScreen<WargameScreen>()
    }
}
