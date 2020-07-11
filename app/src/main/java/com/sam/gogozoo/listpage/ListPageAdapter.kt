package com.sam.gogozoo.listpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.ItemHomeFacilityBinding
import com.sam.gogozoo.databinding.ItemHomeTopBinding
import com.sam.gogozoo.databinding.ItemListBinding

class ListPageAdapter(private val onClickListener: OnclickListener, val viewModel: ListViewModel) : ListAdapter<LocalArea, ListPageAdapter.ViewHolder>(DiffCallback) {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(area: LocalArea) {
            binding.area = area
            binding.imageArea.setImageResource(area.image)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<LocalArea>() {
        override fun areItemsTheSame(oldItem: LocalArea, newItem: LocalArea): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LocalArea, newItem: LocalArea): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val localArea = getItem(position)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(localArea)
            selectedPosition = position
        }

        holder.bind(localArea)
    }

    class OnclickListener(val clickListener: (localArea: LocalArea) -> Unit){
        fun onClick (localArea: LocalArea) = clickListener(localArea)
    }
}