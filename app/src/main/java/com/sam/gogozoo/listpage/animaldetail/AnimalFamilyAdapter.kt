package com.sam.gogozoo.listpage.animaldetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.ItemFamilyBinding
import com.sam.gogozoo.databinding.ItemHomeFacilityBinding
import com.sam.gogozoo.databinding.ItemHomeTopBinding
import com.sam.gogozoo.databinding.ItemListBinding

class AnimalFamilyAdapter(val viewModel: DetailAnimalViewModel) : ListAdapter<String, AnimalFamilyAdapter.ViewHolder>(DiffCallback) {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder(private val binding: ItemFamilyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(string: String) {
            binding.string = string
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFamilyBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val string = getItem(position)

        holder.itemView.setOnClickListener {
            selectedPosition = position
        }

        holder.bind(string)
    }
}