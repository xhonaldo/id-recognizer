package com.example.idrecognizer.common

import io.fotoapparat.preview.Frame
import io.fotoapparat.preview.FrameProcessor

class SimpleFrameProcessor(private val onFrameProcessing: ((Frame) -> Unit)): FrameProcessor {
    override fun process(frame: Frame) {
        onFrameProcessing.invoke(frame)
    }
}