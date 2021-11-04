package com.robin.baseframe.app.network

import android.util.Log
import okhttp3.Call
import okhttp3.EventListener
import java.io.IOException
import java.net.InetAddress

class OkHttpEventListener : EventListener() {
    private val TAG = this.javaClass.simpleName

    private val okhttpEvent = OkhttpEvent()
    override fun dnsStart(call: Call, domainName: String) {
        super.dnsStart(call, domainName)
        okhttpEvent.dnsStartTime = System.currentTimeMillis()
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: MutableList<InetAddress>) {
        super.dnsEnd(call, domainName, inetAddressList)
        okhttpEvent.dnsEndTime = System.currentTimeMillis()
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        super.responseBodyEnd(call, byteCount)
        okhttpEvent.responseBodySize = byteCount
    }

    override fun callEnd(call: Call) {
        super.callEnd(call)
        okhttpEvent.isSuccess = true
    }

    override fun callFailed(call: Call, ioe: IOException) {
        super.callFailed(call, ioe)
        okhttpEvent.isSuccess = false
        okhttpEvent.errorStack = Log.getStackTraceString(ioe)
        Log.d(TAG, "http stack error:${okhttpEvent.errorStack}")
    }

    companion object {
        val FACTORY = Factory { OkHttpEventListener() }
    }
}