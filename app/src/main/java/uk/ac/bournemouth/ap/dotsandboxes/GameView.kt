package uk.ac.bournemouth.ap.dotsandboxes
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Color
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface
import android.view.GestureDetector
import android.view.MotionEvent
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
    private val colCount = 7
    private val rowCount = 7

    //name values
    private val playerName = "Player1"
    private val compName = "Computer"
    var players: List<Player> = listOf(StudentDotsBoxGame.PlayerUser(playerName), StudentDotsBoxGame.PlayerComputer(compName))
    val mGame: StudentDotsBoxGame = StudentDotsBoxGame(colCount,rowCount, players)

    //Listener values
    private val myGestureDetector = GestureDetector(context, myGestureListener())
    private var gameOverListeners = object: DotsAndBoxesGame.GameOverListener {
        override fun onGameOver(game: DotsAndBoxesGame, playerScores: List<Pair<Player, Int>>) {
        //    invalidate()
        }
    }
    private var gameChangeListeners = object: DotsAndBoxesGame.GameChangeListener {
        override fun onGameChange(game: DotsAndBoxesGame) {
            invalidate()
        }
    }

    init {
        mGame.setGameChangeListener(gameChangeListeners)

        mGame.setGameOverListener(gameOverListeners)

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


    var sep: Float = 0f
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Background
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()
        canvas.drawRect(0f, 0f, canvasWidth, canvasHeight, backPaint)

        sep = canvasWidth / colCount.toFloat()
        val gridsep = sep / 2

        // draw scores
        canvas.drawText(playerName, canvasWidth/1.85f, canvasHeight.toFloat() * 0.8f, playerWordsPaint)
        canvas.drawText(mGame.playerScores.toString(), canvasWidth/5f, canvasHeight.toFloat() * 0.8f, playerWordsPaint)

        canvas.drawText(compName, canvasWidth/1.7f, canvasHeight.toFloat() * 0.8f + 150f, computerWordsPaint)
        canvas.drawText(mGame.playerScores.toString(), canvasWidth/5f, canvasHeight.toFloat() * 0.8f + 150f, computerWordsPaint)

        // lines
        for (row in 0 until rowCount) {
            for (col in 0 until colCount) {
                var linePaint: Paint = notDrawnLinePaint
                var boxPaint: Paint = backPaint
                if((row % 2 == 0) && (col % 2 == 0)) {
                    canvas.drawCircle(sep*col+gridsep, sep*row+gridsep, gridsep/4, dotsPaint)
                }
                else if((row % 2 == 0) && (col % 2 != 0)) {
                    if(mGame.lines[row, col].isDrawn) {
                        linePaint = drawnLinePaint
                    } else {
                        linePaint = notDrawnLinePaint
                    }
                    canvas.drawLine((sep*col)-gridsep/2, (sep*row)+((sep/2)-(sep/gridsep)), sep*(col+1) + gridsep/2, (sep*row)+((sep/2)-(sep/gridsep/2)), linePaint)
                }
                else if((row % 2 != 0) && (col % 2 == 0)) {
                    if(mGame.lines[row, col].isDrawn) {
                        linePaint = drawnLinePaint
                    } else {
                        linePaint = notDrawnLinePaint
                    }
                    canvas.drawLine((sep*col)+((sep/2)-(sep/gridsep)), (sep*row)-gridsep/2, (sep*col)+((sep/2)-(sep/gridsep)), (sep*(row+1))+gridsep/2, linePaint)

                } else {
                    var boxOwner: Int = calBoxOwner(row, col)
                    if(boxOwner == 0) {
                        boxPaint = boxPlayerPaint
                    } else if(boxOwner == 0) {
                        boxPaint = boxComputerPaint
                    } else {
                        boxPaint = backPaint
                    }
                    canvas.drawRect((sep*col)-gridsep/2, sep*row-gridsep/2, sep*(col+1)+gridsep/2, sep*(row+1) + gridsep/2, boxPaint)
                }
            }
        }

        /*
        for (x in 1..5) {
            for (y in 1..5) {
                canvas.drawPoint(x*xSep, y*ySep, dotsPaint)
            }
        }
        for (x in 1..4) {
            for (y in 1..5) {
                canvas.drawLine((x-1)*xSep+190f, y*ySep,x*xSep+150f, y*ySep, linesPaint)
            }
        }
        for (y in 1..4) {
            for (x in 1..5) {
                canvas.drawLine((x)*xSep, (y-1)*ySep +190f,x*xSep, y*ySep+160f, linesPaint)
            }
        }
        */
    }

    fun calBoxOwner(row: Int, column: Int ): Int {
        if (mGame.players.get(0) == mGame.getBoxOwner(row, column)) {
            return 1
        } else if (mGame.players.get(1) == mGame.getBoxOwner(row, column)) {
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
            return true
        }
        override fun onSingleTapUp(ev: MotionEvent): Boolean {
            //Cal column
            val xCo = (ev.x / sep).toInt()
            //Cal row
            val yCo = (ev.y / sep).toInt()

            if (xCo in 0 until colCount && yCo in 0 until rowCount)            {
                mGame.gameToken(xCo, yCo)
                mGame.playComputerTurns()
                return true
            }
            return false
        }
    }
    // End of myGestureListener class
}