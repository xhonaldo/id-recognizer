
package com.example.idrecognizer.common.vision_image_processors

import com.example.idrecognizer.common.models.Image
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

abstract class VisionProcessorBase<T> : VisionImageProcessor {

    open lateinit var imageProcessingListener: ImageProcessingListener<T>

    override fun process(image: Image) {
        val imageMetadata = image.metadata
        val firebaseVisionImageMetadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(imageMetadata.width)
            .setHeight(imageMetadata.height)
            .setRotation(imageMetadata.rotation / 90)
            .build()
        detectInVisionImage(
            image,
            FirebaseVisionImage.fromByteBuffer(image.bytes, firebaseVisionImageMetadata)
        )
    }

    private fun detectInVisionImage(originalCameraImage: Image, image: FirebaseVisionImage) {
        detectInImage(image)
            .addOnSuccessListener { results ->
                this@VisionProcessorBase.onSuccess(
                    originalCameraImage,
                    results
                )
            }
            .addOnFailureListener { e -> this@VisionProcessorBase.onFailure(e) }
    }

    protected abstract fun detectInImage(image: FirebaseVisionImage): Task<T>


    protected abstract fun onSuccess(
        originalCameraImage: Image?,
        results: T
    )

    protected abstract fun onFailure(e: Exception)
}