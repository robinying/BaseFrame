package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBindings
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseDialogFragment
import com.robin.baseframe.app.util.inflateBindingWithGeneric
import com.robin.baseframe.app.util.viewbindingdelegate.viewBinding
import com.robin.baseframe.databinding.DialogBottomBinding
import com.robin.baseframe.ui.adapter.DemoAdapter

import android.graphics.drawable.ColorDrawable

import android.view.WindowManager

import android.view.Gravity

import android.view.ViewGroup

import android.app.Dialog


class BottomDialog : BaseDialogFragment(com.robin.baseframe.R.layout.dialog_bottom) {
    private val binding by viewBinding(DialogBottomBinding::bind)
    private val adapter by lazy { DemoAdapter(requireContext()) }
    private val itemList = arrayListOf("Item1", "Item2", "Item3", "Item4", "Item5")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val params = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT //设置宽度为铺满
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.BOTTOM
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        binding.run {
            rvItem.layoutManager = LinearLayoutManager(requireContext())
            rvItem.adapter = adapter
            adapter.setItems(itemList)
        }
    }
}