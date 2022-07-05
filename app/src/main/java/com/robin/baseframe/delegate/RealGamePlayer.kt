package com.robin.baseframe.delegate

import com.robin.baseframe.app.util.LogUtils

class RealGamePlayer(private val name: String) : IGamePlayer {
    override fun rank() {
        LogUtils.debugInfo("$name 排位")
    }

    override fun upgrade() {
        LogUtils.debugInfo("$name 升级")
    }
}