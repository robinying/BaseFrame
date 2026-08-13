package com.robin.module_web

import android.graphics.Color
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView

object WebUtils {
    /** Applies the baseline-safe settings that are shared by every pooled WebView. */
    fun defaultSettings(webView: WebView) {
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.setBackgroundResource(R.color.white)
        webView.overScrollMode = View.OVER_SCROLL_NEVER
        webView.isNestedScrollingEnabled = false

        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.setSupportZoom(false)
        webView.settings.builtInZoomControls = false
        webView.settings.displayZoomControls = false
        webView.settings.textZoom = 100
        @Suppress("DEPRECATION")
        webView.settings.savePassword = false
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.settings.javaScriptEnabled = false
        webView.settings.javaScriptCanOpenWindowsAutomatically = false
        webView.settings.loadsImagesAutomatically = true
        webView.settings.blockNetworkImage = false
        webView.settings.defaultTextEncodingName = "utf-8"
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        webView.settings.domStorageEnabled = false
        webView.settings.databaseEnabled = false
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
    }
}
