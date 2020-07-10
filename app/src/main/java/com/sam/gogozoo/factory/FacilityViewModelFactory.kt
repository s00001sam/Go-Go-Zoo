package com.sam.gogozoo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.data.FacilityItem
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.facilityDialog.FacilityDialogViewModel

@Suppress("UNCHECKED_CAST")
class FacilityViewModelFactory(
    private val repository: ZooRepository,
    private val facilityItem: FacilityItem?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(FacilityDialogViewModel::class.java) ->
                    FacilityDialogViewModel(repository, facilityItem)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}