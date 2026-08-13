package com.robin.baseframe.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.RemoteCallbackList
import android.os.RemoteException
import com.robin.aidldemo.Apple
import com.robin.aidldemo.IRemoteService
import com.robin.aidldemo.IRemoteServiceCallBack
import com.robin.baseframe.app.util.LogUtils

/** Provides apple information to registered AIDL clients. */
class RemoteObserverService : Service() {
    private val mCallbacks = RemoteCallbackList<IRemoteServiceCallBack>()
    private val mHandler = Handler(Looper.getMainLooper())
    private val mNotifyAppleInfo = Runnable { notifyAppleInfo() }

    override fun onCreate() {
        super.onCreate()
        mHandler.postDelayed(mNotifyAppleInfo, NOTIFICATION_DELAY_MILLIS)
    }

    override fun onBind(intent: Intent): IBinder = mBinder

    override fun onDestroy() {
        mHandler.removeCallbacks(mNotifyAppleInfo)
        mCallbacks.kill()
        super.onDestroy()
    }

    private fun notifyAppleInfo() {
        val apple = Apple("红富士", 10f, "Remote Service Info")
        val clientCount = mCallbacks.beginBroadcast()
        try {
            for (index in 0 until clientCount) {
                try {
                    mCallbacks.getBroadcastItem(index).noticeAppleInfo(apple)
                } catch (exception: RemoteException) {
                    LogUtils.warnInfo(TAG, "Unable to notify RemoteObserverService client")
                }
            }
        } finally {
            mCallbacks.finishBroadcast()
        }
    }

    private val mBinder = object : IRemoteService.Stub() {
        override fun registerCallback(callback: IRemoteServiceCallBack?) {
            callback?.let { mCallbacks.register(it) }
        }

        override fun unregisterCallback(callback: IRemoteServiceCallBack?) {
            callback?.let { mCallbacks.unregister(it) }
        }
    }

    private companion object {
        const val NOTIFICATION_DELAY_MILLIS = 3_000L
        const val TAG = "RemoteObserverService"
    }
}
