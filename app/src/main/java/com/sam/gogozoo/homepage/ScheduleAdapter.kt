package com.sam.gogozoo.homepage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.R
import com.sam.gogozoo.bindImageCircle
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.databinding.ItemScheduleBinding
import com.sam.gogozoo.util.Logger

class ScheduleAdapter(val viewModel: HomeViewModel) : ListAdapter<NavInfo, ScheduleAdapter.ViewHolder>(DiffCallback) {

    var selectedPosition = -1
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder.from(parent)
    }


    class ViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind(
            navInfo: NavInfo,
            viewModel: HomeViewModel
        ) {
            binding.navInfo = navInfo

            val filterArea = MockData.localAreas.filter { it.name == navInfo.title }
            val filterAnimal = MockData.localAnimals.filter { it.nameCh == navInfo.title }
            if (filterArea != listOf<LocalArea>()){
                bindImageCircle(binding.circularImageView, filterArea[0].picture)
            } else if (filterAnimal != listOf<LocalAnimal>()){
                bindImageCircle(binding.circularImageView, filterAnimal[0].pictures[0])
            }else
                binding.circularImageView.setImageResource(R.drawable.icon_house)

            viewModel.edit.observe(this, Observer {
                Logger.d("edit=$it")
                if (it == true)
                    binding.buttonDelete.visibility = View.VISIBLE
                if (it == false)
                    binding.buttonDelete.visibility = View.GONE
            })

            binding.buttonDelete.setOnClickListener {
                viewModel.deleteNavInfo.value = navInfo
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemScheduleBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun markAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun markDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<NavInfo>() {
        override fun areItemsTheSame(oldItem: NavInfo, newItem: NavInfo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: NavInfo, newItem: NavInfo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val navInfo = getItem(position)

        holder.itemView.setOnClickListener {
            selectedPosition = position
        }

        holder.bind(navInfo, viewModel)
    }
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }

}