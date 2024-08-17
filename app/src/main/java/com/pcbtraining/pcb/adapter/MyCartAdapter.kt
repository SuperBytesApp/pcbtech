package com.pcbtraining.pcb.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.activity.ProductInfoActivity
import com.pcbtraining.pcb.model.Product

class MyCartAdapter(private val productList: List<Product>, val context: Context) :
    RecyclerView.Adapter<MyCartAdapter.ViewHolder>() {

    fun getProductList(): List<Product> {
        return productList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mycart_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        holder.nameTextView.text = product.pname
        holder.priceTextView.text = "₹" + product.pprice.toDouble().toString()

        holder.nameTextView.text = product.pname
        holder.priceTextView.text = "₹" + (product.pprice.toDouble() * product.quantity).toString()
        holder.tvQuantity.text = product.quantity.toString()

        holder.btnDecrease.setOnClickListener {
            if (product.quantity > 1) {
                product.quantity--
                holder.tvQuantity.text = product.quantity.toString()
                holder.priceTextView.text = "₹" + (product.pprice.toDouble() * product.quantity).toString()
                // Update Firebase database with new quantity
            }
        }

        holder.btnIncrease.setOnClickListener {
            product.quantity++
            holder.tvQuantity.text = product.quantity.toString()
            holder.priceTextView.text = "₹" + (product.pprice.toDouble() * product.quantity).toString()
            // Update Firebase database with new quantity
        }

        Glide.with(holder.itemView).load(product.pimg)
            .error(R.drawable.mtrl_ic_error) // Optional error image
            .into(holder.imageView)


        holder.remove.setOnClickListener {

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val cartItemReference = FirebaseDatabase.getInstance().reference.child("mycart")
                    .child(currentUser.uid).child(product.pdel)  // Assuming product.pkey is the push key

                // Remove the entire node under the push key for the user
                cartItemReference.removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Entire node has been successfully deleted
                        Toast.makeText(context, "Cart item removed", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle the error
                        Toast.makeText(
                            context,
                            task.exception?.localizedMessage ?: "Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                // User not authenticated, handle accordingly
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            }


        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductInfoActivity::class.java).apply {
                // Pass all data to the next activity
                putExtra("product", product)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.productName)
        val priceTextView: TextView = itemView.findViewById(R.id.productPrice)
        val imageView: ImageView = itemView.findViewById(R.id.productImage)
        val remove: ImageView = itemView.findViewById(R.id.delete)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        // Add other TextViews for other product details
    }
}