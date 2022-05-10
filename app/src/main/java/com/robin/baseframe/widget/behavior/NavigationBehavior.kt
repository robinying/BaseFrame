package com.robin.baseframe.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.widget.CustomNavigationView
import com.robin.baseframe.widget.MoveView

class NavigationBehavior(val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<CustomNavigationView>(context, attrs) {
    companion object{
        private const val TAG = "NavigationBehavior"
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: CustomNavigationView, ev: MotionEvent): Boolean {
        Log.i(TAG, "onTouchEvent")
        return super.onTouchEvent(parent, child, ev)
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: CustomNavigationView,
        ev: MotionEvent
    ): Boolean {
        Log.i(TAG, "onTouchEvent")
        return super.onInterceptTouchEvent(parent, child, ev)
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: CustomNavigationView,
        dependency: View
    ): Boolean {
        Log.i(TAG, "layoutDependsOn dependency:${dependency.javaClass.simpleName}")
        return dependency is MoveView
    }


    // 依赖的最后一次距离
    private var lastDependencyY = 0f


    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: CustomNavigationView,
        dependency: View
    ): Boolean {
        // 最大滑动距离
        val maxScroll = child.bottom + child.height
        LogUtils.debugInfo("bottom:"+child.bottom+",height:"+child.height)
        // 最小滑动距离
        val minScroll = child.top
        LogUtils.debugInfo("minScroll:"+child.top)

        // 当前依赖的Y值
        val dependencyY = dependency.y

        // 当前依赖的View滑动距离
        val dependencyScroll = lastDependencyY - dependencyY


        // 当前滑动距离
        var currentScroll = child.y - dependencyScroll

        // 如果比最大的大,那么就 = 最大的
        // 如果比最小的小，那么就 = 最小的
        if (currentScroll >= maxScroll) {
            currentScroll = maxScroll.toFloat()
        } else if (currentScroll <= minScroll) {
            currentScroll = minScroll.toFloat()
        }

        // 设置child y的距离
        child.y = currentScroll

        lastDependencyY = dependencyY

        return super.onDependentViewChanged(parent, child, dependency)
    }
}

