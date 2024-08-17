package com.pcbtraining.pcb.activity


import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.Toast
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
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.databinding.ActivityReferBinding
import com.pcbtraining.pcb.function.HashGenerationUtils

class ReferActivity : AppCompatActivity() {

    lateinit var binding: ActivityReferBinding
    private val surl = "https://payu.herokuapp.com/success"
    private val furl = "https://payu.herokuapp.com/failure"
    private lateinit var database: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)



        binding.share.setOnClickListener {


            shareAppLink()

        }


    }


    private fun shareAppLink() {
        val appPackageName = packageName

        val playStoreLink = try {
            // Try to use the play store link with the app package name
            "https://play.google.com/store/apps/details?id=$appPackageName"
        } catch (e: PackageManager.NameNotFoundException) {
            // If the app is not installed, open the Play Store link in a browser
            "https://play.google.com/store/apps/details?id=$appPackageName"
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "\n$playStoreLink")

        val chooserIntent = Intent.createChooser(intent, "Share your app via")
        startActivity(chooserIntent)
    }
}

