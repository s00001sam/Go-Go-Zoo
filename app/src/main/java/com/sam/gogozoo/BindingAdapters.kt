package com.sam.gogozoo

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.net.Uri
import android.widget.*
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sam.gogozoo.listpage.animaldetail.AnimalCircleAdapter
import com.sam.gogozoo.listpage.animaldetail.AnimalPictureAdapter
import de.hdodenhof.circleimageview.CircleImageView
import com.sam.gogozoo.util.Util.to2fString

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
                    .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
}

@BindingAdapter("imageUrlCircle")
fun bindImageCircle(imgView: CircleImageView, imgUrl: String?) {
    imgUrl?.let {
        val Uri = Uri.parse(imgUrl)
        val imgUri = Uri.buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
}

@BindingAdapter("images")
fun bindRecyclerViewWithImages(recyclerView: RecyclerView, images: List<String>?) {
    images?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is AnimalPictureAdapter -> {
                    submitImages(it)
                }
            }
        }
    }
}

@BindingAdapter("count")
fun bindRecyclerViewByCount(recyclerView: RecyclerView, count: Int?) {
    count?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is AnimalCircleAdapter -> {
                    submitCount(it)
                }
            }
        }
    }
}

@BindingAdapter("circleStatus")
fun bindDetailCircleStatus(imageView: ImageView, isSelected: Boolean = false) {
    imageView.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {

            paint.color = getColor(ZooApplication.appContext, R.color.white)
            paint.isAntiAlias = true

            when (isSelected) {
                true -> {
                    paint.style = Paint.Style.FILL
                }
                false -> {
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = ZooApplication.INSTANCE.resources
                        .getDimensionPixelSize(R.dimen.edge_detail_circle).toFloat()
                }
            }

            canvas.drawCircle(
                this.width / 2, this.height / 2,
                ZooApplication.INSTANCE.resources
                    .getDimensionPixelSize(R.dimen.radius_detail_circle).toFloat(), paint
            )
        }
    })
}

@BindingAdapter("addDecoration")
fun bindDecoration(recyclerView: RecyclerView, decoration: RecyclerView.ItemDecoration?) {
    decoration?.let { recyclerView.addItemDecoration(it) }
}


@BindingAdapter("meter")
fun bindmeter(textView: TextView, price: Int?) {
    price?.let { textView.text = it.toString()+" m" }
}

@BindingAdapter("distance")
fun bindDistance(textView: TextView, distance: Int?) {
    distance?.let {
        if (it < 1000)
            textView.text = "路線總長 $it 公尺"
        else{
            textView.text = "路線總長 ${(it/1000.0).to2fString()} 公里"
            }
    }
}

@BindingAdapter("wasteTime")
fun bindWasteTime(textView: TextView, time: Int?) {
    time?.let {
        if (it < 60)
            textView.text = "步行所需 $it 分鐘"
        else
            textView.text = "步行所需 ${(it/60.0).to2fString()} 小時"

    }
}
