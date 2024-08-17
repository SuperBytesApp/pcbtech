@file:Suppress("DEPRECATION")

package com.pcbtraining.pcb.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth
    lateinit var dialog: ProgressDialog
    lateinit var Idialog: AlertDialog
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        databaseReference = FirebaseDatabase.getInstance().reference



        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        auth = FirebaseAuth.getInstance()


        dialog = ProgressDialog(this)
        dialog.setMessage("Checking...")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert!")
        builder.setMessage("Check Your Internet Connection !")
        builder.setPositiveButton("OK") {
                dialog, which -> dialog.dismiss()
            finish()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss()
            finish() }
        Idialog = builder.create()


        isNetworkConnected()

        dialog.dismiss()


        binding.loginButton.setOnClickListener {

            dialog.show()

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {
                            // Login success
                            // You can navigate to another activity or perform other actions here

                           getUserOnlineStatus()
                        } else {

                            Toast.makeText(this, "something wrong $task", Toast.LENGTH_SHORT).show()
                            // Login failed
                            // Handle the error, e.g., show a message to the user
                        }
                    }
            } else {
                // Handle empty fields
            }



        }





        }




//    override fun onStart() {
//        super.onStart()
//        if (auth.currentUser != null){
//            startActivity(Intent(this , MainActivity::class.java))
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkConnected(): Boolean {
        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.activeNetwork == null){

            Idialog.show()


        }
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


    private fun getUserOnlineStatus() {
        val user = auth.currentUser
        user?.let {
            val uid = user.uid
            val userRef = databaseReference.child("users").child(uid)

            // Check if the user exists
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val isOnline = dataSnapshot.child("isOnline").getValue(String::class.java)

                        if (isOnline != "") {
                            // Use the value of "isOnline" here
                            // You can print it or perform any other actions
                            val deviceId = getDeviceId(this@LoginActivity)

                            if (isOnline.toString() == deviceId.toString()){

                                intent = Intent(this@LoginActivity,MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@LoginActivity, "Logged In", Toast.LENGTH_SHORT).show()

                            }else{

                                Toast.makeText(this@LoginActivity, "This Account Already Logged In Other Device !", Toast.LENGTH_SHORT).show()


                            }


                        } else {


                            val deviceId = getDeviceId(this@LoginActivity)

                            val user = auth.currentUser
                            user?.let {
                                val uid = user.uid
                                val userRef = databaseReference.child("users").child(uid)
                                userRef.child("isOnline").setValue(deviceId.toString())

                                intent = Intent(this@LoginActivity,MainActivity::class.java)
                                startActivity(intent)
                                finish()

                                Toast.makeText(this@LoginActivity, "Logged In", Toast.LENGTH_SHORT).show()

                            }
                            // Handle the case where the "isOnline" field doesn't exist or is null
                            println("isOnline field is null or doesn't exist")
                        }
                    } else {

                        val deviceId = getDeviceId(this@LoginActivity)

                        val user = auth.currentUser
                        user?.let {
                            val uid = user.uid
                            val userRef = databaseReference.child("users").child(uid)
                            userRef.child("isOnline").setValue(deviceId.toString())
                            intent = Intent(this@LoginActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            Toast.makeText(this@LoginActivity, "Logged In", Toast.LENGTH_SHORT).show()

                        }

                        // Handle the case where the user doesn't exist
                        println("User with UID $uid does not exist")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur during the read operation
                    println("Database Error: ${databaseError.message}")
                }
            })
        }
    }



}