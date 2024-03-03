package com.mkdevelopment.myaccounts.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.databinding.IconItemBinding

class IconAdapter(private val onItemClickListener: (Int) -> Unit) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {
    private var dataList = emptyList<Int>()
    private var selectedPosition: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        return IconViewHolder(
            IconItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.binding.icon.setImageResource(currentItem)

        if (position == selectedPosition) {
            holder.binding.categoryLayout.setBackgroundResource(R.drawable.icon_selected)
        } else {
            holder.binding.categoryLayout.setBackgroundResource(R.drawable.icon_unselected)
        }
        holder.binding.categoryLayout.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)
            onItemClickListener(selectedPosition)
        }
        holder.binding.categoryLayout.isSelected = position == selectedPosition
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class IconViewHolder(val binding: IconItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(iconList: List<Int>) {
        dataList = iconList
        notifyDataSetChanged()
    }

}

