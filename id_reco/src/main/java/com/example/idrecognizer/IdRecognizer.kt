package com.example.idrecognizer

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import com.example.idrecognizer.common.IdScanCallback
import com.example.idrecognizer.common.ImageProcessingPipelineBase
import com.example.idrecognizer.common.MrzValidityStatus
import com.example.idrecognizer.common.ScanProgressListener
import com.example.idrecognizer.common.helpers.CropRectangleGenerator
import com.example.idrecognizer.common.helpers.Utils
import com.example.idrecognizer.common.models.*
import com.example.idrecognizer.common.SimpleFrameProcessor
import com.example.idrecognizer.pipelines.id_card_back.IdCardBackProcessingPipeline
import com.example.idrecognizer.pipelines.id_card_front.IdCardFrontProcessingPipeline
import com.example.idrecognizer.pipelines.passport.PassportProcessingPipeline
import com.example.idrecognizer.views.feature_detection_overlay.FeatureBoxGraphic
import com.example.idrecognizer.views.feature_detection_overlay.FeatureGraphicOverlay
import com.example.idrecognizer.views.recognizer_view.RecognizerView
import com.google.firebase.ml.vision.FirebaseVision
import io.fotoapparat.preview.Frame
import java.nio.ByteBuffer

class IdRecognizer(
    private val recognizerView: RecognizerView,
    idType: IdType = IdType.ID_CARD_FRONT
): IdScanCallback, ScanProgressListener {

    private var doStuff = true
    private val textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
    private val faceDetector = FirebaseVision.getInstance().visionFaceDetector

    private lateinit var cropRectangle: Rect
    private lateinit var coordinateTranslator: CoordinateTranslator
    private lateinit var featureGraphicOverlay: FeatureGraphicOverlay
    private var lastScannedFrontIdCard: Bitmap? = null

    private var currentIdType: IdType? = null
    private var imageProcessingPipeline: ImageProcessingPipelineBase? = null

    var idScanCallback: IdScanCallback? = null


    init {
        recognizerView.initFotoapparat(SimpleFrameProcessor { frame ->
            processFrame(frame)
        })

        setCurrentIdType(idType)
        recognizerView.measure(ViewGroup.LayoutParams.MATCH_PARENT, View.MeasureSpec.EXACTLY)
    }

    override fun onFeaturesDetected(features: Array<DetectedFeature>?, metadata: FrameMetadata?) {
        featureGraphicOverlay.clear()
        metadata?.let { featureGraphicOverlay.setCameraInfo(metadata) }
        features?.forEach { feature ->
            featureGraphicOverlay.add(FeatureBoxGraphic(featureGraphicOverlay, feature.boundingBox, feature.isValid))
        }
    }

    override fun onIdScanFinished(idInfo: IdInfo) {
        clearFeatureGraphicOverlay()
        when (idInfo.idType!!) {
            IdType.ID_CARD_FRONT -> {
                setCurrentIdType(IdType.ID_CARD_BACK)
                lastScannedFrontIdCard = idInfo.front
            }
            IdType.ID_CARD_BACK -> {
                lastScannedFrontIdCard?.let {  idInfo.front = lastScannedFrontIdCard }
                idScanCallback?.onIdScanFinished(idInfo)
            }
            IdType.PASSPORT -> {
                idScanCallback?.onIdScanFinished(idInfo)
            }
        }

        recognizerView.displayScanSuccess(true)
    }

    override fun onInvalidMrz(validityStatus: MrzValidityStatus) {
        when (validityStatus) {
            MrzValidityStatus.INVALID_POSITION -> recognizerView.setHintText(R.string.invalid_document_position)
            MrzValidityStatus.INVALID_SIZE -> recognizerView.setHintText(R.string.invalid_document_size)
            MrzValidityStatus.VALID -> recognizerView.setHintText(R.string.valid_document_position)
        }
    }

    override fun onProgressMade(progress: Float) {
        recognizerView.setProgress(progress)
    }

    fun start() {
        doStuff = true
        recognizerView.start()
        if (currentIdType == IdType.ID_CARD_FRONT) {
            recognizerView.setHintText(R.string.start_id_scan)
        } else if (currentIdType == IdType.PASSPORT) {
            recognizerView.setHintText(R.string.start_passport_scan)
        }
    }

    fun stop() {
        doStuff = false
        recognizerView.stop()
    }

    private fun setCurrentIdType(idType: IdType) {
        if (currentIdType == null || currentIdType?.value != idType.value) {
            setupOverlays(idType)
        }
        currentIdType = idType
        setImageProcessingPipeline(idType)
        resetViews()
    }

    private fun setImageProcessingPipeline(idType: IdType) {
        if (imageProcessingPipeline != null) {
            imageProcessingPipeline?.stop()
            resetViews()
        }

        imageProcessingPipeline = when (idType) {
            IdType.ID_CARD_FRONT -> IdCardFrontProcessingPipeline(
                cropRectangle,
                Utils.screenSize,
                faceDetector,
                coordinateTranslator
            )
            IdType.ID_CARD_BACK -> IdCardBackProcessingPipeline(
                cropRectangle,
                Utils.screenSize,
                textRecognizer,
                coordinateTranslator
            )
            IdType.PASSPORT -> PassportProcessingPipeline(
                cropRectangle,
                Utils.screenSize,
                textRecognizer,
                coordinateTranslator
            )
        }

        imageProcessingPipeline?.idScanCallback = this
        imageProcessingPipeline?.scanProgressListener = this
    }

    private fun resetViews() {
        recognizerView.reset()
        clearFeatureGraphicOverlay()
    }

    private fun clearFeatureGraphicOverlay() {
        featureGraphicOverlay.clear()
    }

    private fun setupOverlays(idType: IdType) {
        cropRectangle = CropRectangleGenerator.createCropRectangle(idType)
        coordinateTranslator = CoordinateTranslator(Utils.screenSize.width, Utils.screenSize.height)
        recognizerView.addCropRectangleOverlay(cropRectangle)
        featureGraphicOverlay = FeatureGraphicOverlay(recognizerView.context, null)
        featureGraphicOverlay.setCropRect(cropRectangle)
        recognizerView.addFeatureDetectionOverlay(featureGraphicOverlay)
    }


    private fun processFrame(frame: Frame) {
        if (doStuff) {
            val frameMetadata = FrameMetadata.Builder()
                .setRotation(360 - frame.rotation)
                .setHeight(frame.size.height)
                .setWidth(frame.size.width)
                .build()
            coordinateTranslator.recalculateScale(
                frameMetadata.width.toFloat(),
                frameMetadata.height.toFloat(),
                frameMetadata.rotation
            )
            val byteBuffer = ByteBuffer.wrap(frame.image)
            imageProcessingPipeline!!.process(Image(byteBuffer, frameMetadata))
        }
    }
}