package com.example.idrecognizer.common

import java.util.regex.Pattern

object MrzInfoValidator {
    private val datePattern = Pattern.compile("[0-9]{6}")
    private val namePattern = Pattern.compile("\\w+[A-Z]")
    private val personalNoPattern = Pattern.compile("[a-zA-z][0-9]{8}[a-zA-z]")

    @JvmStatic
    fun isValid(infoType: MrzInfoType, value: String): Boolean {
        when (infoType) {
            MrzInfoType.FIRST_NAME,
            MrzInfoType.LAST_NAME -> return namePattern.matcher(value).matches()
            else -> {}
        }
        // The other infoTypes are guaranteed by the parser to be valid
        return true
    }
}