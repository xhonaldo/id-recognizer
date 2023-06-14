package com.example.idrecognizer.common.mrz_parser

import com.example.idrecognizer.common.BidirectionalMap

object ErrorFixer {
    private val COMMON_MISTAKES =
        BidirectionalMap(intArrayOf(0, 1, 5, 8), charArrayOf('O', 'I', 'S', 'B'))

    @JvmStatic
    fun attemptFixCharacterError(character: Char): Char? {
        return if (Character.isDigit(character)) {
            val charNumeric = Character.getNumericValue(character)
            COMMON_MISTAKES[charNumeric]
        } else {
            COMMON_MISTAKES[character]
        }
    }
}