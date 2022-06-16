package chess

/** Main function. Controls the flow of the program */
fun main() {
    println("Pawns-Only Chess")
    val gameController = GameController()
    gameController.initGame()
    gameController.playGame()
    gameController.endGame()
}

/** Produces a String identical to this except for the first character, which is capitalized */
fun String.firstUpper(): String {
    return this[0].uppercase() + this.substring(1)
}
