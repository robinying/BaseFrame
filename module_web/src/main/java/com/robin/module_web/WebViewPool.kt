package com.robin.module_web

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Looper
import java.util.ArrayDeque

/** Main-thread WebView pool with bounded retention and explicit trimming. */
class WebViewPool private constructor() {

    private val mPool = ArrayDeque<BaseWebView>()
    private var mMaxSize = DEFAULT_MAX_SIZE

    /** Sets the maximum retained instances. Excess cached instances are destroyed. */
    fun setMaxPoolSize(size: Int) {
        checkMainThread()
        mMaxSize = size.coerceAtLeast(0)
        trimToSize(mMaxSize)
    }

    /** Prewarms up to [initSize] instances with the safe default policy. */
    fun init(context: Context, initSize: Int = mMaxSize) {
        checkMainThread()
        repeat((initSize - mPool.size).coerceAtLeast(0)) {
            if (mPool.size < mMaxSize) {
                mPool.addLast(create(context.applicationContext))
            }
        }
    }

    /** Obtains a WebView whose page-specific state has been cleared. */
    fun getWebView(context: Context, policy: WebViewPolicy = WebViewPolicy.DEFAULT): BaseWebView {
        checkMainThread()
        val webView = if (mPool.isEmpty()) {
            create(context.applicationContext)
        } else {
            mPool.removeFirst()
        }
        (webView.context as MutableContextWrapper).baseContext = context
        webView.configure(policy)
        webView.clearHistory()
        webView.resumeTimers()
        return webView
    }

    /** Clears page state and retains the WebView only when capacity remains. */
    fun recycle(webView: BaseWebView) {
        checkMainThread()
        if (mPool.contains(webView)) {
            return
        }
        webView.release()
        webView.pauseTimers()
        (webView.context as? MutableContextWrapper)?.baseContext = webView.context.applicationContext
        if (mPool.size < mMaxSize) {
            mPool.addLast(webView)
        } else {
            webView.destroy()
        }
    }

    /** Destroys cached instances, for example when the process receives trim-memory. */
    fun destroyAll() {
        checkMainThread()
        while (mPool.isNotEmpty()) {
            mPool.removeFirst().apply {
                release()
                destroy()
            }
        }
    }

    private fun create(context: Context): BaseWebView =
        BaseWebView(MutableContextWrapper(context)).apply {
            configure(WebViewPolicy.DEFAULT)
        }

    private fun trimToSize(size: Int) {
        while (mPool.size > size) {
            mPool.removeLast().apply {
                release()
                destroy()
            }
        }
    }

    private fun checkMainThread() {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "WebViewPool must be accessed from the main thread."
        }
    }

    companion object {
        private const val DEFAULT_MAX_SIZE = 1

        @Volatile
        private var sInstance: WebViewPool? = null

        fun getInstance(): WebViewPool = sInstance ?: synchronized(this) {
            sInstance ?: WebViewPool().also { sInstance = it }
        }
    }
}
