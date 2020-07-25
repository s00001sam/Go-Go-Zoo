package com.sam.gogozoo.listpage.animaldetail


import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.data.source.ZooRepository

class DetailAnimalViewModel(private val repository: ZooRepository, private val localAnimal: LocalAnimal?) : ViewModel() {

    val clickLocalAnimal = MutableLiveData<LocalAnimal>()
    val listFamily = MutableLiveData<List<String>>()

    var animalToFac = mutableListOf<LocalFacility>()

    private val _snapPosition = MutableLiveData<Int>()
    val snapPosition: LiveData<Int>
        get() = _snapPosition

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
