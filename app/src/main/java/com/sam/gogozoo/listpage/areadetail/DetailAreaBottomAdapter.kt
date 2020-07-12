package com.sam.gogozoo.listpage.areadetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.bindImage
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.databinding.ItemListAnimalBinding

class DetailAreaBottomAdapter(private val onClickListener: OnclickListener, val viewModel: DetailAreaViewModel) :
    ListAdapter<LocalAnimal, DetailAreaBottomAdapter.ViewHolder>(DiffCallback) {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder(private val binding: ItemListAnimalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(animal: LocalAnimal) {
            binding.animal = animal

            if(animal.pictures != listOf<String>())
                 bindImage(binding.ImageAnimal, animal.pictures[0])

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListAnimalBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<LocalAnimal>() {
        override fun areItemsTheSame(oldItem: LocalAnimal, newItem: LocalAnimal): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LocalAnimal, newItem: LocalAnimal): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val localAnimal = getItem(position)

        holder.itemView.setOnClickListener {

            onClickListener.onClick(localAnimal)
            selectedPosition = position

        }

        holder.bind(localAnimal)
    }

    class OnclickListener(val clickListener: (localAnimal: LocalAnimal) -> Unit){
        fun onClick (localAnimal: LocalAnimal) = clickListener(localAnimal)
    }
}