package com.robin.commonUi.ext


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.robin.baseframe.app.ext.util.toHtml
import com.robin.baseframe.R
import com.robin.baseframe.app.base.appContext
import com.robin.baseframe.app.util.ColorUtils

/**
 * 描述　:项目中自定义类的拓展函数
 */

fun RecyclerView.initFloatBtn(floatbtn: FloatingActionButton) {
    //监听recyclerview滑动到顶部的时候，需要把向上返回顶部的按钮隐藏
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        @SuppressLint("RestrictedApi")
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!canScrollVertically(-1)) {
                floatbtn.visibility = View.INVISIBLE
            }
        }
    })
    floatbtn.backgroundTintList = ColorUtils.getOneColorStateList(appContext)
    floatbtn.setOnClickListener {
        val layoutManager = layoutManager as LinearLayoutManager
        //如果当前recyclerview 最后一个视图位置的索引大于等于40，则迅速返回顶部，否则带有滚动动画效果返回到顶部
        if (layoutManager.findLastVisibleItemPosition() >= 40) {
            scrollToPosition(0)//没有动画迅速返回到顶部(马上)
        } else {
            smoothScrollToPosition(0)//有滚动动画返回到顶部(有点慢)
        }
    }
}

/**
 * 初始化普通的toolbar 只设置标题
 */
fun Toolbar.init(titleStr: String = ""): Toolbar {
    setBackgroundColor(ColorUtils.getColor(appContext))
    title = titleStr
    return this
}

/*
* 初始化标题居中 toolbar
* */
fun Toolbar.initCenterClose(
    context: Context,
    titleStr: String = "",
    backImg: Int = R.drawable.ic_back,
    onBack: (toolbar: Toolbar) -> Unit
): Toolbar {
    //setBackgroundColor(SettingUtil.getColor(appContext))
    setTitleCenter(context, titleStr)
    setNavigationIcon(backImg)
    setNavigationOnClickListener { onBack.invoke(this) }
    return this
}

/*
* 初始化标题居中 toolbar
* */
fun Toolbar.initCenter(
    context: Context,
    titleStr: String = ""
): Toolbar {
    //setBackgroundColor(SettingUtil.getColor(appContext))
    setTitleCenter(context, titleStr)
    return this
}

/**
 * 初始化有返回键的toolbar
 */
fun Toolbar.initClose(
    titleStr: String = "",
    backImg: Int = R.drawable.ic_back,
    onBack: (toolbar: Toolbar) -> Unit
): Toolbar {
    setBackgroundColor(ColorUtils.getColor(appContext))
    title = titleStr.toHtml()
    setNavigationIcon(backImg)
    setNavigationOnClickListener { onBack.invoke(this) }
    return this
}

fun ViewPager2.init(
    fragment: Fragment,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}


/**
 * 隐藏软键盘
 */
fun hideSoftKeyboard(activity: Activity?) {
    activity?.let { act ->
        val view = act.currentFocus
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

fun Toolbar.setTitleCenter(
    context: Context,
    titleStr: String,
    textSize: Float = 22f,
    textColor: Int = Color.WHITE
) {
    val titleText = TextView(context)
    titleText.setTextColor(textColor)
    titleText.text = titleStr
    titleText.textSize = textSize
    titleText.typeface = Typeface.defaultFromStyle(Typeface.BOLD);
    titleText.gravity = Gravity.CENTER
    val layoutParams =
        Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
    layoutParams.gravity = Gravity.CENTER
    titleText.layoutParams = layoutParams
    addView(titleText)
}

fun RecyclerView.onItemVisibilityChange(
    percent: Float = 0.5f,
    block: (itemView: View, adapterIndex: Int, isVisible: Boolean) -> Unit
) {
    // 可复用的矩形区域，避免重复创建
    val childVisibleRect = Rect()
    // 记录所有可见表项搜索的列表
    val visibleAdapterIndexs = mutableSetOf<Int>()
    // 将列表项可见性检测定义为一个 lambda
    val checkVisibility = {
        // 遍历所有 RecyclerView 的子控件
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            // 获取其适配器索引
            val adapterIndex = getChildAdapterPosition(child)
            if(adapterIndex == NO_POSITION) continue
            // 计算子控件可见区域并获取是否可见标记位
            val isChildVisible = child.getLocalVisibleRect(childVisibleRect)
            // 子控件可见面积
            val visibleArea = childVisibleRect.let { it.height() * it.width() }
            // 子控件真实面积
            val realArea = child.width * child.height
            // 比对可见面积和真实面积，若大于阈值，则回调可见，否则不可见
            if (isChildVisible && visibleArea >= realArea * percent) {
                if (visibleAdapterIndexs.add(adapterIndex)) {
                    block(child, adapterIndex, true)
                }
            } else {
                if (adapterIndex in visibleAdapterIndexs) {
                    block(child, adapterIndex, false)
                    visibleAdapterIndexs.remove(adapterIndex)
                }
            }
        }
    }
    // 为列表添加滚动监听器
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            checkVisibility()
        }
    }
    addOnScrollListener(scrollListener)
    // 避免内存泄漏，当列表被移除时，反注册监听器
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

        override fun onViewAttachedToWindow(v: View) {

        }

        override fun onViewDetachedFromWindow(v: View) {
            if (v == null || v !is RecyclerView) return
            v.removeOnScrollListener(scrollListener)
            removeOnAttachStateChangeListener(this)
        }
    })
}

/*
* 可见性检测
* 利用 ViewPager2 提供的 OnPageChangeCallback，
* 在内部记录了上一页，以此来向上层回调上一页不可见事件。
* */
fun ViewPager2.addOnPageVisibilityChangeListener(block: (index: Int, isVisible: Boolean) -> Unit) {
    // 当前页
    var lastPage: Int = currentItem
    // 注册页滚动监听器
    val listener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            // 回调上一页不可见
            if (lastPage != position) {
                block(lastPage, false)
            }
            // 回调当前页可见
            block(position, true)
            lastPage = position
        }
    }
    registerOnPageChangeCallback(listener)
    // 避免内存泄漏
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
        }

        override fun onViewDetachedFromWindow(v: View) {
            if (v == null || v !is ViewPager2) return
            if (ViewCompat.isAttachedToWindow(v)) {
                v.unregisterOnPageChangeCallback(listener)
            }
            removeOnAttachStateChangeListener(this)
        }

    })
}




