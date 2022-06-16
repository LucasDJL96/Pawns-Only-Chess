package chess

data class Player(val name: String, val isFirst: Boolean) {

    val color: String = if (isFirst) "white" else "black"

    val pawns = List<Pawn>(8) { Pawn(if (isFirst) 'W' else 'B') }

    var capturedPawns = 0

    fun addCapture() {
        capturedPawns++
    }

}
