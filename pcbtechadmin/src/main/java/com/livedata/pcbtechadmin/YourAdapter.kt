package com.livedata.pcbtechadmin

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class YourAdapter(private val context: Context) : RecyclerView.Adapter<YourAdapter.YourViewHolder>() {

    val uidList = mutableListOf<String>()

    private val firestore = FirebaseFirestore.getInstance()

    class YourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.cname)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_layout, parent, false)
        return YourViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: YourViewHolder, position: Int) {
        val uid = uidList[position]

        val usersCollection = firestore.collection("users")
        // Query to get the document for the current user
        val userDocument = usersCollection.document(uid)
        // Fetch user data
        userDocument.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) { // Document exists, retrieve data
                    val userName = documentSnapshot.getString("name")
                    holder.nameTextView.text = userName
                    // Now you can use userName as needed, for example, display in a TextView
                } else { Log.d("YourActivity", "No such document") } }.addOnFailureListener { exception ->
                // Handle exceptions
                Log.e("YourActivity", "Error getting user data", exception) }


        holder.itemView.setOnClickListener {
            // Handle item click, show Toast with UID
            val intent = Intent(context, ChattingActivity::class.java)
            intent.putExtra("DATA_KEY", uid)
            context.startActivity(intent)
        }
        // Bind data to your views or just set the UID to a TextView
        // holder.yourTextView.text = uid
    }


    override fun getItemCount(): Int {
        return uidList.size
    }

    fun addUid(uid: String) {
        uidList.add(uid)
        notifyDataSetChanged()
    }
}

