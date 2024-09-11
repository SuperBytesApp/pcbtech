package com.pcbtraining.pcb.activity
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pcbtraining.pcb.R

class AddressInputActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_input)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)


        sharedPreferences = getSharedPreferences("AddressPreferences", Context.MODE_PRIVATE)

        val editTextServiceAddress: EditText = findViewById(R.id.editTextServiceAddress)
        val editTextLandmark: EditText = findViewById(R.id.editTextLandmark)
        val editTextPinCode: EditText = findViewById(R.id.editTextPinCode)
       val phonenumber: EditText = findViewById(R.id.PhoneNumber)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)

        // Populate the spinner with categories
        val categories = arrayOf("Home", "Office", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Handle the submit button click
        buttonSubmit.setOnClickListener {
            val serviceAddress = editTextServiceAddress.text.toString().trim()
            val landmark = editTextLandmark.text.toString().trim()
            val pinCode = editTextPinCode.text.toString().trim()
            val pno = phonenumber.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()

            if (isValidInput(serviceAddress, landmark, pinCode)) {
                // Store the data in SharedPreferences
                storeAddressData(serviceAddress, landmark, pinCode, selectedCategory,pno)

                // TODO: You can perform other actions here, such as navigating to the next screen
            }
        }
    }

    private fun isValidInput(serviceAddress: String, landmark: String, pinCode: String): Boolean {
        // Add your validation conditions here
        if (serviceAddress.isEmpty() || landmark.isEmpty() || pinCode.isEmpty()) {
            // Show an error message or handle validation failure
            Toast.makeText(this, "Please Fill All details", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun storeAddressData(serviceAddress: String, landmark: String, pinCode: String, category: String,pno : String) {
        val editor = sharedPreferences.edit()
        editor.putString("serviceAddress", serviceAddress)
        editor.putString("landmark", landmark)
        editor.putString("pinCode", pinCode)
        editor.putString("category", category)
        editor.putString("no", pno)
        editor.apply()

        val editTextServiceAddress: EditText = findViewById(R.id.editTextServiceAddress)
        val editTextLandmark: EditText = findViewById(R.id.editTextLandmark)
        val editTextPinCode: EditText = findViewById(R.id.editTextPinCode)
        val phno: EditText = findViewById(R.id.PhoneNumber)

        editTextServiceAddress.text.clear()
        editTextPinCode.text.clear()
        editTextLandmark.text.clear()
        phno.text.clear()

        Toast.makeText(this, "Address Added Successfully", Toast.LENGTH_SHORT).show()
    }
}
