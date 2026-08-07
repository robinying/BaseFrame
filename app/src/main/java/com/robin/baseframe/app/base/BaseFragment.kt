package com.robin.baseframe.app.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.robin.baseframe.app.util.inflateBindingWithGeneric
import com.robin.baseframe.app.ext.view.bindDemoToolbar

/**
 * ViewBinding + Fragment 基类 — 提供 ViewBinding 通用脚手架。
 *
 * 子类自行使用 @AndroidEntryPoint + Hilt 注入 ViewModel。
 * MVVM+UDF 推荐使用 Compose + Navigation Compose。
 */
abstract class BaseViewFragment<VB : ViewBinding> : Fragment() {

    lateinit var mActivity: AppCompatActivity

    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBindingWithGeneric(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        bindDemoToolbar(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun initView(savedInstanceState: Bundle?)
}
