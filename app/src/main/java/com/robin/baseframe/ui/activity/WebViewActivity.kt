package com.robin.baseframe.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import com.robin.baseframe.app.base.BaseViewActivity
import com.robin.baseframe.databinding.ActivityWebviewBinding
import com.robin.module_web.BaseWebView
import com.robin.module_web.WebViewPolicy
import com.robin.module_web.WebViewPool

/** Hosts a policy-controlled WebView from the shared module pool. */
class WebViewActivity : BaseViewActivity<ActivityWebviewBinding>() {
    private lateinit var mWebView: BaseWebView

    override fun initView(savedInstanceState: Bundle?) {
        initWebView()
    }

    private fun initWebView() {
        mWebView = WebViewPool.getInstance().getWebView(mActivity, WebViewPolicy.DEFAULT)
        binding.webContent.addView(
            mWebView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onPause() {
        mWebView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mWebView.onResume()
    }

    override fun onDestroy() {
        WebViewPool.getInstance().recycle(mWebView)
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
