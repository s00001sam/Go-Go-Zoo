package com.sam.gogozoo.listpage.animaldetail


import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.util.Util.toOnePlace

class DetailAnimalViewModel(private val repository: ZooRepository, private val localAnimal: LocalAnimal?) : ViewModel() {

    private val _clickLocalAnimal = MutableLiveData<LocalAnimal>()
    val clickLocalAnimal: LiveData<LocalAnimal>
        get() = _clickLocalAnimal

    private val _listFamily = MutableLiveData<List<String>>()
    val listFamily: LiveData<List<String>>
        get() = _listFamily

    private val _snapPosition = MutableLiveData<Int>()
    val snapPosition: LiveData<Int>
        get() = _snapPosition

    private val _navigationAnimal = MutableLiveData<LocalAnimal>()
    val navigationAnimal: LiveData<LocalAnimal>
        get() = _navigationAnimal

    private val _moreAnimals = MutableLiveData<List<LocalAnimal>>()
    val moreAnimals: LiveData<List<LocalAnimal>>
        get() = _moreAnimals

    var animalToFac = mutableListOf<LocalFacility>()

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left = ZooApplication.INSTANCE.resources.getDimensionPixelSize(R.dimen.space_detail_circle)
            }
        }
    }


    init {
    }

    fun setClickLocalAnimal(localAnimal: LocalAnimal?){
        _clickLocalAnimal.value = localAnimal
    }

    fun setDetail(animal: LocalAnimal){
        val list = mutableListOf<String>()
        if (animal.phylum != "")
            list.add(animal.phylum)
        if (animal.clas != "")
            list.add(animal.clas)
        if (animal.order != "")
            list.add(animal.order)
        if (animal.family != "")
            list.add(animal.family)
        _listFamily.value = list

        animal.geos.forEach {latlng ->
            val facility = LocalFacility()
            facility.name = animal.nameCh
            val listLat = mutableListOf<LatLng>()
            listLat.add(latlng)
            facility.geo = listLat
            facility.imageUrl = animal.pictures[0]
            animalToFac.add(facility)
        }
    }

    fun getMoreAnimals(localAnimal: LocalAnimal){
        val filterList = MockData.localAnimals.filter { animal -> animal.location.contains(localAnimal.location.toOnePlace()) }
        val removeList = filterList.toMutableList()
        removeList.remove(localAnimal)
        _moreAnimals.value = removeList
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

    fun displayAnimal(localAnimal: LocalAnimal){
        _navigationAnimal.value = localAnimal
    }
    fun displayAnimalComplete(){
        _navigationAnimal.value = null
    }

}
