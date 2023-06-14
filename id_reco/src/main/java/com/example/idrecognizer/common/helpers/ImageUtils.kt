package com.example.idrecognizer.common.helpers

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.idrecognizer.common.models.Image
import java.io.ByteArrayOutputStream

object ImageUtils {
    @JvmStatic
    fun imageToBitmap(image: Image, cropRect: Rect?): Bitmap? {
        val data = image.bytes
        val metadata = image.metadata
        data.rewind()
        val imageInBuffer = ByteArray(data.limit())
        data[imageInBuffer, 0, imageInBuffer.size]
        try {
            val yuvImage = YuvImage(imageInBuffer, ImageFormat.NV21, metadata.width, metadata.height, null)
            val stream = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, metadata.width, metadata.height), 80, stream)
            val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            stream.close()
            val rotated = correctBitmapRotation(bmp, metadata.rotation)
            if (cropRect != null) {
                val cropped = Bitmap.createBitmap(rotated, cropRect.left, cropRect.top, cropRect.width(), cropRect.height())
                val canvas = Canvas(rotated)
                val paint = Paint()
                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 5f
                canvas.drawRect(cropRect, paint)
                return cropped
            }
            return rotated
        } catch (e: Exception) {
            Log.e("VisionProcessorBase", "Error: " + e.message)
        }
        return null
    }

    @JvmStatic
    fun correctBitmapRotation(bitmap: Bitmap, rotation: Int): Bitmap {
        val matrix = Matrix()
        if (rotation != 0 && rotation != 360) {
            matrix.postRotate(rotation.toFloat())
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        return bitmap
    }

    @JvmStatic
    fun getBitmap(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        return if (drawable is BitmapDrawable) {
            BitmapFactory.decodeResource(context.resources, drawableId)
        } else if (drawable is VectorDrawable) {
            getBitmap(drawable)
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }

    private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }
}