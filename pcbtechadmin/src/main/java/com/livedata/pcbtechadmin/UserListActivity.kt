package com.livedata.pcbtechadmin


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.livedata.pcbtechadmin.Adapter.UserAdapter
import com.livedata.pcbtechadmin.Model.User

class UserListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val userList = mutableListOf<User>()
    private lateinit var userAdapter: UserAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(userList,1)
        recyclerView.adapter = userAdapter

        // Initialize Firebase Firestore
        val db = FirebaseFirestore.getInstance()

        // Retrieve user data from Firestore
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    userList.add(user)
                }
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors here
            }
    }
}
