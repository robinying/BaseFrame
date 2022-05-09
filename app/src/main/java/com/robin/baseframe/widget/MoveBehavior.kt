package com.robin.baseframe.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.robin.baseframe.app.util.LogUtils


class MoveBehavior<T : View>(val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<T>(context, attrs) {
    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        super.onAttachedToLayoutParams(params)
        LogUtils.debugInfo("onAttachedToLayoutParams")
    }

    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
        LogUtils.debugInfo("onDetachedFromLayoutParams")
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: T, ev: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(parent, child, ev)
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: T, ev: MotionEvent): Boolean {
        return super.onTouchEvent(parent, child, ev)
    }

    override fun getScrimColor(parent: CoordinatorLayout, child: T): Int {
        return Color.CYAN
    }

    override fun getScrimOpacity(parent: CoordinatorLayout, child: T): Float {
        return 0.5f
    }

    override fun blocksInteractionBelow(parent: CoordinatorLayout, child: T): Boolean {
        return super.blocksInteractionBelow(parent, child)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: T, dependency: View): Boolean {
        LogUtils.debugInfo(
             "layoutDependsOn\tchild:${child::class.java.simpleName}" +
                    "\tdependency:${dependency::class.java.simpleName}"
        )
        return child is AppCompatTextView && dependency is MoveView
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: T, dependency: View): Boolean {
        LogUtils.debugInfo("onDependentViewChanged")
        child.setBackgroundColor(ColorUtil.randomColor(context))
        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: T, dependency: View) {
        super.onDependentViewRemoved(parent, child, dependency)
    }
}