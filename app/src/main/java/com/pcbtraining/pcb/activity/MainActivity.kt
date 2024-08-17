package com.pcbtraining.pcb.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Display.FLAG_SECURE
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.databinding.ActivityMainBinding
import com.pcbtraining.pcb.model.User

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        navView.setupWithNavController(navController)



        binding.floating.setOnClickListener {
           val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }



    }

    override fun onResume() {
        super.onResume()

        val auth = FirebaseAuth.getInstance()
        // Check if the current user is not null before executing the Firestore query
        auth.currentUser?.let {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val user = document.toObject(User::class.java)
                        val access = user.access

                        if (access == "Delete") {
                            showCustomDialog()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("UserData", "Error getting documents: ", exception)
                }
        }
    }


    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Access Denied")
            .setMessage("You have been removed, and your access has been disabled by the admin.")
            .setPositiveButton("OK") { _, _ ->
                // Finish the current activity
                finish()
            }
            .setNegativeButton("Logout") { _, _ ->
                // Logout the user
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                // Redirect to the login screen or perform other logout actions
                // For example, you might start a new LoginActivity
                // val intent = Intent(this, LoginActivity::class.java)
                // startActivity(intent)
            }

        val dialog: AlertDialog = builder.create()

        // Make the dialog non-cancelable on outside touch
        dialog.setCanceledOnTouchOutside(false)

        // Show the dialog
        dialog.show()
    }

}