package com.robin.baseframe.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.robin.baseframe.widget.MoveView

class FollowBehavior : CoordinatorLayout.Behavior<View> {
    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is MoveView
    }

    private var dependencyX = Float.MAX_VALUE
    private var dependencyY = Float.MAX_VALUE
    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (dependencyX == Float.MAX_VALUE || dependencyY == Float.MAX_VALUE) {
            dependencyX = dependency.x
            dependencyY = dependency.y
        } else {
            val dX = dependency.x - dependencyX
            val dy = dependency.y - dependencyY
            child.translationX -= dX
            child.translationY -= dy
            dependencyX = dependency.x
            dependencyY = dependency.y
        }
        return true
    }
}