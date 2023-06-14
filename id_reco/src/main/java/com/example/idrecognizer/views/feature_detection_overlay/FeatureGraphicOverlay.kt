
package com.example.idrecognizer.views.feature_detection_overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.idrecognizer.common.models.FrameMetadata
import com.google.android.gms.vision.CameraSource

class FeatureGraphicOverlay(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val lock = Any()
    private var previewWidth = 0
    private var widthScaleFactor = 1.0f
    private var previewHeight = 0
    private var heightScaleFactor = 1.0f
    private val facing = CameraSource.CAMERA_FACING_BACK
    private val featureGraphics: MutableList<FeatureGraphic> = ArrayList()
    private var cropRect: Rect? = null

    abstract class FeatureGraphic(private val overlay: FeatureGraphicOverlay) {

        abstract fun draw(canvas: Canvas)

        private fun scaleX(horizontal: Float): Float {
            return horizontal * overlay.widthScaleFactor
        }

        private fun scaleY(vertical: Float): Float {
            return vertical * overlay.heightScaleFactor
        }

        fun translateX(x: Float): Float {
            return if (overlay.facing == CameraSource.CAMERA_FACING_FRONT) {
                overlay.width - scaleX(x)
            } else {
                scaleX(x)
            }
        }

        fun translateY(y: Float): Float {
            return scaleY(y)
        }
    }

    fun setCropRect(cropRect: Rect?) {
        this.cropRect = cropRect
    }

    fun clear() {
        synchronized(lock) { featureGraphics.clear() }
        postInvalidate()
    }

    fun add(featureGraphic: FeatureGraphic) {
        synchronized(lock) { featureGraphics.add(featureGraphic) }
    }


    fun setCameraInfo(metadata: FrameMetadata) {
        synchronized(lock) {
            if (metadata.rotation == 90 || metadata.rotation == 270) {
                previewWidth = metadata.height
                previewHeight = metadata.width
            } else {
                previewWidth = metadata.width
                previewHeight = metadata.height
            }
        }
        postInvalidate()
    }

    @SuppressLint("CanvasSize")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            if (previewWidth != 0 && previewHeight != 0) {
                widthScaleFactor = canvas.width.toFloat() / previewWidth.toFloat()
                heightScaleFactor = canvas.height.toFloat() / previewHeight.toFloat()
            }
            if (cropRect != null) {
                canvas.clipRect(cropRect!!)
            }
            for (featureGraphic in featureGraphics) {
                featureGraphic.draw(canvas)
            }
        }
    }
}