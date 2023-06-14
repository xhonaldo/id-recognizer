package com.example.idrecognizer.pipelines.passport

import com.example.idrecognizer.common.helpers.MrzUtils.extractBiggestLines
import com.example.idrecognizer.common.helpers.MrzUtils.extractLinesFromText
import com.example.idrecognizer.common.helpers.MrzUtils.sortLinesByYPosition
import com.example.idrecognizer.common.helpers.MrzUtils.getBoundingBoxFromLines
import com.example.idrecognizer.CoordinateTranslator
import com.example.idrecognizer.common.BaseResultHandler
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.example.idrecognizer.pipelines.id_card_back.IdCardMrz
import android.graphics.Rect
import com.example.idrecognizer.common.helpers.MrzUtils
import com.example.idrecognizer.common.MrzValidityStatus
import android.util.Log
import com.example.idrecognizer.pipelines.passport.PassportMrzExtractor

internal class PassportMrzExtractor(
    private val coordinateTranslator: CoordinateTranslator
): BaseResultHandler<FirebaseVisionText, IdCardMrz?> {

    companion object {
        private const val MRZ_MIN_RATIO_TO_FRAME = 0.65f
    }

    override fun handle(result: FirebaseVisionText, roi: Rect): IdCardMrz? {
        val biggest2Lines = extractBiggestLines(extractLinesFromText(result), 2) ?: return null
        sortLinesByYPosition(biggest2Lines)
        val joinedRect = getBoundingBoxFromLines(biggest2Lines)
        val validityStatus = validateMrz(joinedRect, roi)

        return IdCardMrz(biggest2Lines, coordinateTranslator.adjustRectToScreenCoordinates(joinedRect), validityStatus)
    }

    private fun validateMrz(mrz: Rect?, roi: Rect): MrzValidityStatus {
        val reverseScaledRoi = coordinateTranslator.adjustCropRectToCameraCoordinates(roi)
        if (mrz!!.width() < reverseScaledRoi.width() * MRZ_MIN_RATIO_TO_FRAME) {
            return MrzValidityStatus.INVALID_SIZE
        }
        return if (
            mrz.top <= reverseScaledRoi.centerY() ||
            mrz.bottom > reverseScaledRoi.bottom ||
            mrz.left < reverseScaledRoi.left ||
            mrz.right > reverseScaledRoi.right
        ) {
            MrzValidityStatus.INVALID_POSITION
        } else
            MrzValidityStatus.VALID
    }


}