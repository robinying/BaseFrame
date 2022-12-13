package com.robin.module_web

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class BaseWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs), LifecycleEventObserver {

    init {
        // WebView 调试模式开关
        setWebContentsDebuggingEnabled(true)
        // 不显示滚动条
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        // 初始化设置
        WebUtils.defaultSettings(context, this)
    }

    /**
     * 获取当前url
     */
    override fun getUrl(): String? {
        return super.getOriginalUrl() ?: return super.getUrl()
    }

    override fun canGoBack(): Boolean {
        val backForwardList = copyBackForwardList()
        val currentIndex = backForwardList.currentIndex - 1
        if (currentIndex >= 0) {
            val item = backForwardList.getItemAtIndex(currentIndex)
            if (item?.url == "about:blank") {
                return false
            }
        }
        return super.canGoBack()
    }

    /**
     * 设置 WebView 生命管控（自动回调生命周期方法）
     */
    fun setLifecycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    /**
     * 生命周期回调
     */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_STOP -> onPause()
            Lifecycle.Event.ON_DESTROY -> {
                source.lifecycle.removeObserver(this)
                onDestroy()
            }
        }
    }

    /**
     * 生命周期 onResume()
     */
    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()
        settings.javaScriptEnabled = true
    }

    /**
     * 生命周期 onPause()
     */
    override fun onPause() {
        super.onPause()
    }

    /**
     * 生命周期 onDestroy()
     * 父类没有 需要自己写
     */
    fun onDestroy() {
        settings.javaScriptEnabled = false
        WebViewPool.getInstance().recycle(this)
    }

    /**
     * 释放资源操作
     */
    fun release() {
        (parent as ViewGroup?)?.removeView(this)
        removeAllViews()
        stopLoading()
        setCustomWebViewClient(null)
        setCustomWebChromeClient(null)
        loadUrl("about:blank")
        clearHistory()
    }

    fun setCustomWebViewClient(client: BaseWebViewClient?) {
        if (client == null) {
            super.setWebViewClient(WebViewClient())
        } else {
            super.setWebViewClient(client)
        }
    }

    fun setCustomWebChromeClient(client: BaseWebChromeClient?) {
        super.setWebChromeClient(client)
    }
}