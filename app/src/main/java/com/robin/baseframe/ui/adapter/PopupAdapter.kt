package com.robin.baseframe.ui.adapter

import android.content.Context
import android.view.ViewGroup
import com.robin.baseframe.app.base.adapter.ItemViewHolder
import com.robin.baseframe.app.base.adapter.RecyclerAdapter
import com.robin.baseframe.app.ext.toast

import com.robin.baseframe.databinding.ItemPopupViewBinding

class PopupAdapter(context: Context) : RecyclerAdapter<String, ItemPopupViewBinding>(context) {
    override fun getViewBinding(parent: ViewGroup): ItemPopupViewBinding {
        return ItemPopupViewBinding.inflate(inflater, parent, false)
    }

    override fun convert(
        holder: ItemViewHolder,
        binding: ItemPopupViewBinding,
        item: String,
        payloads: MutableList<Any>
    ) {
        binding.tvItemView.text = "Item $item"
    }

    override fun registerListener(holder: ItemViewHolder, binding: ItemPopupViewBinding) {
        binding.run {
            root.setOnClickListener {
                val content = getItem(holder.adapterPosition - getHeaderCount())
                val position = holder.adapterPosition - getHeaderCount()
                toast(position.toString() + content)
            }
        }
    }
}