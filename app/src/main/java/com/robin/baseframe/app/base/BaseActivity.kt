package com.robin.baseframe.app.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.robin.baseframe.app.ext.view.bindActivityToolbar
import com.robin.baseframe.app.util.inflateBindingWithGeneric

/**
 * ViewBinding 页面基类 — 提供 ViewBinding 通用脚手架。
 *
 * 子类自行使用 @AndroidEntryPoint + Hilt 注入 ViewModel。
 * MVVM+UDF 推荐使用 Compose [BaseComposeActivity]。
 */
abstract class BaseViewActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var mActivity: AppCompatActivity

    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    abstract fun initView(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        _binding = inflateBindingWithGeneric(layoutInflater)
        setContentView(binding.root)
        initView(savedInstanceState)
        binding.root.bindActivityToolbar()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
