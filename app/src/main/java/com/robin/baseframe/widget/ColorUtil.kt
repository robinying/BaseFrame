package com.robin.baseframe.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.graphics.ColorUtils
import com.robin.baseframe.R
import com.robin.baseframe.app.util.NumberUtils
import kotlin.random.Random

object ColorUtil {

    const val DEFAULT_BRIGHTNESS = 0.75f

    fun gradientColor(fraction: Float, startColor: Int, endColor: Int): Int {
        val startA = startColor shr 24 and 0xff
        val startR = startColor shr 16 and 0xff
        val startG = startColor shr 8 and 0xff
        val startB = startColor and 0xff
        val endA = endColor shr 24 and 0xff
        val endR = endColor shr 16 and 0xff
        val endG = endColor shr 8 and 0xff
        val endB = endColor and 0xff
        val currentA = startA + (fraction * (endA - startA)).toInt() shl 24
        val currentR = startR + (fraction * (endR - startR)).toInt() shl 16
        val currentG = startG + (fraction * (endG - startG)).toInt() shl 8
        val currentB = startB + (fraction * (endB - startB)).toInt()
        return currentA or currentR or currentG or currentB
    }

    fun isDark(color: Int): Boolean {
        return isDark(Color.red(color), Color.green(color), Color.blue(color))
    }

    /**
     * 根据RGB值判断 深色与浅色
     */
    fun isDark(r: Int, g: Int, b: Int): Boolean {
        return r * 0.299 + g * 0.578 + b * 0.114 < 192
    }

    fun brightnessColor(color: Int, @FloatRange(from = 0.0) brightness: Float): Int {
        if (color == Color.TRANSPARENT) return color
        val alpha = Color.alpha(color)
        val hslArray = FloatArray(3)
        ColorUtils.colorToHSL(color, hslArray)
        hslArray[2] = hslArray[2] * brightness
        var result = ColorUtils.HSLToColor(hslArray)
        if (result == Color.BLACK) {
            result = Color.parseColor("#575757")
        } else if (result == Color.WHITE) {
            result = Color.parseColor("#EAEAEA")
        }
        return Color.argb(alpha, Color.red(result), Color.green(result), Color.blue(result))
    }

    fun alphaColor(@FloatRange(from = 0.0, to = 1.0) alpha: Float, sourceColor: Int): Int {
        var alpha = alpha
        alpha = NumberUtils.range(alpha, 0f, 1f)
        val R = Color.red(sourceColor)
        val G = Color.green(sourceColor)
        val B = Color.blue(sourceColor)
        val A = (alpha * 255.0f + 0.5f).toInt()
        return Color.argb(A, R, G, B)
    }


    @SuppressLint("ResourceType")
    fun randomColor(context: Context, @IntRange(from = 0, to = 255) alpha: Int = 255): Int = let {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val random = Random
            return@let Color.argb(
                alpha,
                random.nextInt(225),
                random.nextInt(225),
                random.nextInt(225),
            )
        } else {
            return@let context.resources.getColor(R.color.md_blue_500)
        }
    }
}