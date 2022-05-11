package com.robin.baseframe.ui.fragment

import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.util.KeyBoardUtils
import com.robin.baseframe.databinding.FragmentDslBinding
import com.robin.baseframe.ext.addTextChangedListenerClosure
import com.robin.baseframe.ext.addTextChangedListenerDsl
import com.robin.baseframe.ext.buildSpannableString

class DslFragment : BaseFragment<BaseViewModel, FragmentDslBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        binding.et1.addTextChangedListenerDsl {
            afterTextChanged {
                if (it.toString().length >= 4) {
                    KeyBoardUtils.toggleSoftInput(binding.et1)
                }
            }
        }

        binding.et2.addTextChangedListenerClosure(afterTextChanged = {
            if (it.toString().length >= 4) {
                KeyBoardUtils.toggleSoftInput(binding.et2)
            }
        })

        binding.tvDsl.buildSpannableString {
            addText("我已详细阅读并同意")
            addText("《隐私政策》") {
                setColor("#0099FF")
                onClick(false) {
                    ToastUtils.showShort("Click 隐私政策")
                }
            }
        }
    }
}