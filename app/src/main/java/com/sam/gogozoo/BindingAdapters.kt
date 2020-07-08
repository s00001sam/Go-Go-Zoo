package com.sam.gogozoo

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.os.Build
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.search.SearchAdapter
import java.time.Instant
import java.time.ZoneId
import java.util.*

/**
 * Created by Wayne Chen in Jul. 2019.
 */


//@BindingAdapter("searchs")
//fun bindRecyclerViewWithInfos(recyclerView: RecyclerView, images: List<NavInfo>?) {
//    images?.let {
//        recyclerView.adapter?.apply {
//            when (this) {
//                is SearchAdapter -> {
//                    submitInfos(it)
//                }
//            }
//        }
//    }
//}
//
@BindingAdapter("meter")
fun bindmeter(textView: TextView, price: Int?) {
    price?.let { textView.text = it.toString()+" m" }
}
