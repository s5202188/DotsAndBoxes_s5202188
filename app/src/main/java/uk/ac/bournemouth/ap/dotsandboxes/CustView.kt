package uk.ac.bournemouth.ap.dotsandboxes
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface


public class CustView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)
    //dots lines boxes colors
    private val dotsCol: Int = Color.BLUE
    private val playerBoxCol: Int = Color.GREEN
    private val computerBoxCol: Int = Color.RED
    private val lineCol: Int = Color.BLACK
    private val labelCol: Int = Color.rgb(242, 255, 254)
    private val backCol: Int = Color.rgb(250,250,200)



    //paint variables
    private var backPaint: Paint
    private var wordsPaint: Paint
    private var dotsPaint: Paint

    var xSep: Float = 50f
    var ySep: Float = 50f

    init {
        dotsPaint = Paint().apply {
            setStyle(Style.FILL)
            setColor(dotsCol)
        }
        backPaint = Paint().apply {
            setStyle(Style.FILL)
            setColor(backCol)
        }
        wordsPaint = Paint().apply {
            setColor(labelCol)
            setTextAlign(Paint.Align.CENTER)
            setTextSize(100.toFloat())
            setTypeface(Typeface.SANS_SERIF)
        }

    }

    override fun onDraw(canvas: Canvas) {
        // Background
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()
        canvas.drawRect(0f, 0f, canvasWidth, canvasHeight, backPaint)

        //makes dots fit screen
        val viewWidthDots = canvasWidth / 6f
        val viewHeightDots = canvasHeight / 6f
        xSep = viewWidthDots
        ySep = viewHeightDots

        dotsPaint.setStrokeWidth(20f)
        dotsPaint.setStrokeCap(Paint.Cap.ROUND)
        //set the paint color
        dotsPaint.setColor(Color.CYAN)
        for (x in 1..5) {
            for (y in 1..5) {
                canvas.drawPoint(x*xSep, y*ySep, dotsPaint)
            }
        }
    }
}