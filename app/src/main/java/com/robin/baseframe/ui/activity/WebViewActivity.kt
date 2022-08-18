package com.robin.baseframe.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import com.robin.baseframe.app.base.BaseActivity
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.databinding.ActivityWebviewBinding
import com.robin.baseframe.widget.webview.MyWebView
import com.robin.baseframe.widget.webview.WebViewManager

class WebViewActivity:BaseActivity<BaseViewModel,ActivityWebviewBinding>() {
    private lateinit var mWebView:MyWebView
    private var mWebUrl = "https://www.baidu.com"
    override fun initView(savedInstanceState: Bundle?) {
        initWeb()
    }

    private fun initWeb() {
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mWebView = WebViewManager.obtain(mActivity)  //管理类获取对象
        mWebView.layoutParams = params

        mWebView.addJavascriptInterface(H5CallBackAndroid(), "webkit")

        binding.webContent.addView(mWebView)
    }

    override fun onPause() {
        super.onPause()
        mWebView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mWebView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        WebViewManager.recycle(mWebView)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val webBackForwardList = mWebView.copyBackForwardList()
        val historyOneOriginalUrl = webBackForwardList.getItemAtIndex(0)?.originalUrl
        val curIndex = webBackForwardList.currentIndex

        return if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {

            //判断是否是缓存的WebView
            if (historyOneOriginalUrl?.contains("data:text/html;charset=utf-8") == true) {
                //说明是缓存复用的的WebView
                if (curIndex > 1) {
                    //内部跳转到另外的页面了，可以返回的
                    mWebView.goBack()
                    true
                } else {
                    //等于1的时候就要Finish页面了
                    super.onKeyDown(keyCode, event)
                }
            } else {
                //如果不是缓存复用的WebView，可以直接返回
                mWebView.goBack()
                true
            }
        } else {
            super.onKeyDown(keyCode, event)
        }

    }

    inner class H5CallBackAndroid {

        //图片的点击
        @JavascriptInterface
        fun clickImage(obj: String) {

        }

    }
}