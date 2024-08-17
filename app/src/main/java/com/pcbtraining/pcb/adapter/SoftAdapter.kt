package com.pcbtraining.pcb.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.activity.VideoPlayerActivity
import com.pcbtraining.pcb.model.CourseItems
import com.pcbtraining.pcb.model.DiagramItem
import com.pcbtraining.pcb.model.SoftwareItems

class SoftAdapter(
    var context: Context,
    private var originalDiagramItems: List<SoftwareItems>,
    private var filteredDiagramItems: List<SoftwareItems>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SoftAdapter.LinkViewHolder>() {

    private var isFiltering: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_soft, parent, false)
        return LinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val link = filteredDiagramItems[position]

        holder.linkTextView.text = link.name

        holder.itemView.setOnClickListener {
//                onItemClick(link)

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link.link)
            context.startActivity(intent)


        }
    }

    override fun getItemCount(): Int {
        return if (isFiltering) filteredDiagramItems.size else 0
    }

    fun updateData(newDiagramItems: List<SoftwareItems>) {
        originalDiagramItems = newDiagramItems
        filterData("")  // Reset the filter when data is updated
    }

    fun filterData(query: String) {
        filteredDiagramItems = originalDiagramItems.filter {
            it.name.contains(query, true)
        }
        isFiltering = query.isNotEmpty()
        notifyDataSetChanged()
    }
    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linkTextView: TextView = itemView.findViewById(R.id.linkTextView)
    }
}

