package chess

/**
 * Data class representing a pawn
 * @param symbol represents the color of the pawn. W for white and B for black
 */
data class Pawn(val symbol: Char) {
    /** Whether the pawn has been moved from its initial position */
    var hasMoved = false

    /** Whether the pawn has been captured */
    var captured = false

    /** Sets the pawn has having been moved from its initial position */
    fun setMoved() {
        hasMoved = true
    }

    /**
     * Checks if this pawn belongs to a player
     * @param player the player
     *
     * @return Boolean
     */
    fun belongsTo(player: Player): Boolean {
        return this in player.pawns
    }
}
