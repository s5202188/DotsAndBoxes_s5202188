package org.example.student.dotsboxgame

import uk.ac.bournemouth.ap.dotsandboxeslib.*
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.Matrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableSparseMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.SparseMatrix

class StudentDotsBoxGame(column: Int, row: Int, players: List<Player>) : AbstractDotsAndBoxesGame() {

    private var currentPlayerIndex: Int = 0
    private val columns: Int = column
    private val rows: Int = row

    private var onGameChangeListener: DotsAndBoxesGame.GameChangeListener? = null
    private var onGameOverListeners: DotsAndBoxesGame.GameOverListener? = null

    var linearray : MutableList<MutableList<Pair<Int, Int>>> = createDrawnLines()

    var playerScores = mutableListOf<Int>(0, 0)
    /*
    var playerScores = setPlayerScores()
    private fun setPlayerScores(): MutableList<Int> {
        val retVal = mutableListOf<Int>()
        for (i in 0..players.size) {
            retVal.add(0)
        }
        return retVal
    }
*/

    override val players: List<Player> = players
    //TODO("You will need to get players from your constructor")
    //add a parameter to the constructor with type List<Player> (or Iterable<Player>), then you get that from the constructor parameter to set your property (and remember the players)

    override var currentPlayer: Player = players[currentPlayerIndex]
    //TODO("Determine the current player, like keeping" + "the index into the players list")

    // NOTE: you may want to me more specific in the box type if you use that type in your class
    override val boxes: Matrix<DotsAndBoxesGame.Box> = MutableMatrix(columns, rows, ::StudentBox)
    //TODO("Create a matrix initialized with your own box type")

    //override val lines: SparseMatrix<DotsAndBoxesGame.Line> = MutableSparseMatrix(columns, rows, ::StudentLine)
        //TODO("Create a matrix initialized with your own line type")

    override val lines: SparseMatrix<StudentLine> =
        MutableSparseMatrix(columns + 1, rows * 2 + 1, ::StudentLine) { x, y ->
        y % 2 == 1 || x < columns
    }

    override var isFinished: Boolean = false
        //get() = TODO("Provide this getter. Note you can make it a var to do so")

