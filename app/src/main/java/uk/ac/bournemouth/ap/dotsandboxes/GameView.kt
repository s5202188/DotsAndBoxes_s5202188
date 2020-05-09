package uk.ac.bournemouth.ap.dotsandboxes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import org.example.student.dotsboxgame.StudentDotsBoxGame
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.Player

class GameView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    //dots lines boxes colors
    private val dotsCol: Int = Color.BLUE
    private val playerBoxCol: Int = Color.GREEN
    private val computerBoxCol: Int = Color.RED
    private val drawnLineCol: Int = Color.BLACK
    private val notDrawnLineCol: Int = Color.LTGRAY
    private val wordsCol: Int = Color.BLACK
    private val backCol: Int = Color.WHITE

    //paint variables
    private var backPaint: Paint
    private var wordsPaint: Paint
    private var dotsPaint: Paint
    private var drawnLinePaint: Paint
    private var notDrawnLinePaint: Paint
    private var boxPlayerPaint: Paint
    private var boxComputerPaint: Paint
    private var computerWordsPaint: Paint
    private var playerWordsPaint: Paint

    //board values

    private var gridColumns = 3
    private var gridRows = 3
    private val colCount = gridColumns * 2 + 1
    private val rowCount = gridRows * 2 + 1

    var sep: Float = 0f

    //name values
    var playerName = "Player1"
    var compName = "Computer"

    var players: List<Player> = listOf(StudentDotsBoxGame.User(playerName), StudentDotsBoxGame.PlayerComputer(compName))
    var mGame: StudentDotsBoxGame = StudentDotsBoxGame(colCount,rowCount, players)

    private val myGestureDetector = GestureDetector(context, myGestureListener())

    var gameChangeListenerImp = object: DotsAndBoxesGame.GameChangeListener {
        override fun onGameChange(game: DotsAndBoxesGame) {
            // Things that we want to do in this View when the game state changes
            invalidate()
        }
    }
    var gameOverListenerImp = object: DotsAndBoxesGame.GameOverListener {
        override fun onGameOver(game: DotsAndBoxesGame, playerScores: List<Pair<Player, Int>>) {
            // Things that we want to do in this View when the game state changes
            invalidate()
        }
    }

    init {
        mGame.setGameChangeListener(gameChangeListenerImp)
        mGame.setGameOverListener(gameOverListenerImp)

        dotsPaint = Paint().apply {
            setStyle(Style.FILL)
            setColor(dotsCol)
            setStrokeWidth(20f)
            setStrokeCap(Paint.Cap.ROUND)
        }
        drawnLinePaint = Paint().apply {
            setStyle(Style.FILL)
            setColor(drawnLineCol)
            setStrokeWidth(30f)
        }
        notDrawnLinePaint = Paint().apply {
            setStyle(Style.FILL)
            setColor(notDrawnLineCol)
            setStrokeWidth(16f)
        }
        backPaint = Paint().apply {
            setStyle(Style.FILL)
            setColor(backCol)
        }
        wordsPaint = Paint().apply {
            setColor(wordsCol)
            setTextAlign(Paint.Align.CENTER)
            setTextSize(100.toFloat())
            setTypeface(Typeface.SANS_SERIF)
        }
        computerWordsPaint = Paint().apply {
            setColor(computerBoxCol)
            setTextAlign(Paint.Align.CENTER)
            setTextSize(100.toFloat())
            setTypeface(Typeface.SANS_SERIF)
        }
        playerWordsPaint = Paint().apply {
            setColor(playerBoxCol)
            setTextAlign(Paint.Align.CENTER)
            setTextSize(100.toFloat())
            setTypeface(Typeface.SANS_SERIF)
        }
        boxPlayerPaint = Paint().apply {
            style = Paint.Style.FILL
            setColor(playerBoxCol)
        }
        boxComputerPaint = Paint().apply {
            style = Paint.Style.FILL
            setColor(computerBoxCol)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // measure Background
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()
        // draw canvas
        canvas.drawRect(0f, 0f, canvasWidth, canvasHeight, backPaint)

        sep = canvasWidth / colCount.toFloat()
        val gridSep = sep / 2

        // draw lines, dots and boxes
        for (row in 0 until rowCount) {
            for (col in 0 until colCount) {
                var linePaint: Paint
                var boxPaint: Paint
                if((row % 2 == 0) && (col % 2 == 0)) {
                    canvas.drawCircle(sep*col+gridSep, sep*row+gridSep, gridSep/4, dotsPaint)
                }
                else if((row % 2 == 0) && (col % 2 != 0)) {
                    if(mGame.lines[row, col].isDrawn) {
                        linePaint = drawnLinePaint
                    } else {
                        linePaint = notDrawnLinePaint
                    }
                    canvas.drawLine((sep*col)-gridSep/2, (sep*row)+((sep/2)-(sep/gridSep)), sep*(col+1) + gridSep/2, (sep*row)+((sep/2)-(sep/gridSep/2))-1, linePaint)
                }
                else if((row % 2 != 0) && (col % 2 == 0)) {
                    if(mGame.lines[row, col].isDrawn) {
                        linePaint = drawnLinePaint
                    } else {
                        linePaint = notDrawnLinePaint
                    }
                    canvas.drawLine((sep*col)+((sep/2)-(sep/gridSep)), (sep*row)-gridSep/2, (sep*col)+((sep/2)-(sep/gridSep)), (sep*(row+1))+gridSep/2, linePaint)

                } else {
                    var boxOwner: Int = calBoxOwner(row, col)
                    if(boxOwner == 0) {
                        boxPaint = backPaint
                    } else if(boxOwner == 1) {
                        boxPaint = boxPlayerPaint
                    } else {
                        boxPaint = boxComputerPaint
                    }
                    canvas.drawRect((sep*col)-gridSep/2, sep*row-gridSep/2, sep*(col+1)+gridSep/2, sep*(row+1) + gridSep/2, boxPaint)
                }
            }
        }

        // draw players names and scores in their colours
        canvas.drawText(playerName, canvasWidth/1.85f, canvasHeight.toFloat() * 0.8f, playerWordsPaint)
        canvas.drawText(mGame.playerScores[0].toString(), canvasWidth/5f, canvasHeight * 0.8f, playerWordsPaint)

        canvas.drawText(compName, canvasWidth/1.7f, canvasHeight.toFloat() * 0.8f + 150f, computerWordsPaint)
        canvas.drawText(mGame.playerScores[1].toString(), canvasWidth/5f, canvasHeight * 0.8f + 150f, computerWordsPaint)

        // draw game results
        if(mGame.isFinished) {
            if(mGame.playerScores[0] > mGame.playerScores[1]) {
                canvas.drawText("Congratulations " + playerName + " you win!", canvasWidth/1.85f, canvasHeight * 0.8f, playerWordsPaint)
            } else if(mGame.playerScores[0] < mGame.playerScores[1]) {
                canvas.drawText("Sorry You Lose!", canvasWidth/1.85f, canvasHeight * 0.8f, computerWordsPaint)
            } else {
                canvas.drawText("Game was a Draw!", sep, canvasHeight/2, wordsPaint)
            }
        }
    }

    private fun calBoxOwner(row: Int, column: Int): Int {
        if (mGame.players[0] == mGame.getBoxOwner(row, column)) {
            return 1
        } else if (mGame.players[1] == mGame.getBoxOwner(row, column)) {
            return 2
        }
        return 0
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return myGestureDetector.onTouchEvent(ev) || super.onTouchEvent(ev)
    }

    inner class myGestureListener: GestureDetector.SimpleOnGestureListener() {
        // You should always include onDown() and it should always return true.
        // Otherwise the GestureListener may ignore other events.
        override fun onDown(ev: MotionEvent): Boolean {
            return !(mGame.isFinished)
        }
        override fun onSingleTapUp(ev: MotionEvent): Boolean {
            //Cal column
            val xCo = (ev.x/sep).toInt()
            //Cal row
            val yCo = (ev.y/sep).toInt()
            if(ev.x.toInt() < colCount*sep && ev.y.toInt() < rowCount*sep) {
                mGame.play(yCo, xCo)
                return true
            } else {
                return false
            }
        }
    }
}