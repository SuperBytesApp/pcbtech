package com.livedata.pcbtechadmin.Adapter
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.livedata.pcbtechadmin.R
import com.livedata.pcbtechadmin.Model.User

class UserAdapter(private val userList: List<User>, var activity: Int ) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        private val passwordTextView: TextView = itemView.findViewById(R.id.passwordTextView)
        private val validityTextView: TextView = itemView.findViewById(R.id.validityTextView)
        private val editButton: ConstraintLayout = itemView.findViewById(R.id.itemid)

        @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
        fun bind(user: User) {
            nameTextView.text = user.name
            emailTextView.text = user.email
            passwordTextView.text = user.password
            validityTextView.text = user.number

            // Handle item click to edit user data


            itemView.setOnClickListener {
                // Implement your logic to edit user data here



                val context = itemView.context
                val alertDialog = AlertDialog.Builder(context)
                val dialogView = LayoutInflater.from(context).inflate(R.layout.edit_user_dialog, null)
                val newNameEditText = dialogView.findViewById<EditText>(R.id.newNameEditText)
                val newEmailEditText = dialogView.findViewById<EditText>(R.id.newEmailEditText)
                val newPasswordEditText = dialogView.findViewById<EditText>(R.id.newPasswordEditText)
                val access = dialogView.findViewById<Switch>(R.id.Access)
                val accesst = dialogView.findViewById<Switch>(R.id.Access2)
                val newValidityEditText = dialogView.findViewById<EditText>(R.id.phone)

                var access1 = ""
                var access2 = ""

                // Pre-fill the edit text fields with the current user data
                newNameEditText.setText(user.name)
                newEmailEditText.setText(user.email)
                newPasswordEditText.setText(user.password)
                newValidityEditText.setText(user.number)

                if(user.access == "full")
                {
                    access.isChecked = true
                    access1 =  "full"
                } else
                {
                    access.isChecked = false
                    access1 =  "min"
                }


                if(user.access2 == "full")
                {
                    accesst.isChecked = true
                    access2 =  "full"
                } else
                {
                    accesst.isChecked = false
                    access2 =  "min"
                }

                access.setOnCheckedChangeListener { _, isChecked ->
                    // Update the model with the new state
                    access1 = if(isChecked){ "full" }else{ "min" }

                }

                accesst.setOnCheckedChangeListener { _, isChecked ->
                    // Update the model with the new state
                    access2 = if(isChecked){ "full" }else{ "min" }

                }


                alertDialog.setView(dialogView)
                    .setPositiveButton("Save") { _, _ ->
                        val newName = newNameEditText.text.toString()
                        val newEmail = newEmailEditText.text.toString()
                        val newPassword = newPasswordEditText.text.toString()
                        val newValidity = newValidityEditText.text.toString()
                        var access = access1
                        var acc2 = access2

                        // Update the user data in Firestore
                        updateUserInFirestore(user, newName, newEmail, newPassword, newValidity,access,acc2)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }.setNeutralButton("Delete User") { dialog, _ ->
                        deleteFirestoreDocument(user)
                        dialog.cancel()
                    } .setNeutralButton("Refresh ID") { dialog, _ ->

                        resetpassword(user,activity)

                        dialog.cancel()
                    }
                    .show()

            }


        }


    }

    private fun resetpassword(user: User,id:Int) {

            val database = FirebaseDatabase.getInstance()

            val usersRef: DatabaseReference = database.getReference("users")

            usersRef.child(user.uid).child("isOnline").setValue("")

    }


    private fun updateUserInFirestore(user: User, newName: String, newEmail: String, newPassword: String, newValidity: String,access : String,access2 : String) {
        firestore = FirebaseFirestore.getInstance()

        // Store user data in Firestore
        val userMap = hashMapOf(
            "name" to newName,
            "email" to newEmail,
            "number" to newValidity,
            "password" to newPassword,
            "uid" to user.uid,
            "access" to access,
            "access2" to access2
        )

        firestore.collection("users")
            .document(user.uid)
            .set(userMap)
            .addOnSuccessListener {

                Log.d("success","success")
                // Data stored successfully
                // You can add more logic here
            }
            .addOnFailureListener { e ->

                // Handle errors while storing data
            }
    }


    private fun deleteFirestoreDocument(user: User) {
            val firestore = FirebaseFirestore.getInstance()
            val usersCollection = firestore.collection("users")

            // Delete the document with the user's UID
            usersCollection.document(user.uid)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Firestore document deleted successfully
                        // You might want to notify the user
                        notifyDataSetChanged()
                    } else {
                        // Handle failure
                        // You might want to show an error message to the user
                    }
                }
        }

}
