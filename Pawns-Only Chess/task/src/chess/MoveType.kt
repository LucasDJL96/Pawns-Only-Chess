package chess

/** Enum class representing the types of moves that can be made */
enum class MoveType {
    /** Advance a single cell */
    SINGLE,
    /** Advance two cells */
    DOUBLE,
    /** Move one position in diagonal to capture a piece */
    CAPTURE;
}
