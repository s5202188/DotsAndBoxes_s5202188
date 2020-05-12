package org.example.student.dotsboxgame

import uk.ac.bournemouth.ap.dotsandboxeslib.*
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.Matrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableMatrix
import java.util.*

class StudentDotsBoxGame(columns: Int, rows: Int, players: List<Player>) : AbstractDotsAndBoxesGame() {

    private var currentPlayerIndex: Int = 0
    private val columns: Int = columns
    private val rows: Int = rows

    var playerScores = mutableListOf<Int>(0, 0)

    override val players: List<Player> = players
    //TODO("You will need to get players from your constructor")
    //add a parameter to the constructor with type List<Player> (or Iterable<Player>), then you get that from the constructor parameter to set your property (and remember the players)

    override var currentPlayer: Player = players[currentPlayerIndex]
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

    fun play(xCo: Int, yCo: Int) {
        if(!lines[xCo,  yCo].isDrawn) {
            val boxDrawn = playToken(xCo, yCo)
            if (!isFinished && (!boxDrawn && currentPlayer is PlayerComputer)) {
                playComputerTurns()
            }
        }
    }

    fun playz(xCo: Int, yCo: Int) {
//        if(!lines[xCo,  yCo].isDrawn) {
//            val boxDrawn = playToken(xCo, yCo)
//            if (!isFinished && (!boxDrawn && currentPlayer is PlayerComputer)) {
//                playComputerTurns()
//            }
//        }
        if(!lines[xCo,  yCo].isDrawn) {
            lines[xCo, yCo].drawLine()
        }
    }

    init {
        for(bx in boxes) {
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
                    if (((this.lineX % 2 != 0) && (this.lineY % 2 == 0)) && (this.lineY == 0)) {
                        //box below the line
                        field = Pair(null, boxes[this.lineX, this.lineY + 1])
                    } else if(((this.lineX % 2 != 0) && (this.lineY % 2 == 0)) && (this.lineY == (rows - 1))) {
                        //box above the line
                        field = Pair(boxes[this.lineX, this.lineY - 1], null)
                    } else if(((this.lineX % 2 != 0) && (this.lineY % 2 == 0))) {
                        //boxes below and above the line
                        field = Pair(boxes[this.lineX, this.lineY - 1], boxes[this.lineX, this.lineY + 1])
                    } else if (((this.lineX % 2 == 0) && (this.lineY % 2 != 0)) && (this.lineX == 0)) {
                        //box to right of the line
                        field = Pair(null, boxes[this.lineX + 1, this.lineY])
                    } else if(((this.lineX % 2 == 0) && (this.lineY % 2 != 0)) && (this.lineX == (columns - 1))) {
                        //box to left of the line
                        field = Pair(boxes[this.lineX - 1, this.lineY], null)
                    } else if(((this.lineX % 2 == 0) && (this.lineY % 2 != 0))) {
                        //boxes to  left and right of the line
                        field = Pair(boxes[this.lineX - 1, this.lineY], boxes[this.lineX + 1, this.lineY])
                    }
                }
                return field
            }

        fun validLine(): Boolean {
            if((this.lineX in 0 until columns) && (this.lineY in 0 until rows)) {
                return (((this.lineX % 2 != 0) && (this.lineY % 2 == 0)) || ((this.lineX % 2 == 0) && (this.lineY % 2 != 0)))
            }
            return false
        }

