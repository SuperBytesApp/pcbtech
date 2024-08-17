package com.livedata.pcbtechadmin.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.livedata.pcbtechadmin.Model.Product
import com.livedata.pcbtechadmin.R

class ProductAdapter(private var originalProductList: List<Product>, private val context: Context) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var filteredProductList: List<Product> = originalProductList
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var firestore: FirebaseFirestore


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("MissingInflatedId")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = filteredProductList[position]

        holder.nameTextView.text = product.pname
        holder.priceTextView.text = "₹" + product.pprice.toDouble().toString()

        Glide.with(holder.itemView)
            .load(product.pimg)
            .error(com.payu.ui.R.drawable.mtrl_ic_error) // Optional error image
            .into(holder.imageView)

        holder.delete.setOnClickListener {

            deleteProduct(product.pkey)
        }





        holder.itemView.setOnClickListener {

            val alertDialog = AlertDialog.Builder(context)
            val dialogView = LayoutInflater.from(context).inflate(R.layout.edit_product_dialog, null)
            val newNameEditText = dialogView.findViewById<EditText>(R.id.newNameEditText)
            val newEmailEditText = dialogView.findViewById<EditText>(R.id.newEmailEditText)
            val newPasswordEditText = dialogView.findViewById<EditText>(R.id.newPasswordEditText)
            val newValidityEditText = dialogView.findViewById<EditText>(R.id.newValidityEditText)
            val pimg = dialogView.findViewById<EditText>(R.id.pimg)
            val pimg2 = dialogView.findViewById<EditText>(R.id.pimg2)
            val pimg3 = dialogView.findViewById<EditText>(R.id.pimg3)

           // Pre-fill the edit text fields with the current user data

            newNameEditText.setText(product.pname)
            newEmailEditText.setText(product.pprice)
            newPasswordEditText.setText(product.pstock)
            newValidityEditText.setText(product.pdisc)
            pimg.setText(product.pimg)
            pimg2.setText(product.pimg2)
            pimg3.setText(product.pimg3)


            // edit done pending adding upload photo code diagram and software

            alertDialog.setView(dialogView)
                .setPositiveButton("Save") { _, _ ->
                    val newName = newNameEditText.text.toString()
                    val newprice = newEmailEditText.text.toString()
                    val newstock = newPasswordEditText.text.toString()
                    val newdesc = newValidityEditText.text.toString()
                    val newpimg  = pimg.text.toString()
                    val newpimg2 = pimg2.text.toString()
                    val newpimg3 = pimg3.text.toString()


                    // Update the user data in Firestore
                    updateUserInFirestore(product, newName, newprice, newstock, newdesc, newpimg,newpimg2,newpimg3)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }.show()

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
        val edit: ImageView = itemView.findViewById(R.id.edit)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }


    private fun showConfirmationDialog(key : String) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Confirmation")
        alertDialog.setMessage("Are you sure you want to delete this product?")

        alertDialog.setPositiveButton("Yes") { _, _ ->
            // User clicked "Yes", call the deleteProduct function to delete the item
            deleteProduct(key)

        }

        alertDialog.setNegativeButton("No") { _, _ ->
            // User clicked "No", do nothing or handle as needed
            // You might want to close the activity or show a message, for example
            Toast.makeText(context, "Product deletion canceled", Toast.LENGTH_SHORT).show()
        }

        alertDialog.show()
    }

    private fun deleteProduct(pushId: String) {
        // Create a reference to the specific item using its push ID
        val productReference: DatabaseReference = databaseReference.child("product").child(pushId)

        // Use removeValue to delete the item from the database
        productReference.removeValue()
            .addOnSuccessListener {
                // Delete successful
                Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Delete failed
                Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateUserInFirestore(user: Product, newName: String, newprice: String, newstock: String, newdesc: String, pimg:String, pimg2:String, pimg3:String) {


        // Assuming you have a DatabaseReference
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

// Assuming 'user.id' is the key for the data you want to update
        val userId = user.pkey


        // Construct a Map with the updated data
        val updatedData = mapOf(
            "pname" to newName,
            "pprice" to newprice,
            "pstock" to newstock,
            "pdisc" to newdesc,  // Assuming you have a 'pdesc' field in your data class
            "pimg" to pimg,
            "pimg2" to pimg2,
            "pimg3" to pimg3
        )


// Update the data in the Realtime Database
        databaseReference.child("product").child(userId).updateChildren(updatedData)
            .addOnSuccessListener {
                // Successfully updated user data
                Toast.makeText(context, "Product data updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Handle any errors that occur during the update
                Toast.makeText(context, "Error updating Product data: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }



}
