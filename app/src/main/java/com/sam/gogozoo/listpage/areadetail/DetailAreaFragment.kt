package com.sam.gogozoo.listpage.areadetail

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.facility.LocalFacility
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

        var selectAnimals: List<LocalAnimal>
        if (localArea?.name == "新光特展館（大貓熊館）"){
            selectAnimals = MockData.localAnimals.filter { it.location == "新光特展館(大貓熊館)" }
        }else if (localArea?.name == "熱帶雨林室內館（穿山甲館）"){
            selectAnimals = MockData.localAnimals.filter { it.location.contains("熱帶雨林室內館(穿山甲館)") }
        }else{
            selectAnimals = MockData.localAnimals.filter { localArea?.name?.let { name -> it.location.contains(other = name) }!! }
        }
        Log.d("sam","selectAnimals=$selectAnimals")

        viewModel.animals.value = selectAnimals
        Log.d("sam","mockanimal=${MockData.localAnimals}")
        viewModel.clickLocalArea.value = localArea
        Log.d("sam","detailclickArea=${viewModel.clickLocalArea.value}")
        viewModel.clickLocalArea.observe(viewLifecycleOwner, Observer {area ->
            val facility = LocalFacility()
            facility.name = area.name
            facility.geo = area.geo
            viewModel.areaToFac.add(facility)
        })

        binding.buttonNavigation.setOnClickListener {
            (activity as MainActivity).selectFacility.value = viewModel.areaToFac
            Handler().postDelayed(Runnable {
                this.findNavController().navigate(R.id.homeFragment) }, 300L)
        }

        val adapter = DetailAreaBottomAdapter(DetailAreaBottomAdapter.OnclickListener {
            viewModel.displayAnimal(it)
        },viewModel)

        binding.rcyAnimal.adapter = adapter

        viewModel.animals.observe(viewLifecycleOwner, Observer {
            Log.d("sam","thisAreaAnimals=$it")
            (binding.rcyAnimal.adapter as DetailAreaBottomAdapter).submitList(it)
        })

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.listFragment)
        }

        binding

        viewModel.navigationAnimal.observe(viewLifecycleOwner, Observer {
            if (null != it){
                Log.d("sam","clickAnimal=$it")
                this.findNavController().navigate(DetailAreaFragmentDirections.actionDetailAreaFragmentToDetailAnimalFragment(it))
                viewModel.displayAnimalComplete()
            }
        })
            return binding.root
        }
    }




