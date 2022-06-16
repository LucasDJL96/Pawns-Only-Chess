package chess

import kotlin.math.abs

/** Class representing the board for the game */
class GameBoard {

    /**
     * Link between to cells to allow passing capture. First component is the cell
     * jumped over. Second component is the landing cell
     */
    private var captureLink: Pair<Cell, Cell>? = null

    /** List with the cells on this board */
    private val cells = buildList {
        for (i in 0 until 8) {
            add(buildList {
                for (j in 0 until 8) {
                    add(Cell(i, j))
                }
            })
        }
    }

    /**
     * Puts the pawns of a player on this board
     *
     * @param player the player
     */
    fun putPawns(player: Player) {
        val row = if (player.isFirst) 1 else 6
        for (j in 0 until 8) {
            val pawn = player.pawns[j]
            val cell = cells[row][j]
            cell.putPawn(pawn)
        }
    }

    /**
     * Checks whether a move is legal and if so makes the move
     *
     * @param player the player making the move
     * @param fromCell the where the move starts
     * @param toCell the cell where the move ends
     *
     * @throws IllegalPieceException if there's no pawn in fromCell or if the pawn
     * doesn't belong to the player
     * @throws IllegalMoveException if the move is illegal
     */
    fun checkAndMovePawn(player: Player, fromCell: Cell, toCell: Cell) {
        val moveType = checkMove(player, fromCell, toCell)
        val pawn = fromCell.pawn!!
        when (moveType) {
            MoveType.DOUBLE -> {
                val midRow = (fromCell.row + toCell.row) / 2
                captureLink = cells[midRow][fromCell.col] to toCell
            }
            MoveType.CAPTURE -> captureAtCell(player, toCell)
            MoveType.SINGLE -> {/*Nothing special*/}
        }
        movePawn(pawn, fromCell, toCell)
    }

    /**
     * Checks if a move is legal
     *
     * @param player the player making the move
     * @param fromCell the where the move starts
     * @param toCell the cell where the move ends
     *
     * @return MoveType the type of move
     *
     * @throws IllegalPieceException if there's no pawn in fromCell or if the pawn
     * doesn't belong to the player
     * @throws IllegalMoveException if the move is illegal
     */
    private fun checkMove(player: Player, fromCell: Cell, toCell: Cell): MoveType {
        if (fromCell.pawn == null || !fromCell.pawn!!.belongsTo(player)) {
            throw IllegalPieceException()
        }
        val moveType = determineMoveType(player, fromCell, toCell)
        if (moveType != MoveType.CAPTURE && toCell.pawn != null) {
            throw IllegalMoveException()
        }
        val pawn = fromCell.pawn!!
        if (moveType == MoveType.DOUBLE && pawn.hasMoved) {
            throw IllegalMoveException()
        }
        val midRow = (fromCell.row + toCell.row) / 2
        if (moveType == MoveType.DOUBLE &&
                cells[midRow][toCell.col].pawn != null) {
            throw IllegalMoveException()
        }
        if (moveType == MoveType.CAPTURE && toCell.pawn == null && toCell != captureLink?.first) {
            throw IllegalMoveException()
        }
        return moveType
    }

    /**
     * Determines the type of move
     *
     * @param player the player making the move
     * @param fromCell the where the move starts
     * @param toCell the cell where the move ends
     *
     * @return MoveType the type of move
     *
     * @throws IllegalMoveException if there is no move type exists for the move
     */
    private fun determineMoveType(player: Player, fromCell: Cell, toCell: Cell): MoveType {
        val rowDiff =
            if (player.isFirst) toCell.row - fromCell.row
            else fromCell.row - toCell.row
        return if (fromCell.col == toCell.col) {
            when (rowDiff) {
                1 -> MoveType.SINGLE
                2 -> MoveType.DOUBLE
                else -> throw IllegalMoveException()
            }
        } else if (abs(fromCell.col - toCell.col) == 1) {
            if (rowDiff == 1) {
                MoveType.CAPTURE
            } else {
                throw IllegalMoveException()
            }
        } else {
            throw IllegalMoveException()
        }
    }

    /**
     * Moves a pawn. There should be no pawn in the destination cell. If the move
     * is a capture move, {@see #captureAtCell} should be called first
     *
     * @param pawn the pawn being moved
     * @param fromCell the where the move starts
     * @param toCell the cell where the move ends
     *
     * @throws IllegalStateException if there's already a pawn in toCell
     */
    private fun movePawn(pawn: Pawn, fromCell: Cell,  toCell: Cell) {
        if (toCell.pawn != null) throw IllegalStateException("Can't move to another pawn")
        pawn.setMoved()
        fromCell.empty()
        toCell.putPawn(pawn)
    }

    /**
     * Captures a pawn from a cell
     *
     * @param player the player making the capture
     * @param cell the cell at which the player is moving
     *
     * @throws IllegalStateException if there is no pawn in that cell and there's no
     * capture link on that cell
     */
    private fun captureAtCell(player: Player, cell: Cell) {
        val cellToCapture =
            if (cell.pawn != null) cell
            else if (cell == captureLink?.first) captureLink!!.second
            else throw IllegalStateException()
        val pawn = cellToCapture.pawn!!
        if (pawn.belongsTo(player)) {
            throw IllegalMoveException()
        }
        pawn.captured = true
        cellToCapture.empty()
        player.addCapture()
    }

