package com.robin.baseframe.ext

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.robin.baseframe.app.base.BaseApp
import com.robin.baseframe.app.util.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


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

/**
 * 倒计时的实现
 */
@ExperimentalCoroutinesApi
fun LifecycleOwner.countDown(
    time: Int = 5,
    start: (scope: CoroutineScope) -> Unit,
    end: () -> Unit,
    next: (time: Int) -> Unit
) {

    lifecycleScope.launch {
        // 在这个范围内启动的协程会在Lifecycle被销毁的时候自动取消

        flow {
            (time downTo 0).forEach {
                delay(1000)
                emit(it)
            }
        }.onStart {
            // 倒计时开始 ，在这里可以让Button 禁止点击状态
            start(this@launch)

        }.onCompletion {
            // 倒计时结束 ，在这里可以让Button 恢复点击状态
            end()

        }.catch {
            //错误
            LogUtils.debugInfo(it.message ?: "Unkown Error")

        }.collect {
            // 在这里 更新值来显示到UI
            next(it)
        }
    }
}






