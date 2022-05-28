package com.robin.baseframe.viewmodel

import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.util.LogUtils
import java.lang.ref.ReferenceQueue
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

class MainViewModel : BaseViewModel() {

    //回收
    fun testReference() {
        launchOnIO {
            var stu: Object? = Object()

            LogUtils.debugInfo("软引用")
            var softReferenceQueue = ReferenceQueue<Any>()
            var softReference = SoftReference<Any>(stu, softReferenceQueue)
            LogUtils.debugInfo("softReference:" + softReference.get())
            LogUtils.debugInfo("soft queue:" + softReferenceQueue.poll())

            stu = null
            System.gc()
            Thread.sleep(2000)
            LogUtils.debugInfo("gc之后对象存活情况")
            LogUtils.debugInfo("softReference:" + softReference.get())
            LogUtils.debugInfo("soft queue:" + softReferenceQueue.poll())

            var weak: Object? = Object()
            LogUtils.debugInfo("弱引用")
            var weakReferenceQueue = ReferenceQueue<Any>()
            var weakReference = WeakReference<Any>(weak, weakReferenceQueue)
            LogUtils.debugInfo("weakReference:" + weakReference.get())
            LogUtils.debugInfo("weak queue:" + weakReferenceQueue.poll())

            weak = null
            System.gc()
            Thread.sleep(2000)
            LogUtils.debugInfo("gc之后对象存活情况")
            LogUtils.debugInfo("weakReference:" + weakReference.get())
            LogUtils.debugInfo("weak queue:" + weakReferenceQueue.poll())
        }
    }
}