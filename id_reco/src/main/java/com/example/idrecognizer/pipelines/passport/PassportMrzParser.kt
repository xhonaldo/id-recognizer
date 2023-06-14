package com.example.idrecognizer.pipelines.passport

import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.example.idrecognizer.common.mrz_parser.MrzParserBase
import java.lang.StringBuilder
import java.util.regex.Pattern

class PassportMrzParser internal constructor(lines: List<FirebaseVisionText.Line?>?) :
    MrzParserBase(lines) {

    private val isFirstLineValid: Boolean
    private val isSecondLineValid: Boolean
    private var trimmedFirstLine: String? = null
    private var trimmedSecondLine: String? = null
    private val secondLinePattern = Pattern.compile("[0-9]{7}[M,F][0-9]{7}[A-Z][0-9]{8}[A-Z]")
    private var firstName: String? = null
    private var lastName: String? = null

    init {
        isFirstLineValid = validateFirstLine()
        isSecondLineValid = validateSecondLine()
        extractFirstAndLastName()
    }

    private fun extractFirstAndLastName() {
        if (!isFirstLineValid) return
        val lastNameBuilder = StringBuilder()
        val charArray = trimmedFirstLine!!.toCharArray()
        var lastNameEndIndex = 0
        for (i in charArray.indices) {
            val ch = charArray[i]
            if (Character.isAlphabetic(ch.code)) {
                lastNameBuilder.append(ch)
            } else {
                lastNameEndIndex = i
                break
            }
        }
        if (lastNameBuilder.length < 2) {
            return
        }
        lastName = lastNameBuilder.toString()
        var i = trimmedFirstLine!!.length - 1
        var hasFoundStartOfName = false
        val firstNameBuilder = StringBuilder()
        while (i > lastNameEndIndex) {
            val ch = trimmedFirstLine!![i]
            if (hasFoundStartOfName) {
                if (Character.isAlphabetic(ch.code)) {
                    firstNameBuilder.append(ch)
                } else {
                    break
                }
            } else {
                if (Character.isAlphabetic(ch.code)) {
                    firstNameBuilder.append(ch)
                    hasFoundStartOfName = true
                }
            }
            i--
        }
        if (firstNameBuilder.length < 2) {
            return
        }
        firstName = firstNameBuilder.reverse().toString()
    }

    private fun validateSecondLine(): Boolean {
        if (mrzTextLines.size != 2) {
            return false
        }
        val secondLine = mrzTextLines[1]
        if (secondLine.length != 44) {
            return false
        }
        trimmedSecondLine = getTrimmedSecondLine()
        return trimmedSecondLine?.let { secondLinePattern.matcher(it).matches() } == true
    }

    private fun validateFirstLine(): Boolean {
        val firstLine = mrzTextLines[0]
        // 10 is just an arbitrary number
        if (firstLine.length < 10) {
            return false
        }
        trimmedFirstLine = getTrimmedFirstLine()
        val prefix = firstLine.substring(0, 5)
        return prefix == "P<ALB"
    }

    private fun getTrimmedFirstLine(): String {
        return mrzTextLines[0].substring(5)
    }

    private fun getTrimmedSecondLine(): String {
        return mrzTextLines[1].substring(13, 38)
    }

    override fun extractPersonalNo(): String {
        return if (!isSecondLineValid) ""
        else trimmedSecondLine!!.substring(15)
    }

    override fun extractBirthdate(): String {
        return if (!isSecondLineValid) ""
        else formatDate(trimmedSecondLine!!.substring(0, 6), false)
    }

    override fun extractFirstName(): String {
        return firstName!!
    }

    override fun extractLastName(): String {
        return lastName!!
    }

    override fun extractGender(): String {
        return if (!isSecondLineValid) ""
        else trimmedSecondLine!!.substring(7, 8)
    }

    override fun extractExpiryDate(): String {
        return if (!isSecondLineValid) ""
        else formatDate(trimmedSecondLine!!.substring(8, 14), true)
    }
}