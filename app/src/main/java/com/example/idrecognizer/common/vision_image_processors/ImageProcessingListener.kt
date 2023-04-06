package com.example.idrecognizer.common.vision_image_processors

import com.example.idrecognizer.common.models.Image

interface ImageProcessingListener<T> {
    fun onImageProcessingSuccess(result: T, processedImage: Image)
}