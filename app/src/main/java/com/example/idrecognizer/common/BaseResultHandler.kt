package com.example.idrecognizer.common

import android.graphics.Rect

interface BaseResultHandler<T, G> {
    fun handle(result: T, roi: Rect): G
}