        override fun drawLine() {
            if(isDrawn) throw IllegalStateException("Line already drawn")

            val boxDrawn = playToken(lineX, lineY)
            if (!isFinished && (!boxDrawn && currentPlayer is PlayerComputer)) {
                playComputerTurns()
            }
//            isDrawn = true
//            fireGameC()
//            fireGameChange()
            //TODO("Implement the logic for a player drawing a line. Don't forget to inform the listeners (fireGameChange, fireGameOver)")
            //NOTE read the documentation in the interface, you must also update the current player.
        }
    }

    inner class StudentBox(boxX: Int, boxY: Int) : AbstractBox(boxX, boxY) {
        //get() = TODO("Provide this getter. Note you can make it a var to do so")

        override var owningPlayer: Player? = null

        //override val boundingLines: MutableList<DotsAndBoxesGame.Line> = mutableListOf()
        //override var boundingLines: List<DotsAndBoxesGame.Line> = mutableListOf()
        override val boundingLines: MutableList<StudentLine> = mutableListOf()
        //get() = TODO("Look up the correct lines from the game outer class")

        fun setBoundingLines() {
            boundingLines.add(lines[this.boxX, this.boxY - 1])
            boundingLines.add(lines[this.boxX, this.boxY + 1])
            boundingLines.add(lines[this.boxX - 1, this.boxY])
            boundingLines.add(lines[this.boxX + 1, this.boxY])
        }

        fun checkBoxesLinesDrawn(): Boolean {
            if (boundingLines.all{it.isDrawn}) {
                this.owningPlayer = currentPlayer
                return true
            } else {
                return false
            }
        }

        fun validBox(): Boolean = ((this.boxX % 2 != 0) && (this.boxY % 2 != 0))
    }

    class User(pName: String) : HumanPlayer() {
        var name: String = ""
        init {
            name = pName
        }
    }

    class PlayerComputer(cName: String): ComputerPlayer() {
        var name: String = ""
        init {
            name = cName
        }
        override fun makeMove(game: DotsAndBoxesGame) {
//            if(game is StudentDotsBoxGame) {
//                val randCol = game.computerLines.random()
//                val randRow = randCol.random()
//                game.playToken(randRow.first, randRow.second)
//                game.computerLines[game.computerLines.indexOf(randCol)].removeAt(randCol.indexOf(randRow))
//                if(randCol.isEmpty()) {
//                    game.computerLines.removeAt(game.computerLines.indexOf(randCol))
//                }
//            }
            val lines = game.lines.filter { !it.isDrawn }.random()
            lines.drawLine()
        }
    }

    var computerLines = initiateLineArray()

    private fun initiateLineArray(): MutableList<MutableList<Pair<Int, Int>>> {
        val lin = mutableListOf<MutableList<Pair<Int, Int>>>()
        for(x in 0 until columns) {
            val col = mutableListOf<Pair<Int, Int>>()
            for (y in 0 until rows) {
                if(lines[x, y].validLine()) {
                    col.add(Pair(x, y))
                }
            }
            lin.add(col)
        }
        return lin
    }

    fun playToken(xCo: Int, yCo: Int): Boolean {
        var drawBox = false

        if(!lines[xCo, yCo].isDrawn && lines[xCo, yCo].validLine()) {
            lines[xCo, yCo].isDrawn = true
            if(lines[xCo, yCo].adjacentBoxes.first != null) {
                if(lines[xCo, yCo].adjacentBoxes.first!!.checkBoxesLinesDrawn()) {
                    playerScores[currentPlayerIndex] ++
                    drawBox = true
                }
            }
            if(lines[xCo, yCo].adjacentBoxes.second != null) {
                if(lines[xCo, yCo].adjacentBoxes.second!!.checkBoxesLinesDrawn()) {
                    playerScores[currentPlayerIndex] ++
                    drawBox = true
                }
            }
            fireGameC()
            fireGameChange()
            if (drawBox && (lines.all{it.isDrawn})) {
                isFinished = true
                fireGameO(getPlayersScores())
                fireGameOver(getPlayersScores())
            }
            if (!drawBox) {
                if(currentPlayerIndex < 1) {
                    currentPlayerIndex ++
                } else {
                    currentPlayerIndex --
                }
                currentPlayer = players[currentPlayerIndex]
            }
        }
        return drawBox
    }

    private fun getPlayersScores(): List<Pair<Player, Int>> {
        val pScore = mutableListOf<Pair<Player, Int>>()
        for (i in players.indices) {
            pScore[i] = Pair(players[i], playerScores[i])
        }
        return pScore
    }

    fun getBoxOwner(col: Int, row: Int): Player? {
        return boxes[col, row].owningPlayer
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