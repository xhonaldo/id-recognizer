package com.example.idrecognizer.common

import android.util.SparseArray

class BidirectionalMap(ints: IntArray, chars: CharArray) {
    private val map: SparseArray<Char> = SparseArray()
    private val reversedMap: HashMap<Char, Int> = HashMap()

    init {
        for (i in ints.indices) {
            put(ints[i], chars[i])
        }
    }

    private fun put(key: Int, value: Char) {
        map.put(key, value)
        reversedMap[value] = key
    }

    operator fun get(key: Int): Char {
        return map[key]
    }

    operator fun get(key: Char): Char? {
        val value = reversedMap[key]
        return value?.toString()?.get(0)
    }

    fun has(key: Char): Boolean {
        return reversedMap.containsKey(key)
    }

    fun has(key: Int?): Boolean {
        return map.indexOfKey(key!!) >= -1
    }
}