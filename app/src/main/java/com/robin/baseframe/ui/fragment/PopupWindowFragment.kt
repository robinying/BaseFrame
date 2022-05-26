package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.databinding.FragmentPopUpWindowBinding
import com.robin.baseframe.widget.popup.PopWindow

class PopupWindowFragment:BaseFragment<BaseViewModel,FragmentPopUpWindowBinding>() {
    private var mPopUpWindow:PopWindow? = null
    private var mPopDownWindow:PopWindow? = null
    private var mPopLeftWindow:PopWindow? = null
    private var mPopRightWindow:PopWindow? = null
    override fun initView(savedInstanceState: Bundle?) {
        createPop()
        binding.btPopUp.onClick{
            mPopUpWindow?.showOnTargetTop(binding.btPopUp)
        }
        binding.btPopDown.onClick{
            mPopDownWindow?.showOnTargetBottom(it)
        }
        binding.btPopLeft.onClick{
            mPopLeftWindow?.showOnTargetLeft(it)
        }
        binding.btPopRight.onClick{
            mPopRightWindow?.showOnTargetRight(it)
        }
    }

    private fun createPop(){
        mPopUpWindow = PopWindow.Builder(mActivity)
            .setView(R.layout.popup_demo)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            .setAnimStyle(R.style.AnimUp)
            .create()

        mPopDownWindow = PopWindow.Builder(mActivity)
            .setView(R.layout.popup_demo)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            .setAnimStyle(R.style.AnimDown)
            .create()

        mPopLeftWindow = PopWindow.Builder(mActivity)
            .setView(R.layout.popup_demo)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            .setAnimStyle(R.style.AnimLeft)
            .create()

        mPopRightWindow = PopWindow.Builder(mActivity)
            .setView(R.layout.popup_demo)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            .setAnimStyle(R.style.AnimRight)
            .create()
    }
}