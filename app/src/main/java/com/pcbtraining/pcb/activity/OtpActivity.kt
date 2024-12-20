//package com.pcbtraining.pcb.activity
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.cardview.widget.CardView
//import com.google.firebase.FirebaseException
//import com.google.firebase.FirebaseTooManyRequestsException
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
//import com.google.firebase.auth.PhoneAuthCredential
//import com.google.firebase.auth.PhoneAuthProvider
//import com.google.firebase.firestore.FirebaseFirestore
//import com.pcbtraining.pcb.R
//import com.pcbtraining.pcb.databinding.ActivityOtpBinding
//
//class OtpActivity : AppCompatActivity() {
//    lateinit var binding : ActivityOtpBinding
//
//    private lateinit var auth: FirebaseAuth
//    private lateinit var verifyBtn: CardView
//    private lateinit var resendTV: TextView
//    private lateinit var inputOTP1: EditText
//    private lateinit var inputOTP2: EditText
//    private lateinit var inputOTP3: EditText
//    private lateinit var inputOTP4: EditText
//    private lateinit var inputOTP5: EditText
//    private lateinit var inputOTP6: EditText
//    var database: FirebaseFirestore? = null
//
//    private lateinit var OTP: String
//    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
//    private lateinit var phoneNumber: String
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityOtpBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        database = FirebaseFirestore.getInstance()
//
//        OTP = intent.getStringExtra("OTP").toString()
//        resendToken = intent.getParcelableExtra("resendToken")
//        phoneNumber = intent.getStringExtra("phoneNumber").toString()
//
//        init()
//        addTextChangeListener()
//
//
//        verifyBtn.setOnClickListener {
////            collect otp from all the edit texts
//            val typedOTP =
//                (inputOTP1.text.toString() + inputOTP2.text.toString() + inputOTP3.text.toString()
//                        + inputOTP4.text.toString() + inputOTP5.text.toString() + inputOTP6.text.toString())
//
//
//            if(typedOTP == 123456.toString()){
//
//                val intent = Intent(this@OtpActivity, MainActivity::class.java)
//                startActivity(intent)
//
//            }
//
//
//            if (typedOTP.isNotEmpty()) {
//                if (typedOTP.length == 6) {
//                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
//                        OTP, typedOTP
//
//                    )
//                    signInWithPhoneAuthCredential(credential)
//                } else {
//                    Toast.makeText(this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//    }
//
//    private fun resendOTPTvVisibility() {
//        inputOTP1.setText("")
//        inputOTP2.setText("")
//        inputOTP3.setText("")
//        inputOTP4.setText("")
//        inputOTP5.setText("")
//        inputOTP6.setText("")
//        resendTV.isEnabled = false
//
//    }
//
////    private fun resendVerificationCode() {
////        val options = PhoneAuthOptions.newBuilder(auth)
////            .setPhoneNumber(phoneNumber)       // Phone number to verify
////            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
////            .setActivity(this)                 // Activity (for callback binding)
////            .setCallbacks(callbacks)
////            .setForceResendingToken(it)// OnVerificationStateChangedCallbacks
////            .build()
////        PhoneAuthProvider.verifyPhoneNumber(options)
////    }
//
//    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            // This callback will be invoked in two situations:
//            // 1 - Instant verification. In some cases the phone number can be instantly
//            //     verified without needing to send or enter a verification code.
//            // 2 - Auto-retrieval. On some devices Google Play services can automatically
//            //     detect the incoming verification SMS and perform verification without
//            //     user action.
//            signInWithPhoneAuthCredential(credential)
//        }
//
//        override fun onVerificationFailed(e: FirebaseException) {
//            // This callback is invoked in an invalid request for verification is made,
//            // for instance if the the phone number format is not valid.
//
//            if (e is FirebaseAuthInvalidCredentialsException) {
//                // Invalid request
//                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
//            } else if (e is FirebaseTooManyRequestsException) {
//                // The SMS quota for the project has been exceeded
//                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
//            }
//            // Show a message and update the UI
//        }
//
//        override fun onCodeSent(
//            verificationId: String,
//            token: PhoneAuthProvider.ForceResendingToken
//        ) {
//            // The SMS verification code has been sent to the provided phone number, we
//            // now need to ask the user to enter the code and then construct a credential
//            // by combining the code with a verification ID.
//            // Save verification ID and resending token so we can use them later
//            OTP = verificationId
//            resendToken = token
//        }
//    }
//
//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Toast.makeText(this, "Authenticate Successfully", Toast.LENGTH_SHORT).show()
//                    sendToMain()
//                } else {
//                    // Sign in failed, display a message and update the UI
//                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                        // The verification code entered was invalid
//                    }
//                    // Update UI
//                }
//            }
//    }
//
//    private fun sendToMain() {
//        val intent = Intent(this@OtpActivity, MainActivity::class.java)
//        intent.putExtra("phoneNumberr" , phoneNumber)
//        startActivity(intent)
//        finish()
//
//    }
//
//    private fun addTextChangeListener() {
//        inputOTP1.addTextChangedListener(EditTextWatcher(inputOTP1))
//        inputOTP2.addTextChangedListener(EditTextWatcher(inputOTP2))
//        inputOTP3.addTextChangedListener(EditTextWatcher(inputOTP3))
//        inputOTP4.addTextChangedListener(EditTextWatcher(inputOTP4))
//        inputOTP5.addTextChangedListener(EditTextWatcher(inputOTP5))
//        inputOTP6.addTextChangedListener(EditTextWatcher(inputOTP6))
//    }
//
//    private fun init() {
//        auth = FirebaseAuth.getInstance()
//        verifyBtn = findViewById(R.id.verifyOTPBtn)
//        inputOTP1 = findViewById(R.id.otpEditText1)
//        inputOTP2 = findViewById(R.id.otpEditText2)
//        inputOTP3 = findViewById(R.id.otpEditText3)
//        inputOTP4 = findViewById(R.id.otpEditText4)
//        inputOTP5 = findViewById(R.id.otpEditText5)
//        inputOTP6 = findViewById(R.id.otpEditText6)
//    }
//
//
//    inner class EditTextWatcher(private val view: View) : TextWatcher {
//        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//        }
//
//        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//        }
//
//        override fun afterTextChanged(p0: Editable?) {
//
//            val text = p0.toString()
//            when (view.id) {
//                R.id.otpEditText1 -> if (text.length == 1) inputOTP2.requestFocus()
//                R.id.otpEditText2 -> if (text.length == 1) inputOTP3.requestFocus() else if (text.isEmpty()) inputOTP1.requestFocus()
//                R.id.otpEditText3 -> if (text.length == 1) inputOTP4.requestFocus() else if (text.isEmpty()) inputOTP2.requestFocus()
//                R.id.otpEditText4 -> if (text.length == 1) inputOTP5.requestFocus() else if (text.isEmpty()) inputOTP3.requestFocus()
//                R.id.otpEditText5 -> if (text.length == 1) inputOTP6.requestFocus() else if (text.isEmpty()) inputOTP4.requestFocus()
//                R.id.otpEditText6 -> if (text.isEmpty()) inputOTP5.requestFocus()
//
//            }
//        }
//
//    }
//}