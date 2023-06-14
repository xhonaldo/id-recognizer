package com.example.idrecognizer.common.helpers

import android.graphics.Rect
import android.util.Size
import com.example.idrecognizer.common.models.IdType
import kotlin.math.roundToInt

object CropRectangleGenerator {
    private const val OFFSET_LEFT = 0.05f
    private const val PASSPORT_ASPECT_RATIO = 0.72f
    private const val ID_CARD_ASPECT_RATIO = 0.63f

    fun createCropRectangle(idType: IdType, size: Size = Utils.screenSize): Rect {
        val aspectRatio = if (idType == IdType.PASSPORT)
            PASSPORT_ASPECT_RATIO
        else
            ID_CARD_ASPECT_RATIO


        val left = (size.width * OFFSET_LEFT).roundToInt()
        val right = size.width - left
        val width = right - left
        val halfHeight = (width * aspectRatio / 2).roundToInt()
        val centerY = size.height / 2
        val top = centerY - halfHeight
        val bottom = centerY + halfHeight

        return Rect(left, top, right, bottom)
    }
}