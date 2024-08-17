package com.pcbtraining.pcb.ui.frag

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.activity.LoginActivity
import com.pcbtraining.pcb.activity.MainActivity
import com.pcbtraining.pcb.activity.ProductActivity
import com.pcbtraining.pcb.activity.SplashActivity
import com.pcbtraining.pcb.activity.WebViewActivity
import com.pcbtraining.pcb.databinding.ActivityOtpBinding
import com.pcbtraining.pcb.databinding.FragmentHomeBinding
import com.pcbtraining.pcb.databinding.FragmentUserBinding


class UserFragment : Fragment() {

    lateinit var binding: FragmentUserBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root


        databaseReference = FirebaseDatabase.getInstance().reference


        auth = FirebaseAuth.getInstance()



        binding.btnSearch.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

         binding.btnlogout.setOnClickListener {

             val user = auth.currentUser
             user?.let { currentUser ->
                 val uid = currentUser.uid
                 val userRef = databaseReference.child("users").child(uid)

                 userRef.child("isOnline").setValue("")

                 FirebaseAuth.getInstance().signOut()

                 val intent = Intent(requireContext(), SplashActivity::class.java)
                 startActivity(intent)
             Toast.makeText(requireContext(), "LogOut Success", Toast.LENGTH_SHORT).show()
             }
         }



        binding.term.setOnClickListener {
            val url = "https://www.termsandconditionsgenerator.com/live.php?token=BgvQnJq72nlJZ9XOYENk9PKooyDROai7"
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
        }

        binding.privacy.setOnClickListener {
            val url = "https://www.privacypolicygenerator.info/live.php?token=ALqyd1ZHcCny0qXMBjaN97vFpOmkvFmq"
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
        }

        binding.refund.setOnClickListener {
            val url = "https://pcbtech.in/refundpolicy.html"
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
        }



        return root

    }

    fun username(){



        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val targetUid = FirebaseAuth.getInstance().currentUser!!.uid

        val userDocumentRef = firebaseFirestore.collection("users").document(targetUid)

        userDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Document exists, retrieve data
                    val userName = documentSnapshot.getString("name")
                    val usernum = documentSnapshot.getString("number")
                    // Now you can use the userName as needed

                    // Example: Display the name in a TextView
                    userName?.let {
                        binding.textView4.text = it
                    }
                    usernum?.let {
                        binding.textView5.text = it
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



    }

    override fun onResume() {
        super.onResume()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {  username() }


    }


}