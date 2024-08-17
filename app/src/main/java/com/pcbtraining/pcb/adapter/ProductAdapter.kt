package com.pcbtraining.pcb.adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.activity.ProductInfoActivity
import com.pcbtraining.pcb.model.Product
class ProductAdapter(private var originalProductList: List<Product>, private val context: Context) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var filteredProductList: List<Product> = originalProductList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = filteredProductList[position]

        holder.nameTextView.text = product.pname
        holder.priceTextView.text = "₹" + product.pprice.toDouble().toString()

        Glide.with(holder.itemView)
            .load(product.pimg)
            .error(R.drawable.mtrl_ic_error) // Optional error image
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductInfoActivity::class.java).apply {
                // Pass all data to the next activity
                putExtra("product", product)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return filteredProductList.size
    }

    fun filterData(query: String) {
        filteredProductList = originalProductList.filter {
            it.pname.toLowerCase().contains(query.toLowerCase())
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.productName)
        val priceTextView: TextView = itemView.findViewById(R.id.productPrice)
        val imageView: ImageView = itemView.findViewById(R.id.productImage)
    }
}
