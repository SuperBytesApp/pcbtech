package com.pcbtraining.pcb.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.pcbtraining.pcb.adapter.ImagePagerAdapter
import com.pcbtraining.pcb.databinding.ActivityProductInfoBinding
import com.pcbtraining.pcb.function.HashGenerationUtils
import com.pcbtraining.pcb.model.AddressData
import com.pcbtraining.pcb.model.Order
import com.pcbtraining.pcb.model.Product

@Suppress("DEPRECATION")
class ProductInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityProductInfoBinding
    private val db = FirebaseFirestore.getInstance()
    private val surl = "https://payu.herokuapp.com/success"
    private val furl = "https://payu.herokuapp.com/failure"
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var salt: String
    lateinit var key: String
    private var quantity = 1
    private var totalPrice = 0.0


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)


        key = ""
        salt = ""


        val db = FirebaseFirestore.getInstance()
        val adminDocumentPath = "admin/admin"
        db.document(adminDocumentPath).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // DocumentSnapshot data may be null if the document exists but has no fields
                    val key1 = document.getString("key")
                    val salt1 = document.getString("saltkey")

                    key = key1.toString()
                    salt = salt1.toString()


                } else {
                    println("No such document")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
            }



        binding.account.setOnClickListener {

            intent = Intent(this,MyCartActivity::class.java)
            startActivity(intent)

        }


        sharedPreferences = getSharedPreferences("AddressPreferences", Context.MODE_PRIVATE)

        val currentUser = FirebaseAuth.getInstance().currentUser



        // Retrieve the data from the Intent
        val product: Product? = intent.getSerializableExtra("product") as? Product

        if (product != null) {

            binding.totalAmount.text = "Price : " + product.pprice.toDouble().toString()
//            binding.cartAmount.text = "₹ "+product.pprice.toDouble().toString()
            binding.title.text = product.pname
            binding.des.text = product.pdisc

            totalPrice = product.pprice.toDouble() * quantity
            binding.cartAmount.text = "₹ $totalPrice"

            binding.addCart.setOnClickListener {
                if (currentUser != null) {
                    if (quantity > product.pstock.toInt()) {
                        Toast.makeText(this, "Cannot add more items. Limited stock available.", Toast.LENGTH_SHORT).show()
                    } else {

                        // cart store
                        addcart(product)

                    }
                } else {
                    Toast.makeText(this, "You are not a subscribed member", Toast.LENGTH_SHORT).show()
                }
            }






            val imageList = listOf(product.pimg, product.pimg2, product.pimg3)




            val adapter = ImagePagerAdapter(imageList)
            adapter.onImageClick = { imageUrl ->
                // Open the image in a zoomable view (e.g., using a dialog or a new activity)
                showZoomableImage(imageUrl)
            }
            binding.viewPager.adapter = adapter



            // Set current page text
            binding.textView3.text = "1/${imageList.size}"

            // Set up page change listener to update page indicator text
            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.textView3.text = "${position + 1}/${imageList.size}"
                }
            })

            // Add click listeners to the arrow buttons
            binding.cardView3.setOnClickListener {
                binding.viewPager.currentItem = ( binding.viewPager.currentItem - 1).coerceAtLeast(0)
            }

            binding.cardView4.setOnClickListener {
                binding.viewPager.currentItem = ( binding.viewPager.currentItem + 1).coerceAtMost(imageList.size - 1)
            }


            binding.addCount.setOnClickListener {
                if (quantity < product.pstock.toInt()) {
                    quantity++
                    updateTotalPrice(product)
                } else {
                    Toast.makeText(this, "Cannot add more items. Limited stock available.", Toast.LENGTH_SHORT).show()
                }
            }

            binding.removeCount.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    updateTotalPrice(product)
                } else {
                    Toast.makeText(this, "Quantity cannot be less than 1.", Toast.LENGTH_SHORT).show()
                }
            }




        }




        binding.cardView5.setOnClickListener {

            intent = Intent(this, AddressInputActivity::class.java)
            startActivity(intent)

        }



        binding.imageView.setOnClickListener {
            onBackPressed()
        }



    }


    private fun updateTotalPrice(product: Product) {
        totalPrice = product.pprice.toDouble() * quantity
        binding.cartAmount.text = "₹ $totalPrice"
        binding.count.text = quantity.toString()
    }


    fun paymentSetup(product: Product){

//        val total = findViewById<EditText>(R.id.des)
//        val Amount = total.text.toString()

        val additionalParamsMap: HashMap<String, Any?> = HashMap()
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF1] = "udf1"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF2] = "udf2"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF3] = "udf3"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF4] = "udf4"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF5] = "udf5"
        additionalParamsMap[PayUCheckoutProConstants.SODEXO_SOURCE_ID] = "srcid123"

        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount("$totalPrice")
            .setIsProduction(true)
            .setKey("$key")
            .setProductInfo("Purchased")
            .setPhone("8888888888")
            .setTransactionId(System.currentTimeMillis().toString())
            .setFirstName("Product Buy")
            .setEmail("pcbtech00@gmail.com")
            .setSurl(surl)
            .setFurl(furl)
            .setAdditionalParams(additionalParamsMap)
            .build()

        PayUCheckoutPro.open(
            this, payUPaymentParams,
            object : PayUCheckoutProListener {


                override fun onPaymentSuccess(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]


                    // db success after fun

                    val order = Order(
                        userId = FirebaseAuth.getInstance().currentUser!!.uid.toString(), // Replace with the actual user ID
                        status = "Payment Success",
                        productName = product.pname,
                        productId = product.pkey, // Replace with the actual product ID
                        address = binding.location.text.toString(),  // Replace with the actual address
                        price = product.pprice,
                        img = product.pimg,
                        qty = quantity.toString()
                        // Add other relevant fields
                    )

                    storeOrder(order)

                    Toast.makeText(this@ProductInfoActivity, "payment success", Toast.LENGTH_SHORT).show()

                }


                override fun onPaymentFailure(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]

                    Toast.makeText(this@ProductInfoActivity, "payment fails", Toast.LENGTH_SHORT).show()

                }


                override fun onPaymentCancel(isTxnInitiated:Boolean) {

                    Toast.makeText(this@ProductInfoActivity, "payment cancel", Toast.LENGTH_SHORT).show()


                }


                override fun onError(errorResponse: ErrorResponse) {
                    val errorMessage = errorResponse.errorMessage ?: "Unknown error"
                    Log.e("PayU", "Error: $errorMessage")
                    // Show a user-friendly error message
                    Toast.makeText(this@ProductInfoActivity, "Payment failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    //For setting webview properties, if any. Check Customized Integration section for more details on this
                }

                override fun generateHash(
                    valueMap: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener
                ) {
                    if (valueMap.containsKey(PayUCheckoutProConstants.CP_HASH_STRING) && valueMap.containsKey(
                            PayUCheckoutProConstants.CP_HASH_NAME
                        ) && valueMap.containsKey(PayUCheckoutProConstants.CP_HASH_NAME) != null) {

                        val hashData = valueMap[PayUCheckoutProConstants.CP_HASH_STRING]
                        val hashName = valueMap[PayUCheckoutProConstants.CP_HASH_NAME]

                        //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                        val hash: String? = HashGenerationUtils.generateHashFromSDK(hashData.toString(),
                            salt
                        )
                        if (!TextUtils.isEmpty(hash)) {
                            val dataMap: HashMap<String, String?> = HashMap()
                            dataMap[hashName!!] = hash!!
                            hashGenerationListener.onHashGenerated(dataMap)
                        }
                    }
                }
            })
    }


    override fun onResume() {
        super.onResume()

        val addressData = retrieveAddressData()

        val retrievedServiceAddress = addressData.serviceAddress
        val retrievedLandmark = addressData.landmark
        val retrievedPinCode = addressData.pinCode
        val num = addressData.no
        val retrievedCategory = addressData.category


        binding.location.text = "$retrievedCategory , $retrievedServiceAddress , $retrievedLandmark , $retrievedPinCode ,$num"

    }

    private fun retrieveAddressData(): AddressData {
        val serviceAddress = sharedPreferences.getString("serviceAddress", "") ?: ""
        val landmark = sharedPreferences.getString("landmark", "") ?: ""
        val pinCode = sharedPreferences.getString("pinCode", "") ?: ""
        val no = sharedPreferences.getString("no", "") ?: ""
        val category = sharedPreferences.getString("category", "") ?: ""

        return AddressData(serviceAddress, landmark, pinCode,no ,category)
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



    fun addcart(product: Product){
        val databaseReference = FirebaseDatabase.getInstance().reference.child("mycart")
        val pushId = databaseReference.push().key
        val productMap = HashMap<String, Any>()
        productMap["pkey"] = product.pkey
        productMap["pdisc"] = product.pdisc
        productMap["pimg"] = product.pimg
        productMap["pimg2"] = product.pimg2
        productMap["pimg3"] = product.pimg3
        productMap["pname"] = product.pname
        productMap["pprice"] = product.pprice
        productMap["pstock"] = product.pstock
        productMap["pdel"] = pushId.toString()
        productMap["quantity"] = quantity


        val uid = FirebaseAuth.getInstance().currentUser!!.uid // Replace with the actual UID of the user

// Assuming pushId is not null (it should be generated using push())
        if (pushId != null) {
            databaseReference.child(uid).child(pushId).updateChildren(productMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Data has been successfully stored
                        Toast.makeText(this, "Cart Add Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle the error
                        Toast.makeText(this, task.exception!!.localizedMessage!!.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


    private fun showZoomableImage(imageUrl: String) {
        // Start the ZoomableImageActivity to display the zoomable image
        val intent = Intent(this, ZoomableImageActivity::class.java)
        intent.putExtra("image_url", imageUrl)
        startActivity(intent)
    }


}

// Create a data class to hold the address data