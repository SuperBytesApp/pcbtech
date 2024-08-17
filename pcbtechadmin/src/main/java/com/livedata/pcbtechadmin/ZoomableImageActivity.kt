package com.livedata.pcbtechadmin


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class ZoomableImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoomable_image)

        val imageUrl = intent.getStringExtra("image_url")

        if (imageUrl != null) {
            val photoView: PhotoView = findViewById(R.id.photoView)
            Glide.with(this)
                .load(imageUrl)
                .into(photoView)
        }
    }
}
