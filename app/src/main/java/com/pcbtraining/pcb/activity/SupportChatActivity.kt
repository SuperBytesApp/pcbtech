package com.pcbtraining.pcb.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.ChatAdapter
import com.pcbtraining.pcb.databinding.ActivitySupportChatBinding
import com.pcbtraining.pcb.model.ChatMessage
import java.util.Date
import java.util.HashMap

class SupportChatActivity : AppCompatActivity() {

    lateinit var binding: ActivitySupportChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    lateinit var name : String
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private val PICK_IMAGE_REQUEST = 1
    lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)


        tabletscreencenter()


        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        name = ""

        val targetUid = FirebaseAuth.getInstance().currentUser!!.uid

        val userDocumentRef = firebaseFirestore.collection("users").document(targetUid)

        userDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Document exists, retrieve data
                    val userName = documentSnapshot.getString("name")
                    // Now you can use the userName as needed

                    // Example: Display the name in a TextView
                    userName?.let {
                        name = it
                    }
                } else {
                    // Document does not exist
                    // Handle accordingly
                }
            }
            .addOnFailureListener { exception ->
                // Handle failures
                // For example, log the error or display a message to the user
            }



        var id = FirebaseAuth.getInstance().currentUser!!.uid
        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference.child("support").child(id)

        // Setup RecyclerView
        chatAdapter = ChatAdapter(messages,targetUid,this)
        binding.recycler.adapter = chatAdapter

        // Load existing messages
        loadMessages()

        // Set click listeners
        binding.recordFab.setOnClickListener { sendMessage(name , "") }



        dialog = ProgressDialog(this)
        dialog.setMessage("Wait..")


        binding.imageView4.setOnClickListener {

            dialog.show()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
            )

        }

    }

    private fun loadMessages() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let {
                    messages.add(it)
                    chatAdapter.notifyDataSetChanged()
                    binding.recycler.scrollToPosition(messages.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Not needed for a simple chat app
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Not needed for a simple chat app
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Not needed for a simple chat app
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun sendMessage(name: String , filepath : String ) {
        val messageText = binding.messageEditText.text.toString().trim()
        if (messageText.isNotEmpty() || filepath != "") {
            val message = ChatMessage(
                senderId = FirebaseAuth.getInstance().currentUser!!.uid.toString(), // Replace with user ID
                senderName = name, // Replace with user name
                message = messageText,
                timestamp = System.currentTimeMillis(),
                imageUrl = filepath
            )

            database.push().setValue(message)
            binding.messageEditText.text.clear()
            binding.readyimg.visibility = android.view.View.GONE

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            val storage = FirebaseStorage.getInstance()
            val time = Date().time
            val reference = storage.reference.child("Profiles").child(time.toString())
            reference.putFile(uri!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { uri ->
                        var filePath = uri.toString()
                        val obj = HashMap<String, Any>()
                        obj["imgUrl"] = filePath


                        dialog.dismiss()
                        binding.readyimg.visibility = android.view.View.VISIBLE
                        Glide.with(this)
                            .load(filePath)
                            .into(binding.readyimg)

                        Toast.makeText(this, "Image Ready To Send", Toast.LENGTH_SHORT).show()

                        binding.recordFab.setOnClickListener {
                            sendMessage(name,filePath)
                            filePath = ""
                        }

                    }
                } else {
                    binding.readyimg.visibility = android.view.View.GONE
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }


        }
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

