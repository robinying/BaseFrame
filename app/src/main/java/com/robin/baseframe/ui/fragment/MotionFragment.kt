package com.robin.baseframe.ui.fragment

import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.databinding.FragmentMotionBinding

class MotionFragment : BaseFragment<BaseViewModel, FragmentMotionBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        binding.motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                LogUtils.debugInfo("onTransitionStarted")
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                LogUtils.debugInfo("onTransitionChange")
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                LogUtils.debugInfo("onTransitionCompleted")
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                LogUtils.debugInfo("onTransitionTrigger")
            }

        })
    }
}