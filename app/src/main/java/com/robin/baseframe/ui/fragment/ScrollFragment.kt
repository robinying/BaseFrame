package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.databinding.FragmentScrollViewBinding
import com.robin.baseframe.ui.adapter.DemoAdapter
import com.robin.baseframe.viewmodel.ScrollViewModel
import com.robin.module_web.WebViewPool

class ScrollFragment : BaseFragment<ScrollViewModel, FragmentScrollViewBinding>() {

    private val demoAdapter by lazy { DemoAdapter(requireContext()) }
    // 从缓存池获取
    private val mWebView by lazy { WebViewPool.getInstance().getWebView(requireContext()) }
    override fun initView(savedInstanceState: Bundle?) {
        binding.recycleView.layoutManager = LinearLayoutManager(mActivity)
        binding.recycleView.adapter = demoAdapter
        demoAdapter.setOnItemClickListener { holder, item ->

        }
        // 设置生命周期监听
        mWebView.setLifecycleOwner(this)
        // 添加到 RelativeLayout 容器中
        binding.root.addView(
            mWebView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
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