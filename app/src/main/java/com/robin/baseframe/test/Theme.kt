package com.robin.baseframe.test

import android.content.Context
import android.util.TypedValue

object Theme {
    /**
     * 获取主题属性的资源id
     */
    fun getThemeColor(context: Context, attr: Int, defaultColor: Int): Int {
        val obtainStyledAttributes = context.theme.obtainStyledAttributes(intArrayOf(attr))
        val redIds = IntArray(obtainStyledAttributes.indexCount)
        for (i in 0 until obtainStyledAttributes.indexCount) {
            val type = obtainStyledAttributes.getType(i)
            redIds[i] =
                    //
                if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
                    obtainStyledAttributes.getColor(i, defaultColor)
                } else {
                    defaultColor
                }
        }
        obtainStyledAttributes.recycle()
        return redIds[0]
    }

    /**
     * 获取主题属性的资源id，方案二
     */
    fun getThemeColor2(context: Context, attr: Int, defaultColor: Int): Int {
        val typedValue = TypedValue()
        val success = context.theme.resolveAttribute(
            attr,
            typedValue,
            true
        )
        return if (success) {
            if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT
            ) {
                typedValue.data
            } else {
                defaultColor
            }
        } else {
            defaultColor
        }
    }

}