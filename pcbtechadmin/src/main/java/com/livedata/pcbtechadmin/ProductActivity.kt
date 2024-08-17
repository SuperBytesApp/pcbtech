package com.livedata.pcbtechadmin

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.livedata.pcbtechadmin.Adapter.ProductAdapter
import com.livedata.pcbtechadmin.Model.Product
import com.livedata.pcbtechadmin.databinding.ActivityProductBinding

class ProductActivity : AppCompatActivity() {

    lateinit var binding : ActivityProductBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)


        binding.Allproduct.layoutManager = GridLayoutManager(this, 2,GridLayoutManager.VERTICAL,false)
        productAdapter = ProductAdapter(emptyList(),this)
        binding.Allproduct.adapter = productAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("product")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                productAdapter = ProductAdapter(productList,this@ProductActivity)
                binding.Allproduct.adapter = productAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProductActivity, error.message, Toast.LENGTH_SHORT).show()
                // Handle the error
            }
        })

//
//        binding.searchEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
//                // Handle before text change
//            }
//
//            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
//                // Handle on text change
//                productAdapter.filterData(charSequence?.toString().orEmpty())
//            }
//
//            override fun afterTextChanged(editable: Editable?) {
//                // Handle after text change
//            }
//        })





    }


}