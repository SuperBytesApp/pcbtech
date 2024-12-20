package com.pcbtraining.pcb.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
import com.pcbtraining.pcb.databinding.ActivityMyOrderBinding

class MyOrderActivity : AppCompatActivity() {

    lateinit var binding : ActivityMyOrderBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderList: MutableList<Order>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Assuming you have a RecyclerView in your activity layout with the id "recyclerViewOrderHistory"

        tabletscreencenter()

        // Load user orders
        loadUserOrders()

    }

    private fun loadUserOrders() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("purchases").orderByChild("userId").equalTo(userId)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<Order>()
                for (dataSnapshot in snapshot.children) {
//                    val order = orderSnapshot.getValue(Order::class.java)
                    val order = dataSnapshot.getValue(Order::class.java)

                    order?.let { ordersList.add(it) }
                }

                if (ordersList.isNotEmpty()) {
                    setupOrdersRecyclerView(ordersList)
                } else {
                    Toast.makeText(this@MyOrderActivity, "No Orders Found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyOrderActivity, "Failed to load orders: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupOrdersRecyclerView(ordersList: List<Order>) {
        val recyclerView = findViewById<RecyclerView>(R.id.myordershis)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = OrdersAdapter(ordersList)
    }


    fun tabletscreencenter(){
        val rootLayout = findViewById<FrameLayout>(R.id.container)

// Check if the device is in landscape mode
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

// Check if the device is a tablet
        val isTablet = (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE

        if (isTablet && isLandscape) {
            // Set phone-like size in landscape mode on tablet
            val params = rootLayout.layoutParams
            params.width = resources.getDimensionPixelSize(R.dimen.phone_width)
            params.height = resources.getDimensionPixelSize(R.dimen.phone_height)
            rootLayout.layoutParams = params

            // Request layout update to apply changes
            rootLayout.requestLayout()
        }


    }

}