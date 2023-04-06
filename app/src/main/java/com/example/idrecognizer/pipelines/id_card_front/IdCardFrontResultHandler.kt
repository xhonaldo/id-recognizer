package com.example.idrecognizer.pipelines.id_card_front

import android.graphics.Rect
import com.example.idrecognizer.CoordinateTranslator
import com.example.idrecognizer.common.BaseResultHandler
import com.google.firebase.ml.vision.face.FirebaseVisionFace

internal class IdCardFrontResultHandler(private val coordinateTranslator: CoordinateTranslator) :
    BaseResultHandler<List<FirebaseVisionFace>, IdCardFrontResult?> {

    companion object {
        private const val FRONT_MIN_X = 0.05f
        private const val FRONT_MAX_X = 0.20f
        private const val FRONT_MIN_Y = 0.15f
        private const val FRONT_MAX_Y = 0.55f
        private const val FRONT_MAX_AREA = 0.08f
    }

    override fun handle(result: List<FirebaseVisionFace>, roi: Rect): IdCardFrontResult? {
        val adjustedRoi = coordinateTranslator.adjustCropRectToCameraCoordinates(roi)
        result.forEach { face ->
            val faceRect = face.boundingBox
            if (adjustedRoi.contains(faceRect)) {

                faceRect.left -= adjustedRoi.left
                faceRect.top -= adjustedRoi.top
                faceRect.right -= adjustedRoi.left
                faceRect.bottom -= adjustedRoi.top

                if (faceFitsInBoundaries(faceRect, adjustedRoi))
                    return IdCardFrontResult(null, faceRect)
            }
        }

        return null
    }

    private fun faceFitsInBoundaries(face: Rect, roi: Rect): Boolean {
        val fX = face.centerX()
        val fY = face.centerY()
        val faceArea = face.width() * face.height()
        val imageWidth = roi.width()
        val imageHeight = roi.height()
        return fX >= imageWidth * FRONT_MIN_X &&
                fX <= imageWidth * FRONT_MAX_X &&
                fY >= imageHeight * FRONT_MIN_Y &&
                fY <= imageHeight * FRONT_MAX_Y &&
                faceArea <= imageHeight * imageWidth * FRONT_MAX_AREA
    }


}