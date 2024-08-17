package com.pcbtraining.pcb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.pcbtraining.pcb.R

class ImagePagerAdapter(private val imageList: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    var onImageClick: ((imageUrl: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageList[position]

        Glide.with(holder.itemView)
            .load(imageUrl)
            .error(R.drawable.mtrl_ic_error) // Optional error image
            .into(holder.photoView)

        // Set click listener to open the image in a zoomable view
        holder.photoView.setOnClickListener {
            // Pass the image URL to the activity for displaying in a zoomable view
            onImageClick?.invoke(imageUrl)

        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoView: PhotoView = itemView.findViewById(R.id.photoView)
    }
}