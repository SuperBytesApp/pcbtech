package com.pcbtraining.pcb.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.activity.AllDiagramActivity
import com.pcbtraining.pcb.activity.AllDiagrampdfActivity
import com.pcbtraining.pcb.activity.CourseBuyActivity
import com.pcbtraining.pcb.activity.LoginActivity
import com.pcbtraining.pcb.activity.MyCartActivity
import com.pcbtraining.pcb.activity.MyCourseActivity
import com.pcbtraining.pcb.activity.MyOrderActivity
import com.pcbtraining.pcb.activity.PaymentHistoryActivity
import com.pcbtraining.pcb.activity.ProductActivity
import com.pcbtraining.pcb.activity.ReferActivity
import com.pcbtraining.pcb.activity.SensorActivity
import com.pcbtraining.pcb.activity.SoftwareActivity
import com.pcbtraining.pcb.activity.SupportChatActivity
import com.pcbtraining.pcb.adapter.AllDiagramAdapter
import com.pcbtraining.pcb.adapter.ProductAdapter
import com.pcbtraining.pcb.databinding.FragmentHomeBinding
import com.pcbtraining.pcb.model.DiagramData
import com.pcbtraining.pcb.model.Product
import com.pcbtraining.pcb.model.User
import com.pcbtraining.pcb.ui.frag.TestPointActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var access : String
    lateinit var access2 : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        access =""
        access2 =""


        binding.productRecyclerView.layoutManager = GridLayoutManager(context, 1,GridLayoutManager.HORIZONTAL,false)
        productAdapter = ProductAdapter(emptyList(), requireContext())
        binding.productRecyclerView.adapter = productAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("product")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                productAdapter = ProductAdapter(productList,requireContext())
                binding.productRecyclerView.adapter = productAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                // Handle the error
            }
        })




        binding.payment.setOnClickListener {
            val intent = Intent(requireContext(), PaymentHistoryActivity::class.java)
            startActivity(intent) }
        binding.mycourse.setOnClickListener {
            val intent = Intent(requireContext(), CourseBuyActivity::class.java)
            startActivity(intent) }
        binding.refer.setOnClickListener {
            val intent = Intent(requireContext(), ReferActivity::class.java)
            startActivity(intent) }
        binding.myorder.setOnClickListener {
            val intent = Intent(requireContext(), MyOrderActivity::class.java)
            startActivity(intent) }
        binding.allproduct.setOnClickListener {
            val intent = Intent(requireContext(), ProductActivity::class.java)
            startActivity(intent) }
        binding.support.setOnClickListener {
            if (access == "full") { val intent = Intent(context, SupportChatActivity::class.java)
                startActivity(intent)  }else if (access.isEmpty()){ Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show()
                }else{ Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show() }
        }
        binding.seemore.setOnClickListener {
            val intent = Intent(requireContext(), ProductActivity::class.java)
            startActivity(intent)
        }
        binding.sellallproduct.setOnClickListener {
            val intent = Intent(requireContext(), ProductActivity::class.java)
            startActivity(intent)
        }
        binding.courseCard.setOnClickListener {
            val intent = Intent(requireContext(), CourseBuyActivity::class.java)
            startActivity(intent)
        }
         binding.titlee.setOnClickListener {
            val intent = Intent(requireContext(), CourseBuyActivity::class.java)
            startActivity(intent)
        }
        binding.account.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        binding.addcart.setOnClickListener {
            val intent = Intent(requireContext(), MyCartActivity::class.java)
            startActivity(intent)
        }
        binding.upcomingcourse.setOnClickListener {
             Toast.makeText(requireContext(), "Upcoming Course..", Toast.LENGTH_SHORT).show()
        }
        binding.viewAllsoftware.setOnClickListener {
            if (access == "full"){ val intent = Intent(requireContext(), SoftwareActivity::class.java)
                startActivity(intent)
            }else if (access.isEmpty()){ Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show()
            }else{ Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show() }
        }
         binding.sensordata.setOnClickListener {
            if (access == "full"){ val intent = Intent(requireContext(), SensorActivity::class.java)
                startActivity(intent)
            }else if (access.isEmpty()){ Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show()
            }else{ Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show() }
        }
        binding.imgtestpoint.setOnClickListener {
            if (access == "full"){ val intent = Intent(requireContext(), TestPointActivity::class.java)
                startActivity(intent)
            }else if (access.isEmpty()){ Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show()
            }else{ Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show() }
        }




//        homediagram()



        return root

    }


    override fun onResume() {
        super.onResume()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) { getUserData(currentUser.uid)
            binding.account.visibility = View.GONE }
        else{ binding.account.visibility = View.VISIBLE }


        binding.viewAllCourse.setOnClickListener {

            if (currentUser != null) {
                val intent = Intent(requireContext(), AllDiagrampdfActivity::class.java)
                startActivity(intent)

            }else{

                Toast.makeText(requireContext(), "Your Are Not Subscribe Member", Toast.LENGTH_SHORT).show()
            }




        }


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
                        val access11 = user!!.access2
                        val number = user.number
                        val uid = user.uid

                        access = access1.toString()
                        access2 = access11.toString()
                        binding.username.text = name.toString()

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


//    fun homediagram(){
//
//
//        val database = FirebaseDatabase.getInstance()
//        val diagramRef = database.getReference("diagram")
//
//// ...
//
//// Set up RecyclerView
//        val adapter = AllDiagramAdapter(1) // You need to create this adapter
//        binding.homediagram.adapter = adapter
//        binding.homediagram.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL,false)
//
//// Attach a listener to read the data
//        diagramRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val diagramList = mutableListOf<DiagramData>()
//
//                for (dataSnapshot in snapshot.children) {
//                    val diagram = dataSnapshot.getValue(DiagramData::class.java)
//                    diagram?.let { diagramList.add(it) }
//                }
//
//                adapter.submitList(diagramList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle error
//            }
//        })
//
//
//    }






}