package com.robin.module_web

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi

/**
 * WebView client that blocks non-HTTPS and untrusted navigation.
 *
 * Resource interception is intentionally absent: blocking network I/O from
 * [shouldInterceptRequest] can stall WebView's loader thread.
 */
class BaseWebViewClient(
    private val policy: WebViewPolicy = WebViewPolicy.DEFAULT
) : WebViewClient() {

    override fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler,
        error: SslError
    ) {
        handler.cancel()
    }

    @RequiresApi(23)
    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        if (request.isForMainFrame) {
            super.onReceivedError(view, request, error)
        }
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest
    ): Boolean {
        loadIfTrusted(view, request.url.toString())
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        loadIfTrusted(view, url)
        return true
    }

    private fun loadIfTrusted(view: WebView, url: String): Boolean {
        if (!policy.isTrusted(url)) {
            return false
        }
        if (view is BaseWebView) {
            return view.loadTrustedUrl(url)
        }
        view.loadUrl(url)
        return true
    }
}
