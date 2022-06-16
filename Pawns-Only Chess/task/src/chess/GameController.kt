package chess

/** Class representing a controller for the state of the game */
class GameController() {

    /**
     * A CyclicPair representing the player of this game. The first player starts
     * in the first component
     */
    private val players: CyclicPair<Player> by lazy {
        println("First Player's name:")
        val name1 = readln()
        println("Second Player's name:")
        val name2 = readln()
        val player1 = Player(name1, isFirst = true)
        val player2 = Player(name2, isFirst = false)
        CyclicPair(player1, player2)
    }

    /** THe board for this game */
    private val board = GameBoard()

    /** Whether a player wants to eit the game */
    private var exitFlag = false

    /** The winner of this game. Nullable */
    private var winner: Player? = null

    /** Initializes this game */
    fun initGame() {
        players
        board.putPawns(players.current())
        board.putPawns(players.peekNext())
    }

    /** Prints the current state of the game */
    private fun printGameState() = board.printState()

    /** Starts a round of the game */
    private fun startRound() {
        val currentPlayer = players.current()
        board.clearCaptureLink(currentPlayer)
    }

    /** Controls the flow for a round of this game */
    private fun playRound() {
        while (true) {
            val currentPlayer = players.current()
            println("${currentPlayer.name}'s turn:")
            val input = readln()
            if (input == "exit") {
                exitFlag = true
                break
            } else if (!input.matches(GameBoard.allowedMovesRegex)) {
                println("Invalid Input")
                continue
            }
            val (from, to) = board.moveFromString(input)
            try {
                board.checkAndMovePawn(currentPlayer, from, to)
                break
            } catch (e: IllegalPieceException) {
                println("No ${currentPlayer.color.firstUpper()} pawn at $from")
                continue
            } catch (e: IllegalMoveException) {
                println("Invalid Input")
                continue
            }
        }
    }

    /** Finishes a round of this game */
    private fun finishRound() {
        players.next()
    }

    /** Controls the flow of the game */
    fun playGame() {
        do {
            printGameState()
            startRound()
            playRound()
            finishRound()
        } while (!isGameFinished())
    }

    /** Checks whether the game is finished */
    private fun isGameFinished(): Boolean {
        if (exitFlag) return true
        val lastPlayer = players.last()
        if (lastPlayer.capturedPawns == 8 || board.checkIfPawnOnLastRow(lastPlayer)) {
            winner = lastPlayer
            return true
        }
        val nextPlayer = players.current()
        if (!board.playerHasMoves(nextPlayer)) {
            return true
        }
        return false
    }

    /** Ends the game */
    fun endGame() {
        if (!exitFlag) {
            printGameState()
            if (winner != null) {
                println("${winner!!.color} Wins!")
            } else {
                println("Stalemate!")
            }
        }
        println("Bye!")
    }

}
