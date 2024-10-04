package com.livedata.pcbtechadmin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.livedata.pcbtechadmin.Model.Order
import com.livedata.pcbtechadmin.databinding.ItemOrderBinding
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(private var orderList: MutableList<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun updateOrderList(newOrderList: List<Order>) {
        orderList.clear()
        orderList.addAll(newOrderList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orderList.size

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.userIdTextView.text = "User ID: ${order.userId}"
            binding.totalCostTextView.text = "Total Cost: ₹${order.totalCost}"
            binding.purchaseTimeTextView.text = "Purchase Time: ${formatDate(order.purchaseTime)}"

            // Display address
            binding.addressTextView.text = """
                Address: ${order.address.serviceAddress}, ${order.address.landmark}, 
                ${order.address.pinCode}, ${order.address.no}, ${order.address.category}
            """.trimIndent()

            // Display products in the order
            val productDetails = order.products.joinToString(separator = "\n") { product ->
                "${product.productName} (Qty: ${product.quantity}, ₹${product.price})"
            }
            binding.productsTextView.text = "Products:\n$productDetails"
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}

