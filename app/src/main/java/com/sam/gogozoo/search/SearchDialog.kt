package com.sam.gogozoo.search

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.data.Control
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.databinding.DialogSearchBinding
import com.sam.gogozoo.ext.getVmFactory


/**
 * A simple [Fragment] subclass.
 */
class SearchDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<SearchViewModel> { getVmFactory() }
    lateinit var binding: DialogSearchBinding

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

        viewModel.listNav.value = MockData.allMarkers

        // Inflate the layout for this fragment
        binding = DialogSearchBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.searchDialog.startAnimation(AnimationUtils.loadAnimation(context,
            R.anim.anim_search_in
        ))

        var adapter = SearchAdapter(viewModel.sortInfo, viewModel)

        viewModel.listNav.observe(viewLifecycleOwner, Observer {
            val sortInfo = it.sortedBy { it.meter }
            adapter =  SearchAdapter(sortInfo,viewModel)
            binding.rcySearch.adapter = adapter
            adapter.notifyDataSetChanged()
        })

        Log.d("sam","info=${viewModel.infos}")
//        adapter.submitInfos(viewModel.infos)

        val searchBar = binding.searchBar
        searchBar.setHint("動物...")
        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {

            }
        })

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("sam","leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        viewModel.selectIofo.observe(viewLifecycleOwner, Observer {
            Log.d("sam", "selectInfo=$it")
            val list = MockData.animals.filter { info ->
                info.title == it.title
            }
            val areaList = MockData.areas.filter { info ->
                info.title == it.title
            }
            var image = 0
            list.forEach {info ->
                image = info.drawable
            }
            areaList.forEach {
                image = it.drawable
            }
            it.image = image
            (activity as MainActivity).info.value = it
            (activity as MainActivity).markInfo.value = it
            Control.hasPolyline = false
            dismiss()
        })

        return binding.root
    }

    override fun dismiss() {
        binding.searchDialog.startAnimation(AnimationUtils.loadAnimation(context,
            R.anim.anim_search_out
        ))
        Handler().postDelayed({ super.dismiss() }, 200)
    }

}
