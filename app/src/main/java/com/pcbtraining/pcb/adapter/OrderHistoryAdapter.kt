package com.pcbtraining.pcb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.model.Order

// Your RecyclerView adapter

class OrderHistoryAdapter(private val orderList: List<Order>) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]

        // Bind order data to views
        holder.textViewProductName.text = order.productName
        holder.textViewOrderDate.text = "Payment: ${order.status}"
        holder.textViewOrderStatus.text = "Address: ${order.address}"
        holder.textViewOrderTotal.text = "Total: ₹${order.price}"


        Glide.with(holder.itemView)
            .load(order.img)
            .error(R.drawable.mtrl_ic_error) // Optional error image
            .into(holder.imageView)


        // Bind more details as needed
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
        val textViewOrderDate: TextView = itemView.findViewById(R.id.textViewOrderDate)
        val textViewOrderStatus: TextView = itemView.findViewById(R.id.textViewOrderStatus)
        val textViewOrderTotal: TextView = itemView.findViewById(R.id.textViewOrderTotal)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewProduct) // Add this line

        // Add more views as needed
    }
}
