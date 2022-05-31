package com.robin.baseframe.service

import android.app.Service
import android.content.Intent
import android.os.*
import com.robin.aidldemo.Apple
import com.robin.aidldemo.IRemoteService
import com.robin.aidldemo.IRemoteServiceCallBack

class RemoteObserverService : Service() {
    private val APPLE_INFO = 0x0706
    val mCallbacks = RemoteCallbackList<IRemoteServiceCallBack>()

    override fun onCreate() {
        super.onCreate()
        Thread {
            try {
                Thread.sleep((3 * 1000).toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val apple = Apple("红富士", 10f, "Remote Service Info")
            val message = Message.obtain()
            message.what = APPLE_INFO
            message.obj = apple
            mHandler.sendMessage(message)
        }.start()
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                APPLE_INFO -> {
                    val apple: Apple = msg.obj as Apple
                    //观察者模式，通知所有客户端
                    val clientNum = mCallbacks.beginBroadcast()
                    var i = 0
                    while (i < clientNum) {
                        val callBack = mCallbacks.getBroadcastItem(i)
                        if (callBack != null && apple != null) {
                            try {
                                callBack.noticeAppleInfo(apple)
                            } catch (e: RemoteException) {
                                e.printStackTrace()
                            }
                        }
                        mCallbacks.finishBroadcast()
                        i++
                    }
                }
            }
            super.handleMessage(msg)
        }
    }
    override fun onBind(intent: Intent): IBinder {
        return mIBinder
    }

    private val mIBinder = object :IRemoteService.Stub(){
        override fun registerCallback(cb: IRemoteServiceCallBack?) {
            cb?.let {
                mCallbacks.register(it)
            }
        }

        override fun unregisterCallback(cb: IRemoteServiceCallBack?) {
            cb?.let {
                mCallbacks.unregister(it)
            }
        }

    }
}