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

/**
 * Lifecycle-aware WebView with a deny-by-default security policy.
 */
class BaseWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(context, attrs), LifecycleEventObserver {

    private var mPolicy: WebViewPolicy = WebViewPolicy.DEFAULT

    init {
        setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        WebUtils.defaultSettings(this)
        applyPolicy()
    }

    override fun getUrl(): String? = super.getOriginalUrl() ?: super.getUrl()

    override fun canGoBack(): Boolean {
        val backForwardList = copyBackForwardList()
        val previousIndex = backForwardList.currentIndex - 1
        if (previousIndex >= 0 && backForwardList.getItemAtIndex(previousIndex)?.url == "about:blank") {
            return false
        }
        return super.canGoBack()
    }

    /** Applies a policy before the caller loads trusted remote content. */
    fun configure(policy: WebViewPolicy) {
        mPolicy = policy
        applyPolicy()
        setCustomWebViewClient(BaseWebViewClient(policy))
        setCustomWebChromeClient(BaseWebChromeClient(policy))
    }

    /** Starts a page load only when the URL belongs to the configured HTTPS allowlist. */
    fun loadTrustedUrl(url: String): Boolean {
        if (!mPolicy.isTrusted(url)) {
            return false
        }
        settings.javaScriptEnabled = mPolicy.allowsJavaScript(url)
        settings.domStorageEnabled = mPolicy.allowsJavaScript(url)
        settings.databaseEnabled = mPolicy.allowsJavaScript(url)
        super.loadUrl(url)
        return true
    }

    /** Registers lifecycle callbacks for the owner of this WebView instance. */
    fun setLifecycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_STOP -> onPause()
            Lifecycle.Event.ON_DESTROY -> {
                source.lifecycle.removeObserver(this)
                releaseToPool()
            }
            else -> Unit
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    /** Returns this instance to the pool. Call once when its owner no longer needs it. */
    fun releaseToPool() {
        WebViewPool.getInstance().recycle(this)
    }

    /** Clears all page-specific state before pooling or destruction. */
    fun release() {
        (parent as? ViewGroup)?.removeView(this)
        stopLoading()
        removeAllViews()
        removeJavascriptInterface(DEFAULT_BRIDGE_NAME)
        super.setWebViewClient(WebViewClient())
        super.setWebChromeClient(null)
        loadUrl("about:blank")
        clearHistory()
        settings.javaScriptEnabled = false
        settings.domStorageEnabled = false
        settings.databaseEnabled = false
    }

    fun setCustomWebViewClient(client: BaseWebViewClient?) {
        super.setWebViewClient(client ?: WebViewClient())
    }

    fun setCustomWebChromeClient(client: BaseWebChromeClient?) {
        super.setWebChromeClient(client)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun applyPolicy() {
        settings.javaScriptEnabled = false
        settings.domStorageEnabled = false
        settings.databaseEnabled = false
        removeJavascriptInterface(DEFAULT_BRIDGE_NAME)
    }

    private companion object {
        const val DEFAULT_BRIDGE_NAME = "webkit"
    }
}
