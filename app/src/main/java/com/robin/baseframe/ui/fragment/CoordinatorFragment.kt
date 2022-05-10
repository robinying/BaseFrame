package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.databinding.FragmentCoordinatorBinding

class CoordinatorFragment : BaseFragment<BaseViewModel, FragmentCoordinatorBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        binding.mainView.viewTreeObserver.addOnPreDrawListener {
            LogUtils.debugInfo("View changed")
            return@addOnPreDrawListener true
        }
        binding.mainView.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                LogUtils.debugInfo("onChildViewAdded child:" + child?.javaClass?.simpleName)
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
                LogUtils.debugInfo("onChildViewRemoved child:" + child?.javaClass?.simpleName)
            }
        })

    }
}