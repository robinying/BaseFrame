package com.robin.baseframe

import android.os.Looper
import androidx.multidex.MultiDex
import com.robin.baseframe.app.base.BaseApp
import com.robin.baseframe.app.ext.util.openLog
import com.robin.baseframe.widget.webview.WebViewManager
import per.goweii.anylayer.AnyLayer

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
        
    }
}