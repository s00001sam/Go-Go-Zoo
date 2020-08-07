package com.sam.gogozoo.listpage.areadetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.util.Util.getString

class DetailAreaViewModel(private val repository: ZooRepository, private val localArea: LocalArea?) : ViewModel() {

    private val _clickLocalArea = MutableLiveData<LocalArea>()

    val clickLocalArea: LiveData<LocalArea>
        get() = _clickLocalArea

    private val _animals = MutableLiveData<List<LocalAnimal>>()

    val animals: LiveData<List<LocalAnimal>>
        get() = _animals

    private val _navigationAnimal = MutableLiveData<LocalAnimal>()

    val navigationAnimal: LiveData<LocalAnimal>
        get() = _navigationAnimal

    var areaToFac = mutableListOf<LocalFacility>()

    init {

    }

    fun setClickArea(localArea: LocalArea?){
        _clickLocalArea.value = localArea
    }

    fun displayAnimal(localAnimal: LocalAnimal){
        _navigationAnimal.value = localAnimal
    }
    fun displayAnimalComplete(){
        _navigationAnimal.value = null
    }

    fun findAnimals(localArea: LocalArea){
        var selectAnimals: List<LocalAnimal>
        if (localArea.name == getString(R.string.panda_place)){
            selectAnimals = MockData.localAnimals.filter { it.location == getString(R.string.panda_place2) }
        }else if (localArea.name == getString(R.string.pangolin_place)){
            selectAnimals = MockData.localAnimals.filter { it.location.contains(getString(R.string.pangolin_place2)) }
        }else{
            selectAnimals = MockData.localAnimals.filter { localArea.name.let { name -> it.location.contains(other = name) } }
        }
        _animals.value = selectAnimals
    }

    fun setAreaToFac(area: LocalArea){
        val facility = LocalFacility()
        facility.name = area.name
        facility.geo = area.geo
        areaToFac.add(facility)
    }
}
