package com.pcbtraining.pcb.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.rpc.Help.Link
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.activity.VideoPlayerActivity
import com.pcbtraining.pcb.model.CourseItems

class LinksAdapter(var context: Context, private var links: List<CourseItems>) : RecyclerView.Adapter<LinksAdapter.LinkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_link, parent, false)
        return LinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val link = links[position]
        holder.linkTextView.text = link.title

        Glide.with(context)
            .load(link.img)
            .error(R.drawable.ic_baseline_ondemand_video_24) // Set your error placeholder drawable
            .into(holder.img)

        holder.itemView.setOnClickListener {
//                onItemClick(link)

            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_URL", link.link)
            context.startActivity(intent)


        }
    }

    override fun getItemCount(): Int {
        return links.size
    }

    fun updateData(newLinks: List<CourseItems>) {
        links = newLinks
        notifyDataSetChanged()
    }

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linkTextView: TextView = itemView.findViewById(R.id.linkTextView)
        val img: ImageView = itemView.findViewById(R.id.imageView555)
    }
}

