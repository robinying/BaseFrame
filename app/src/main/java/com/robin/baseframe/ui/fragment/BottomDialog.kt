package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseDialogFragment
import com.robin.baseframe.app.base.adapter.ItemAnimation
import com.robin.baseframe.app.base.adapter.animations.SlideInLeftAnimation
import com.robin.baseframe.app.ext.util.screenHeight
import com.robin.baseframe.app.util.viewbindingdelegate.viewBinding
import com.robin.baseframe.databinding.DialogBottomBinding
import com.robin.baseframe.databinding.RvHeaderBinding
import com.robin.baseframe.ui.adapter.DemoAdapter

/*
* DialogFragment布局文件中root使用ConstraintLayout时不显示，使用LinearLayout显示，
* 动画要在set根路径下添加duration，否则不生效
* */
class BottomDialog : BaseDialogFragment(R.layout.dialog_bottom) {
    private val binding by viewBinding(DialogBottomBinding::bind)
    private val adapter by lazy { DemoAdapter(requireContext()) }
    private val itemList = arrayListOf(
        "Item1",
        "Item2",
        "Item3",
        "Item4",
        "Item5",
        "Item1",
        "Item2",
        "Item3",
        "Item4",
        "Item5"
    )


    override fun onStart() {
        super.onStart()
        val params = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT //设置宽度为铺满
        //params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.height = (requireContext().screenHeight * 0.4f).toInt()
        params?.gravity = Gravity.BOTTOM
        params?.windowAnimations = R.style.BottomDialogAnimation
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        binding.run {
            rvItem.layoutManager = LinearLayoutManager(requireContext())
            val itemAnimation = ItemAnimation.create()
            itemAnimation.itemAnimation = SlideInLeftAnimation()
            itemAnimation.enabled(true)
            adapter.itemAnimation = itemAnimation
            adapter.bindToRecyclerView(rvItem)
            rvItem.postDelayed({
                adapter.setItems(itemList)
            }, 1000)
            adapter.addHeaderView { viewGroup ->
                RvHeaderBinding.inflate(layoutInflater, viewGroup, false).apply {
                    tvHeader.text = " Bottom Header"

                }
            }
        }
    }
}