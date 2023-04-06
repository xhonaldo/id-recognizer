package com.example.idrecognizer.common.helpers

import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.util.ArrayList
import com.example.idrecognizer.common.helpers.MrzUtils
import java.util.Collections
import java.util.Comparator
import com.example.idrecognizer.CoordinateTranslator
import android.graphics.Rect

object MrzUtils {

    @JvmStatic
    fun extractLinesFromText(text: FirebaseVisionText): List<FirebaseVisionText.Line> {
        val lines: MutableList<FirebaseVisionText.Line> = ArrayList()
        for (block in text.textBlocks) {
            lines.addAll(block.lines)
        }
        return lines
    }

    @JvmStatic
    fun extractBiggestLines(totalLines: List<FirebaseVisionText.Line>, requiredLines: Int): List<FirebaseVisionText.Line>? {
        if (requiredLines > totalLines.size) {
            return null
        }
        sortLinesBySizeDesc(totalLines)
        val biggest: MutableList<FirebaseVisionText.Line> = ArrayList()
        for (i in 0 until requiredLines) {
            biggest.add(totalLines[i])
        }
        return biggest
    }

    private fun sortLinesBySizeDesc(lines: List<FirebaseVisionText.Line>?) {
        Collections.sort(lines) { o1: FirebaseVisionText.Line, o2: FirebaseVisionText.Line -> o2.text.length - o1.text.length }
    }


    @JvmStatic
    fun sortLinesByYPosition(lines: List<FirebaseVisionText.Line>?) {
        Collections.sort(lines) { o1: FirebaseVisionText.Line, o2: FirebaseVisionText.Line ->
            if (o1.boundingBox == null || o2.boundingBox == null) {
                return@sort 0
            }
            o1.boundingBox!!.top - o2.boundingBox!!.top
        }
    }

    @JvmStatic
    fun getBoundingBoxFromLines(lines: List<FirebaseVisionText.Line>): Rect? {
        if (lines.isEmpty()) {
            return null
        }
        var joinLeft = Int.MAX_VALUE
        var joinRight = Int.MIN_VALUE
        var joinTop = Int.MAX_VALUE
        var joinBottom = Int.MIN_VALUE
        for (line in lines) {
            val boundingBox = line.boundingBox
            if (boundingBox != null) {
                val left = boundingBox.left
                val right = boundingBox.right
                val top = boundingBox.top
                val bottom = boundingBox.bottom
                if (left < joinLeft) {
                    joinLeft = left
                }
                if (top < joinTop) {
                    joinTop = top
                }
                if (right > joinRight) {
                    joinRight = right
                }
                if (bottom > joinBottom) {
                    joinBottom = bottom
                }
            }
        }
        return Rect(joinLeft, joinTop, joinRight, joinBottom)
    }
}