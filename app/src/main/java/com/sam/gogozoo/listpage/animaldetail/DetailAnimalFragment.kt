package com.sam.gogozoo.listpage.animaldetail

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
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.maps.model.LatLng
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.FragmentDetailAnimalBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Logger.d
import com.sam.gogozoo.util.Util.getDinstance
import com.sam.gogozoo.util.Util.toOnePlace

class DetailAnimalFragment : Fragment() {

    private val viewModel by viewModels<DetailAnimalViewModel> { getVmFactory(
        DetailAnimalFragmentArgs.fromBundle(requireArguments()).localAnimal
    ) }
    lateinit var binding: FragmentDetailAnimalBinding

    companion object {
        fun newInstance() = DetailAnimalFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailAnimalBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val pictureAdapter = AnimalPictureAdapter()
        binding.rcyImage.adapter = pictureAdapter

        val animal = DetailAnimalFragmentArgs.fromBundle(requireArguments()).localAnimal
        viewModel.setClickLocalAnimal(animal)

        viewModel.clickLocalAnimal.observe(viewLifecycleOwner, Observer {
            it?.let {
                setName(it)
                viewModel.setDetail(it)
                viewModel.getMoreAnimals(it)
            }
        })

        val moreAdapter = MoreAnimalAdapter(MoreAnimalAdapter.OnclickListener {
            viewModel.displayAnimal(it)
        },viewModel)

        binding.rcyMoreAnimal.adapter = moreAdapter

        viewModel.moreAnimals.observe(viewLifecycleOwner, Observer {
            Logger.d("moreAnimals=$it")
            it?.let {
                (binding.rcyMoreAnimal.adapter as MoreAnimalAdapter).submitList(it)
            }
        })

        viewModel.navigationAnimal.observe(viewLifecycleOwner, Observer {
            if (null != it){
                Logger.d("clickAnimal=$it")
                this.findNavController().navigate(DetailAnimalFragmentDirections.actionGlobalDetailAnimalFragment(it))
                viewModel.displayAnimalComplete()
            }
        })

        binding.buttonNavigation.setOnClickListener {
            mainViewModel.setSelectNavAnimal(viewModel.animalToFac)
            Handler().postDelayed(Runnable {
                this.findNavController().navigate(R.id.homeFragment) }, 300L)
        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.listFragment)
        }

        val familyAdapter = AnimalFamilyAdapter(viewModel)
        binding.rcyFamily.adapter = familyAdapter
        viewModel.listFamily.observe(viewLifecycleOwner, Observer {
            it?.let {
                setRcyFamily(it)
            }
        })

        val linearSnapHelper = LinearSnapHelper().apply {
            attachToRecyclerView(binding.rcyImage)
        }

        binding.rcyImage.setOnScrollChangeListener { _, _, _, _, _ ->
            viewModel.onGalleryScrollChange(binding.rcyImage.layoutManager, linearSnapHelper)
        }

        binding.rcyCircles.adapter = AnimalCircleAdapter()

        // set the initial position to the center of infinite gallery
        viewModel.clickLocalAnimal.value?.let { localAnimal ->
            binding.rcyImage.scrollToPosition(localAnimal.pictures.size * 100)

            viewModel.snapPosition.observe(viewLifecycleOwner, Observer {
                it?.let {
                    (binding.rcyCircles.adapter as AnimalCircleAdapter).selectedPosition.value =
                        (it % localAnimal.pictures.size)
                }
            })
        }

        return binding.root
    }

    private fun setRcyFamily(it: List<String>?) {
        if (it == listOf<String>()) {
            binding.rcyFamily.visibility = View.GONE
        } else {
            (binding.rcyFamily.adapter as AnimalFamilyAdapter).submitList(it)
        }
    }

    private fun setName(it: LocalAnimal) {
        if (it.nameEn != "") {
            binding.textName.text = it.nameCh + "\n" + it.nameEn
        }else {
            binding.textName.text = it.nameCh
        }
    }

}
