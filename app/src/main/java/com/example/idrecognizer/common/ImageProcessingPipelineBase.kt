package com.example.idrecognizer.common

import android.graphics.Rect
import android.util.Size
import com.example.idrecognizer.common.helpers.RectUtils.fitRectInPlane
import com.example.idrecognizer.common.models.Image

abstract class ImageProcessingPipelineBase(
    val cropRect: Rect,
    private val previewSize: Size
) {

    var idScanCallback: IdScanCallback? = null
    var scanProgressListener: ScanProgressListener? = null
    protected lateinit var lastSuccessfulImage: Image
    private var hasCropRectBeenAdjusted = false


    fun process(image: Image) {
        if (!hasCropRectBeenAdjusted) {
            adjustCropRectToImage(image)
            hasCropRectBeenAdjusted = true
        }
        processImage(image)
    }

    open fun stop() {
        idScanCallback = null
        scanProgressListener = null
    }


    private fun adjustCropRectToImage(image: Image) {
        val metadata = image.metadata
        if (metadata.rotation == 90) {
            fitRectInPlane(cropRect, previewSize, Size(metadata.height, metadata.width))
        } else {
            fitRectInPlane(cropRect, previewSize, Size(metadata.width, metadata.height))
        }
    }



    protected abstract fun processImage(image: Image)



}