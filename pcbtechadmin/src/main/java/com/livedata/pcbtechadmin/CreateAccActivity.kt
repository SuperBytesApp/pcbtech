package com.livedata.pcbtechadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.livedata.pcbtechadmin.databinding.ActivityCreateAccBinding

class CreateAccActivity : AppCompatActivity() {

    lateinit var binding : ActivityCreateAccBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var access = ""


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        val radioGroupOptions: RadioGroup = findViewById(R.id.radioGroupOptions)

        radioGroupOptions.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton: RadioButton = findViewById(checkedId)

            // Handle radio button click
            when (checkedId) {
                R.id.radioButtonFull -> access = "full"
                R.id.radioButtonMin -> access = "min"
            }
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val validityDate = binding.validityDateEditText.text.toString()


            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User registration successful, get the user ID
                        val user = auth.currentUser
                        val userId = user?.uid

                        // Store user data in Firestore
                        val userMap = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "number" to validityDate,
                            "password" to password,
                            "uid" to userId.toString(),
                            "access" to access
                        )

                        if (userId != null) {
                            firestore.collection("users")
                                .document(userId)
                                .set(userMap)
                                .addOnSuccessListener {


                                    Toast.makeText(this, "user created successfully", Toast.LENGTH_SHORT).show()
                                    // Data stored successfully
                                    // You can add more logic here

                                    binding.nameEditText.text.clear()
                                    binding.emailEditText.text.clear()
                                    binding.passwordEditText.text.clear()
                                    binding.validityDateEditText.text.clear()

                                }
                                .addOnFailureListener { e ->

                                    Toast.makeText(this, "${e.localizedMessage}", Toast.LENGTH_SHORT).show()

                                    // Handle errors while storing data
                                }
                        }
                    } else {
                        // User registration failed
                        // Handle the error, e.g., display an error message
                    }
                }
        }
    }
}
