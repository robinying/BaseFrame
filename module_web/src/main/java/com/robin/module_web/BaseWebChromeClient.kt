package com.robin.module_web

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog

/** WebChromeClient that only exposes policy-approved browser capabilities. */
class BaseWebChromeClient(
    private val policy: WebViewPolicy = WebViewPolicy.DEFAULT
) : WebChromeClient() {

    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Web console: ${consoleMessage.message()}")
        }
        return super.onConsoleMessage(consoleMessage)
    }

    override fun onJsAlert(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        if (!policy.allowsJavaScript(url)) {
            result.cancel()
            return true
        }
        AlertDialog.Builder(view.context)
            .setTitle("网页提示")
            .setMessage(message)
            .setPositiveButton("确认") { dialog, _ ->
                dialog.dismiss()
                result.confirm()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
                result.cancel()
            }
            .show()
        return true
    }

    override fun onJsConfirm(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        if (!policy.allowsJavaScript(url)) {
            result.cancel()
            return true
        }
        AlertDialog.Builder(view.context)
            .setTitle("网页提示")
            .setMessage(message)
            .setPositiveButton("确认") { dialog, _ ->
                dialog.dismiss()
                result.confirm()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
                result.cancel()
            }
            .show()
        return true
    }

    override fun onGeolocationPermissionsShowPrompt(
        origin: String,
        callback: GeolocationPermissions.Callback
    ) {
        callback.invoke(origin, policy.allowsGeolocation(origin), false)
    }

    private companion object {
        const val TAG = "BaseWebChromeClient"
    }
}
