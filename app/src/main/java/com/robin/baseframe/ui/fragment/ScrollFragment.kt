package com.robin.baseframe.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.databinding.FragmentScrollViewBinding
import com.robin.baseframe.ui.adapter.DemoAdapter
import com.robin.baseframe.viewmodel.ScrollViewModel

class ScrollFragment : BaseFragment<ScrollViewModel, FragmentScrollViewBinding>() {

    private val demoAdapter by lazy { DemoAdapter(requireContext()) }
    override fun initView(savedInstanceState: Bundle?) {
        binding.recycleView.layoutManager = LinearLayoutManager(mActivity)
        binding.recycleView.adapter = demoAdapter
        demoAdapter.setOnItemClickListener { holder, item ->

        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mViewModel.getData()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.itemListData.observe(viewLifecycleOwner) {
            demoAdapter.setItems(it)
        }
    }
}