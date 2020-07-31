package com.sam.gogozoo.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.data.calendar.LocalCalendar
import com.sam.gogozoo.databinding.ItemCalendarBinding

class CalendarAdapter(val viewModel: CalendarViewModel) : ListAdapter<LocalCalendar, CalendarAdapter.ViewHolder>(DiffCallback) {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder(private val binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(calendar: LocalCalendar) {
            binding.calendar = calendar
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCalendarBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<LocalCalendar>() {
        override fun areItemsTheSame(oldItem: LocalCalendar, newItem: LocalCalendar): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LocalCalendar, newItem: LocalCalendar): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calendar = getItem(position)

        holder.itemView.setOnClickListener {
            selectedPosition = position
            viewModel.selectLocalCalendar.value = getItem(selectedPosition)
        }

        holder.bind(calendar)
    }
}