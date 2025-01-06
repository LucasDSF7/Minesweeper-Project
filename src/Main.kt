class MinesweeperBoard(private val boardSize: Int = 9, private val nBombs: Int = 20) {

    // board has all clues and bombs displayed
    private var board: MutableList<MutableList<String>> = mutableListOf()
    // playerBoard has clues and bombs to be explored
    private var playerBoard: MutableList<MutableList<String>> = mutableListOf()

    init {
        generateEmptyBoard()
        playerBoard = board.map { it.toMutableList() }.toMutableList()
    }

    private fun generateEmptyBoard() {
        repeat(boardSize) {
            val row = mutableListOf<String>()
            repeat(boardSize) { row.add(".") }
            board += row
        }
    }

    private fun randomBombsPlacements(x: Int, y: Int) {
        // range.remove removes the possibility of placing a bomb beside the first player move
        val range = (0 until(boardSize * boardSize)).toMutableList()
        for (i in y - 1 .. y + 1) {
            for (j in x - 1 .. x + 1) {
                range.remove(i * boardSize + j)
            }
        }
        val bombsPlace = range.shuffled().take(nBombs)
        for (n in bombsPlace) {
            board[n / boardSize][n % boardSize] = "X"
        }
    }

    private fun cluesPlacements() {
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (board[i][j] == ".") {
                    populateClues(i, j)
                }
            }
        }
    }

    private fun populateClues(x: Int, y: Int) {
        var countBombs = 0
        for (i in x - 1..x + 1) {
            for (j in y - 1..y + 1) {
                try {
                    if (board[i][j] == "X") countBombs += 1
                }
                catch (e: IndexOutOfBoundsException) {
                    continue
                }
            }
        }
        if (countBombs > 0) board[x][y] = countBombs.toString()
    }

    private fun displayBoard(boardPrint: MutableList<MutableList<String>> = playerBoard) {
        println(" |${(1..boardSize).joinToString(" ")}|\n")
        println("-|${List(boardSize) {"-"}.joinToString(" ")}|\n")
        for (i in 0 until boardSize) {
            println("${i + 1}|${ boardPrint[i].joinToString(separator = " ") }|\n")
        }
        println("-|${List(boardSize) {"-"}.joinToString(" ")}|\n")
        println(" |${(1..boardSize).joinToString(" ")}|\n")
    }

    private fun playerInput(): List<String> {
        println("Set/unset mine marks or claim a cell as free: ")
        val input = readln().split(" ")
        try {
            input[0].toInt()
            input[1].toInt()
            if (input[2] !in setOf("free", "mine")) {
                println("Invalid command. Should be free or mine")
                return listOf("invalid", "invalid", "invalid")
            }
        }
        catch (e: NumberFormatException) {
            println("Should inform numbers as coordinates")
            return listOf("invalid", "invalid", "invalid")
        }
        catch (e: IndexOutOfBoundsException) {
            println("Not enough inputs was passed")
            return listOf("invalid", "invalid", "invalid")
        }
        if (input[0].toInt() > boardSize || input[1].toInt() > boardSize) {
            println("One of the coordinates is bigger than the board size")
            return listOf("invalid", "invalid", "invalid")
        }
        return input
    }

    private fun floodBoard(x: Int, y: Int)  {
        // floodBoard use Breadth First Search to uncover empty cells
        val start = listOf(x, y)
        val queue = mutableListOf(start)
        val visited = mutableSetOf(start)
        while (queue.isNotEmpty()) {
            val (i, j) = queue.removeFirst()
            for (move in listOf(
                listOf(i + 1, j), listOf(i - 1, j), listOf(i, j + 1), listOf(i, j - 1),
                listOf(i + 1, j + 1), listOf(i + 1, j - 1), listOf(i - 1, j + 1), listOf(i - 1, j - 1))) {
                if (isValidFlood(visited, move)) {
                    val clue = board[move[1]][move[0]]
                    visited.add(move)
                    if (clue == ".") {
                        queue.add(move)
                        playerBoard[move[1]][move[0]] = "/"
                    }
                    else playerBoard[move[1]][move[0]] = clue
                }
            }
        }
    }

    private fun isValidFlood(visited: Set<List<Int>>, move: List<Int>): Boolean {
        val (x, y) = move
        return (move !in visited && 0 <= x && x < boardSize && 0 <= y && y < boardSize && board[y][x] != "X")
    }

    private fun updatePlayerBoard(x: Int, y: Int, command: String) {
        if (command == "mine" && playerBoard[y][x] in listOf(".", "*")) {
            playerBoard[y][x] = if (playerBoard[y][x] == "*") "."
            else "*"
            return
        }
        if (board[y][x] != ".") playerBoard[y][x] = board[y][x]
        else {
            floodBoard(x, y)
            playerBoard[y][x] = "/"
        }
    }

    private fun isEndGame(): Boolean {
        if (playerBoard.any { it.contains("X") }) {
            println("You stepped on a mine and failed!")
            return true
        }
        var correctMarks = 0
        var incorrectMarks = 0
        var unexploredBoard = 0
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (playerBoard[i][j] == "*" && board[i][j] == "X") correctMarks += 1
                if (playerBoard[i][j] == "*" && board[i][j] != "X") incorrectMarks += 1
                if (playerBoard[i][j] == "." && board[i][j] != "X") unexploredBoard += 1
            }
        }
        if ((correctMarks == nBombs && incorrectMarks == 0) || unexploredBoard == 0) {
            println("Congratulations! You found all the mines!")
            return true
        }
        return false
    }

    fun runningGame() {
        var firstMove = true
        displayBoard()
        while (!isEndGame()) {
            val (x, y, command) = playerInput()
            if (x == "invalid") continue
            if (firstMove && command == "free") {
                firstMove = false
                randomBombsPlacements(x.toInt() - 1, y.toInt() - 1)
                cluesPlacements()
            }
            updatePlayerBoard(x.toInt() - 1, y.toInt() - 1, command)
            displayBoard()
        }
    }

}

fun main() {
    var numberBombs: Int
    while (true) {
        print("How many mines do you want on the field? ")
        try {numberBombs = readln().toInt()}
        catch (e: NumberFormatException) {
            println("Inform a integer for the number of bombs")
            continue
        }
        break
    }

    val board = MinesweeperBoard(nBombs = numberBombs)
    board.runningGame()
}
