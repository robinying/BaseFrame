package com.robin.baseframe.app.util

import android.text.TextUtils
import android.util.Log
import com.robin.baseframe.app.ext.util.openLog

/**
 * 作者　: hegaojian
 * 时间　: 2020/3/26
 * 描述　:
 */
object LogUtils {
    private const val DEFAULT_TAG = "BaseLog"
    fun debugInfo(tag: String?, msg: String?) {
        if (!openLog || TextUtils.isEmpty(msg)) {
            return
        }
        Log.d(tag, msg!!)
    }

    fun debugInfo(msg: String?) {
        debugInfo(
            DEFAULT_TAG,
            msg
        )
    }

    fun warnInfo(tag: String?, msg: String?) {
        if (!openLog || TextUtils.isEmpty(msg)) {
            return
        }
        Log.w(tag, msg!!)
    }

    fun warnInfo(msg: String?) {
        warnInfo(
            DEFAULT_TAG,
            msg
        )
    }

    /**
     * 这里使用自己分节的方式来输出足够长度的 message
     *
     * @param tag 标签
     * @param msg 日志内容
     */
    fun debugLongInfo(tag: String?, msg: String) {
        var msg = msg
        if (!openLog || TextUtils.isEmpty(msg)) {
            return
        }
        msg = msg.trim { it <= ' ' }
        var index = 0
        val maxLength = 3500
        var sub: String
        while (index < msg.length) {
            sub = if (msg.length <= index + maxLength) {
                msg.substring(index)
            } else {
                msg.substring(index, index + maxLength)
            }
            index += maxLength
            Log.d(tag, sub.trim { it <= ' ' })
        }
    }

    fun debugLongInfo(msg: String) {
        debugLongInfo(
            DEFAULT_TAG,
            msg
        )
    }
}

fun <T> Collection<T>.print(mapper: (T) -> String) =
    StringBuilder("\n[").also { sb ->
        //遍历集合元素将元素转换成感兴趣的字串，并独占一行
        this.forEach { e -> sb.append("\n\t${mapper(e)},") }
        sb.append("\n]")
    }.toString()


/**
 * 打印 Map，生成结构化键值对子串
 * @param space 行缩进量
 */
fun <K, V> Map<K, V?>.print(space: Int = 0): String {
    //'生成当前层次的行缩进，用space个空格表示，当前层次每一行内容都需要带上缩进'
    val indent = StringBuilder().apply {
        repeat(space) { append(" ") }
    }.toString()
    return StringBuilder("\n${indent}{").also { sb ->
        this.iterator().forEach { entry ->
            //'如果值是 Map 类型，则递归调用print()生成其结构化键值对子串，否则返回值本身'
            val value = entry.value.let { v ->
                (v as? Map<*, *>)?.print("${indent}${entry.key} = ".length) ?: v.toString()
            }
            sb.append("\n\t${indent}[${entry.key}] = $value,")
        }
        sb.append("\n${indent}}")
    }.toString()
}