package com.sam.gogozoo.search

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.databinding.ItemSearchBinding

class SearchAdapter(private val allInfos:List<NavInfo>, val viewModel: SearchViewModel): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>(),Filterable {

    private lateinit var context: Context
    var selectedPosition = -1
    // the data of adapter
    private var infos: List<NavInfo>? = allInfos

    class SearchViewHolder(private var binding: ItemSearchBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(info: NavInfo) {
            binding.info = info
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        context = parent.context
        return SearchViewHolder(ItemSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            selectedPosition = position
            viewModel.selectIofo.value = infos?.get(selectedPosition)
        }
        infos?.let {
            val info = it[position]
            holder.bind(info)
        }
    }

    override fun getItemCount(): Int {
        return infos?.size ?: 0
    }

//    fun submitInfos(infos: List<NavInfo>) {
//        this.infos = infos
//        notifyDataSetChanged()
//    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                infos = filterResults.values as List<NavInfo>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                filterResults.values = if (queryString==null || queryString.isEmpty())
                    allInfos
                else
                    allInfos?.filter {
                        it.title.toLowerCase().contains(queryString)
                    }
                return filterResults
            }
        }
    }
}