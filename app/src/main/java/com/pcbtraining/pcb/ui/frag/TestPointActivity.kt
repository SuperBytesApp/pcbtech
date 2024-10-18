package com.pcbtraining.pcb.ui.frag

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.activity.AllDiagrampdfActivity
import com.pcbtraining.pcb.activity.CourseBuyActivity
import com.pcbtraining.pcb.activity.SensorActivity
import com.pcbtraining.pcb.databinding.ActivityTestPointBinding
import com.pcbtraining.pcb.model.PaymentHistory
import com.pcbtraining.pcb.model.User
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest

class TestPointActivity : AppCompatActivity() {

    lateinit var binding : ActivityTestPointBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private val paymentHistoryRef = database.getReference("paymenthistory")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestPointBinding.inflate(layoutInflater)
        setContentView(binding.root)


        tabletscreencenter()

        phonepeCall()




    }

    override fun onResume() {
        super.onResume()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) { getUserData(currentUser.uid) }
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


                        val access1 = user?.access2 ?: "" // Default to empty string if null

                        if (access1 == "full") {
                            binding.hideimagetestpoint.visibility = View.GONE
                            val fragmentManager: FragmentManager = supportFragmentManager
                            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
                            val targetFragment = TestpointFragment()
                            transaction.replace(R.id.testpointfrag, targetFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        } else {
                            Toast.makeText(this, "You Are Not a Subscribed Member", Toast.LENGTH_SHORT).show()
                            binding.hideimagetestpoint.visibility = View.VISIBLE
                        }

                    }
                } else {
                    println("No such document")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
            }
    }



    @SuppressLint("SuspiciousIndentation")
    private fun phonepeCall() {
        try {
            PhonePe.init(this@TestPointActivity, PhonePeEnvironment.RELEASE, "M221LXTRKYPP1", "")
        } catch (e: PhonePeInitException) {
            e.printStackTrace()
            Log.e("PhonePeInit", "Initialization Failed: ${e.message}")
            return
        }

        val data = JSONObject()
        data.put("merchantTransactionId", System.currentTimeMillis().toString())
        data.put("merchantId", "M221LXTRKYPP1")
        data.put("merchantUserId", System.currentTimeMillis().toString())
        data.put("amount", convertINRToPaise(3000))
        data.put("mobileNumber", "917520867718")
        data.put("callbackUrl", "https://webhook.site/a12f58c4-f4d1-4d8e-bc75-19bfd00e9891")
        val mPaymentInstrument = JSONObject()
        mPaymentInstrument.put("type", "PAY_PAGE")
        data.put("paymentInstrument", mPaymentInstrument)

        val base64Body: String = Base64.encodeToString(data.toString().toByteArray(Charset.defaultCharset()), Base64.NO_WRAP)
        val checksum = sha256(base64Body + "/pg/v1/pay" + "bc5403fa-acf0-4ac5-bd03-5b3647437e92") + "###1"
        val b2BPGRequest = B2BPGRequestBuilder().setData(base64Body).setChecksum(checksum).setUrl("/pg/v1/pay").build()


        binding.addCart.setOnClickListener {
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
                Toast.makeText(this, "Payment Successful Please Wait Don't Back..", Toast.LENGTH_LONG).show()

                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) { coursedb(currentUser.uid) }

            } else {

                Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun sha256(input: String): String {
        val bytes: ByteArray = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun convertINRToPaise(amountInINR: Int): Int {
        return amountInINR * 100
    }

    fun coursedb(uid: String) {
        // Get an instance of Firestore
        val db = FirebaseFirestore.getInstance()

        // Define the collection name and the document reference for the user
        val collectionName = "users"
        val documentReference = db.collection(collectionName).document(uid)

        // Create a hashMap with the new field to add
        val data = hashMapOf(
            "access2" to "full" // Adding the 'access2' field with the value 'full'
        )

        // Update the document, merging with existing fields
        documentReference.set(data, SetOptions.merge())
            .addOnSuccessListener {
                // Display a success message to the user
                Toast.makeText(this, "Course Bought Successfully", Toast.LENGTH_SHORT).show()

                // Define payment details
                val price = "3000" // Replace with the actual price
                val title = "PCB TECH Image Test Point" // Replace with the actual product title
                // Add payment history to the database
                addPaymentHistory(uid, price, title)
            }
            .addOnFailureListener { e ->
                // Display an error message if the update fails
                Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun addPaymentHistory(uid: String, price: String, title: String) {
        val paymentHistory = PaymentHistory(uid, price, title)

        // Push generates a unique key for each entry
        val pushKey = paymentHistoryRef.push().key

        pushKey?.let {
            paymentHistoryRef.child(uid).child(it).setValue(paymentHistory)
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
        }
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