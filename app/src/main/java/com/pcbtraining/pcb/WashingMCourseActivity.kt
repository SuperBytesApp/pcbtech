package com.pcbtraining.pcb

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.pcbtraining.pcb.databinding.ActivityWashingMcourseBinding
import com.pcbtraining.pcb.model.CourseOrder
import com.pcbtraining.pcb.model.PaymentHistory
import com.pcbtraining.pcb.model.User
import com.pcbtraining.pcb.ui.frag.CourseSeenFragment
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest

class WashingMCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWashingMcourseBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val paymentHistoryRef = FirebaseDatabase.getInstance().getReference("paymenthistory")
    private var amount: String = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWashingMcourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Disable screenshots
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        tabletscreencenter()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("courseorder")

        setCoursePrice()
        handleUserSession()
        setClickListeners()
    }

    // Set the course price from Firestore
    private fun setCoursePrice() {
        db.document("admin/admin").get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.courseprice.text = "₹$amount/- Including GST"
                    phonepeCall()
                } else {
                    Log.e("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting document: ${exception.localizedMessage}")
            }
    }

    // Handle user session and access
    private fun handleUserSession() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            retrieveUserData()
        } else {
            binding.coursebuy.visibility = View.VISIBLE
        }
    }

    // Set click listeners for buttons
    private fun setClickListeners() {
        binding.buy.setOnClickListener { binding.registerpayment.visibility = View.VISIBLE }
        binding.cross.setOnClickListener { binding.registerpayment.visibility = View.GONE }
    }

    private fun retrieveUserData() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                if (user?.waccess != "full") {
                    binding.coursebuy.visibility = View.VISIBLE
                } else {
                    loadfrag()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserData", "Error getting user document: ${exception.localizedMessage}")
            }
    }

    private fun loadfrag() {
        val fragment = CourseSeenFragment().apply {
            arguments = Bundle().apply {
                putString("button_clicked", "maincourse3")
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.coursefrag, fragment)
            .addToBackStack(null)
            .commit()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun phonepeCall() {
        try {
            PhonePe.init(this, PhonePeEnvironment.RELEASE, "M221LXTRKYPP1", "")
        } catch (e: PhonePeInitException) {
            Log.e("PhonePeInit", "Initialization Failed: ${e.message}")
            return
        }

        val paymentData = createPaymentData()
        val base64Body = Base64.encodeToString(paymentData.toString().toByteArray(Charset.defaultCharset()), Base64.NO_WRAP)
        val checksum = "${sha256(base64Body + "/pg/v1/pay" + "bc5403fa-acf0-4ac5-bd03-5b3647437e92")}###1"
        val b2BPGRequest = B2BPGRequestBuilder().setData(base64Body).setChecksum(checksum).setUrl("/pg/v1/pay").build()

        binding.rpay.setOnClickListener {
            if (validateFields()) {
                try {
                    startActivityForResult(PhonePe.getImplicitIntent(this, b2BPGRequest, "")!!, 1)
                } catch (e: PhonePeInitException) {
                    Log.e("PhonePeIntent", "Error creating intent: ${e.message}")
                }
            }
        }
    }

    private fun createPaymentData(): JSONObject {
        return JSONObject().apply {
            put("merchantTransactionId", System.currentTimeMillis().toString())
            put("merchantId", "M221LXTRKYPP1")
            put("merchantUserId", System.currentTimeMillis().toString())
            put("amount", convertINRToPaise(amount.toInt()))
            put("mobileNumber", "917520867718")
            put("callbackUrl", "https://webhook.site/a12f58c4-f4d1-4d8e-bc75-19bfd00e9891")
            put("paymentInstrument", JSONObject().put("type", "PAY_PAGE"))
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val fields = listOf(binding.name, binding.num, binding.email, binding.password, binding.city, binding.pincode, binding.state)
        for (field in fields) {
            if (field.text.isBlank()) {
                field.error = "Field cannot be empty"
                isValid = false
            }
        }
        return isValid
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            val status = data?.getStringExtra("code") // Check if there's a status string in the Intent

            when (status) {
                "PAYMENT_SUCCESS" -> {
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show()
                    // auth.currentUser?.uid?.let { coursedb(it) }
                }

                "PAYMENT_ERROR" -> {
                    handleTransactionError("Payment Failed")
                }
                "PAYMENT_DECLINED" -> {
                    handleTransactionError("Transaction Cancelled")
                }
                "PAYMENT_PENDING" -> {
                    handleTransactionError("Payment Pending")
                }
                else -> {
                    val errorMessage = extractErrorMessage(data)
                    handleTransactionError(errorMessage)
                }
            }
        }
    }


    private fun extractErrorMessage(data: Intent?): String {
        return data?.getStringExtra("error_message") ?: "Unknown Error Occurred"
    }


    private fun handleTransactionError(errorMessage: String) {
        val detailedMessage = when {
            errorMessage.contains("BAD_REQUEST", ignoreCase = true) -> "Bad Request: The transaction request was malformed."
            errorMessage.contains("AUTHORIZATION_FAILED", ignoreCase = true) -> "Authorization Failed: Please check your credentials."
            errorMessage.contains("INTERNAL_SERVER_ERROR", ignoreCase = true) -> "Internal Server Error: Something went wrong on the server."
            errorMessage.contains("TRANSACTION_NOT_FOUND", ignoreCase = true) -> "Transaction Not Found: The requested transaction could not be found."
            errorMessage.contains("PAYMENT_ERROR", ignoreCase = true) -> "Payment Error: There was an issue with the payment."
            errorMessage.contains("PAYMENT_PENDING", ignoreCase = true) -> "Payment Pending: The payment is still being processed."
            errorMessage.contains("PAYMENT_DECLINED", ignoreCase = true) -> "Payment Declined: The payment was declined."
            errorMessage.contains("TIMED_OUT", ignoreCase = true) -> "Transaction Timed Out: The transaction request timed out."
            else -> "Error: $errorMessage"
        }



        Toast.makeText(this, detailedMessage, Toast.LENGTH_LONG).show()
        Log.e("PaymentError", detailedMessage)
        recreate()
    }




    private fun addPaymentHistory(uid: String, price: String, title: String) {
        val paymentHistory = PaymentHistory(uid, price, title)

        // Push generates a unique key for each entry
        val pushKey = paymentHistoryRef.push().key

        pushKey?.let {
            paymentHistoryRef.child(uid).child(it).setValue(paymentHistory)
                .addOnSuccessListener {
                    showToast("Payment history added successfully")
                }
                .addOnFailureListener {
                    showToast("Failed to add payment history")
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun coursedb(uid: String) {
        val data = hashMapOf("waccess" to "full", "uid" to uid)
        db.collection("users").document(uid).set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Course Bought Successfully", Toast.LENGTH_SHORT).show()
                addPaymentHistory(uid, amount, "Inverter Washing Machine PCB Online Course")
                saveDataToDatabase()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    private fun convertINRToPaise(amountInINR: Int): Int = amountInINR * 100

    private fun sha256(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    private fun tabletscreencenter() {
        val rootLayout = findViewById<FrameLayout>(R.id.container)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
            (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            rootLayout.layoutParams = rootLayout.layoutParams.apply {
                width = resources.getDimensionPixelSize(R.dimen.phone_width)
                height = resources.getDimensionPixelSize(R.dimen.phone_height)
            }
            rootLayout.requestLayout()
        }
    }

    private fun saveDataToDatabase() {
        val nameValue = binding.name.text.toString()
        val numValue = binding.num.text.toString()
        val emailValue = binding.email.text.toString()
        val cityValue = binding.city.text.toString()
        val pincodeValue = binding.pincode.text.toString()
        val stateValue = binding.state.text.toString()

        // Generate a new key for the data
        val key = databaseReference.push().key

        // Create a CourseOrder object with the entered data
        val courseOrder = CourseOrder(nameValue, numValue, emailValue, cityValue, pincodeValue, stateValue,key!!)

        // Save the data to the Realtime Database under the generated key
        key.let {
            databaseReference.child(it).setValue(courseOrder)
        }
    }



}
