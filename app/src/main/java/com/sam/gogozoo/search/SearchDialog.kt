package com.sam.gogozoo.search

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.data.Control
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.databinding.DialogSearchBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.sortByMeter


/**
 * A simple [Fragment] subclass.
 */
class SearchDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<SearchViewModel> { getVmFactory() }
    lateinit var binding: DialogSearchBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NO_FRAME,
            R.style.LoginDialog
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.setListNav(MockData.allMarkers)

        // Inflate the layout for this fragment
        binding = DialogSearchBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.searchDialog.startAnimation(AnimationUtils.loadAnimation(context,
            R.anim.anim_search_in
        ))
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        var adapter = SearchAdapter(viewModel.sortInfo, viewModel)

        viewModel.listNav.observe(viewLifecycleOwner, Observer {
            it?.let {
                val sortInfo = sortByMeter(it)
                adapter =  SearchAdapter(sortInfo,viewModel)
                binding.rcySearch.adapter = adapter
                setSearchBar(adapter)
                adapter.notifyDataSetChanged()
            }
        })


        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        viewModel.selectInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                setInfoDialog(it)
                dismiss()
            }
        })

        return binding.root
    }

    private fun setInfoDialog(navInfo: NavInfo) {
        val list = MockData.animals.filter { info ->
            info.title == navInfo.title
        }
        val areaList = MockData.areas.filter { info ->
            info.title == navInfo.title
        }
        var image = 0
        list.forEach { info ->
            image = info.drawable
        }
        areaList.forEach {
            image = it.drawable
        }
        navInfo.image = image
        mainViewModel.setInfo(navInfo)
        mainViewModel.setMarkInfo(navInfo)
        Control.hasPolyline = false
    }

    private fun setSearchBar(adapter: SearchAdapter) {
        val searchBar = binding.searchBar
        searchBar.setHint(getString(R.string.animal_search))
        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun dismiss() {
        binding.searchDialog.startAnimation(AnimationUtils.loadAnimation(context,
            R.anim.anim_search_out
        ))
        Handler().postDelayed({ super.dismiss() }, 200)
    }

}
