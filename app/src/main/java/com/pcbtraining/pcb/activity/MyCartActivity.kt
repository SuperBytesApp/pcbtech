package com.pcbtraining.pcb.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.MyCartAdapter
import com.pcbtraining.pcb.adapter.OrderHistoryAdapter
import com.pcbtraining.pcb.adapter.ProductAdapter
import com.pcbtraining.pcb.databinding.ActivityMyCartBinding
import com.pcbtraining.pcb.function.HashGenerationUtils
import com.pcbtraining.pcb.model.AddressData
import com.pcbtraining.pcb.model.Order
import com.pcbtraining.pcb.model.Product
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest

class MyCartActivity : AppCompatActivity() {

    lateinit var binding : ActivityMyCartBinding
    private lateinit var productAdapter: MyCartAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var sharedPreferences: SharedPreferences

    lateinit var location : String
    lateinit var totalCost : String
    lateinit var totalname : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        location = ""
        totalname = ""
        totalCost = ""


        sharedPreferences = getSharedPreferences("AddressPreferences", Context.MODE_PRIVATE)

        // Initialize cart first
        mycart(this)

        // Now, call getdetails() after the cart and productAdapter are initialized
        getdetails()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {

            // payment gateway

            binding.buyButton.setOnClickListener {
                // Handle the Buy button click

                val addressData = retrieveAddressData()
                val retrievedServiceAddress = addressData.serviceAddress
                val retrievedLandmark = addressData.landmark
                val retrievedPinCode = addressData.pinCode


                if (retrievedServiceAddress == "" && retrievedLandmark == "" && retrievedPinCode == ""){
                    Toast.makeText(this, "Please Fill Your Full Address", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                phonepeCall()

            }
            setupSwipeRefreshLayout()
        }




    }


    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            // Implement your refresh logic here
            refreshData()
        }
    }

    private fun refreshData() {
        // Implement your data refresh logic here
        // For example, you can re-fetch data from Firebase or perform any other refresh operation
        calculateTotalCost()
        Totalname()
        // After completing the refresh operation, stop the refreshing animation
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        // Removed the call to calculateTotalCost here
    }

    fun mycart(context:Context){
        binding.productRecyclerView.layoutManager = GridLayoutManager(context, 1,
            GridLayoutManager.VERTICAL,false)
        productAdapter = MyCartAdapter(emptyList(), this)
        binding.productRecyclerView.adapter = productAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("mycart").child(FirebaseAuth.getInstance().currentUser!!.uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                productAdapter = MyCartAdapter(productList,this@MyCartActivity)
                binding.productRecyclerView.adapter = productAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyCartActivity, error.message, Toast.LENGTH_SHORT).show()
                // Handle the error

            }
        })
    }



    private fun getdetails() {
        // Calculate total cost and implement your buy logic here
        totalCost = calculateTotalCost().toString()
        totalname = Totalname()
        val addressData = retrieveAddressData()
        val retrievedServiceAddress = addressData.serviceAddress
        val retrievedLandmark = addressData.landmark
        val retrievedPinCode = addressData.pinCode

        location = retrievedServiceAddress + retrievedLandmark + retrievedPinCode


    }




    fun calculateTotalCost(): Double {
        // Calculate the total cost based on the products in the cart
        var totalCost = 0.0
        for (product in productAdapter.getProductList()) {
            totalCost += product.pprice.toDouble() * product.quantity
        }
        binding.totalCostTextView.text = "Rs $totalCost"
        return totalCost
    }

    fun Totalname(): String {
        // Calculate the total cost based on the products in the cart
        val productList = productAdapter.getProductList()
        var totalProductText = ""

        for ((index, product) in productList.withIndex()) {
            // Add a point and a new line for each product
            totalProductText += "${index + 1}. ${product.pname}\n"
        }
//        binding.test.text = totalProductText
        return totalProductText
    }

    private fun retrieveAddressData(): AddressData {
        val serviceAddress = sharedPreferences.getString("serviceAddress", "") ?: ""
        val landmark = sharedPreferences.getString("landmark", "") ?: ""
        val pinCode = sharedPreferences.getString("pinCode", "") ?: ""
        val category = sharedPreferences.getString("category", "") ?: ""

        return AddressData(serviceAddress, landmark, pinCode, category)
    }


    @SuppressLint("SuspiciousIndentation")
    private fun phonepeCall() {
        try {
            PhonePe.init(this@MyCartActivity, PhonePeEnvironment.RELEASE, "M221LXTRKYPP1", "")
        } catch (e: PhonePeInitException) {
            e.printStackTrace()
            Log.e("PhonePeInit", "Initialization Failed: ${e.message}")
            return
        }

        val data = JSONObject()
        data.put("merchantTransactionId", System.currentTimeMillis().toString())
        data.put("merchantId", "M221LXTRKYPP1")
        data.put("merchantUserId", System.currentTimeMillis().toString())
        // Convert totalCost to Double first, then to Int (paise)
        val totalCostDouble = totalCost.toDoubleOrNull() ?: 0.0
        data.put("amount", convertINRToPaise(totalCostDouble))
        data.put("mobileNumber", "917520867718")
        data.put("callbackUrl", "https://webhook.site/a12f58c4-f4d1-4d8e-bc75-19bfd00e9891")
        val mPaymentInstrument = JSONObject()
        mPaymentInstrument.put("type", "PAY_PAGE")
        data.put("paymentInstrument", mPaymentInstrument)

        val base64Body: String = Base64.encodeToString(data.toString().toByteArray(Charset.defaultCharset()), Base64.NO_WRAP)
        val checksum = sha256(base64Body + "/pg/v1/pay" + "bc5403fa-acf0-4ac5-bd03-5b3647437e92") + "###1"
        val b2BPGRequest = B2BPGRequestBuilder().setData(base64Body).setChecksum(checksum).setUrl("/pg/v1/pay").build()


        try {
            startActivityForResult(PhonePe.getImplicitIntent(this, b2BPGRequest, "")!!, 1)
        } catch (e: PhonePeInitException) {
            e.printStackTrace()
            Log.e("PhonePeIntent", "Error while creating implicit intent: ${e.message}")
        }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this@MyCartActivity, "Payment Successful Please Wait Don't Back..", Toast.LENGTH_LONG).show()

                // payment success code


                val order = Order(
                    userId = FirebaseAuth.getInstance().currentUser!!.uid.toString(), // Replace with the actual user ID
                    status = "Payment Success",
                    productName = totalname,
                    productId = "", // Replace with the actual product ID
                    address = location,  // Replace with the actual address
                    price = totalCost,
                    img = "img",
                    qty = "1"
                    // Add other relevant fields
                )
                storeOrder(order)

            } else {
                Toast.makeText(this@MyCartActivity, "Transaction Failed $resultCode", Toast.LENGTH_SHORT).show()
//                finish()
            }
        }
    }


    fun storeOrder(order: Order) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val ordersRef: DatabaseReference = database.getReference("orders").child(order.userId)

        // Generate a new unique key for each order
        val newOrderRef = ordersRef.push()

        // Set the order data
        newOrderRef.setValue(order)
            .addOnSuccessListener {
                Toast.makeText(this, "Order Success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }




    private fun sha256(input: String): String {
        val bytes: ByteArray = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }


    private fun convertINRToPaise(amountInINR: Double): Int {
        // Multiply the Double value by 100 to convert INR to paise, then convert to Int
        var a = (amountInINR * 100).toInt()
        Toast.makeText(this@MyCartActivity, "$a", Toast.LENGTH_SHORT).show()
        return (amountInINR * 100).toInt()
    }

}

