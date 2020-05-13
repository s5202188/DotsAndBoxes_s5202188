package org.example.student.dotsboxgame

import uk.ac.bournemouth.ap.dotsandboxeslib.*
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.Matrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableMatrix

class StudentDotsBoxGame(columns: Int, rows: Int, players: List<Player>) : AbstractDotsAndBoxesGame() {

    private val columns: Int = columns
    private val rows: Int = rows
    private var currentPlayerIndex: Int = 0

    var playerScores = mutableListOf<Int>(0, 0)

    override val players: List<Player> = players
    //TODO("You will need to get players from your constructor")
    //add a parameter to the constructor with type List<Player> (or Iterable<Player>), then you get that from the constructor parameter to set your property (and remember the players)

//    override var currentPlayer: Player = players[currentPlayerIndex]
    override val currentPlayer: Player
    get() {
        return players[currentPlayerIndex]
    }

    //TODO("Determine the current player, like keeping" + "the index into the players list")

    // NOTE: you may want to me more specific in the box type if you use that type in your class
    //override val boxes: Matrix<DotsAndBoxesGame.Box> = MutableMatrix(columns, rows, ::StudentBox)
    override val boxes: Matrix<StudentBox> = MutableMatrix<StudentBox>(columns, rows, ::StudentBox)
    //TODO("Create a matrix initialized with your own box type")

    //override val lines: SparseMatrix<DotsAndBoxesGame.Line> = MutableSparseMatrix(columns, rows, ::StudentLine)
    override val lines: Matrix<StudentLine> = MutableMatrix<StudentLine>(columns, rows, ::StudentLine)

    //TODO("Create a matrix initialized with your own line type")

    override var isFinished: Boolean = false
    //get() = TODO("Provide this getter. Note you can make it a var to do so")

    override fun playComputerTurns() {
        var current = currentPlayer
        while (current is ComputerPlayer && !isFinished) {
            current.makeMove(this)
            current = currentPlayer
        }
    }

    //old ver
//    fun play(xCo: Int, yCo: Int) {
//        if(!lines[xCo,  yCo].isDrawn) {
//            val boxDrawn = playToken(xCo, yCo)
//            if (!isFinished && (!boxDrawn && currentPlayer is PlayerComputer)) {
//                playComputerTurns()
//            }
//        }
//    }

    fun play(xCo: Int, yCo: Int) {
        lines[xCo, yCo].drawLine()
    }

    init {
        for(bx:StudentDotsBoxGame.StudentBox in boxes) {
            if(bx.validBox()) bx.setBoundingLines()
        }
    }

    /**
     * This is an inner class as it needs to refer to the game to be able to look up the correct
     * lines and boxes. Alternatively you can have a game property that does the same thing without
     * it being an inner class.
     */
    inner class StudentLine(lineX: Int, lineY: Int) : AbstractLine(lineX, lineY) {
        override var isDrawn: Boolean = false
        //get() = TODO("Provide this getter. Note you can make it a var to do so")

        //"You need to look up the correct boxes for this to work")
        //Need to add checks so that a line on the edge of the grid only has one adjacent box
        override var adjacentBoxes: Pair<StudentBox?, StudentBox?> = Pair(null, null)
            //TODO("You need to look up the correct boxes for this to work")
            get() {
                if (validLine()) {
                    if (((lineX % 2 != 0) && (lineY % 2 == 0)) && (lineY == 0)) {
                        field = Pair(null, boxes[lineX, lineY + 1])
                    } else if(((lineX % 2 != 0) && (lineY % 2 == 0)) && (lineY == (rows - 1))) {
                        field = Pair(boxes[lineX, lineY - 1], null)
                    } else if(((lineX % 2 != 0) && (lineY % 2 == 0))) {
                        field = Pair(boxes[lineX, lineY - 1], boxes[lineX, lineY + 1])
                    } else if (((lineX % 2 == 0) && (lineY % 2 != 0)) && (lineX == 0)) {
                        field = Pair(null, boxes[lineX + 1, lineY])
                    } else if(((lineX % 2 == 0) && (lineY % 2 != 0)) && (lineX == (columns - 1))) {
                        field = Pair(boxes[lineX - 1, lineY], null)
                    } else if(((lineX % 2 == 0) && (lineY % 2 != 0))) {
                        field = Pair(boxes[lineX - 1, lineY], boxes[lineX + 1, lineY])
                    }
                }
                return field
            }

        fun validLine(): Boolean {
            if ((lineX in 0 until columns) && (lineY in 0 until rows)) {
                if (((lineX % 2 == 0) && (lineY % 2 != 0)) || ((lineX % 2 != 0) && (lineY % 2 == 0))) {
                    return true
                }
            }
            return false
        }

        override fun drawLine() {
            if (!isFinished && lines[lineX, lineY].isDrawn == false) {
                var boxDrawn : Boolean = playToken(lineX, lineY)
                if (!boxDrawn && currentPlayer is PlayerComputer) {
                    playComputerTurns()
                }
            } else if (isFinished) {
                fireGameO(getPlayersScores())
                fireGameOver(getPlayersScores())
            }
            //TODO("Implement the logic for a player drawing a line. Don't forget to inform the listeners (fireGameChange, fireGameOver)")
            //NOTE read the documentation in the interface, you must also update the current player.
        }
    }

