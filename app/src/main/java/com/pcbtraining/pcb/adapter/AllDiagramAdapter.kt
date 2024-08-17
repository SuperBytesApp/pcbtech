package com.pcbtraining.pcb.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.activity.AllDiagramActivity
import com.pcbtraining.pcb.activity.AllDiagrampdfActivity
import com.pcbtraining.pcb.model.DiagramData

class AllDiagramAdapter(val screen: Int) :
    ListAdapter<DiagramData, AllDiagramAdapter.DiagramViewHolder>(DiagramDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagramViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_alldiagram, parent, false)
        return DiagramViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiagramViewHolder, position: Int) {
        val diagram = getItem(position)
        val layoutParams = holder.itemView.layoutParams as GridLayoutManager.LayoutParams

        if (screen != 2 && position >= 4) {
            // If screen is not 2 and position is beyond the first 4 items, hide the view
            layoutParams.height = 0
            layoutParams.width = 0
            holder.itemView.visibility = View.GONE
        } else {
            // Bind the item if the condition is not met
            holder.bind(diagram)
            holder.itemView.visibility = View.VISIBLE
        }
    }


    class DiagramViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.dianame)
        private val tdiaTextView: TextView = itemView.findViewById(R.id.totaldia)
        private val dimg: ImageView = itemView.findViewById(R.id.dimg)

        var currentUser = FirebaseAuth.getInstance().currentUser


        fun bind(diagram: DiagramData) {
            nameTextView.text = diagram.name
            tdiaTextView.text = diagram.tdia

            Glide.with(itemView.context)
                .load(diagram.link)
                .error(R.drawable.logo) // Error image to display if the load fails (optional)
                .into(dimg)

            itemView.setOnClickListener {

                if (currentUser != null) {
                    val intent = Intent(itemView.context, AllDiagrampdfActivity::class.java).apply {
                        // Pass all data to the next activity
                        putExtra("product", diagram)
                    }
                    itemView.context.startActivity(intent)
                } else {
                    Toast.makeText(
                        itemView.context,
                        "Your Are Not Subscribe Member",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    class DiagramDiffCallback : DiffUtil.ItemCallback<DiagramData>() {
        override fun areItemsTheSame(oldItem: DiagramData, newItem: DiagramData): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: DiagramData, newItem: DiagramData): Boolean {
            return oldItem == newItem
        }
    }
}
