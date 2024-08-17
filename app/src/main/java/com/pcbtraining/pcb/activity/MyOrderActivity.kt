package com.pcbtraining.pcb.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.OrderHistoryAdapter
import com.pcbtraining.pcb.databinding.ActivityMyOrderBinding
import com.pcbtraining.pcb.model.Order

class MyOrderActivity : AppCompatActivity() {

    lateinit var binding : ActivityMyOrderBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderList: MutableList<Order>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Assuming you have a RecyclerView in your activity layout with the id "recyclerViewOrderHistory"



        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) { order() }else{ Toast.makeText(this, "login required", Toast.LENGTH_SHORT).show() }



    }

    fun order(){

        val recyclerView: RecyclerView = findViewById(R.id.myordershis)
        recyclerView.layoutManager = LinearLayoutManager(this)

        orderList = mutableListOf()
        // Get the current user's UID
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid.toString()

        // Set up the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("orders").child(currentUserUid)

        // Set up a ValueEventListener to fetch data
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()

                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let { orderList.add(it) }
                }

                // Update the RecyclerView adapter with the new data
                val adapter = OrderHistoryAdapter(orderList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors, if any
            }
        })

    }

}