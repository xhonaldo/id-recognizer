package com.example.idrecognizer.pipelines.id_card_back

import android.graphics.Rect
import android.util.Log
import android.util.Size
import com.example.idrecognizer.CoordinateTranslator
import com.example.idrecognizer.common.ImageProcessingPipelineBase
import com.example.idrecognizer.common.MrzValidityStatus
import com.example.idrecognizer.common.MrzValidityStatus.*
import com.example.idrecognizer.common.helpers.ImageUtils.imageToBitmap
import com.example.idrecognizer.common.models.DetectedFeature
import com.example.idrecognizer.common.models.IdInfo
import com.example.idrecognizer.common.models.Image
import com.example.idrecognizer.common.result_assembler.OnFinalResultAssembledListener
import com.example.idrecognizer.common.vision_image_processors.ImageProcessingListener
import com.example.idrecognizer.common.vision_image_processors.TextRecognitionProcessor
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer

class IdCardBackProcessingPipeline(
    cropRect: Rect,
    previewSize: Size,
    textRecognizer: FirebaseVisionTextRecognizer,
    private val coordinateTranslator: CoordinateTranslator
) : ImageProcessingPipelineBase(cropRect, previewSize), ImageProcessingListener<FirebaseVisionText>, OnFinalResultAssembledListener<IdInfo> {

    private val imageProcessor: TextRecognitionProcessor
    private val resultHandler: IdCardMrzExtractor
    private val resultAssembler: IdCardBackResultAssembler
    private var isProcessingImage = false

    init {
        imageProcessor = TextRecognitionProcessor(textRecognizer).apply {
            imageProcessingListener = this@IdCardBackProcessingPipeline
        }

        resultHandler = IdCardMrzExtractor(coordinateTranslator)

        resultAssembler = IdCardBackResultAssembler().apply {
            onFinalResultAssembledListener = this@IdCardBackProcessingPipeline
        }

    }

    public override fun processImage(image: Image) {
        if (!isProcessingImage) {
            imageProcessor.process(image)
            isProcessingImage = true
        }
    }

    override fun onImageProcessingSuccess(result: FirebaseVisionText, processedImage: Image) {
        isProcessingImage = false
        if (result.text.isNotEmpty()) {
            resultHandler.handle(result, cropRect)?.let {
                handeProcessingResult(it, processedImage)
            }
        }
    }

    override fun onScanProgressMade(progress: Float) {
        if (scanProgressListener != null) {
            scanProgressListener!!.onProgressMade(progress)
        }
    }

    override fun onFinalResultAssembled(assembledResult: IdInfo) {
        assembledResult.back = imageToBitmap(lastSuccessfulImage, coordinateTranslator.adjustCropRectToCameraCoordinates(cropRect))
        idScanCallback?.onIdScanFinished(assembledResult)

    }

    private fun handeProcessingResult(handledResult: IdCardMrz, processedImage: Image) {
        when(handledResult.validityStatus) {
            VALID -> {
                lastSuccessfulImage = processedImage
                resultAssembler.addSampleToPool(handledResult)
                val feature = DetectedFeature(handledResult.mrzRect, true)
                scanProgressListener?.onFeaturesDetected(arrayOf(feature), processedImage.metadata)
                scanProgressListener?.onInvalidMrz(VALID)
            }
            INVALID_POSITION -> {
                val feature = DetectedFeature(handledResult.mrzRect, false)
                scanProgressListener?.onFeaturesDetected(arrayOf(feature), processedImage.metadata)
                scanProgressListener?.onInvalidMrz(INVALID_POSITION)

            }
            INVALID_SIZE -> {
                scanProgressListener?.onInvalidMrz(INVALID_SIZE)
                scanProgressListener?.onFeaturesDetected(null, null)
            }
        }
    }
}