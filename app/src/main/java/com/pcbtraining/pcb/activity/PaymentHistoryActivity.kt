package com.pcbtraining.pcb.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
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
import com.pcbtraining.pcb.adapter.PaymentHistoryAdapter
import com.pcbtraining.pcb.model.PaymentHistory

class PaymentHistoryActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter
    private val paymentHistoryList: MutableList<PaymentHistory> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)



        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) { order() }else{ Toast.makeText(this, "login required", Toast.LENGTH_SHORT).show() }



    }

    fun order(){
        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().reference.child("paymenthistory")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        paymentHistoryAdapter = PaymentHistoryAdapter(paymentHistoryList)
        recyclerView.adapter = paymentHistoryAdapter

        // Retrieve data from Firebase
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val paymentHistory = childSnapshot.getValue(PaymentHistory::class.java)
                    if (paymentHistory != null) {
                        paymentHistoryList.add(paymentHistory)
                    }
                }
                paymentHistoryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })}
}