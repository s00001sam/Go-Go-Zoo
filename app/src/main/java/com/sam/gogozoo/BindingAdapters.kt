package com.sam.gogozoo

import android.net.Uri
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sam.gogozoo.listpage.animaldetail.AnimalPictureAdapter
import de.hdodenhof.circleimageview.CircleImageView

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


@BindingAdapter("meter")
fun bindmeter(textView: TextView, price: Int?) {
    price?.let { textView.text = it.toString()+" m" }
}

@BindingAdapter("marquee")
fun bindmarquee(textView: TextView, string: String?) {
    string?.let { textView.text = "    "+it+"                                               " }
}
