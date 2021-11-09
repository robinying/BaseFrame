package com.robin.baseframe.ui.adapter

import android.content.Context
import android.view.ViewGroup
import com.robin.baseframe.app.base.adapter.ItemViewHolder
import com.robin.baseframe.app.base.adapter.RecyclerAdapter
import com.robin.baseframe.app.ext.toast
import com.robin.baseframe.databinding.ItemDemoStrBinding

class DemoAdapter(context: Context) : RecyclerAdapter<String, ItemDemoStrBinding>(context) {
    override fun getViewBinding(parent: ViewGroup): ItemDemoStrBinding {
        return ItemDemoStrBinding.inflate(inflater, parent, false)
    }

    override fun convert(
        holder: ItemViewHolder,
        binding: ItemDemoStrBinding,
        item: String,
        payloads: MutableList<Any>
    ) {
        binding.apply {
            tvItem.text = item
        }
    }

    override fun registerListener(holder: ItemViewHolder, binding: ItemDemoStrBinding) {
        binding.run {
            root.setOnClickListener {
                val content = getItem(holder.adapterPosition)
                toast(content ?: "")

            }

        }
    }
}