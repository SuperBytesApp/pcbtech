package com.livedata.pcbtechadmin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.livedata.pcbtechadmin.Model.Order
import com.livedata.pcbtechadmin.databinding.ActivityOrderProductBinding

class OrderProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderProductBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        // Load orders from Firebase
        loadOrdersFromFirebase()
    }

    private fun setupRecyclerView() {
        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(mutableListOf())
        binding.orderRecyclerView.adapter = orderAdapter
    }

    private fun loadOrdersFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("purchases")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let { orderList.add(it) }
                }
                orderAdapter.updateOrderList(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrderProductActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", error.message)
            }
        })
    }
}
