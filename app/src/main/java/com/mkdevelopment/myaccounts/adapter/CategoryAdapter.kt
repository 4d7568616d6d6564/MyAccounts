package com.mkdevelopment.myaccounts.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.common.ItemClickListener
import com.mkdevelopment.myaccounts.common.ItemLongClickListener
import com.mkdevelopment.myaccounts.database.CategoryEntity
import com.mkdevelopment.myaccounts.databinding.CategoryItemBinding

class CategoryAdapter(
    private val itemClickListener: ItemClickListener,
    private val itemLongClickListener: ItemLongClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private var dataList = emptyList<CategoryEntity>()
    private var selectedPosition: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            CategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.binding.categoryText.text = currentItem.title
        holder.binding.categoryLayout.isSelected = position == selectedPosition

        if (position == selectedPosition) {
            holder.binding.categoryLayout.setBackgroundResource(R.drawable.category_selected)
            holder.binding.categoryText.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.category_selected_text
                )
            )
            val gilroyBlackTypeface =
                ResourcesCompat.getFont(holder.itemView.context, R.font.gilroy_bold)
            holder.binding.categoryText.typeface = gilroyBlackTypeface
        } else {
            holder.binding.categoryLayout.setBackgroundResource(R.drawable.category_unselected)
            holder.binding.categoryText.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.category_unselected_text
                )
            )
            val gilroyBlackTypeface =
                ResourcesCompat.getFont(holder.itemView.context, R.font.gilroy_regular)
            holder.binding.categoryText.typeface = gilroyBlackTypeface
        }
        holder.binding.categoryLayout.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)
            itemClickListener.onItemClick(currentItem.id)
        }

        holder.binding.categoryLayout.isLongClickable = true
        holder.binding.categoryLayout.setOnLongClickListener {
            if (currentItem.id != 0) {
                itemLongClickListener.onItemClick(currentItem)
            }
            true
        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class CategoryViewHolder(val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(categoryList: List<CategoryEntity>) {
        dataList = categoryList
        selectedPosition = 0
        notifyDataSetChanged()
    }


}

