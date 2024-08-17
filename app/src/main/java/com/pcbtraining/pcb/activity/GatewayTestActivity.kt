package com.pcbtraining.pcb.activity

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pcbtraining.pcb.databinding.ActivityGatewayTestBinding
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest

class GatewayTestActivity : AppCompatActivity() {

    lateinit var binding: ActivityGatewayTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGatewayTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phonepeCall()

    }


    private fun phonepeCall() {
        try {
            PhonePe.init(this@GatewayTestActivity, PhonePeEnvironment.RELEASE, "M221LXTRKYPP1", "")
        } catch (e: PhonePeInitException) {
            e.printStackTrace()
            Log.e("PhonePeInit", "Initialization Failed: ${e.message}")
            return
        }

        val data = JSONObject()
        data.put("merchantTransactionId", System.currentTimeMillis().toString())
        data.put("merchantId", "M221LXTRKYPP1")
        data.put("merchantUserId", System.currentTimeMillis().toString())
        data.put("amount", 300)
        data.put("mobileNumber", "9818732863")
        data.put("callbackUrl", "https://webhook.site/a12f58c4-f4d1-4d8e-bc75-19bfd00e9891")
        val mPaymentInstrument = JSONObject()
        mPaymentInstrument.put("type", "PAY_PAGE")
        data.put("paymentInstrument", mPaymentInstrument)

        val base64Body: String = Base64.encodeToString(data.toString().toByteArray(Charset.defaultCharset()), Base64.NO_WRAP)
        val checksum = sha256(base64Body + "/pg/v1/pay" + "bc5403fa-acf0-4ac5-bd03-5b3647437e92") + "###1"
        val b2BPGRequest = B2BPGRequestBuilder().setData(base64Body).setChecksum(checksum).setUrl("/pg/v1/pay").build()

        binding.paying.setOnClickListener {
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
                Toast.makeText(this@GatewayTestActivity, "Payment Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@GatewayTestActivity, "Transaction Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sha256(input: String): String {
        val bytes: ByteArray = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
