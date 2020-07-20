package com.sam.gogozoo.route

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.databinding.ItemScheduleBinding
import java.util.*

class RouteAdapter(private val allInfos:List<NavInfo>, val viewModel: RouteViewModel): RecyclerView.Adapter<RouteAdapter.RouteViewHolder>(), ItemMoveSwipeListener {

    private lateinit var context: Context
    var selectedPosition = -1
    // the data of adapter
    private var infos: MutableList<NavInfo>? = allInfos.toMutableList()

    fun ItemTouchHelperAdapter(list: MutableList<NavInfo>) {
        this.infos = list
    }

    class RouteViewHolder(private var binding: ItemScheduleBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(info: NavInfo) {
            binding.navInfo = info
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        context = parent.context
        return RouteViewHolder(ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            selectedPosition = position
        }
        infos?.let {
            val info = it[position]
            holder.bind(info)
        }
    }

    override fun getItemCount(): Int {
        return infos?.size ?: 0
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        // Collections.swap() 該方法是用來交換位置
        Collections.swap(infos, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


    override fun onItemSwipe(position: Int) {
        infos?.removeAt(position)
        notifyItemRemoved(position)
    }


}