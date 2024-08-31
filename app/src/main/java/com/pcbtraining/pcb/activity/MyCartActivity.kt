package com.pcbtraining.pcb.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.MyCartAdapter
import com.pcbtraining.pcb.databinding.ActivityMyCartBinding
import com.pcbtraining.pcb.model.AddressData
import com.pcbtraining.pcb.model.Product
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest

class MyCartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyCartBinding
    private lateinit var productAdapter: MyCartAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var sharedPreferences: SharedPreferences

    private var totalCost: Double = 0.0
    private var totalname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Secure the window
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("AddressPreferences", Context.MODE_PRIVATE)

        // Setup RecyclerView and SwipeRefreshLayout
        setupRecyclerView()
        setupSwipeRefreshLayout()

        // Load the cart and get details
        mycart()
        getdetails()

        // Set up buy button click listener
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            binding.buyButton.setOnClickListener {
                val addressData = retrieveAddressData()
                if (addressData.serviceAddress.isEmpty() || addressData.landmark.isEmpty() || addressData.pinCode.isEmpty()) {
                    Toast.makeText(this, "Please Fill Your Full Address", Toast.LENGTH_SHORT).show()
                } else {
                    phonepeCall()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.productRecyclerView.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)

        // When creating the adapter, ensure the list is mutable
        val products: MutableList<Product> = mutableListOf()
        productAdapter =  MyCartAdapter(products, this)
        binding.productRecyclerView.adapter = productAdapter

    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        calculateTotalCost()
        totalname = getTotalProductNames()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun mycart() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("mycart").child(userId)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                productAdapter.updateProductList(productList)
                calculateTotalCost()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyCartActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", error.message)
            }
        })
    }

    private fun getdetails() {
        totalCost = calculateTotalCost()
        totalname = getTotalProductNames()
    }

    private fun calculateTotalCost(): Double {
        totalCost = productAdapter.getProductList().sumOf { it.pprice.toDouble() * it.quantity }
        binding.totalCostTextView.text = "Rs $totalCost"
        return totalCost
    }

    private fun getTotalProductNames(): String {
        return productAdapter.getProductList().mapIndexed { index, product ->
            "${index + 1}. ${product.pname}"
        }.joinToString("\n")
    }

    private fun retrieveAddressData(): AddressData {
        return AddressData(
            sharedPreferences.getString("serviceAddress", "") ?: "",
            sharedPreferences.getString("landmark", "") ?: "",
            sharedPreferences.getString("pinCode", "") ?: "",
            sharedPreferences.getString("category", "") ?: ""
        )
    }

    @SuppressLint("SuspiciousIndentation")
    private fun phonepeCall() {
        try {
            PhonePe.init(this@MyCartActivity, PhonePeEnvironment.RELEASE, "M221LXTRKYPP1", "")
        } catch (e: PhonePeInitException) {
            Log.e("PhonePeInit", "Initialization Failed: ${e.message}", e)
            return
        }

        val data = JSONObject().apply {
            put("merchantTransactionId", System.currentTimeMillis().toString())
            put("merchantId", "M221LXTRKYPP1")
            put("merchantUserId", System.currentTimeMillis().toString())
            put("amount", convertINRToPaise(totalCost))
            put("mobileNumber", "917520867718")
            put("callbackUrl", "https://webhook.site/a12f58c4-f4d1-4d8e-bc75-19bfd00e9891")
            put("paymentInstrument", JSONObject().put("type", "PAY_PAGE"))
        }

        val base64Body = Base64.encodeToString(data.toString().toByteArray(Charset.defaultCharset()), Base64.NO_WRAP)
        val checksum = sha256(base64Body + "/pg/v1/pay" + "bc5403fa-acf0-4ac5-bd03-5b3647437e92") + "###1"
        val b2BPGRequest = B2BPGRequestBuilder().setChecksum(checksum).setData(base64Body).build()
        startActivityForResult(PhonePe.getImplicitIntent(this, b2BPGRequest, "")!!, 100)
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charset.defaultCharset()))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun convertINRToPaise(amountInINR: Double): Int {
        return (amountInINR * 100).toInt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            data?.let {
                val transactionState = it.getStringExtra("state")
                Toast.makeText(this, "Payment State: $transactionState", Toast.LENGTH_SHORT).show()
                Log.i("PhonePePayment", "Transaction State: $transactionState")
            }
        }
    }
}
