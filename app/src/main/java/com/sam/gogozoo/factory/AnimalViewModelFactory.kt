package com.sam.gogozoo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.listpage.animaldetail.DetailAnimalViewModel
import com.sam.gogozoo.listpage.areadetail.DetailAreaViewModel

@Suppress("UNCHECKED_CAST")
class AnimalViewModelFactory(
    private val repository: ZooRepository,
    private val localAnimal: LocalAnimal?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(DetailAnimalViewModel::class.java) ->
                    DetailAnimalViewModel(repository, localAnimal)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}