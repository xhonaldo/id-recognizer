package com.example.idrecognizer.common.helpers

import android.content.Context
import android.content.res.Resources
import android.util.Size
import java.lang.StringBuilder
import java.util.Locale
import java.util.HashMap
import android.util.TypedValue
import io.fotoapparat.parameter.Resolution
import kotlin.math.roundToInt

object Utils {
    @JvmStatic
    val screenSize get() = Size(
            Resources.getSystem().displayMetrics.widthPixels,
            Resources.getSystem().displayMetrics.heightPixels
        )

    @JvmStatic
    fun resolutionToSize(resolution: Resolution) = Size(resolution.width, resolution.height)

    @JvmStatic
    fun getCharCountInString(str: String, ch: Char) = str.filter { it == ch }.length

    @JvmStatic
    fun <T> mostCommon(list: List<T>): T {
        val map: MutableMap<T, Int> = HashMap()
        list.forEach {
            val temp = map[it]
            map[it] = if (temp == null) 1 else temp + 1
        }

        var max: Map.Entry<T, Int>? = null
        map.entries.forEach {
            if (max == null || it.value > max?.value!!) max = it
        }

        return max!!.key
    }

    @JvmStatic
    fun dpToPx(context: Context, dp: Float): Int {
        return (dp * context.resources.displayMetrics.density).roundToInt()
    }

    @JvmStatic
    fun spToPx(context: Context, sp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}