package com.sam.gogozoo.listpage.areadetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.data.source.ZooRepository

class DetailAreaViewModel(private val repository: ZooRepository, private val localArea: LocalArea?) : ViewModel() {

    val clickLocalArea = MutableLiveData<LocalArea>()
    val animals = MutableLiveData<List<LocalAnimal>>()

    var areaToFac = mutableListOf<LocalFacility>()

    private val _navigationAnimal = MutableLiveData<LocalAnimal>()
    val navigationAnimal: LiveData<LocalAnimal>
        get() = _navigationAnimal

    init {

    }

    fun displayAnimal(localAnimal: LocalAnimal){
        _navigationAnimal.value = localAnimal
    }
    fun displayAnimalComplete(){
        _navigationAnimal.value = null
    }
}
