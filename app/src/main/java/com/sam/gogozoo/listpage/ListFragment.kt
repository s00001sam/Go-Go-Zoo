package com.sam.gogozoo.listpage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.databinding.ListFragmentBinding
import com.sam.gogozoo.ext.getVmFactory

class ListFragment : Fragment() {

    private val viewModel by viewModels<ListViewModel> { getVmFactory() }

    companion object {
        fun newInstance() = ListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ListFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.mockAreas.value = MockData.localAreas.sortedBy { it.meter }

        val adapter = ListPageAdapter(ListPageAdapter.OnclickListener{
            viewModel.displayArea(it)
        },viewModel)
        binding.rcyList.adapter = adapter

        viewModel.mockAreas.observe(viewLifecycleOwner, Observer {
            it?.let {
                (binding.rcyList.adapter as ListPageAdapter).submitList(it)
            }
        })

        viewModel.navigationArea.observe(viewLifecycleOwner, Observer {
            if (null != it){
                Log.d("sam","clickArea=$it")
                this.findNavController().navigate(ListFragmentDirections.actionListFragmentToDetailAreaFragment(it))
                viewModel.displayAreaComplete()
            }
        })



        return binding.root
    }


}
