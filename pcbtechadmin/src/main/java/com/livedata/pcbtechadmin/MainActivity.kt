package com.livedata.pcbtechadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.livedata.pcbtechadmin.AddDataActivity.AddDiagramActivity
import com.livedata.pcbtechadmin.AddDataActivity.AddProductActivity
import com.livedata.pcbtechadmin.AddDataActivity.AddSoftwareActivity
import com.livedata.pcbtechadmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var totaluser : Int = 0

    private lateinit var database: FirebaseDatabase
    private lateinit var productsReference: DatabaseReference

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        binding.createacc.setOnClickListener {
            intent = Intent(this,CreateAccActivity::class.java)
            startActivity(intent)
        }

        binding.refresh.setOnClickListener {
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.userlist2.setOnClickListener {
            intent = Intent(this,UserListActivity::class.java)
            startActivity(intent)
        }

        binding.upstox.setOnClickListener {
            intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

        binding.productlist2.setOnClickListener {
            intent = Intent(this,ProductActivity::class.java)
            startActivity(intent)
        }
        binding.dupload.setOnClickListener {
            intent = Intent(this,AddDiagramActivity::class.java)
            startActivity(intent)
        }

        binding.addsoft.setOnClickListener {
            intent = Intent(this,AddSoftwareActivity::class.java)
            startActivity(intent)
        }

        binding.chat.setOnClickListener {
            intent = Intent(this,ChatActivity::class.java)
            startActivity(intent)
        }

        binding.cchat.setOnClickListener {
            intent = Intent(this,CommunityActivity::class.java)
            startActivity(intent)
        }
        binding.diaedit.setOnClickListener {
            intent = Intent(this,DiagramEditActivity::class.java)
            startActivity(intent)
        }

        binding.videoup.setOnClickListener {
            intent = Intent(this,VideoUploadActivity::class.java)
            startActivity(intent)
        }






    }

    fun count(){

        val db = FirebaseFirestore.getInstance()

// Reference to your Firestore collection
        val collectionReference = db.collection("users")

// Use the get() method to fetch all documents in the collection
        collectionReference.get()
            .addOnSuccessListener { documents ->
                // Get the count of documents in the collection
                val count = documents.size()
                // Do something with the count, e.g., display it or use it in your app
                binding.totaluser.text = count.toString()

                totaluser += count
            }
            .addOnFailureListener { e ->
                // Handle any errors that occur
                println("Error getting documents: ${e.message}")
            }

    }


    fun access(){


        val searchString = "full"

        // Reference to the "users" collection in Firestore
        val usersCollection = firestore.collection("users")

        // Query to get documents where the "access" field matches the full string
        usersCollection.whereEqualTo("access", searchString)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Get the count of matching documents
                val matchingUserCount = querySnapshot.size()

                binding.upstoxcount.text = "$matchingUserCount"

                binding.kitecount.text = "$matchingUserCount"


                // Now you can use matchingUserCount as needed
            }
            .addOnFailureListener { exception ->
                // Handle exceptions
                Log.e("YourActivity", "Error counting users", exception)
            }

    }

    private fun countProducts() {
        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Reference to the "products" node in the Realtime Database
        productsReference = database.reference.child("product")

        productsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the count of products
                val productCount = dataSnapshot.childrenCount

                binding.angelcount.text = "$productCount"

                // Now you can use productCount as needed

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("YourActivity", "Error counting products", databaseError.toException())
            }
        })
    }



    override fun onResume() {
        super.onResume()
        count()
        access()
        countProducts()
    }

}