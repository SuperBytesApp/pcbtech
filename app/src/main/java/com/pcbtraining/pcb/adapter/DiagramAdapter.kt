package com.pcbtraining.pcb.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.activity.DiagramActivity
import com.pcbtraining.pcb.model.DiagramData
import com.pcbtraining.pcb.model.DiagramItem

class DiagramAdapter(
        var context: Context,
        private var originalDiagramItems: List<DiagramItem>,
        private var filteredDiagramItems: List<DiagramItem>) : RecyclerView.Adapter<DiagramAdapter.DiagramViewHolder>() {

        private var isFiltering: Boolean = false

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagramViewHolder {
                val view =
                        LayoutInflater.from(parent.context).inflate(R.layout.item_diagram, parent, false)
                return DiagramViewHolder(view)
        }

        override fun onBindViewHolder(holder: DiagramViewHolder, position: Int) {
                val diagramItem = filteredDiagramItems[position]
                holder.linkTextView.text = diagramItem.name
                holder.full.setOnClickListener {
                        val intent = Intent(context, DiagramActivity::class.java)
                        intent.putExtra("VIDEO_URL", diagramItem.link)
                        context.startActivity(intent)
                }
        }

        override fun getItemCount(): Int {
                return if (isFiltering) filteredDiagramItems.size else 0
        }

        fun updateData(newDiagramItems: List<DiagramItem>) {
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

        class DiagramViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val linkTextView: TextView = itemView.findViewById(R.id.linkTextView)
                val full: CardView = itemView.findViewById(R.id.full)
        }
}




class DiagramAdapterr(var context: Context, private var links: List<DiagramData>, private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<DiagramAdapterr.LinkViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
                val view =
                        LayoutInflater.from(parent.context).inflate(R.layout.item_diagram, parent, false)
                return LinkViewHolder(view)
        }

        override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
                val link = links[position]
                holder.linkTextView.text = "PCBTECH PDF"
                holder.itemView.setOnClickListener {
                        //                onItemClick(link)

                        val intent = Intent(context, DiagramActivity::class.java)
                        intent.putExtra("VIDEO_URL", link.link)
                        context.startActivity(intent)



                }
        }

        override fun getItemCount(): Int {
                return links.size
        }

        fun updateData(newLinks: List<DiagramData>) {
                links = newLinks
                notifyDataSetChanged()
        }

        class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val linkTextView: TextView = itemView.findViewById(R.id.linkTextView)
        }
}
