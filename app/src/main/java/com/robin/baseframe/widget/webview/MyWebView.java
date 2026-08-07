package com.robin.baseframe.widget.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class MyWebView extends WebView {

   private WebSettings mWebSettings;
   private boolean isNeedExe = true;

   public MyWebView(Context context) {
      super(context);
      initView();
   }

   public MyWebView(Context context, AttributeSet attrs) {
      super(context, attrs);
      initView();
   }

   public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      initView();
   }

   @SuppressLint({"ObsoleteSdkInt", "SetJavaScriptEnabled"})
   private void initView() {

      mWebSettings = getSettings();
      mWebSettings.setSupportZoom(false);
      mWebSettings.setBuiltInZoomControls(false);
      mWebSettings.setDefaultTextEncodingName("utf-8");
      mWebSettings.setJavaScriptEnabled(true);
      mWebSettings.setDefaultFontSize(16);
      mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
      mWebSettings.setGeolocationEnabled(true);   //允许访问地址

      // 禁止文件跨域访问
      mWebSettings.setAllowFileAccess(false);
      mWebSettings.setAllowFileAccessFromFileURLs(false);
      mWebSettings.setAllowUniversalAccessFromFileURLs(false);

      setVerticalScrollBarEnabled(false);
      setVerticalScrollbarOverlay(false);
      setHorizontalScrollBarEnabled(false);
      setHorizontalScrollbarOverlay(false);
      setOverScrollMode(OVER_SCROLL_NEVER);
      setFocusable(true);
      setHorizontalScrollBarEnabled(false);
      setDrawingCacheEnabled(true);

      //加载https的兼容
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         //两者都可以
         mWebSettings.setMixedContentMode(mWebSettings.getMixedContentMode());
         //mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
      }


      //先加载页面再加载图片，这里先禁止图片加载
      if (Build.VERSION.SDK_INT >= 19) {
         mWebSettings.setLoadsImagesAutomatically(true);
      } else {
         mWebSettings.setLoadsImagesAutomatically(false);
      }


      setWebViewClient(mWebViewClient);
      setWebChromeClient(mWebChromeClient);
   }


   WebViewClient mWebViewClient = new WebViewClient() {
      // SSL 证书校验失败时直接拒绝，不提供用户绕过选项
      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
         handler.cancel();
      }

      //页面加载完成，展示图片
      @Override
      public void onPageFinished(WebView view, String url) {
         if (!mWebSettings.getLoadsImagesAutomatically()) {
            mWebSettings.setLoadsImagesAutomatically(true);
         }
      }

      //在当前的webview中跳转到新的url
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
         if (mListener != null) mListener.onInnerLinkChecked();

         if (Build.VERSION.SDK_INT < 26) {
            if (!TextUtils.isEmpty(url)) {
               view.loadUrl(url);
            }
            return true;
         }
         return false;
      }

      //WebView加载错误的回调
      @Override
      public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
         super.onReceivedError(view, request, error);
         if (mListener != null) mListener.onWebLoadError();
      }

      //拦截WebView中的网络请求
      @Nullable
      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
         return super.shouldInterceptRequest(view, request);
      }

   };


   WebChromeClient mWebChromeClient = new WebChromeClient() {
      //获取html的title标签
      @Override
      public void onReceivedTitle(WebView view, String title) {
         if (mListener != null) mListener.titleChange(title);
         super.onReceivedTitle(view, title);
      }

      //获取页面加载的进度
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
         if (mListener != null) mListener.progressChange(newProgress);
         super.onProgressChanged(view, newProgress);

         if (newProgress > 95 && isNeedExe) {
            isNeedExe = !isNeedExe;

            if (newProgress == 100) {
               //注入js代码测量webview高度
               loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height)");
            }
         }

      }

      // 指定源的网页内容在没有设置权限状态下尝试使用地理位置API。
      @Override
      public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
         boolean allow = true;   // 是否允许origin使用定位API
         boolean retain = false; // 内核是否记住这次制授权
         callback.invoke(origin, true, false);
      }

      // 之前调用 onGeolocationPermissionsShowPrompt() 申请的授权被取消时，隐藏相关的UI。
      @Override
      public void onGeolocationPermissionsHidePrompt() {
      }

      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
      @Override
      public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
         //启动系统相册

         new Handler().post(() -> {
            if (mFilesListener != null) mFilesListener.onWebFileSelect(filePathCallback);
         });

         return true;
      }

   };

   //网页状态的回调相关处理
   private OnWebChangeListener mListener;

   public interface OnWebChangeListener {
      void titleChange(String title);

      void progressChange(int progress);

      void onInnerLinkChecked();

      void onWebLoadError();
   }

   public void setOnWebChangeListener(OnWebChangeListener listener) {
      mListener = listener;
   }

   //网页选择图片文件的回调相关处理
   private OnWebChooseFileListener mFilesListener;

   public interface OnWebChooseFileListener {

      void onWebFileSelect(ValueCallback<Uri[]> callback);
   }

   public void setOnWebChooseFileListener(OnWebChooseFileListener listener) {
      mFilesListener = listener;
   }


   /**
    * 暴露方法，是否滑动到底部
    */
   public boolean isScrollBottom() {
      if (getContentHeight() * getScale() == (getHeight() + getScrollY())) {
         //说明已经到底了
         return true;
      } else {
         return false;
      }
   }

}
