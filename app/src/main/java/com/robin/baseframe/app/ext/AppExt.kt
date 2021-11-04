package com.robin.baseframe.ext

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.robin.baseframe.app.base.BaseApp


import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


/**
 * 获取进程号对应的进程名
 *
 * @param pid 进程号
 * @return 进程名
 */
fun getProcessName(pid: Int): String? {
    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
        var processName = reader.readLine()
        if (!TextUtils.isEmpty(processName)) {
            processName = processName.trim { it <= ' ' }
        }
        return processName
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
    } finally {
        try {
            reader?.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

    }
    return null
}

inline fun <reified T : Any> Activity.getValue(
    lable: String, defaultvalue: T? = null
) = lazy {
    val value = intent?.extras?.get(lable)
    if (value is T) value else defaultvalue
}

inline fun <reified T : Any> Activity.getValueNonNull(
    lable: String, defaultvalue: T? = null
) = lazy {
    val value = intent?.extras?.get(lable)
    requireNotNull((if (value is T) value else defaultvalue)) { lable }
}

// Fragment related
inline fun <reified T : Any> Fragment.getValue(lable: String, defaultvalue: T? = null) = lazy {
    val value = arguments?.get(lable)
    if (value is T) value else defaultvalue
}

inline fun <reified T : Any> Fragment.getValueNonNull(lable: String, defaultvalue: T? = null) =
    lazy {
        val value = arguments?.get(lable)
        requireNotNull(if (value is T) value else defaultvalue) { lable }
    }


fun Int.asColor() = ContextCompat.getColor(BaseApp.instance, this)
fun Int.asDrawable() = ContextCompat.getDrawable(BaseApp.instance, this)





