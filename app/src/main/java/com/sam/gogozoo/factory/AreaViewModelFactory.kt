package com.sam.gogozoo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.data.FacilityItem
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.facilityDialog.FacilityDialogViewModel
import com.sam.gogozoo.listpage.animaldetail.DetailAreaViewModel

@Suppress("UNCHECKED_CAST")
class AreaViewModelFactory(
    private val repository: ZooRepository,
    private val localArea: LocalArea?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(DetailAreaViewModel::class.java) ->
                    DetailAreaViewModel(repository, localArea)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}