package com.sam.gogozoo.listpage.areadetail

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.databinding.FragmentDetailAreaBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger

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
        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val localArea = DetailAreaFragmentArgs.fromBundle(requireArguments()).localArea
        viewModel.setClickArea(localArea)

        viewModel.clickLocalArea.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.findAnimals(it)
                viewModel.setAreaToFac(it)
            }
        })

        binding.buttonNavigation.setOnClickListener {
            mainViewModel.setSelectNavAnimal(viewModel.areaToFac)
            Handler().postDelayed(Runnable {
                this.findNavController().navigate(R.id.homeFragment) }, 300L)
        }

        val adapter = DetailAreaBottomAdapter(DetailAreaBottomAdapter.OnclickListener {
            viewModel.displayAnimal(it)
        },viewModel)

        binding.rcyAnimal.adapter = adapter

        viewModel.animals.observe(viewLifecycleOwner, Observer {
            it?.let {
                (binding.rcyAnimal.adapter as DetailAreaBottomAdapter).submitList(it)
            }
        })

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.listFragment)
        }

        viewModel.navigationAnimal.observe(viewLifecycleOwner, Observer {
            if (null != it){
                Logger.d("clickAnimal=$it")
                this.findNavController().navigate(DetailAreaFragmentDirections.actionDetailAreaFragmentToDetailAnimalFragment(it))
                viewModel.displayAnimalComplete()
            }
        })

        return binding.root
        }

    }




