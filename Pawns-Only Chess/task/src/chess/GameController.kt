package chess

class GameController(val player1: Player, val player2: Player, val board: GameBoard) {

    val turnDecider = CyclicPair(player1, player2)

    init {
        board.putPawns(player1)
        board.putPawns(player2)
    }
}