    override fun playComputerTurns() {
        var current = currentPlayer
        while (current is ComputerPlayer && ! isFinished) {
            current.makeMove(this)
            current = currentPlayer
        }
    }
    init {
        for(box in boxes) {
            box.boundingLines
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


        //override var adjacentBoxes: Pair<StudentBox?, StudentBox?> = Pair(null, null)
        //override val adjacentBoxes: Pair<StudentBox?, StudentBox?>
        override val adjacentBoxes: Pair<DotsAndBoxesGame.Box?, DotsAndBoxesGame.Box?>
            //TODO("You need to look up the correct boxes for this to work")
            get() {
                if (validLine()) {
                    //horizontal boxes
                    if((this.lineX % 2 != 0) && (this.lineY % 2 == 0)) {
                        //Get the boxes below and above the line
                        return Pair(boxes[this.lineX, this.lineY - 1], boxes[this.lineX, this.lineY + 1])
                    } else if ((this.lineX % 2 != 0) && (this.lineY % 2 == 0) && (this.lineY == 0)) {
                        //Get the box below the line
                        return Pair(null, boxes[this.lineX, this.lineY + 1])
                    } else if((this.lineX % 2 != 0) && (this.lineY % 2 == 0) && (this.lineY == (rows - 1))) {
                        //Get the box above the line
                        return Pair(boxes[this.lineX, this.lineY - 1], null)
                    }

                    //vertical boxes
                    if((this.lineX % 2 == 0) && (this.lineY % 2 != 0)) {
                        //Get the boxes to the left and right of the line
                        return Pair(boxes[this.lineX - 1, this.lineY], boxes[this.lineX + 1, this.lineY])
                    } else if ((this.lineX % 2 == 0) && (this.lineY % 2 != 0) && (this.lineX == 0)) {
                        //Get the box to the right of the line
                        return Pair(null, boxes[this.lineX + 1, this.lineY])
                    } else if((this.lineX % 2 == 0) && (this.lineY % 2 != 0) && (this.lineX == (rows - 1))) {
                        //Get the box to the left of the line
                        return Pair(boxes[this.lineX - 1, this.lineY], null)
                    }
                }
                return Pair(null, null)
            }

        fun validLine(): Boolean {
            if((this.lineX in 0 until columns) && (this.lineY in 0 until rows)) {
                return ((this.lineX % 2 != 0) && (this.lineY % 2 == 0) || (this.lineX % 2 == 0) && (this.lineY % 2 != 0))
            }
            return false
        }

        override fun drawLine() {
            isDrawn = true
            //initiateGameChange()
            fireGameChange()
            //TODO("Implement the logic for a player drawing a line. Don't forget to inform the listeners (fireGameChange, fireGameOver)")
            // NOTE read the documentation in the interface, you must also update the current player.
        }
    }

    inner class StudentBox(boxX: Int, boxY: Int) : AbstractBox(boxX, boxY) {
        override var owningPlayer: Player? = null
        //get() = TODO("Provide this getter. Note you can make it a var to do so")

        override val boundingLines: MutableList<DotsAndBoxesGame.Line> = mutableListOf()
        //override val boundingLines: Iterable<DotsAndBoxesGame.Line>
            //get() = TODO("Look up the correct lines from the game outer class")

        fun setBoundingLines() {
            for(box in boxes) {
                boundingLines.add(lines[this.boxX, this.boxY + 1])
                boundingLines.add(lines[this.boxX + 1, this.boxY])
                boundingLines.add(lines[this.boxX, this.boxY - 1])
                boundingLines.add(lines[this.boxX - 1, this.boxY])
            }
        }

        fun checkBoxLinesDrawn(): Boolean {
            if(boundingLines[0].isDrawn && boundingLines[1].isDrawn && boundingLines[2].isDrawn && boundingLines[3].isDrawn) {
                this.owningPlayer = currentPlayer
                return true
            } else {
                return false
            }
        }

        private fun validBox(): Boolean {
            return (this.boxX % 2 != 0) && (this.boxY % 2 != 0)
        }
    }

    class PlayerUser(player_Name: String) : HumanPlayer() {
        private var pname = player_Name
            get() {
                return field
            }
            set(value) {
                field = value
            }
    }

    class PlayerComputer(compName: String): ComputerPlayer() {
        private var cname = compName
            get() {
                return field
            }
            set(value) {
                field = value
            }

        override fun makeMove(game: DotsAndBoxesGame) {
            /*
            if(game is StudentDotsBoxGame) {
                val selectedColumn = game.linearray.random()
                val chosenColumnLine = selectedColumn.random()

                game.gameToken(chosenColumnLine.first, chosenColumnLine.second)
                game.linearray[game.linearray.indexOf(selectedColumn)].removeAt(selectedColumn.indexOf(chosenColumnLine))

                if(selectedColumn.isEmpty()) {
                    game.linearray.removeAt(game.linearray.indexOf(selectedColumn))
                }
            }
             */
        }
    }



    private fun createDrawnLines(): MutableList<MutableList<Pair<Int, Int>>>{
        val linesarray = mutableListOf<MutableList<Pair<Int, Int>>>()
        //The array is filled with zeroes, indicating that none of the lines have been drawn yet
        for(gridX in 0 until columns) {
            val col = mutableListOf<Pair<Int, Int>>()
            for (gridY in 0 until rows) {
                //Line will only be added if it has valid coordinates
                if(lines[gridX, gridY].validLine()) {
                    col.add(Pair(gridX, gridY))
                }
            }
            linesarray.add(col)
        }
        return linesarray
    }







    fun setGameChangeListener(gameChangeListenerImp: DotsAndBoxesGame.GameChangeListener) {
        onGameChangeListener = gameChangeListenerImp
    }

    fun setGameOverListener(gameOverListenerImp: DotsAndBoxesGame.GameOverListener) {
        onGameOverListeners = gameOverListenerImp
    }

    fun initiateGameChange() {
        onGameChangeListener?.onGameChange(this)
    }
    fun initiateGameOver(score: List<Pair<Player, Int>>) {
        onGameOverListeners?.onGameOver(this, score)
    }

    fun playerDrawnBox() {
        playerScores[currentPlayerIndex] ++
    }

    fun gameToken(tCol: Int, tRow: Int): Boolean {
        /*
        if(!lines[tCol, tRow].isDrawn && lines[tCol, tRow].validLine()) {
            lines[tCol, tRow].drawLine()
            if(lines[tCol, tRow].adjacentBoxes.first != null) {
                if(lines[tCol, tRow].adjacentBoxes.first!!.checkBoxLinesDrawn()) {
                    playerDrawnBox()
                    return true
                }

            } else if(lines[tCol, tRow].adjacentBoxes != null) {
                if(lines[tCol, tRow].adjacentBoxes.second!!.checkBoxLinesDrawn()) {
                    playerDrawnBox()
                    return true
                }
            //} else if(linearray.isEmpty()) {
            } else if(lines[tCol, tRow].isDrawn) {
                isFinished = true
                initiateGameOver(scores)
                return true
            } else {
                return false
            }

            if(currentPlayerIndex < (players.size - 1)) {
                currentPlayerIndex += 1
            } else {
                currentPlayerIndex = 0
            }
            currentPlayer = players[currentPlayerIndex]
            return true
        }

         */
        return false
    }


    fun getBoxOwner(boxCol: Int, boxRow: Int): Player? {
        return boxes[boxCol, boxRow].owningPlayer
    }


}