package com.pcbtraining.pcb.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.utils.URLEncodedUtils
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.databinding.ActivityCourseBuyBinding
import com.pcbtraining.pcb.function.HashGenerationUtils
import com.pcbtraining.pcb.model.CourseOrder
import com.pcbtraining.pcb.model.PaymentHistory
import com.pcbtraining.pcb.model.User
import com.pcbtraining.pcb.ui.frag.CourseDiagramFragment
import com.pcbtraining.pcb.ui.frag.CourseSeenFragment
import com.pcbtraining.pcb.ui.home.HomeFragment
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.Charset
import java.security.MessageDigest

class CourseBuyActivity : AppCompatActivity() {

    lateinit var binding : ActivityCourseBuyBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var databaseReference: DatabaseReference
    lateinit var key: String

    lateinit var amount: String

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private val paymentHistoryRef = database.getReference("paymenthistory")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        auth = FirebaseAuth.getInstance()

        amount = ""


        val db = FirebaseFirestore.getInstance()
        val adminDocumentPath = "admin/admin"
        db.document(adminDocumentPath).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // DocumentSnapshot data may be null if the document exists but has no fields
                    val amount1 = document.getString("amount")
                    amount = amount1.toString()
                    binding.courseprice.text = "₹$amount/- Including GST"


                    // phone pay payment gateway

                    phonepeCall()

                } else {
                    println("No such document")
                }

            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
            }







        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) { retrieveUserData() }else{ binding.coursebuy.visibility = View.VISIBLE }
        databaseReference = FirebaseDatabase.getInstance().reference.child("courseorder")


        binding.buy.setOnClickListener { binding.registerpayment.visibility = View.VISIBLE }

        binding.cross.setOnClickListener {  binding.registerpayment.visibility = View.GONE}



    }

    private fun retrieveUserData() {


        var userId = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Convert Firestore data to User object
                    val user = documentSnapshot.toObject(User::class.java)

                    // Do something with the user object
                    user?.let {

                        if (it.access != "full"){ binding.coursebuy.visibility = View.VISIBLE }else{
                            loadfrag()
                        }

                        // Access other fields as needed
                    }
                } else {
                    Log.d("ExampleActivity", "User document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ExampleActivity", "Error getting user document", exception)
            }



    }




    fun coursedb(uid: String){

        val db = FirebaseFirestore.getInstance()

        val collectionName = "users"
        val documentReference = db.collection(collectionName).document(uid)

        val data = hashMapOf(
            "access" to "full",
            "name" to binding.name.text.toString(),
            "email" to binding.email.text.toString(),
            "password" to binding.password.text.toString(),
            "number" to binding.num.text.toString(),
            "uid" to uid
        )
        documentReference.set(data)
            .addOnSuccessListener {

                Toast.makeText(this@CourseBuyActivity, "Course Buy Successfully", Toast.LENGTH_SHORT).show()

                val uid = uid // Replace with the actual user ID
                val price = amount // Replace with the actual price
                val title = "PCB TECH Course" // Replace with the actual product title

                addPaymentHistory(uid, price, title)
                saveDataToDatabase()

            }
            .addOnFailureListener { e ->

                Toast.makeText(this@CourseBuyActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()

            }

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

    fun loadfrag(){



        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        val targetFragment = CourseSeenFragment()


        // Replace the current fragment with the target fragment
        transaction.replace(R.id.coursefrag, targetFragment)
        transaction.addToBackStack(null) // Add to back stack to handle back navigation
        transaction.commit()




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


    @SuppressLint("SuspiciousIndentation")
    private fun phonepeCall() {
        try {
            PhonePe.init(this@CourseBuyActivity, PhonePeEnvironment.RELEASE, "M221LXTRKYPP1", "")
        } catch (e: PhonePeInitException) {
            e.printStackTrace()
            Log.e("PhonePeInit", "Initialization Failed: ${e.message}")
            return
        }

        val data = JSONObject()
        data.put("merchantTransactionId", System.currentTimeMillis().toString())
        data.put("merchantId", "M221LXTRKYPP1")
        data.put("merchantUserId", System.currentTimeMillis().toString())
        data.put("amount", convertINRToPaise(amount.toInt()))
        data.put("mobileNumber", "917520867718")
        data.put("callbackUrl", "https://webhook.site/a12f58c4-f4d1-4d8e-bc75-19bfd00e9891")
        val mPaymentInstrument = JSONObject()
        mPaymentInstrument.put("type", "PAY_PAGE")
        data.put("paymentInstrument", mPaymentInstrument)

        val base64Body: String = Base64.encodeToString(data.toString().toByteArray(Charset.defaultCharset()), Base64.NO_WRAP)
        val checksum = sha256(base64Body + "/pg/v1/pay" + "bc5403fa-acf0-4ac5-bd03-5b3647437e92") + "###1"
        val b2BPGRequest = B2BPGRequestBuilder().setData(base64Body).setChecksum(checksum).setUrl("/pg/v1/pay").build()


            binding.rpay.setOnClickListener {

                if (binding.name.text.toString() == "") {
                    binding.name.error = "Field cannot be empty"
                    return@setOnClickListener
                }
                if (binding.num.text.toString() == "") {
                    binding.num.error = "Field cannot be empty"
                    return@setOnClickListener
                }
                if (binding.email.text.toString() == "") {
                    binding.email.error = "Field cannot be empty"
                    return@setOnClickListener
                }
                if (binding.password.text.toString() == "") {
                    binding.password.error = "Field cannot be empty"
                    return@setOnClickListener
                }
                if (binding.city.text.toString() == "") {
                    binding.city.error = "Field cannot be empty"
                    return@setOnClickListener
                }
                if (binding.pincode.text.toString() == "") {
                    binding.pincode.error = "Field cannot be empty"
                    return@setOnClickListener
                }
                if (binding.state.text.toString() == "") {
                    binding.state.error = "Field cannot be empty"
                    return@setOnClickListener
                }


                // payment gateway

                try {
                    startActivityForResult(PhonePe.getImplicitIntent(this, b2BPGRequest, "")!!, 1)
                } catch (e: PhonePeInitException) {
                    e.printStackTrace()
                    Log.e("PhonePeIntent", "Error while creating implicit intent: ${e.message}")
                }


            }



    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this@CourseBuyActivity, "Payment Successful Please Wait Don't Back..", Toast.LENGTH_LONG).show()

                if (binding.email.text.isNotEmpty() && binding.password.text.isNotEmpty()) {
                    signUpWithEmail(binding.email.text.toString().trim(), binding.password.text.toString().trim())
                } else {
                    Toast.makeText(this, "Something Wrong Contact Support Team..", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this@CourseBuyActivity, "Transaction Failed", Toast.LENGTH_SHORT).show()


                intent = Intent(this,CourseBuyActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun sha256(input: String): String {
        val bytes: ByteArray = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }


    private fun signUpWithEmail(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user
                    // 's information
                    val user = auth.currentUser!!.uid.toString()
                    coursedb(user)
                    Toast.makeText(this, "Sign up successful. Please Wait Don't Back", Toast.LENGTH_LONG).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun convertINRToPaise(amountInINR: Int): Int {
        return amountInINR * 100
    }

}