package com.example.idrecognizer.pipelines.id_card_front

import android.graphics.Rect
import android.util.Log
import android.util.Size
import com.example.idrecognizer.CoordinateTranslator
import com.example.idrecognizer.common.ImageProcessingPipelineBase
import com.example.idrecognizer.common.helpers.ImageUtils.imageToBitmap
import com.example.idrecognizer.common.models.IdInfo
import com.example.idrecognizer.common.models.IdType
import com.example.idrecognizer.common.models.Image
import com.example.idrecognizer.common.vision_image_processors.FaceDetectionProcessor
import com.example.idrecognizer.common.vision_image_processors.ImageProcessingListener
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector

class IdCardFrontProcessingPipeline(
    cropRect: Rect,
    previewSize: Size,
    faceDetector: FirebaseVisionFaceDetector,
    private val coordinateTranslator: CoordinateTranslator
) : ImageProcessingPipelineBase(cropRect, previewSize), ImageProcessingListener<List<FirebaseVisionFace>> {

    companion object {
        private const val SCAN_NR_THRESHHOLD = 2
    }

    private val imageProcessor: FaceDetectionProcessor
    private val resultHandler: IdCardFrontResultHandler
    private var scannedSamples: Int

    init {
        imageProcessor = FaceDetectionProcessor(faceDetector)
        imageProcessor.imageProcessingListener = this
        resultHandler = IdCardFrontResultHandler(coordinateTranslator)
        scannedSamples = 0
    }

    override fun stop() {
        imageProcessor.stop()
    }

    public override fun processImage(image: Image) {
        Log.d("lmao", "startingProcessing")
        imageProcessor.process(image)
    }

    override fun onImageProcessingSuccess(result: List<FirebaseVisionFace>, processedImage: Image) {
        Log.d("lmao", "stoppingProcessing")
        val handledResult = resultHandler.handle(result, cropRect)
        if (handledResult != null) {
            if (scannedSamples < SCAN_NR_THRESHHOLD) {
                lastSuccessfulImage = processedImage
                scannedSamples++
            } else {
                idScanCallback?.onIdScanFinished(createIdInfo())
                reset()
            }
        }
    }

    private fun reset() {
        scannedSamples = 0
    }

    private fun createIdInfo(): IdInfo {
        val idInfo = IdInfo()
        idInfo.idType = IdType.ID_CARD_FRONT
        idInfo.front = imageToBitmap(lastSuccessfulImage, coordinateTranslator.adjustCropRectToCameraCoordinates(cropRect))
        return idInfo
    }
}