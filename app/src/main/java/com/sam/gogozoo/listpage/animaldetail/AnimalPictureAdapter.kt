package com.sam.gogozoo.listpage.animaldetail

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sam.gogozoo.R
import com.sam.gogozoo.databinding.ItemAnimalImageBinding

class AnimalPictureAdapter : RecyclerView.Adapter<AnimalPictureAdapter.ImageViewHolder>() {

    private lateinit var context: Context
    // the data of adapter
    private var images: List<String>? = null

    class ImageViewHolder(private var binding: ItemAnimalImageBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, imageUrl: String) {

            imageUrl.let {
                binding.image = imageUrl

                // Make sure the size of each image item can display correct
                val displayMetrics = DisplayMetrics()
                (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
                binding.imagePictures.layoutParams = ConstraintLayout.LayoutParams(displayMetrics.widthPixels,
                    context.resources.getDimensionPixelSize(R.dimen.animal_image_high))

                binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        return ImageViewHolder(ItemAnimalImageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        images?.let {
            holder.bind(context, it[getRealPosition(position)])
        }
    }

    override fun getItemCount(): Int {
        return images?.let { Int.MAX_VALUE } ?: 0
    }

    private fun getRealPosition(position: Int): Int = images?.let {
        position % it.size
    } ?: 0

    /**
     * Submit data list and refresh adapter by [notifyDataSetChanged]
     * @param images: [List] [String]
     */
    fun submitImages(images: List<String>) {
        this.images = images
        notifyDataSetChanged()
    }
}
