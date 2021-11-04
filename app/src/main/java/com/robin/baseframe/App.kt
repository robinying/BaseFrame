package com.robin.baseframe

import androidx.multidex.MultiDex
import com.robin.baseframe.app.base.BaseApp

class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }
}