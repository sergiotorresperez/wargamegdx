package es.garrapeta.wargame.logic

fun Set<Unit>.isValidGroup(): Boolean {
    return this.all { unit ->
        val others = this.minus(unit)
        return others.isEmpty() || others.any { other ->
            unit.isContiguousWith(other)
        }
    }
}
