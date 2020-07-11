package com.sam.gogozoo

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.net.Uri
import android.os.Build
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.search.SearchAdapter
import java.time.Instant
import java.time.ZoneId
import java.util.*

/**
 * Created by Wayne Chen in Jul. 2019.
 */


@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val Uri = Uri.parse(imgUrl)
        val imgUri = Uri.buildUpon().scheme("http").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

@BindingAdapter("meter")
fun bindmeter(textView: TextView, price: Int?) {
    price?.let { textView.text = it.toString()+" m" }
}

@BindingAdapter("marquee")
fun bindmarquee(textView: TextView, string: String?) {
    string?.let { textView.text = "    "+it+"                                               " }
}
