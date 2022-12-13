package com.robin.baseframe.app.ext.view

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat

/**
 * 设置view显示
 */
fun View.visible() {
    visibility = View.VISIBLE
}


/**
 * 设置view占位隐藏
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * 根据条件设置view显示隐藏 为true 显示，为false 隐藏
 */
fun View.visibleOrGone(flag: Boolean) {
    visibility = if (flag) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/**
 * 根据条件设置view显示隐藏 为true 显示，为false 隐藏
 */
fun View.visibleOrInvisible(flag: Boolean) {
    visibility = if (flag) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

/**
 * 设置view隐藏
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * 快捷设置View的自定义纯色带圆角背景
 *
 * @receiver View
 * @param color Int 颜色值
 * @param cornerRadius Float 圆角 单位px
 */
fun View.setRoundRectBg(
    @ColorInt color: Int,
    cornerRadius: Float = 15F
) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
        background = GradientDrawable().apply {
            setColor(color)
            setCornerRadius(cornerRadius)
        }
    }
}

/**
 * 将view转为bitmap
 */
@Deprecated("use View.drawToBitmap()")
fun View.toBitmap(scale: Float = 1f, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    if (this is ImageView) {
        if (drawable is BitmapDrawable) return (drawable as BitmapDrawable).bitmap
    }
    this.clearFocus()
    val bitmap = createBitmapSafely(
        (width * scale).toInt(),
        (height * scale).toInt(),
        config,
        1
    )
    if (bitmap != null) {
        Canvas().run {
            setBitmap(bitmap)
            save()
            drawColor(Color.WHITE)
            scale(scale, scale)
            this@toBitmap.draw(this)
            restore()
            setBitmap(null)
        }
    }
    return bitmap
}

fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(width, height, config, retryCount - 1)
        }
        return null
    }
}


/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 * @param interval 时间间隔 默认0.5秒
 * @param action 执行方法
 */
var lastClickTime = 0L
fun View.clickNoRepeat(interval: Long = 500, action: (view: View) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action(it)
    }
}


fun Any?.notNull(notNullAction: (value: Any) -> Unit, nullAction1: () -> Unit) {
    if (this != null) {
        notNullAction.invoke(this)
    } else {
        nullAction1.invoke()
    }
}

fun View.extraAnimClickListener(animator: ValueAnimator, action: (View) -> Unit) {
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> animator.start()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> animator.reverse()
        }
        false
    }
    setOnClickListener { action(this) }
}


/**
 * Registers the [block] lambda as [View.OnClickListener] to this View.
 *
 * If this View is not clickable, it becomes clickable.
 */
inline fun View.onClick(block: View.OnClickListener) = setOnClickListener(block)

/**
 * Register the [block] lambda as [View.OnLongClickListener] to this View.
 * By default, [consume] is set to true because it's the most common use case, but you can set it
 * to false.
 * If you want to return a value dynamically, use [View.setOnLongClickListener] instead.
 *
 * If this view is not long clickable, it becomes long clickable.
 */
inline fun View.onLongClick(
    consume: Boolean = true,
    crossinline block: () -> Unit
) = setOnLongClickListener { block(); consume }

val View.isInScreen: Boolean
    get() = ViewCompat.isAttachedToWindow(this) && visibility == View.VISIBLE && getLocalVisibleRect(Rect())

fun View.onVisibilityChange(
    viewGroups: List<ViewGroup> = emptyList(), // 会被插入 Fragment 的容器集合
    needScrollListener: Boolean = true,
    block: (view: View, isVisible: Boolean) -> Unit
) {
    val KEY_VISIBILITY = "KEY_VISIBILITY".hashCode()
    val KEY_HAS_LISTENER = "KEY_HAS_LISTENER".hashCode()
    // 若当前控件已监听可见性，则返回
    if (getTag(KEY_HAS_LISTENER) == true) return

    // 检测可见性
    val checkVisibility = {
        // 获取上一次可见性
        val lastVisibility = getTag(KEY_VISIBILITY) as? Boolean
        // 判断控件是否出现在屏幕中
        val isInScreen = this.isInScreen
        // 首次可见性变更
        if (lastVisibility == null) {
            if (isInScreen) {
                block(this, true)
                setTag(KEY_VISIBILITY, true)
            }
        }
        // 非首次可见性变更
        else if (lastVisibility != isInScreen) {
            block(this, isInScreen)
            setTag(KEY_VISIBILITY, isInScreen)
        }
    }

    // 全局重绘监听器
    class LayoutListener : ViewTreeObserver.OnGlobalLayoutListener {
        // 标记位用于区别是否是遮挡case
        var addedView: View? = null
        override fun onGlobalLayout() {
            // 遮挡 case
            if (addedView != null) {
                // 插入视图矩形区域
                val addedRect = Rect().also { addedView?.getGlobalVisibleRect(it) }
                // 当前视图矩形区域
                val rect = Rect().also { this@onVisibilityChange.getGlobalVisibleRect(it) }
                // 如果插入视图矩形区域包含当前视图矩形区域，则视为当前控件不可见
                if (addedRect.contains(rect)) {
                    block(this@onVisibilityChange, false)
                    setTag(KEY_VISIBILITY, false)
                } else {
                    block(this@onVisibilityChange, true)
                    setTag(KEY_VISIBILITY, true)
                }
            }
            // 非遮挡 case
            else {
                checkVisibility()
            }
        }
    }

    val layoutListener = LayoutListener()
    // 编辑容器监听其插入视图时机
    viewGroups.forEachIndexed { index, viewGroup ->
        viewGroup.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                // 当控件插入，则置标记位
                layoutListener.addedView = child
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
                // 当控件移除，则置标记位
                layoutListener.addedView = null
            }
        })
    }
    viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    // 全局滚动监听器
    var scrollListener:ViewTreeObserver.OnScrollChangedListener? = null
    if (needScrollListener) {
        scrollListener = ViewTreeObserver.OnScrollChangedListener { checkVisibility() }
        viewTreeObserver.addOnScrollChangedListener(scrollListener)
    }
    // 全局焦点变化监听器
    val focusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
        val lastVisibility = getTag(KEY_VISIBILITY) as? Boolean
        val isInScreen = this.isInScreen
        if (hasFocus) {
            if (lastVisibility != isInScreen) {
                block(this, isInScreen)
                setTag(KEY_VISIBILITY, isInScreen)
            }
        } else {
            if (lastVisibility == true) {
                block(this, false)
                setTag(KEY_VISIBILITY, false)
            }
        }
    }
    viewTreeObserver.addOnWindowFocusChangeListener(focusChangeListener)
    // 为避免内存泄漏，当视图被移出的同时反注册监听器
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
        }

        override fun onViewDetachedFromWindow(v: View?) {
            v ?: return
            // 有时候 View detach 后，还会执行全局重绘，为此退后反注册
            post {
                try {
                    v.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
                } catch (_: java.lang.Exception) {
                    v.viewTreeObserver.removeGlobalOnLayoutListener(layoutListener)
                }
                v.viewTreeObserver.removeOnWindowFocusChangeListener(focusChangeListener)
                if(scrollListener !=null) v.viewTreeObserver.removeOnScrollChangedListener(scrollListener)
                viewGroups.forEach { it.setOnHierarchyChangeListener(null) }
            }
            removeOnAttachStateChangeListener(this)
        }
    })
    // 标记已设置监听器
    setTag(KEY_HAS_LISTENER, true)
}


