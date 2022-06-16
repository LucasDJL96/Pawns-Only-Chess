package chess

/**
 * Data class representing a player
 *
 * @param name the name of the player
 * @param isFirst whether this player moves first
 */
data class Player(val name: String, val isFirst: Boolean) {

    /**
     * Represents the color of this player's pawns: white if this player moves first
     * and black otherwise
     */
    val color: String = if (isFirst) "white" else "black"

    /** List with the pawns of this player */
    val pawns = List(8) { Pawn(if (isFirst) 'W' else 'B') }

    /** How many pawns this player has captures */
    var capturedPawns = 0

    /** Increases the number of captured pawns by one */
    fun addCapture() {
        capturedPawns++
    }

}
