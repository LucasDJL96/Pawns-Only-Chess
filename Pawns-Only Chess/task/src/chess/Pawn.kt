package chess

data class Pawn(val symbol: Char) {
    var hasMoved = false

    var captured = false

    fun setMoved() {
        hasMoved = true
    }

    fun belongsTo(player: Player): Boolean {
        return this in player.pawns
    }
}
