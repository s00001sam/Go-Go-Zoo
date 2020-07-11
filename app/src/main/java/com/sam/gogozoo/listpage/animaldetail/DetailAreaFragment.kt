package com.sam.gogozoo.listpage.animaldetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.databinding.FragmentDetailAreaBinding
import com.sam.gogozoo.ext.getVmFactory

class DetailAreaFragment : Fragment() {

    companion object {
        fun newInstance() =
            DetailAreaFragment()
    }

    lateinit var binding: FragmentDetailAreaBinding
    private val viewModel by viewModels<DetailAreaViewModel> { getVmFactory(
        DetailAreaFragmentArgs.fromBundle(requireArguments()).localArea)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailAreaBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val localArea = DetailAreaFragmentArgs.fromBundle(requireArguments()).localArea
        viewModel.animals.value = MockData.localAnimals.filter { it.location == localArea?.name }
        viewModel.clickLocalArea.value = localArea
        Log.d("sam","detailclickArea=${viewModel.clickLocalArea.value}")

        val adapter = DetailAreaBottomAdapter(viewModel)
        binding.rcyAnimal.adapter = adapter

        viewModel.animals.observe(viewLifecycleOwner, Observer {
            Log.d("sam","thisAreaAnimals=$it")
            (binding.rcyAnimal.adapter as DetailAreaBottomAdapter).submitList(it)
        })


        return binding.root
    }



}
