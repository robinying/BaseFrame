package com.robin.baseframe.app.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.preference.PreferenceManager
import android.view.View
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.robin.baseframe.R
import com.robin.baseframe.app.base.Ktx
import java.lang.reflect.InvocationTargetException
import kotlin.math.roundToInt

object ColorUtils {
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

    /**
     * 获取当前主题颜色
     */
    fun getColor(context: Context): Int {
        val setting = PreferenceManager.getDefaultSharedPreferences(context)
        val defaultColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val color = setting.getInt("color", defaultColor)
        return if (color != 0 && Color.alpha(color) != 255) {
            defaultColor
        } else {
            color
        }

    }

    /**
     * 设置主题颜色
     */
    fun setColor(context: Context, color: Int) {
        val setting = PreferenceManager.getDefaultSharedPreferences(context)
        setting.edit().putInt("color", color).apply()
    }

    fun getColorStateList(context: Context): ColorStateList {
        val colors = intArrayOf(getColor(context), ContextCompat.getColor(context, R.color.colorGray))
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_checked, android.R.attr.state_checked)
        states[1] = intArrayOf()
        return ColorStateList(states, colors)
    }
    fun getColorStateList(color: Int): ColorStateList {
        val colors = intArrayOf(color, ContextCompat.getColor(Ktx.app, R.color.colorGray))
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_checked, android.R.attr.state_checked)
        states[1] = intArrayOf()
        return ColorStateList(states, colors)
    }

    fun getOneColorStateList(context: Context): ColorStateList {
        val colors = intArrayOf(getColor(context))
        val states = arrayOfNulls<IntArray>(1)
        states[0] = intArrayOf()
        return ColorStateList(states, colors)
    }

    fun getOneColorStateList(color: Int): ColorStateList {
        val colors = intArrayOf(color)
        val states = arrayOfNulls<IntArray>(1)
        states[0] = intArrayOf()
        return ColorStateList(states, colors)
    }

    /**
     * 设置shap文件的颜色
     *
     * @param view
     * @param color
     */
    fun setShapColor(view: View, color: Int) {
        val drawable = view.background as GradientDrawable
        drawable.setColor(color)
    }

    /**
     * 设置shap的渐变颜色
     */
    fun setShapColor(view: View, color: IntArray, orientation: GradientDrawable.Orientation) {
        val drawable = view.background as GradientDrawable
        drawable.orientation = orientation//渐变方向
        drawable.colors = color//渐变颜色数组
    }

    /**
     * 设置selector文件的颜色
     *
     * @param view
     * @param yesColor
     * @param noColor
     */
    fun setSelectorColor(view: View, yesColor: Int, noColor: Int) {
        val mySelectorGrad = view.background as StateListDrawable
        try {
            val slDraClass = StateListDrawable::class.java
            val getStateCountMethod = slDraClass.getDeclaredMethod("getStateCount", *arrayOfNulls(0))
            val getStateSetMethod = slDraClass.getDeclaredMethod("getStateSet", Int::class.javaPrimitiveType)
            val getDrawableMethod = slDraClass.getDeclaredMethod("getStateDrawable", Int::class.javaPrimitiveType)
            val count = getStateCountMethod.invoke(mySelectorGrad) as Int//对应item标签
            for (i in 0 until count) {
                val stateSet = getStateSetMethod.invoke(mySelectorGrad, i) as IntArray//对应item标签中的 android:state_xxxx
                if (stateSet.isEmpty()) {
                    val drawable = getDrawableMethod.invoke(mySelectorGrad, i) as GradientDrawable//这就是你要获得的Enabled为false时候的drawable
                    drawable.setColor(yesColor)
                } else {
                    for (j in stateSet.indices) {
                        val drawable = getDrawableMethod.invoke(mySelectorGrad, i) as GradientDrawable//这就是你要获得的Enabled为false时候的drawable
                        drawable.setColor(noColor)
                    }
                }
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    /**
     * 设置颜色透明一半
     * @param color
     * @return
     */
    fun translucentColor(color: Int): Int {
        val factor = 0.5f
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}