package com.example.idrecognizer.common.result_assembler

abstract class ResultAssemblerBase<T, G> {

    private var samplePool = ArrayList<T>()
    lateinit var onFinalResultAssembledListener: OnFinalResultAssembledListener<G>

    fun addSampleToPool(sample: T) {
        samplePool.add(sample)
        onSampleAdded(sample)
    }

    protected abstract fun onSampleAdded(sample: T)
}