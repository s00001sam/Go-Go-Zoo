package com.sam.gogozoo.listpage.animaldetail


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.data.source.ZooRepository

class DetailAnimalViewModel(private val repository: ZooRepository, private val localAnimal: LocalAnimal?) : ViewModel() {

    val clickLocalAnimal = MutableLiveData<LocalAnimal>()
    val listFamily = MutableLiveData<List<String>>()

    private val _snapPosition = MutableLiveData<Int>()

    var animalToFac = mutableListOf<LocalFacility>()

    val snapPosition: LiveData<Int>
        get() = _snapPosition


    init {

    }

    fun onGalleryScrollChange(layoutManager: RecyclerView.LayoutManager?, linearSnapHelper: LinearSnapHelper) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let {
                if (it != snapPosition.value) {
                    _snapPosition.value = it
                }
            }
        }
    }

}
