package com.sam.gogozoo.stepcount.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.data.StepInfo
import com.sam.gogozoo.databinding.ItemHomeTopBinding
import com.sam.gogozoo.databinding.ItemRecordBinding
import com.sam.gogozoo.util.Util.StampToDate
import com.sam.gogozoo.util.Util.to2fString
import com.sam.gogozoo.util.Util.to3fString
import com.sam.gogozoo.util.Util.toTimeString

class RecordAdapter(val viewModel: RecordViewModel) : ListAdapter<StepInfo, RecordAdapter.ViewHolder>(DiffCallback) {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stepInfo: StepInfo) {
            binding.stepInfo = stepInfo

            val min = ((stepInfo.time)/60).toTimeString()
            val sec = ((stepInfo.time)% 60).toTimeString()
            binding.textDate.text = stepInfo.createdTime.StampToDate()
            binding.textCountTime.text = "${min}分 ${sec}秒"
            binding.textKm.text = stepInfo.kilometer.to3fString()
            binding.textKcal.text = stepInfo.kcal.to2fString()

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRecordBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<StepInfo>() {
        override fun areItemsTheSame(oldItem: StepInfo, newItem: StepInfo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: StepInfo, newItem: StepInfo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stepInfo = getItem(position)

        holder.itemView.setOnClickListener {
            selectedPosition = position
        }

        holder.bind(stepInfo)
    }
}