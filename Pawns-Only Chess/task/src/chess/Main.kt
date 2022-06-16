package chess

fun main() {
    println("Pawns-Only Chess")
    val (name1, name2) = getNames()
    val player1 = Player(name1, isFirst = true)
    val player2 = Player(name2, isFirst = false)
    val players = CyclicPair(player1, player2)
    val board = GameBoard()
    val gameController = GameController(player1, player2, board)
    board.printState()
    while (true) {
        val currentPlayer = players.current()
        println("${currentPlayer.name}'s turn:")
        val input = readln()
        if (input == "exit") {
            break
        } else if (!input.matches(GameBoard.allowedMoves)) {
            println("Invalid Input")
            continue
        }
        val (from, to) = board.moveFromString(input)
        try {
            board.checkAndMovePawn(currentPlayer, from, to)
        } catch (e: IllegalPieceException) {
            println("No ${currentPlayer.color.firstUpper()} pawn at $from")
            continue
        } catch (e: IllegalMoveException) {
            println("Invalid Input")
            continue
        }
        board.printState()
        if (currentPlayer.capturedPawns == 8 || board.checkIfPawnOnLastRow(currentPlayer)) {
            println("${currentPlayer.color} Wins!")
            break
        }
        val nextPlayer = players.next()
        board.clearCaptureFlags(nextPlayer)
        if (!board.checkIfPlayerHasMoves(nextPlayer)) {
            println("Stalemate!")
            break
        }
    }
    println("Bye!")
}

fun getNames(): Pair<String, String> {
    println("First Player's name:")
    val name1 = readln()
    println("Second Player's name:")
    val name2 = readln()
    return Pair(name1, name2)
}

fun String.firstUpper(): String {
    return this[0].uppercase() + this.substring(1)
}
