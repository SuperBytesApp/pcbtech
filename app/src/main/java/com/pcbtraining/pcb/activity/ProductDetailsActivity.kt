package com.pcbtraining.pcb.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pcbtraining.pcb.databinding.ActivityProductDetailsBinding
import com.pcbtraining.pcb.model.Product

class ProductDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityProductDetailsBinding ///--*//----//--10
    private lateinit var productDetailsAdapter: ProductDetailsAdapter
    private lateinit var databaseReference: DatabaseReference
    private var totalCost: Double = 0.0
    private val shippingCharge: Double = 50.0 // Example shipping charge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        // Setup RecyclerView
//        setupRecyclerView()
//
//        // Load purchase data
//        loadPurchaseDetails()
    }
//
//    private fun setupRecyclerView() {
//        binding.productDetailsRecyclerView.layoutManager = LinearLayoutManager(this)
//        productDetailsAdapter = ProductDetailsAdapter(mutableListOf(), this)
//        binding.productDetailsRecyclerView.adapter = productDetailsAdapter
//    }
//
//    private fun loadPurchaseDetails() {
//
//        val userId = FirebaseAuth.getInstance().currentUser!!.uid
//        databaseReference = FirebaseDatabase.getInstance().getReference("mycart").child(userId)
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val productList = mutableListOf<Product>()
//                for (productSnapshot in snapshot.children) {
//                    val product = productSnapshot.getValue(Product::class.java)
//                    product?.let {
//                        productList.add(it)
//                        totalCost += it.pprice.toDouble() * it.quantity
//                    }
//
//            productDetailsAdapter.updateProductList(productList)
//            updateUI(totalCost)
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@ProductDetailsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
//                Log.e("FirebaseError", error.message)
//            }
//        })
//
//    }
//
//    private fun updateUI(totalCost: Double) {
//        val finalTotal = totalCost + shippingCharge
//        binding.totalCostTextView.text = "Total Cost: Rs $totalCost"
//        binding.shippingChargeTextView.text = "Shipping Charge: Rs $shippingCharge"
//        binding.finalTotalCostTextView.text = "Final Total: Rs $finalTotal"
//    }
}
