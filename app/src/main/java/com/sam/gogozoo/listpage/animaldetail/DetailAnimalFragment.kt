package com.sam.gogozoo.listpage.animaldetail

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.maps.model.LatLng
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.FragmentDetailAnimalBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Util.getDinstance

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

        val pictureAdapter = AnimalPictureAdapter()
        binding.rcyImage.adapter = pictureAdapter

        val animal = DetailAnimalFragmentArgs.fromBundle(requireArguments()).localAnimal
        viewModel.clickLocalAnimal.value = animal



        viewModel.clickLocalAnimal.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.nameEn != "")
                    binding.textName.text = it.nameCh + "\n" + it.nameEn
                else
                    binding.textName.text = it.nameCh

                val list = mutableListOf<String>()
                if (it.phylum != "")
                    list.add(it.phylum)
                if (it.clas != "")
                    list.add(it.clas)
                if (it.order != "")
                    list.add(it.order)
                if (it.family != "")
                    list.add(it.family)
                viewModel.listFamily.value = list

                it.geos.forEach {latlng ->
                    val facility = LocalFacility()
                    facility.name = it.nameCh
                    val listLat = mutableListOf<LatLng>()
                    listLat.add(latlng)
                    facility.geo = listLat
                    facility.imageUrl = it.pictures[0]
                    viewModel.animalToFac.add(facility)
                }
            }
        })

        binding.buttonNavigation.setOnClickListener {
            (activity as MainActivity).selectFacility.value = viewModel.animalToFac
            Handler().postDelayed(Runnable {
                this.findNavController().navigate(R.id.homeFragment) }, 300L)
        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val familyAdapter = AnimalFamilyAdapter(viewModel)
        binding.rcyFamily.adapter = familyAdapter
        viewModel.listFamily.observe(viewLifecycleOwner, Observer {
            if (it == listOf<String>()){
                binding.rcyFamily.visibility = View.GONE
            } else {
                (binding.rcyFamily.adapter as AnimalFamilyAdapter).submitList(it)
            }
        })

        val linearSnapHelper = LinearSnapHelper().apply {
            attachToRecyclerView(binding.rcyImage)
        }

        binding.rcyImage.setOnScrollChangeListener { _, _, _, _, _ ->
            viewModel.onGalleryScrollChange(
                binding.rcyImage.layoutManager,
                linearSnapHelper
            )
        }

        // set the initial position to the center of infinite gallery
        viewModel.clickLocalAnimal.value?.let { animal ->
            binding.rcyImage.scrollToPosition(animal.pictures.size * 100)

//            viewModel.snapPosition.observe(viewLifecycleOwner, Observer {
//                (binding.recyclerDetailCircles.adapter as DetailCircleAdapter).selectedPosition.value =
//                    (it % product.images.size)
//            })
        }



        return binding.root
    }


}
