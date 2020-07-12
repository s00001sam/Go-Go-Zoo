package com.sam.gogozoo.ext

import androidx.fragment.app.Fragment
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.FacilityItem
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.animal.Animal
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.factory.*


/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Extension functions for Fragment.
 */
fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as ZooApplication).repository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(navInfo: NavInfo?): InfoViewModelFactory {
    val repository = (requireContext().applicationContext as ZooApplication).repository
    return InfoViewModelFactory(repository, navInfo)
}

fun Fragment.getVmFactory(facilityItem: FacilityItem?): FacilityViewModelFactory {
    val repository = (requireContext().applicationContext as ZooApplication).repository
    return FacilityViewModelFactory(repository, facilityItem)
}

fun Fragment.getVmFactory(localArea: LocalArea?): AreaViewModelFactory {
    val repository = (requireContext().applicationContext as ZooApplication).repository
    return AreaViewModelFactory(repository, localArea)
}

fun Fragment.getVmFactory(localAnimal: LocalAnimal?): AnimalViewModelFactory {
    val repository = (requireContext().applicationContext as ZooApplication).repository
    return AnimalViewModelFactory(repository, localAnimal)
}