    inner class StudentBox(boxX: Int, boxY: Int) : AbstractBox(boxX, boxY) {
        //get() = TODO("Provide this getter. Note you can make it a var to do so")

        override var owningPlayer: Player? = null

        //override val boundingLines: MutableList<DotsAndBoxesGame.Line> = mutableListOf()
        override val boundingLines: MutableList<StudentLine> = mutableListOf()
        //get() = TODO("Look up the correct lines from the game outer class")

        fun setBoundingLines() {
            boundingLines.add(lines[boxX, boxY - 1])
            boundingLines.add(lines[boxX, boxY + 1])
            boundingLines.add(lines[boxX - 1, boxY])
            boundingLines.add(lines[boxX + 1, boxY])
        }

        fun checkBoundingLines(): Boolean {
            if (boundingLines.all{it.isDrawn == true}) {
                owningPlayer = currentPlayer
                return true
            } else {
                return false
            }
        }

        fun validBox(): Boolean = ((this.boxX % 2 != 0) && (this.boxY % 2 != 0))
    }

    class User(pName: String) : HumanPlayer() {
        var name: String = ""
        init { name = pName }
    }

    class PlayerComputer(cName: String): ComputerPlayer() {
        var name: String = ""
        init { name = cName }
        override fun makeMove(game: DotsAndBoxesGame) {
            val lines = game.lines.filter {
                !it.isDrawn}.random()
            lines.drawLine()
        }
    }

    fun playToken(xCo: Int, yCo: Int): Boolean {
        var boxDrawn : Boolean = false
        if(lines[xCo, yCo].validLine() && !(lines[xCo, yCo].isDrawn)) {
            lines[xCo, yCo].isDrawn = true
            if(lines[xCo, yCo].adjacentBoxes.first != null) {
                if(lines[xCo, yCo].adjacentBoxes.first!!.checkBoundingLines()) {
                    playerScores[currentPlayerIndex] ++
                    boxDrawn = true
                }
            }
            if(lines[xCo, yCo].adjacentBoxes.second != null) {
                if(lines[xCo, yCo].adjacentBoxes.second!!.checkBoundingLines()) {
                    playerScores[currentPlayerIndex] ++
                    boxDrawn = true
                }
            }
            if (!boxDrawn) {
                if(currentPlayerIndex == 0) {
                    currentPlayerIndex ++
                } else {
                    currentPlayerIndex --
                }
            }
            if (boxDrawn) {
                if (lines.all{!it.validLine() || it.isDrawn}) {
                    isFinished = true
                }
            }
            fireGameC()
            fireGameChange()
        }
        return boxDrawn
    }

    fun getBoxOwner(col: Int, row: Int): Player? {
        return boxes[col, row].owningPlayer
    }

    private fun getPlayersScores(): List<Pair<Player, Int>> {
        val pScore = mutableListOf<Pair<Player, Int>>()
        for (i in players.indices) {
            pScore[i] = Pair(players[i], playerScores[i])
        }
        return pScore
    }



    var onGameChangeListener: DotsAndBoxesGame.GameChangeListener? = null

    fun setGameChangeListener(gameChangeListenerImp: DotsAndBoxesGame.GameChangeListener) {
        onGameChangeListener = gameChangeListenerImp
    }
    fun fireGameC() {
        onGameChangeListener?.onGameChange(this)
    }

    var onGameOverListener: DotsAndBoxesGame.GameOverListener? = null

    fun setGameOverListener(gameOverListenerImp: DotsAndBoxesGame.GameOverListener) {
        onGameOverListener = gameOverListenerImp
    }

    fun fireGameO(playerScores: List<Pair<Player, Int>>) {
        onGameOverListener?.onGameOver(this, playerScores)
    }
}