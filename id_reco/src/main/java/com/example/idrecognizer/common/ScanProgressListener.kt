package com.example.idrecognizer.common

import com.example.idrecognizer.common.models.DetectedFeature
import com.example.idrecognizer.common.models.FrameMetadata

interface ScanProgressListener {
    fun onFeaturesDetected(features: Array<DetectedFeature>?, metadata: FrameMetadata?)
    fun onProgressMade(progress: Float)
    fun onInvalidMrz(validityStatus: MrzValidityStatus)
}