package com.example.idrecognizer.common.helpers

import android.graphics.Rect
import com.example.idrecognizer.common.helpers.RectUtils
import android.util.Log
import android.util.Size
import kotlin.math.abs

object RectUtils {
    @JvmStatic
    fun fitRectInPlane(rect: Rect, sourceSize: Size, destinationSize: Size) {
        if (sourceSize.height > destinationSize.height || sourceSize.width > destinationSize.width) {
            if (areOfEqualRatio(sourceSize, destinationSize)) {
                scaleRectToSize(rect, sourceSize, destinationSize)
            }
        } else if (sourceSize.height < destinationSize.height || sourceSize.width < destinationSize.width) {
            offsetRectToSize(rect, destinationSize)
        }
    }

    @JvmStatic
    fun rotateRect90Degrees(rect: Rect): Rect {
        val rotated = Rect()
        rotated.top = rect.left
        rotated.right = rect.bottom
        rotated.bottom = rect.right
        rotated.left = rect.top
        return rotated
    }

    private fun offsetRectToSize(rect: Rect, destinationSize: Size) {
        val originalWidthHalved = rect.width() / 2
        val centerX = destinationSize.width / 2
        rect.left = centerX - originalWidthHalved
        rect.right = centerX + originalWidthHalved
    }

    private fun scaleRectToSize(rect: Rect, sourceSize: Size, destinationSize: Size) {
        val ratioW = (destinationSize.width.toFloat() / sourceSize.width).toDouble()
        rect.left *= ratioW.toInt()
        rect.right *= ratioW.toInt()
        val ratioH = (destinationSize.height.toFloat() / sourceSize.height).toDouble()
        rect.top *= ratioH.toInt()
        rect.bottom *= ratioH.toInt()
    }

    private fun areOfEqualRatio(size1: Size, size2: Size): Boolean {
        val ratio1 = size1.width.toFloat() / size1.height
        val ratio2 = size2.width.toFloat() / size2.height
        return abs(ratio1 - ratio2) < 0.0001
    }
}