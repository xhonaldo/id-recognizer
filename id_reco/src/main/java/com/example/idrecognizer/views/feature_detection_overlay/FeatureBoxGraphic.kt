package com.example.idrecognizer.views.feature_detection_overlay

import android.graphics.*
import com.example.idrecognizer.views.feature_detection_overlay.FeatureGraphicOverlay.FeatureGraphic

class FeatureBoxGraphic(
    overlay: FeatureGraphicOverlay,
    private val rect: Rect,
    valid: Boolean
) : FeatureGraphic(overlay) {

    companion object {
        private const val STROKE_COLOR_VALID = Color.GREEN
        private const val STROKE_COLOR_NON_VALID = Color.RED
        private const val STROKE_WIDTH = 4.0f
    }


    private val rectPaint = Paint()

    init {
        rectPaint.color = if (valid) STROKE_COLOR_VALID else STROKE_COLOR_NON_VALID
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH
    }

    override fun draw(canvas: Canvas) {
        RectF(rect).apply {
            left = translateX(rect.left.toFloat())
            top = translateY(rect.top.toFloat())
            right = translateX(rect.right.toFloat())
            bottom = translateY(rect.bottom.toFloat())
        }.also { rectF -> canvas.drawRect(rectF, rectPaint) }


    }


}