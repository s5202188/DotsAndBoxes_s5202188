package org.example.student.dotsboxgame

import uk.ac.bournemouth.ap.dotsandboxeslib.*
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.Matrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableMatrix

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

    //override val lines: SparseMatrix<StudentLine> = MutableSparseMatrix(columns + 1, rows * 2 + 1, ::StudentLine) { x, y -> y % 2 == 1 || x < columns }
    //override val lines: SparseMatrix<DotsAndBoxesGame.Line> = MutableSparseMatrix(columns, rows, ::StudentLine)
    override val lines: Matrix<StudentLine> = MutableMatrix<StudentLine>(columns, rows, ::StudentLine)
    //TODO("Create a matrix initialized with your own line type")

    override var isFinished: Boolean = false
    //get() = TODO("Provide this getter. Note you can make it a var to do so")

    override fun playComputerTurns() {
        var current = currentPlayer
        while (current is ComputerPlayer && ! isFinished) {
            current.makeMove(this)
            current = currentPlayer
        }
    }

    fun play(xCo: Int, yCo: Int) {
        playToken(xCo, yCo)
        playComputerTurns()
    }


    init {
        for(box in boxes) {
            if(box.validBox()) {
                box.setBoundingLines()
            }
        }
        playComputerTurns()
    }

    /**
     * This is an inner class as it needs to refer to the game to be able to look up the correct
     * lines and boxes. Alternatively you can have a game property that does the same thing without
     * it being an inner class.
     */
    inner class StudentLine(lineX: Int, lineY: Int) : AbstractLine(lineX, lineY) {
        override var isDrawn: Boolean = false
        //get() = TODO("Provide this getter. Note you can make it a var to do so")

        fun validLine(): Boolean {
            if((this.lineX in 0 until columns) && (this.lineY in 0 until rows)) {
                if (isHorizontal() || isVertical())
                    return true
            }
            return false
        }

        //"You need to look up the correct boxes for this to work")
        //Need to add checks so that a line on the edge of the grid only has one adjacent box
        override var adjacentBoxes: Pair<StudentBox?, StudentBox?> = Pair(null, null)
            //TODO("You need to look up the correct boxes for this to work")
            get() {
                if (this.validLine()) {
                    //Line is horizontal
                    if (isHorizontal() && (this.lineY == 0)) {
                        //Get the box below the line
                        field = Pair(null, boxes[this.lineX, this.lineY + 1])
                    } else if(isHorizontal() && (this.lineY == (rows - 1))) {
                        //Get the box above the line
                        field = Pair(boxes[this.lineX, this.lineY - 1], null)
                    } else if(isHorizontal()) {
                        //Get the boxes below and above the line
                        field = Pair(boxes[this.lineX, this.lineY - 1], boxes[this.lineX, this.lineY + 1])
                    }
                    //Line is horizontal
                    else if (isVertical() && (this.lineX == 0)) {
                        //Get the box to the right of the line
                        field = Pair(null, boxes[this.lineX + 1, this.lineY])
                    } else if(isVertical() && (this.lineX == (columns - 1))) {
                        //Get the box to the left of the line
                        field = Pair(boxes[this.lineX - 1, this.lineY], null)
                    } else if(isVertical()) {
                        //Get the boxes to the left and right of the line
                        field = Pair(boxes[this.lineX - 1, this.lineY], boxes[this.lineX + 1, this.lineY])
                    }
                }
                return field
            }

        fun isHorizontal(): Boolean = ((this.lineX % 2 != 0) && (this.lineY % 2 == 0))

        fun isVertical(): Boolean  = ((this.lineX % 2 == 0) && (this.lineY % 2 != 0))

        override fun drawLine() {
            isDrawn = true
            //fireGameC()

            //TODO("Implement the logic for a player drawing a line. Don't forget to inform the listeners (fireGameChange, fireGameOver)")
            // NOTE read the documentation in the interface, you must also update the current player.
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
            //if(boundingLines[0].isDrawn && boundingLines[1].isDrawn && boundingLines[2].isDrawn && boundingLines[3].isDrawn) {
            if (boundingLines.all{ it.isDrawn }) {
                this.owningPlayer = currentPlayer
                return true
            } else {
                return false
            }
        }

        fun validBox(): Boolean = ((this.boxX % 2 != 0) && (this.boxY % 2 != 0))
    }

    class User(pName: String) : HumanPlayer() {
        private var name: String = ""
        init {
            name = pName
        }
    }

    class PlayerComputer(cName: String): ComputerPlayer() {
        private var name: String = ""
        init {
            name = cName
        }
        override fun makeMove(gameRef: DotsAndBoxesGame) {
            if(gameRef is StudentDotsBoxGame) {
                //Select a random column of the grid
                val chosenColumn = gameRef.lineArray.random()
                //Select a random line from the column
                val chosenColumnLine = chosenColumn.random()
                //Invoke the playTurnToken method using the selected line
                gameRef.playToken(chosenColumnLine.first, chosenColumnLine.second)
                //Remove the chosen line from the column of un-drawn lines
                gameRef.lineArray[gameRef.lineArray.indexOf(chosenColumn)].removeAt(chosenColumn.indexOf(chosenColumnLine))
                //Remove the line column from the list if the column is now empty
                if(chosenColumn.isEmpty()) {
                    gameRef.lineArray.removeAt(gameRef.lineArray.indexOf(chosenColumn))
                }
            }
            //val line = gameRef.lines.filter { !it.isDrawn }.random()
            //line.drawLine()
        }
    }



    var lineArray = createDrawnLines()

    fun createDrawnLines(): MutableList<MutableList<Pair<Int, Int>>> {
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

    fun getBox(col: Int, row: Int): Player? {
        return boxes[col, row].owningPlayer
    }

    fun playToken(xCo: Int, yCo: Int): Boolean {
        var boxDrawn = false

        if(!lines[xCo, yCo].isDrawn && lines[xCo, yCo].validLine()) {
            lines[xCo, yCo].drawLine()

            if(lines[xCo, yCo].adjacentBoxes.first != null) {
                if(lines[xCo, yCo].adjacentBoxes.first!!.checkBoxesLinesDrawn()) {
                    playerScores[currentPlayerIndex] ++
                    boxDrawn = true
                }
            }
            if(lines[xCo, yCo].adjacentBoxes.second != null) {
                if(lines[xCo, yCo].adjacentBoxes.second!!.checkBoxesLinesDrawn()) {
                    playerScores[currentPlayerIndex] ++
                    boxDrawn = true
                }
            }
            fireGameC()
            fireGameChange()
            if (boxDrawn == true && lines.all{it.isDrawn}) {
                isFinished = true
                fireGameO(getPlayersScores())
                fireGameOver(getPlayersScores())
            }
            if(currentPlayerIndex < 1) {
                currentPlayerIndex ++
            } else {
                currentPlayerIndex --
            }
            currentPlayer = players[currentPlayerIndex]
            boxDrawn = true
        }
        return boxDrawn
    }

    private fun getPlayersScores(): List<Pair<Player, Int>> {
        val pScore = mutableListOf<Pair<Player, Int>>()
        pScore[0] = Pair(players[0], playerScores[0])
        pScore[1] = Pair(players[1], playerScores[1])
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