package com.livedata.pcbtechadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: YourAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)



        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("support") // Replace with your node

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.precycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        adapter = YourAdapter(this)
        recyclerView.adapter = adapter

        // Listen for changes in the database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the list before adding UIDs to avoid duplicates
                adapter.uidList.clear()

                for (childSnapshot in snapshot.children) {
                    val uid = childSnapshot.key
                    uid?.let {
                        adapter.addUid(it)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}