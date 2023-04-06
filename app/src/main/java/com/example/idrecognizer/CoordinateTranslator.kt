package com.example.idrecognizer

import android.graphics.Rect

class CoordinateTranslator(
    private val targetWidth: Int,
    private val targetHeight: Int
) {

    private var frameHeight = 0f
    private var frameWidth = 0f
    private var scale = 1f

    fun recalculateScale(previewWidth: Float, previewHeight: Float, rotation: Int) {
        if (rotation == 90 || rotation == 270) {
            recalculateScale(previewHeight, previewWidth)
        } else {
            recalculateScale(previewWidth, previewHeight)
        }
    }

    private fun recalculateScale(previewWidth: Float, previewHeight: Float) {
        frameHeight = previewHeight.toInt().toFloat()
        frameWidth = previewWidth.toInt().toFloat()
        val scaleX = targetWidth / previewWidth
        val scaleY = targetHeight / previewHeight
        scale = scaleX.coerceAtLeast(scaleY)
    }

    fun adjustRectToScreenCoordinates(input: Rect?): Rect {

        val scaledWidth = (frameWidth * scale).toInt()
        val scaledHeight = (frameHeight * scale).toInt()
        val scaledOffsetX = (0.coerceAtLeast(scaledWidth - targetWidth) / 2).toFloat()
        val scaledOffsetY = (0.coerceAtLeast(scaledHeight - targetHeight) / 2).toFloat()
        val offsetScaleX = scaledOffsetX / scaledWidth
        val offsetScaleY = scaledOffsetY / scaledHeight
        val offsetX = frameWidth * offsetScaleX
        val offsetY = frameHeight * offsetScaleY
        input?.let {
            val left = input.left - offsetX.toInt()
            val right = input.right + offsetX.toInt()
            val top = input.top - offsetY.toInt()
            val bottom = input.bottom + offsetY.toInt()
            return Rect(left, top, right, bottom)
        }

        return Rect()
    }

    fun adjustCropRectToCameraCoordinates(input: Rect): Rect {
        val scaledHalfWidth = input.width() / scale / 2
        val scaledHalfHeight = input.height() / scale / 2
        val centerX = frameWidth / 2
        val centerY = frameHeight / 2
        val left = (centerX - scaledHalfWidth).toInt()
        val right = (centerX + scaledHalfWidth).toInt()
        val top = (centerY - scaledHalfHeight).toInt()
        val bottom = (centerY + scaledHalfHeight).toInt()
        return Rect(left, top, right, bottom)
    }
}