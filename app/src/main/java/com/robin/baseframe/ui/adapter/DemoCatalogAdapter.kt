package com.robin.baseframe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.robin.baseframe.databinding.ItemDemoCatalogBinding
import com.robin.baseframe.databinding.ItemDemoCategoryBinding
import com.robin.baseframe.ui.home.DemoCatalogRow

class DemoCatalogAdapter(
    private val onItemClick: (DemoCatalogRow.DemoItem) -> Unit
) : ListAdapter<DemoCatalogRow, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is DemoCatalogRow.CategoryHeader -> VIEW_TYPE_CATEGORY
        is DemoCatalogRow.DemoItem -> VIEW_TYPE_DEMO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CATEGORY -> CategoryViewHolder(
                ItemDemoCategoryBinding.inflate(inflater, parent, false)
            )

            else -> DemoViewHolder(
                ItemDemoCatalogBinding.inflate(inflater, parent, false),
                onItemClick
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> holder.bind(getItem(position) as DemoCatalogRow.CategoryHeader)
            is DemoViewHolder -> holder.bind(getItem(position) as DemoCatalogRow.DemoItem)
        }
    }

    fun isCategory(position: Int): Boolean = getItem(position) is DemoCatalogRow.CategoryHeader

    private class CategoryViewHolder(
        private val binding: ItemDemoCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: DemoCatalogRow.CategoryHeader) {
            binding.categoryTitle.setText(row.category.titleRes)
        }
    }

    private class DemoViewHolder(
        private val binding: ItemDemoCatalogBinding,
        private val onItemClick: (DemoCatalogRow.DemoItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: DemoCatalogRow.DemoItem) {
            val item = row.item
            binding.demoIcon.setImageResource(item.iconRes)
            binding.demoTitle.setText(item.titleRes)
            binding.demoSummary.setText(item.summaryRes)
            binding.root.setOnClickListener { onItemClick(row) }
        }
    }

    private companion object {
        const val VIEW_TYPE_CATEGORY = 0
        const val VIEW_TYPE_DEMO = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DemoCatalogRow>() {
            override fun areItemsTheSame(oldItem: DemoCatalogRow, newItem: DemoCatalogRow): Boolean {
                return when {
                    oldItem is DemoCatalogRow.CategoryHeader && newItem is DemoCatalogRow.CategoryHeader ->
                        oldItem.category == newItem.category

                    oldItem is DemoCatalogRow.DemoItem && newItem is DemoCatalogRow.DemoItem ->
                        oldItem.item.id == newItem.item.id

                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: DemoCatalogRow, newItem: DemoCatalogRow): Boolean =
                oldItem == newItem
        }
    }
}
