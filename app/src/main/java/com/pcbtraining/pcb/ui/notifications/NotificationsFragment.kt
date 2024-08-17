package com.pcbtraining.pcb.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pcbtraining.pcb.activity.SupportChatActivity
import com.pcbtraining.pcb.activity.ChatActivity
import com.pcbtraining.pcb.databinding.FragmentNotificationsBinding
import com.pcbtraining.pcb.model.User

class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    lateinit var access : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        access = ""
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {

            getUserData(currentUser.uid)


        }



        binding.cardView.setOnClickListener {

            if (access == "full") {
                var intent = Intent(context, SupportChatActivity::class.java)
                startActivity(intent)  }else if (access.isEmpty()){

                Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show()
            }



        }

         binding.communitychat.setOnClickListener {

             if (access == "full") {
                 var intent = Intent(context,ChatActivity::class.java)
                 startActivity(intent)  }else if (access.isEmpty()){

                 Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show()

             }else{
                 Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show()
             }


         }


        return root
    }




    fun getUserData(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users") // Change to your Firestore collection name

        usersCollection.document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)

                    // Now 'user' contains the data from Firestore
                    if (user != null) {
                        val name = user.name
                        val email = user.email
                        val access1 = user.access
                        val number = user.number
                        val uid = user.uid


                        access = access1

                        // Use the retrieved data as needed
                        println("Name: $name, Email: $email, Access: $access, Number: $number, UID: $uid")
                    }
                } else {
                    println("No such document")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
            }
    }




}