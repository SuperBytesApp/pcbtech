package com.pcbtraining.pcb.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
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
    private val shippingCharge: Double = 200.0 // Example shipping charge

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
        tabletscreencenter()

        binding.buydetailsscreen.visibility = View.GONE
        binding.buyButton2.setOnClickListener { binding.buydetailsscreen.visibility = View.VISIBLE }
        binding.swipeRefreshLayout.setOnClickListener { binding.buydetailsscreen.visibility = View.GONE }
        binding.productRecyclerView.setOnClickListener { binding.buydetailsscreen.visibility = View.GONE }
        binding.cancelButton.setOnClickListener { binding.buydetailsscreen.visibility = View.GONE }



        binding.refund.setOnClickListener {
            val url = "https://pcbtech.in/refundpolicy.html"
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
        }

        // Set up buy button click listener
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            binding.buyButton.setOnClickListener {
                val addressData = retrieveAddressData()
                if (addressData.serviceAddress.isEmpty() || addressData.landmark.isEmpty() || addressData.pinCode.isEmpty()) {
                    Toast.makeText(this,"Please Fill Your Full Address",Toast.LENGTH_SHORT).show()
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
        totalCost += shippingCharge
        binding.shippingChargeTextView.text = "Shipping Charge: ₹ $shippingCharge"
        binding.totalCostTextView.text = "Rs $totalCost"
        updateUI(totalCost)
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
            sharedPreferences.getString("no", "") ?: "",
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

    companion object {
        const val PHONEPE_PAYMENT_REQUEST_CODE = 100
        const val TRANSACTION_STATE_SUCCESS = "success"
        const val TRANSACTION_STATE_FAILED = "failed"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the result is from PhonePe payment request
        if (requestCode == PHONEPE_PAYMENT_REQUEST_CODE) {
            handlePhonePePaymentResult(data)
        }
    }

    private fun handlePhonePePaymentResult(data: Intent?) {
        // If the data is null, handle it early and return
        if (data == null) {
            Log.e("PhonePePayment", "Transaction data is null")
            showToast("No data received from the transaction")
            return
        }

        // Extract the transaction state
        val transactionState = data.getStringExtra("state")?.trim()?.lowercase()
        Log.i("PhonePePayment", "Transaction State: $transactionState")

        // Check the transaction state and handle accordingly
        when (transactionState) {
            TRANSACTION_STATE_SUCCESS -> handlePaymentSuccess()
            TRANSACTION_STATE_FAILED -> handlePaymentFailure(transactionState)
            else -> handleUnexpectedState(transactionState)
        }
    }

    private fun handlePaymentSuccess() {
        showToast("Payment Successful")
        Log.i("PhonePePayment", "Payment was successful. Saving purchase data.")
        savePurchaseData()
    }

    private fun handlePaymentFailure(transactionState: String?) {
        val errorMessage = "Payment Failed: $transactionState"
        showToast(errorMessage)
        Log.e("PhonePePayment", errorMessage)
    }

    private fun handleUnexpectedState(transactionState: String?) {
        val errorMessage = "Unexpected Payment State: $transactionState"
        showToast(errorMessage)
        Log.w("PhonePePayment", errorMessage)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    private fun savePurchaseData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        // Create a unique key for each purchase
        val purchaseId = FirebaseDatabase.getInstance().getReference("purchases").push().key
        // Prepare the product details list to save
        val productList = productAdapter.getProductList().map { product ->
            mapOf(
                "productName" to product.pname,
                "quantity" to product.quantity,
                "price" to product.pprice
            )
        }
        // Retrieve the user's address from shared preferences
        val addressData = retrieveAddressData()
        // Create a map to store the entire purchase data
        val purchaseData = mapOf(
            "userId" to userId,
            "products" to productList,
            "totalCost" to totalCost,
            "address" to mapOf(
                "serviceAddress" to addressData.serviceAddress,
                "landmark" to addressData.landmark,
                "pinCode" to addressData.pinCode,
                "no" to addressData.no,
                "category" to addressData.category
            ),
            "purchaseTime" to System.currentTimeMillis() // Add timestamp
        )
        // Save the purchase data in Firebase under the 'purchases' node
        purchaseId?.let {
            FirebaseDatabase.getInstance().getReference("purchases").child(it).setValue(purchaseData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Purchase Data Saved Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to Save Purchase Data", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


    private fun updateUI(totalCost: Double) {
       var a = totalCost - shippingCharge
        binding.totalCostTextView2.text = "Total Cost: ₹ $a"
        binding.finalTotalCostTextView.text = "Total: ₹ $totalCost"
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
