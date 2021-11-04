package com.robin.baseframe.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object KeyBoardUtils {
    fun showKeyboard(view: View) {
        val imm = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm != null) {
            view.requestFocus()
            imm.showSoftInput(view, 0)
        }
    }

    fun hideKeyboard(view: View) {
        val imm = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun toggleSoftInput(view: View) {
        val imm = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.toggleSoftInput(0, 0)
    }

    private var softInputHeight = 0
    private var softInputHeightChanged = false

    private var isNavigationBarShow = false
    private var navigationHeight = 0

    private var anyView: View? = null
    private var listener: ISoftInputChanged? = null
    private var isSoftInputShowing = false

    interface ISoftInputChanged {
        fun onChanged(isSoftInputShow: Boolean, softInputHeight: Int, viewOffset: Int)
    }

    @SuppressLint("StaticFieldLeak")
    fun attachSoftInput(anyView: EditText?, listener: ISoftInputChanged?) {
        if (anyView == null || listener == null) return

        //根View
        val rootView = anyView.rootView ?: return
        navigationHeight = getNavigationBarHeight(anyView.context)

        //anyView为需要调整高度的View，理论上来说可以是任意的View
        this.anyView = anyView
        this.listener = listener
        rootView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            //对于Activity来说，该高度即为屏幕高度
            val rootHeight = rootView.height
            val rect = Rect()
            //获取当前可见部分，默认可见部分是除了状态栏和导航栏剩下的部分
            rootView.getWindowVisibleDisplayFrame(rect)
            if (rootHeight - rect.bottom == navigationHeight) {
                //如果可见部分底部与屏幕底部刚好相差导航栏的高度，则认为有导航栏
                isNavigationBarShow = true
            } else if (rootHeight - rect.bottom == 0) {
                //如果可见部分底部与屏幕底部平齐，说明没有导航栏
                isNavigationBarShow = false
            }

            //cal softInput height
            var isSoftInputShow = false
            var softInputHeight = 0
            //如果有导航栏，则要去除导航栏的高度
            val mutableHeight = if (isNavigationBarShow == true) navigationHeight else 0
            if (rootHeight - mutableHeight > rect.bottom) {
                //除去导航栏高度后，可见区域仍然小于屏幕高度，则说明键盘弹起了
                isSoftInputShow = true
                //键盘高度
                softInputHeight = rootHeight - mutableHeight - rect.bottom
                if (this.softInputHeight != softInputHeight) {
                    softInputHeightChanged = true
                    this.softInputHeight = softInputHeight
                } else {
                    softInputHeightChanged = false
                }
            }

            //获取目标View的位置坐标
            val location = IntArray(2)
            anyView.getLocationOnScreen(location)

            //条件1减少不必要的回调，只关心前后发生变化的
            //条件2针对软键盘切换手写、拼音键等键盘高度发生变化
            if (isSoftInputShowing != isSoftInputShow || isSoftInputShow && softInputHeightChanged) {
                if (listener != null) {
                    //第三个参数为该View需要调整的偏移量
                    //此处的坐标都是相对屏幕左上角(0,0)为基准的
                    listener.onChanged(isSoftInputShow, softInputHeight, location[1] + anyView.height - rect.bottom)
                }
                isSoftInputShowing = isSoftInputShow
            }
        }
    }

    //***************STATIC METHOD******************
    fun getNavigationBarHeight(context: Context?): Int {
        if (context == null) return 0
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}