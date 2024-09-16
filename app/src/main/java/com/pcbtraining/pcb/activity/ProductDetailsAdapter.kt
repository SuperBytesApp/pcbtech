package com.pcbtraining.pcb.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.model.Product

class ProductDetailsAdapter(
    private var productList: MutableList<Product>,
    private val context: Context
) : RecyclerView.Adapter<ProductDetailsAdapter.ProductDetailsViewHolder>() {

    inner class ProductDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productQuantityTextView: TextView = itemView.findViewById(R.id.productQuantityTextView)
        val totalPriceTextView: TextView = itemView.findViewById(R.id.totalPriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product_details, parent, false)
        return ProductDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductDetailsViewHolder, position: Int) {
        val product = productList[position]

        // Set product details in the views
        holder.productNameTextView.text = product.pname
        holder.productPriceTextView.text = "Price: Rs ${product.pprice}"
        holder.productQuantityTextView.text = "Quantity: ${product.quantity}"
        holder.totalPriceTextView.text = "Total: Rs ${product.pprice.toDouble() * product.quantity}"
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    // Update product list when new data is loaded
    fun updateProductList(newProductList: MutableList<Product>) {
        productList = newProductList
        notifyDataSetChanged()
    }
}
