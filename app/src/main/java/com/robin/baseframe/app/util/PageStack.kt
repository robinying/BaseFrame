package com.robin.baseframe.app.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import java.util.*

object PageStack : Application.ActivityLifecycleCallbacks {
    val stack = LinkedList<Activity>()
    // Fragment 集合
    val fragments = hashMapOf<Activity, MutableList<Fragment>>()
    // Fragment 生命周期监听器
    private val fragmentLifecycleCallbacks by lazy(LazyThreadSafetyMode.NONE) {
        object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                // 当 Fragment 被创建后，把它和相关的 activity 存入 map
                f.activity?.also { activity ->
                    fragments[activity]?.also { it.add(f) } ?: run { fragments[activity] = mutableListOf(f) }
                }
            }
        }
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        stack.add(activity)
        // 注册 Fragment 生命周期监听器
        (activity as? FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
    }
    override fun onActivityDestroyed(activity: Activity) {
        stack.remove(activity)
        // 移除 Fragment 生命周期监听器
        (activity as? FragmentActivity)?.supportFragmentManager?.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        // 清空 Fragment 集合
        fragments[activity]?.clear()
        fragments.remove(activity)
    }
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
}