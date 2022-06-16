package chess

import java.util.*
import kotlin.math.abs

class GameBoard {

    var captureFlag: Pair<Cell, Cell>? = null

    private val cells = buildList {
        for (i in 0 until 8) {
            add(buildList {
                for (j in 0 until 8) {
                    add(Cell(i, j))
                }
            })
        }
    }

    fun putPawns(player: Player) {
        val row = if (player.isFirst) 1 else 6
        for (j in 0 until 8) {
            val pawn = player.pawns[j]
            val cell = cells[row][j]
            cell.putPawn(pawn)
        }
    }

    fun checkAndMovePawn(player: Player, fromCell: Cell, toCell: Cell) {
        val moveType = checkMove(player, fromCell, toCell)
        val pawn = fromCell.pawn!!
        when (moveType) {
            MoveType.DOUBLE -> {
                val midRow = (fromCell.row + toCell.row) / 2
                captureFlag = cells[midRow][fromCell.col] to toCell
            }
            MoveType.CAPTURE -> captureAtCell(player, toCell)
            MoveType.SINGLE -> {/*Nothing special*/}
        }
        movePawn(pawn, fromCell, toCell)
    }

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
        if (moveType == MoveType.CAPTURE && toCell.pawn == null && toCell != captureFlag?.first) {
            throw IllegalMoveException()
        }
        return moveType
    }

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

    private fun movePawn(pawn: Pawn, fromCell: Cell,  toCell: Cell) {
        if (toCell.pawn != null) throw IllegalStateException("Can't move to another pawn")
        pawn.setMoved()
        fromCell.empty()
        toCell.putPawn(pawn)
    }

    private fun captureAtCell(player: Player, cell: Cell) {
        val cellToCapture =
            if (cell.pawn != null) cell
            else if (cell == captureFlag?.first) captureFlag!!.second
            else throw IllegalStateException()
        val pawn = cellToCapture.pawn!!
        if (pawn.belongsTo(player)) {
            throw IllegalMoveException()
        }
        pawn.captured = true
        cellToCapture.empty()
        player.addCapture()
    }

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

    private fun cellFromString(str: String): Cell {
        if (str.length != 2) throw IllegalArgumentException("Not a valid position")
        val col = cols.indexOf(str[0])
        if (col == -1) throw IllegalArgumentException("Not a valid position")
        val row = try {
            str[1].digitToInt() - 1
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Not a valid position")
        }
        return cells[row][col]
    }

    fun moveFromString(input: String): Pair<Cell, Cell> {
        if (!input.matches(allowedMoves)) throw IllegalArgumentException()
        val from = input.substring(0..1)
        val to = input.substring(2..3)
        val fromCell = cellFromString(from)
        val toCell = cellFromString(to)
        return Pair(fromCell, toCell)
    }

    fun clearCaptureFlags(player: Player) {
        if (captureFlag != null && (captureFlag!!.second.pawn == null
                || captureFlag!!.second.pawn!!.belongsTo(player))) {
            captureFlag = null
        }
    }

    fun checkIfPawnOnLastRow(player: Player): Boolean {
        val row = if (player.isFirst) 7 else 0
        return cells[row].any {
            it.pawn != null && it.pawn!!.belongsTo(player)
        }
    }

    fun checkIfPlayerHasMoves(player: Player): Boolean {
        val playerCells = cells.flatten().filter {
                it.pawn != null && it.pawn!!.belongsTo(player)
            }
        return playerCells.any { hasMovesLeft(it, player) }
    }

    private fun hasMovesLeft(cell: Cell, player: Player): Boolean {
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
            diagonalCell1 == captureFlag?.first ||
            diagonalCell1?.pawn != null &&
            !diagonalCell1.pawn!!.belongsTo(player)
        ) return true
        val diagonalCell2 = if (cell.col - 1 >= 0) {
            cells[cell.row + sign][cell.col - 1]
        } else null
        if (
            diagonalCell2 != null &&
            diagonalCell2 == captureFlag?.first ||
            diagonalCell2?.pawn != null &&
            !diagonalCell2.pawn!!.belongsTo(player)
        ) return true
        return false
    }

    companion object {
        val allowedMoves = """([a-h][1-8]){2}""".toRegex()

        private const val cols = "abcdefgh"
    }

    data class Cell(val row: Int, val col: Int) {
        var pawn: Pawn? = null

        fun putPawn(pawn: Pawn) {
            this.pawn = pawn
        }

        fun empty() {
            this.pawn = null
        }

        override fun toString(): String {
            return "${cols[col]}${row + 1}"
        }

    }

    enum class MoveType {
        SINGLE, DOUBLE, CAPTURE;
    }

}
