package com.robin.baseframe.app.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.robin.baseframe.app.ext.dismissLoadingExt
import com.robin.baseframe.app.ext.getVmClazz
import com.robin.baseframe.app.ext.showLoadingExt
import com.robin.baseframe.app.ext.view.bindDemoToolbar
import com.robin.baseframe.app.util.inflateBindingWithGeneric

/**
 * 旧版 Fragment 兼容层 — 保留 mViewModel / lazyLoadData / createObserver 生命周期钩子。
 *
 * 存量页面在迁移到 MVVM+UDF 前使用，新代码请使用 [BaseViewFragment] + Compose。
 */
@Deprecated("Use BaseViewFragment with MVVM+UDF pattern")
abstract class LegacyBaseFragment<VM : LegacyViewModel, VB : ViewBinding> : Fragment() {

    lateinit var mViewModel: VM

    lateinit var mActivity: AppCompatActivity

    private val handler = Handler(Looper.getMainLooper())

    private var isFirst: Boolean = true

    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBindingWithGeneric(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = true
        mViewModel = createViewModel()
        initView(savedInstanceState)
        bindDemoToolbar(view)
        createObserver()
        registorDefUIChange()
        initData()
    }

    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    abstract fun initView(savedInstanceState: Bundle?)

    open fun lazyLoadData() {}

    open fun createObserver() {}

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            handler.postDelayed({
                lazyLoadData()
                isFirst = false
            }, lazyLoadTime())
        }
    }

    override fun onPause() {
        super.onPause()
    }

    open fun initData() {}

    open fun showLoading(message: String = "请求网络中...") {
        showLoadingExt(message)
    }

    open fun dismissLoading() {
        dismissLoadingExt()
    }

    private fun registorDefUIChange() {
        mViewModel.loadingChange.showDialog.observe(viewLifecycleOwner, Observer {
            showLoading(it)
        })
        mViewModel.loadingChange.dismissDialog.observe(viewLifecycleOwner, Observer {
            dismissLoading()
        })
    }

    open fun lazyLoadTime(): Long {
        return 300
    }
}
