package es.garrapeta.wargame.logic

class GameLogicState {
    val units = mutableListOf<Unit>()

    fun addArmyUnit(unit: Unit) {
        this.units.add(unit)
    }
}
