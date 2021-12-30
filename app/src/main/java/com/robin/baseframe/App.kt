package com.robin.baseframe

import androidx.multidex.MultiDex
import com.robin.baseframe.app.base.BaseApp
import com.robin.baseframe.app.ext.util.openLog
import per.goweii.anylayer.AnyLayer

class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        AnyLayer.init(this)
        openLog = BuildConfig.DEBUG

        
    }
}