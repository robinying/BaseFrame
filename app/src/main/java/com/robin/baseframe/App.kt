package com.robin.baseframe

import android.os.Looper
import androidx.multidex.MultiDex
import com.robin.baseframe.app.base.BaseApp
import com.robin.baseframe.app.ext.util.openLog
import com.robin.baseframe.widget.webview.WebViewManager
import com.robin.module_web.WebViewPool
import per.goweii.anylayer.AnyLayer
import java.lang.Integer.min

class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        AnyLayer.init(this)
        openLog = BuildConfig.DEBUG
        //空闲的时候初始化WebView容器
        Looper.myQueue().addIdleHandler {
            //初始化WebView缓存容器
            WebViewManager.prepare(this)
            false
        }
        // 根据手机 CPU 核心数（或者手机内存等条件）设置缓存池容量
        WebViewPool.getInstance().setMaxPoolSize(min(Runtime.getRuntime().availableProcessors(), 3))
        WebViewPool.getInstance().init(applicationContext)
    }
}