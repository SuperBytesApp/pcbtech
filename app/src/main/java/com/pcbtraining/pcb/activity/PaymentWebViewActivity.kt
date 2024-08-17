package com.pcbtraining.pcb.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.databinding.ActivityPaymentWebViewBinding
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.PaymentApp
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import dev.shreyaspatil.easyupipayment.model.TransactionStatus

class PaymentWebViewActivity : AppCompatActivity(), PaymentStatusListener {

    private lateinit var easyUpiPayment: EasyUpiPayment
    lateinit var binding : ActivityPaymentWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        val transactionId = "TID" + System.currentTimeMillis()
        binding.fieldTransactionId.setText(transactionId)
        binding.fieldTransactionRefId.setText(transactionId)

        // Setup click listener for Pay button
        binding.buttonPay.setOnClickListener { pay() }
    }

    private fun pay() {
        val payeeVpa = binding.fieldVpa.text.toString()
        val payeeName = binding.fieldName.text.toString()
        val transactionId = binding.fieldTransactionId.text.toString()
        val transactionRefId = binding.fieldTransactionRefId.text.toString()
        val payeeMerchantCode = binding.fieldPayeeMerchantCode.text.toString()
        val description = binding.fieldDescription.text.toString()
        val amount = binding.fieldAmount.text.toString()
        val paymentAppChoice = binding.radioAppChoice

        val paymentApp = when (paymentAppChoice.checkedRadioButtonId) {
            R.id.app_default -> PaymentApp.ALL
            R.id.app_amazonpay -> PaymentApp.AMAZON_PAY
            R.id.app_bhim_upi -> PaymentApp.BHIM_UPI
            R.id.app_google_pay -> PaymentApp.GOOGLE_PAY
            R.id.app_phonepe -> PaymentApp.PHONE_PE
            R.id.app_paytm -> PaymentApp.PAYTM
            else -> throw IllegalStateException("Unexpected value: " + paymentAppChoice.id)
        }

        try {
            // START PAYMENT INITIALIZATION
            easyUpiPayment = EasyUpiPayment(this) {
                this.paymentApp = paymentApp
                this.payeeVpa = payeeVpa
                this.payeeName = payeeName
                this.transactionId = transactionId
                this.transactionRefId = transactionRefId
                this.payeeMerchantCode = payeeMerchantCode
                this.description = description
                this.amount = amount
            }
            // END INITIALIZATION

            // Register Listener for Events
            easyUpiPayment.setPaymentStatusListener(this)

            // Start payment / transaction
            easyUpiPayment.startPayment()
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Error: ${e.message}")
        }
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
        // Transaction Completed
        Log.d("TransactionDetails", transactionDetails.toString())
        toast(transactionDetails.toString())

        when (transactionDetails.transactionStatus) {
            TransactionStatus.SUCCESS -> onTransactionSuccess()
            TransactionStatus.FAILURE -> onTransactionFailed()
            TransactionStatus.SUBMITTED -> onTransactionSubmitted()
        }
    }

    override fun onTransactionCancelled() {
        // Payment Cancelled by User
        toast("Cancelled by user")

    }

    private fun onTransactionSuccess() {
        // Payment Success
        toast("Success")

    }

    private fun onTransactionSubmitted() {
        // Payment Pending
        toast("Pending | Submitted")

    }

    private fun onTransactionFailed() {
        // Payment Failed
        toast("Failed")

    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

//    Uncomment this if you have inherited [android.app.Activity] and not [androidx.appcompat.app.AppCompatActivity]
//
//	override fun onDestroy() {
//		super.onDestroy()
//		easyUpiPayment.removePaymentStatusListener()
//	}
}