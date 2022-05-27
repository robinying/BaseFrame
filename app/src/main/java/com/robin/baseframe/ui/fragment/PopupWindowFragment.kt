package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.ext.toast
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.databinding.FragmentPopUpWindowBinding
import com.robin.baseframe.ui.adapter.PopupAdapter
import com.robin.baseframe.widget.popup.PopWindow

class PopupWindowFragment : BaseFragment<BaseViewModel, FragmentPopUpWindowBinding>(), PopWindow.ViewInterface {
    private var mPopUpWindow: PopWindow? = null
    private var mPopDownWindow: PopWindow? = null
    private var mPopLeftWindow: PopWindow? = null
    private var mPopRightWindow: PopWindow? = null
    private var mPopAllWindow:PopWindow?=null
    private val mPopupAdapter by lazy { PopupAdapter(mActivity) }
    private val popStr = arrayListOf(
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin",
        "Robin"
    )

    override fun initView(savedInstanceState: Bundle?) {
        createPop()
        binding.btPopAll.onClick{
            mPopAllWindow?.showAtLocation(binding.mainCl, Gravity.BOTTOM, 0, 0)
        }
        binding.btPopUp.onClick {
            mPopUpWindow?.showOnTargetTop(binding.btPopUp)
        }
        binding.btPopDown.onClick {
            mPopDownWindow?.showOnTargetBottom(it)
        }
        binding.btPopLeft.onClick {
            mPopLeftWindow?.showOnTargetLeft(it)
        }
        binding.btPopRight.onClick {
            mPopRightWindow?.showOnTargetRight(it)
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mPopupAdapter.setItems(popStr)

    }

    private fun createPop() {
        mPopUpWindow = PopWindow.Builder(mActivity)
            .setView(R.layout.popup_demo)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setAnimStyle(R.style.AnimUp)
            .setChildrenView(this)
            .create()

        mPopDownWindow = PopWindow.Builder(mActivity)
            .setView(R.layout.popup_demo)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setAnimStyle(R.style.AnimDown)
            .create()

        mPopLeftWindow = PopWindow.Builder(mActivity)
            .setView(R.layout.popup_demo)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setAnimStyle(R.style.AnimLeft)
            .create()

        mPopRightWindow = PopWindow.Builder(mActivity)
            .setView(R.layout.popup_demo)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setAnimStyle(R.style.AnimRight)
            .create()
        mPopAllWindow = PopWindow.Builder(mActivity)
            .setView(R.layout.popup_rv)
            .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            //.setBackGroundLevel(0.5f) //取值范围0.0f-1.0f 值越小越暗
            .setAnimStyle(R.style.AnimUp)
            .setChildrenView(this)
            .create()
    }

    override fun getChildView(view: View, layoutResId: Int, pop: PopupWindow) {
        when (layoutResId) {
            R.layout.popup_demo -> {
                val tvLike = view.findViewById<TextView>(R.id.tv_like)
                val tvHate = view.findViewById<TextView>(R.id.tv_hate)
                tvLike.setOnClickListener {
                    toast("赞一个")
                    mPopUpWindow?.dismiss()
                }
                tvHate.setOnClickListener {
                    toast("踩一下")
                    mPopUpWindow?.dismiss()
                }
            }
            R.layout.popup_rv->{
                val recycleView = view.findViewById<RecyclerView>(R.id.recycle_view)
                recycleView.layoutManager = GridLayoutManager(mActivity, 3)
                recycleView.adapter = mPopupAdapter
//                mPopupAdapter.setOnItemClickListener { holder, item ->
//                    mPopAllWindow?.dismiss()
//                }

            }
        }
    }
}