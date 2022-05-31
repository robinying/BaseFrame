package com.robin.baseframe.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

import com.robin.aidldemo.Apple
import com.robin.aidldemo.IApiCallBack

class RemoteService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    private val mBinder: IApiCallBack.Stub = object : IApiCallBack.Stub() {

        override fun getAppleInfo(): Apple {
            return Apple("蛇果", 20f, "server info")
        }
    }
}