package com.sam.gogozoo.listpage.animaldetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.source.ZooRepository

class DetailAreaViewModel(private val repository: ZooRepository, private val localArea: LocalArea?) : ViewModel() {

    val clickLocalArea = MutableLiveData<LocalArea>()
    val animals = MutableLiveData<List<LocalAnimal>>()

    init {

    }

}
