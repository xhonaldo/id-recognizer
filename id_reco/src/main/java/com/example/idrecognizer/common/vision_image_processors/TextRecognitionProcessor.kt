
package com.example.idrecognizer.common.vision_image_processors

import android.util.Log
import com.example.idrecognizer.common.models.Image
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import java.io.IOException


class TextRecognitionProcessor(
    private val detector: FirebaseVisionTextRecognizer
) : VisionProcessorBase<FirebaseVisionText>() {

    companion object {
        private const val TAG = "lmao"
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Text Detector: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<FirebaseVisionText> {
        return detector.processImage(image)
    }

    override fun onSuccess(originalCameraImage: Image?, results: FirebaseVisionText) {
        imageProcessingListener.onImageProcessingSuccess(results, originalCameraImage!!)
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Text detection failed.$e")
    }


}