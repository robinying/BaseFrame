package com.robin.baseframe

import androidx.multidex.MultiDex
import com.robin.baseframe.app.base.BaseApp
import com.robin.baseframe.app.ext.util.openLog
import com.robin.baseframe.app.util.PageStack
import com.robin.module_web.WebViewPool
import dagger.hilt.android.HiltAndroidApp
import per.goweii.anylayer.AnyLayer
import java.lang.Integer.min

@HiltAndroidApp
class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        AnyLayer.init(this)
        openLog = BuildConfig.DEBUG
        WebViewPool.getInstance().setMaxPoolSize(min(Runtime.getRuntime().availableProcessors(), 3))
        WebViewPool.getInstance().init(applicationContext)
        registerActivityLifecycleCallbacks(PageStack)
    }
}