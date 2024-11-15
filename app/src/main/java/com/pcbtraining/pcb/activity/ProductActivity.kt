package com.pcbtraining.pcb.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.ProductAdapter
import com.pcbtraining.pcb.databinding.ActivityProductBinding
import com.pcbtraining.pcb.model.AddressData
import com.pcbtraining.pcb.model.Product

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

        tabletscreencenter()


        sharedPreferences = getSharedPreferences("AddressPreferences", Context.MODE_PRIVATE)


        binding.textView9.setOnClickListener {
            intent = Intent(this, AddressInputActivity::class.java)
            startActivity(intent)
        }
        binding.account.setOnClickListener {
            intent = Intent(this,MyCartActivity::class.java)
            startActivity(intent)
        }




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


        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Handle before text change
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Handle on text change
                productAdapter.filterData(charSequence?.toString().orEmpty())
            }

            override fun afterTextChanged(editable: Editable?) {
                // Handle after text change
            }
        })





    }

    override fun onResume() {
        super.onResume()

        val addressData = retrieveAddressData()

        val retrievedServiceAddress = addressData.serviceAddress
        val retrievedLandmark = addressData.landmark
        val retrievedPinCode = addressData.pinCode
        val number = addressData.no
        val retrievedCategory = addressData.category


        binding.textView9.text = "$retrievedCategory , $retrievedServiceAddress , $retrievedLandmark, $number , $retrievedPinCode"


    }


    private fun retrieveAddressData(): AddressData {
        val serviceAddress = sharedPreferences.getString("serviceAddress", "") ?: ""
        val landmark = sharedPreferences.getString("landmark", "") ?: ""
        val pinCode = sharedPreferences.getString("pinCode", "") ?: ""
        val no = sharedPreferences.getString("no", "") ?: ""
        val category = sharedPreferences.getString("category", "") ?: ""

        return AddressData(serviceAddress,landmark,pinCode,no,category)
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