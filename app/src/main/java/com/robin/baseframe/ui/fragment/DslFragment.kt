package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import com.blankj.utilcode.util.ToastUtils
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.util.KeyBoardUtils
import com.robin.baseframe.databinding.FragmentDslBinding
import com.robin.baseframe.app.ext.addTextChangedListenerClosure
import com.robin.baseframe.app.ext.addTextChangedListenerDsl
import com.robin.baseframe.app.ext.buildSpannableString
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.test.bind
import com.robin.baseframe.widget.dialog.ADialog
import com.robin.baseframe.widget.dialog.BDialog
import com.robin.baseframe.widget.dialog.CDialog
import com.robin.baseframe.widget.dialog.DialogChain

class DslFragment : BaseFragment<BaseViewModel, FragmentDslBinding>() {
    private var timer: CountDownTimer? = null
    private lateinit var mDialogChain: DialogChain
    override fun initView(savedInstanceState: Bundle?) {
        createDialogChain()
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
        binding.btChainDialog.onClick{
            mDialogChain.process()
        }
        timer = object : CountDownTimer(30000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                binding.moonPhaseView.mPhase = (millisUntilFinished / 100).toInt()
            }

            override fun onFinish() {

            }
        }
        timer?.start()
        binding.card1.bind(R.mipmap.ic_launcher, "name String", "description String")
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }

    private fun createDialogChain(){
        mDialogChain = DialogChain.create()
            .attach(this)
            .addInterceptor(ADialog(mActivity))
            .addInterceptor(BDialog(mActivity))
            .addInterceptor(CDialog(mActivity))
            .build()
    }
}