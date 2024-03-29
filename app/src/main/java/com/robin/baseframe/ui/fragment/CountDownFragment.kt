package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.event.SharedFlowBus
import com.robin.baseframe.app.ext.toast
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.data.bean.User
import com.robin.baseframe.databinding.FragmentCountDownBinding
import com.robin.baseframe.ext.countDown
import com.robin.baseframe.viewmodel.CountDownViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class CountDownFragment : BaseFragment<CountDownViewModel, FragmentCountDownBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        binding.btStart.onClick {
            mViewModel.globalLiveData.cancelCount()
        }
        binding.btStop.onClick {
            mViewModel.globalLiveData.cancelCount()
        }
        binding.btCreateData.onClick {
            mViewModel.userLiveData.value = User("robin", (1..100).random())
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mViewModel.globalLiveData.startCount(30)
        SharedFlowBus.with(User::class.java).tryEmit(User("robin", 32))
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.globalLiveData.observe(viewLifecycleOwner) {
            binding.tvCountDown.text = it
        }
        mViewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            LogUtils.debugInfo("userLiveData update ${it.javaClass.simpleName} --- $it")
        })
        mViewModel.userNameLiveData.observe(viewLifecycleOwner) {
            LogUtils.debugInfo("userNameLiveData update ${it.javaClass.simpleName} --- $it")
        }
        SharedFlowBus.on(User::class.java).observe(viewLifecycleOwner) {
            LogUtils.debugInfo("get User info:${it.name}--${it.age}")
        }
    }

    fun startCountDown() {

        var timeDownScope: CoroutineScope? = null

        countDown(
            time = 60,
            start = {
                timeDownScope = it
                LogUtils.debugInfo("开始")

            },
            end = {
                LogUtils.debugInfo("结速倒计时")
                toast("结速倒计时")

            },
            next = {

                LogUtils.debugInfo("当时计数：$it")

                if (it == 50) {
                    timeDownScope?.cancel()
                }

            })
    }
}