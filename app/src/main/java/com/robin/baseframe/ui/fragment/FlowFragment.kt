package com.robin.baseframe.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.robin.baseframe.app.base.LegacyBaseFragment
import com.robin.baseframe.app.base.LegacyViewModel
import com.robin.baseframe.databinding.FragmentFlowBinding
import com.robin.baseframe.viewmodel.FlowViewModel

class FlowFragment : LegacyBaseFragment<FlowViewModel, FragmentFlowBinding>() {
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mViewModel.coldFlowDemo()
        mViewModel.cacheRepositityDemo()
        mViewModel.test2RepoDemo()
        mViewModel.getHomeData()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.loadStatus.observe(viewLifecycleOwner) {
            binding.tvLoadStatus.text = it
        }
        mViewModel.dataInt.observe(viewLifecycleOwner) {
            binding.tvData.text = it.toString()
        }
    }
}