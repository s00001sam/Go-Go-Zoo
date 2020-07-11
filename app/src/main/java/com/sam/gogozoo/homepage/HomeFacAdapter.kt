package com.sam.gogozoo.homepage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.ItemHomeFacilityBinding
import com.sam.gogozoo.databinding.ItemHomeTopBinding

class HomeFacAdapter(val viewModel: HomeViewModel) : ListAdapter<LocalFacility, HomeFacAdapter.ViewHolder>(DiffCallback) {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder(private val binding: ItemHomeFacilityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(facility: LocalFacility) {
            binding.facility = facility
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHomeFacilityBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<LocalFacility>() {
        override fun areItemsTheSame(oldItem: LocalFacility, newItem: LocalFacility): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LocalFacility, newItem: LocalFacility): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val facility = getItem(position)

        holder.itemView.setOnClickListener {
            selectedPosition = position
            viewModel.selectFac.value = getItem(selectedPosition)
        }

        holder.bind(facility)
    }
}