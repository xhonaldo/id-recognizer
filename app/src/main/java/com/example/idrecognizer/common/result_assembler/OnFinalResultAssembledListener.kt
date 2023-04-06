package com.example.idrecognizer.common.result_assembler

interface OnFinalResultAssembledListener<T> {

    fun onScanProgressMade(progress: Float)
    fun onFinalResultAssembled(assembledResult: T)
}