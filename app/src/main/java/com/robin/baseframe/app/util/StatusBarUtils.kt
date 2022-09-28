package com.robin.baseframe.app.util

import android.R
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.blankj.utilcode.util.DeviceUtils
import com.robin.baseframe.app.ext.util.dp2px
import java.lang.reflect.Field


/**
 * 状态栏透明,状态栏黑色文字，状态栏颜色，沉浸式状态栏
 */
object StatusBarUtils {
    private var DEFAULT_COLOR = 0
    private var DEFAULT_ALPHA = 0f

    /**
     * 设置状态栏背景颜色
     */
    fun setColor(activity: Activity, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor = color
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val systemContent: ViewGroup = activity.findViewById(R.id.content)
            val statusBarView = View(activity)
            val lp: ViewGroup.LayoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
            statusBarView.setBackgroundColor(color)
            systemContent.getChildAt(0).fitsSystemWindows = true
            systemContent.addView(statusBarView, 0, lp)
        }
    }

    fun immersive(activity: Activity) {
        immersive(activity, DEFAULT_COLOR, DEFAULT_ALPHA)
    }

    fun immersive(activity: Activity, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        immersive(activity.window, color, alpha)
    }

    fun immersive(activity: Activity, color: Int) {
        immersive(activity.window, color, 1f)
    }

    fun immersive(window: Window) {
        immersive(window, DEFAULT_COLOR, DEFAULT_ALPHA)
    }

    fun immersive(window: Window, color: Int) {
        immersive(window, color, 1f)
    }

    fun immersive(window: Window, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = mixtureColor(color, alpha)
            var systemUiVisibility: Int = window.decorView.systemUiVisibility
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.decorView.setSystemUiVisibility(systemUiVisibility)
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            setTranslucentView(window.decorView as ViewGroup, color, alpha)
        } else if (Build.VERSION.SDK_INT > 16) {
            var systemUiVisibility: Int = window.decorView.getSystemUiVisibility()
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.decorView.setSystemUiVisibility(systemUiVisibility)
        }
    }

    /**
     * 创建假的透明栏
     */
    fun setTranslucentView(container: ViewGroup, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        if (Build.VERSION.SDK_INT >= 19) {
            val mixtureColor = mixtureColor(color, alpha)
            var translucentView: View? = container.findViewById(R.id.custom)
            if (translucentView == null && mixtureColor != 0) {
                translucentView = View(container.context)
                translucentView.id = R.id.custom
                val lp: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.getContext())
                )
                container.addView(translucentView, lp)
            }
            translucentView?.setBackgroundColor(mixtureColor)
        }
    }

    fun mixtureColor(color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
        val a = if (color and -0x1000000 == 0) 0xff else color ushr 24
        return color and 0x00ffffff or ((a * alpha).toInt() shl 24)
    }
    // ========================  状态栏字体颜色设置  ↓ ================================
    /**
     * 设置状态栏黑色字体图标
     */
    fun setStatusBarBlackText(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = activity.window
            val decorView: View = window.decorView
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            return true
        }
        return false
    }

    /**
     * 设置状态栏白色字体图标
     */
    fun setStatusBarWhiteText(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = activity.window
            val decorView: View = window.decorView
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
            return true
        }
        return false
    }

    // ========================  状态栏字体颜色设置  ↑================================
    // 在某些机子上存在不同的density值，所以增加两个虚拟值
    private var sStatusBarHeight = -1
    private const val sVirtualDensity = -1f
    private const val STATUS_BAR_DEFAULT_HEIGHT_DP = 25 // 大部分状态栏都是25dp

    /**
     * 获取状态栏的高度。
     */
    fun getStatusBarHeight(context: Context): Int {
        if (sStatusBarHeight == -1) {
            initStatusBarHeight(context)
        }
        return sStatusBarHeight
    }

    private fun initStatusBarHeight(context: Context) {
        val clazz: Class<*>
        var obj: Any? = null
        var field: Field? = null
        try {
            clazz = Class.forName("com.android.internal.R\$dimen")
            obj = clazz.newInstance()
            if (field == null) {
                field = clazz.getField("status_bar_height")
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        if (field != null && obj != null) {
            try {
                val id: Int = field.get(obj).toString().toInt()
                sStatusBarHeight = context.getResources().getDimensionPixelSize(id)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        if (DeviceUtils.isTablet()
            && sStatusBarHeight > context.dp2px(STATUS_BAR_DEFAULT_HEIGHT_DP.toFloat())
        ) {
            //状态栏高度大于25dp的平板，状态栏通常在下方
            sStatusBarHeight = 0
        } else {
            if (sStatusBarHeight <= 0) {
                if (sVirtualDensity == -1f) {
                    sStatusBarHeight = context.dp2px(STATUS_BAR_DEFAULT_HEIGHT_DP.toFloat())
                } else {
                    sStatusBarHeight = (STATUS_BAR_DEFAULT_HEIGHT_DP * sVirtualDensity + 0.5f).toInt()
                }
            }
        }
    }
    // ========================  适配状态栏高度  ↓ ================================
    /**
     * 适配状态栏高度的View - 设置Padding
     */
    fun fitsStatusBarViewPadding(view: View) {
        //增加高度
        val lp: ViewGroup.LayoutParams = view.getLayoutParams()
        lp.height += getStatusBarHeight(view.getContext())

        //设置PaddingTop
        view.setPadding(
            view.paddingLeft,
            view.paddingTop + getStatusBarHeight(view.context),
            view.paddingRight,
            view.paddingBottom
        )
    }

    /**
     * 适配状态栏高度的View - 设置Margin
     */
    fun fitsStatusBarViewMargin(view: View) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {

            //已经添加过了不要再次设置
            if (view.tag != null && view.tag.equals("fitStatusBar")) {
                return
            }
            val layoutParams: ViewGroup.MarginLayoutParams = view.getLayoutParams() as ViewGroup.MarginLayoutParams
            val marginTop: Int = layoutParams.topMargin
            val setMarginTop = marginTop + getStatusBarHeight(view.getContext())
            view.tag = "fitStatusBar"
            layoutParams.topMargin = setMarginTop
            view.requestLayout()
        }
    }

    /**
     * 适配状态栏高度的View - 使用布局包裹
     */
    fun fitsStatusBarViewLayout(view: View) {
        val fitParent: ViewParent = view.parent
        if (fitParent != null) {
            if (fitParent is LinearLayout && (fitParent as ViewGroup).getTag() != null &&
                (fitParent as ViewGroup).getTag().equals("fitLayout")
            ) {
                //已经添加过了不要再次设置
                return
            }

            //给当前布局包装一个适应布局
            val fitGroup: ViewGroup = fitParent as ViewGroup
            fitGroup.removeView(view)
            val fitLayout = LinearLayout(view.getContext())
            fitLayout.orientation = LinearLayout.VERTICAL
            fitLayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            fitLayout.tag = "fitLayout"

            //先加一个状态栏高度的布局
            val statusView = View(view.context)
            statusView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(view.context)
            )
            fitLayout.addView(statusView)
            val fitViewParams: ViewGroup.LayoutParams = view.layoutParams
            fitLayout.addView(view)
            fitGroup.addView(fitLayout)
        }
    }
}