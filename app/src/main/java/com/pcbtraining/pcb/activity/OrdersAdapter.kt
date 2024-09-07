package com.pcbtraining.pcb.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pcbtraining.pcb.databinding.ItemOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersAdapter(private val ordersList: List<Order>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = ordersList[position]

        // Set total cost (assumed to be stored as a Long)
        holder.binding.totalCostTextView.text = "Total: Rs ${order.totalCost}"

        // Display product details (Ensure all fields exist and types are correct)
        val productsText = order.products.joinToString("\n") { product ->
            val productName = product["productName"] as? String ?: "Unknown"
            val quantity = product["quantity"] as? Long ?: 0L // Assume quantity is Long
            val price = product["price"] as? String ?: "Unknown" // Assume price is Long
            "Product: $productName, Qty: $quantity, Price: Rs $price"
        }
        holder.binding.productsTextView.text = productsText

        // Display address details
        val serviceAddress = order.address["serviceAddress"] ?: "No address"
        val landmark = order.address["landmark"] ?: "No landmark"
        val pinCode = order.address["pinCode"] ?: "No pincode"
        val addressText = "Address: $serviceAddress, Landmark: $landmark, Pincode: $pinCode"
        holder.binding.addressTextView.text = addressText

        // Display purchase time (formatted)
        holder.binding.purchaseTimeTextView.text = "Purchased on: ${formatPurchaseTime(order.purchaseTime)}"
    }

    override fun getItemCount(): Int = ordersList.size

    // Helper function to format the purchase time from a timestamp
    private fun formatPurchaseTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
