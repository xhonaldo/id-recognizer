package com.example.idrecognizer.views

import android.content.Context
import android.graphics.*
import android.os.Handler
import com.example.idrecognizer.common.helpers.Utils.dpToPx
import com.example.idrecognizer.common.helpers.Utils.spToPx
import com.example.idrecognizer.common.helpers.ImageUtils.getBitmap
import android.view.View
import android.util.AttributeSet
import com.example.idrecognizer.common.helpers.ImageUtils
import com.example.idrecognizer.R
import java.lang.Runnable

class IdRectangleOverlay : View {
    private lateinit var orangeFillPaint: Paint
    private lateinit var roundedRect: RectF

    private lateinit var cropRect: Rect
    private lateinit var strokePaint: Paint
    private lateinit var whitePaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var clipPath: Path
    private var fullScreenRect: Rect? = null

    private var drawTurnCardView = false
    private var turnOverIcon: Bitmap? = null
    private var turnCardText: String? = null

    private var turnImageSize = 0
    private var verticalSpacing = 0
    private var rectangleRadius = 0
    private var orangeColor = 0

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {
        orangeColor = Color.parseColor("#d65027")
        strokePaint = Paint().apply {
            color = orangeColor
            strokeWidth = dpToPx(context, 3f).toFloat()
            style = Paint.Style.STROKE
        }

        orangeFillPaint = Paint().apply {
            color = orangeColor
            style = Paint.Style.FILL
        }

        whitePaint = Paint().apply {
            color = Color.WHITE
        }

        textPaint = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = spToPx(context, 20).toFloat()
           typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        turnImageSize = dpToPx(context, 70f)
        val turnOverIconUnscaled = getBitmap(context, R.drawable.ic_turn_over)
        turnOverIcon = Bitmap.createScaledBitmap(turnOverIconUnscaled, turnImageSize, turnImageSize, false)
        turnCardText = context.getString(R.string.turn_card)
        rectangleRadius = dpToPx(context, 16f)
        verticalSpacing = dpToPx(context, 20f)
    }

    fun setCropRect(rect: Rect) {
        cropRect = rect
        roundedRect = RectF(cropRect)

        Path().apply {
            addRoundRect(roundedRect, rectangleRadius.toFloat(), rectangleRadius.toFloat(), Path.Direction.CW)
        }.also {
            clipPath = it
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (drawTurnCardView) {
            canvas.drawRoundRect(roundedRect, rectangleRadius.toFloat(), rectangleRadius.toFloat(), orangeFillPaint)
            val imageLeft = cropRect.left + cropRect.width() / 2 - turnImageSize / 2
            val imageTop = cropRect.top + cropRect.height() / 2 - turnImageSize / 2
            canvas.drawBitmap(turnOverIcon!!, imageLeft.toFloat(), imageTop.toFloat(), whitePaint)
            canvas.drawText(turnCardText!!, cropRect.centerX().toFloat(), (imageTop + turnImageSize + verticalSpacing).toFloat(), textPaint)
        }
        if (fullScreenRect == null)
            fullScreenRect = Rect(width, height, 0, 0)

        canvas.clipPath(clipPath, Region.Op.DIFFERENCE)
        canvas.drawARGB(150, 0, 0, 0)
        canvas.drawRoundRect(roundedRect, rectangleRadius.toFloat(), rectangleRadius.toFloat(), strokePaint
        )
    }

    fun toggle(withFlipView: Boolean) {
        if (withFlipView) {
            drawTurnCardView = true
            Handler().postDelayed({ drawTurnCardView = false; invalidate() }, 1500)
        } else {
            strokePaint.color = Color.rgb(0, 255, 0)
            Handler().postDelayed({ strokePaint.color = orangeColor; invalidate() }, 500)
        }
        invalidate()
    }
}