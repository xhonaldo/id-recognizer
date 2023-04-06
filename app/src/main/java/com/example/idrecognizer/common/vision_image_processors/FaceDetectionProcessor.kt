
package com.example.idrecognizer.common.vision_image_processors

import android.util.Log
import com.example.idrecognizer.common.models.Image
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import java.io.IOException


class FaceDetectionProcessor(
    private val detector: FirebaseVisionFaceDetector
): VisionProcessorBase<List<FirebaseVisionFace>>() {

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun onSuccess(originalCameraImage: Image?, faces: List<FirebaseVisionFace>) {
        Log.d("lmao", "FACEDETECTION")
        imageProcessingListener.onImageProcessingSuccess(faces, originalCameraImage!!)
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private const val TAG = "FaceDetectionProcessor"
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionFace>> {
        return detector.detectInImage(image)
    }
}