    /** Prints the state of this board */
    fun printState() {
        print("  ")
        for (j in 'a'..'h') print("+---")
        println("+")
        for (i in 8 downTo 1) {
            print("$i ")

            for (j in 'a'..'h') {
                val cell = cellFromString("$j$i")
                val char =
                    if (cell.pawn == null) ' '
                    else cell.pawn!!.symbol
                print("| $char ")
            }
            println("|")
            print("  ")
            for (j in 'a'..'h') print("+---")
            println("+")
        }
        print("  ")
        for (j in 'a'..'h') print("  $j ")
        println(" ")
    }

    /**
     * Parses a move from a String
     *
     * @param input the String
     *
     * @return Pair<Cell, Cell> with the starting cell on the first component and
     * the destination cell on the second
     *
     * @throws IllegalPositionException if the input doesn't correspond to a move
     */
    fun moveFromString(input: String): Pair<Cell, Cell> {
        if (!input.matches(allowedMovesRegex)) throw IllegalPositionException()
        val from = input.substring(0..1)
        val to = input.substring(2..3)
        val fromCell = cellFromString(from)
        val toCell = cellFromString(to)
        return Pair(fromCell, toCell)
    }

    /**
     * Gets a cell on this board from a String
     *
     * @param str the string.
     *
     * @return Cell the cell corresponding to the string
     *
     * @throws IllegalPositionException if the string doesn't represent a valid cell
     */
    private fun cellFromString(str: String): Cell {
        if (str.length != 2) throw IllegalPositionException()
        val col = cols.indexOf(str[0])
        if (col == -1) throw IllegalPositionException()
        val row = try {
            str[1].digitToInt() - 1
        } catch (e: NumberFormatException) {
            throw IllegalPositionException()
        }
        if (row !in 0..7) throw IllegalPositionException()
        return cells[row][col]
    }

    /**
     * Clears the capture link after it expires. This method should be called at
     * the beginning of a player's turn to clear the capture link they may have set
     * on their previous turn. It also clears any capture links plinking to an empty
     * cell
     *
     * @param player the player who set the capture link
     */
    fun clearCaptureLink(player: Player) {
        if (captureLink != null && (captureLink!!.second.pawn == null
                || captureLink!!.second.pawn!!.belongsTo(player))) {
            captureLink = null
        }
    }

    /**
     * Checks if a pawn from a player has made it to the last row
     *
     * @param player the player
     *
     * @return Boolean
     */
    fun checkIfPawnOnLastRow(player: Player): Boolean {
        val row = if (player.isFirst) 7 else 0
        return cells[row].any {
            it.pawn != null && it.pawn!!.belongsTo(player)
        }
    }

    /**
     * Checks whether a player has any moves left
     *
     * @param player the player
     *
     * @return Boolean
     */
    fun playerHasMoves(player: Player): Boolean {
        val playerCells = cells.flatten().filter {
                it.pawn != null && it.pawn!!.belongsTo(player)
            }
        return playerCells.any { hasMovesLeft(it, player) }
    }

    /**
     * Checks whether a pawn on a cell of a player has moves left
     *
     * @param cell the cell where the pawn is
     * @param player the player owning the pawn
     *
     * @return Boolean
     *
     * @throws IllegalStateException if there is no pawn of the player at the cell
     */
    private fun hasMovesLeft(cell: Cell, player: Player): Boolean {
        if (cell.pawn == null || !cell.pawn!!.belongsTo(player)) throw IllegalStateException()
        val sign = if (player.isFirst) 1 else -1
        val singleCell = cells[cell.row + sign][cell.col]
        if (singleCell.pawn == null) return true
        // Since double step requires both cells in front to be empty
        // it has already been covered
        val diagonalCell1 = if (cell.col + 1 <= 7) {
             cells[cell.row + sign][cell.col + 1]
        } else null
        if (
            diagonalCell1 != null &&
            diagonalCell1 == captureLink?.first ||
            diagonalCell1?.pawn != null &&
            !diagonalCell1.pawn!!.belongsTo(player)
        ) return true
        val diagonalCell2 = if (cell.col - 1 >= 0) {
            cells[cell.row + sign][cell.col - 1]
        } else null
        if (
            diagonalCell2 != null &&
            diagonalCell2 == captureLink?.first ||
            diagonalCell2?.pawn != null &&
            !diagonalCell2.pawn!!.belongsTo(player)
        ) return true
        return false
    }

    companion object {
        /** Regex for parsing moves */
        val allowedMovesRegex = """([a-h][1-8]){2}""".toRegex()

        /** String containing the names of the columns in order */
        private const val cols = "abcdefgh"
    }

    /**
     * Data class representing a cell og this board
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    data class Cell(val row: Int, val col: Int) {
        /** The pawn on this cell. Nullable */
        var pawn: Pawn? = null

        /**
         * Puts a pawn on this cell
         * @param pawn the pawn
         */
        fun putPawn(pawn: Pawn) {
            this.pawn = pawn
        }

        /** Removes the pawn from this cell */
        fun empty() {
            this.pawn = null
        }

        /** Returns a string representation of this cell in the form [a-h][1-8] */
        override fun toString(): String {
            return "${cols[col]}${row + 1}"
        }

    }

}
