package com.livedata.pcbtechadmin.AddDataActivity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.livedata.pcbtechadmin.databinding.ActivityAddProductBinding
import java.util.Date
import java.util.HashMap

class AddProductActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddProductBinding
//    lateinit var leadValue: String
    private val PICK_IMAGE_REQUEST = 1
    lateinit var dialog: ProgressDialog
    var check = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this)
        dialog.setMessage("Wait..")


        binding.productimg.setOnClickListener {

            check = 1
            dialog.show()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
            )

        }


        binding.img2.setOnClickListener {
            dialog.show()
            check = 4

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
            )

        }



        binding.img3.setOnClickListener {

            check = 5
            dialog.show()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
            )

        }


//        val bundle = intent.extras
//        leadValue = bundle!!.getString("product")!!
//
//        Toast.makeText(this, leadValue.toString(), Toast.LENGTH_SHORT).show()
//
//



        binding.submi.setOnClickListener {

            dialog.show()

//            val bundle = intent.extras
////            leadValue = bundle!!.getString("product")!!
////
////            val output = addSlashToString(leadValue)

            submit()

        }


    }


    fun addSlashToString(input: String): String {
        return input.replace(" ", "")
    }


    fun submit() {


        data class ProductData(
            val pname: String, // name
            val pprice: String,
            val pstock: String,
            val pdisc: String,
            val pimg: String,
            val pimg2: String,
            val pimg3: String,
            val pkey: String)


        val ex2 = binding.name.text.toString().trim()
        val desc = binding.bio.text.toString().trim()
        val price = binding.tearn.text.toString().trim()
        val stock = binding.upto.text.toString().trim()
        val pimg = binding.productimg.text.toString().trim()
        val img2 = binding.img2.text.toString().trim()
        val img3 = binding.img3.text.toString().trim()

        val database = FirebaseDatabase.getInstance().reference.child("product")
        val newNode = database.push()
        val pushId = newNode.key
        val userData = ProductData(ex2, price, stock, desc, pimg, img2 , img3,pushId.toString())



        newNode.setValue(userData)

        dialog.dismiss()
        Toast.makeText(this, "Product Created !", Toast.LENGTH_SHORT).show()

        intent = Intent(this, AddProductActivity::class.java)
        startActivity(intent)
        finish()


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            val storage = FirebaseStorage.getInstance()
            val time = Date().time
            val reference = storage.reference.child("Profiles").child(time.toString())
            reference.putFile(uri!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { uri ->
                        val filePath = uri.toString()
                        val obj = HashMap<String, Any>()
                        obj["imgUrl"] = filePath
                        Toast.makeText(this, check.toString(), Toast.LENGTH_SHORT).show()

                        if (check == 1) {

                            dialog.dismiss()
                            binding.productimg.setText(filePath)
                            Toast.makeText(this, "Product Image Added", Toast.LENGTH_SHORT).show()

                        } else if (check == 2) {

                            dialog.dismiss()

                        } else if (check == 3) {

                            dialog.dismiss()

                        } else if (check == 4) {

                            dialog.dismiss()
                            binding.img2.setText(filePath)
                            Toast.makeText(this, "Product Image 2 Image Added", Toast.LENGTH_SHORT).show()

                        } else if (check == 5) {
                            dialog.dismiss()
                            binding.img3.setText(filePath)
                            Toast.makeText(this, "market3 Image Added", Toast.LENGTH_SHORT).show()

                        } else if (check == 6) {

                            dialog.dismiss()

                        } else if (check == 7) {

                            dialog.dismiss()

                        } else if (check == 8) {

                            dialog.dismiss()

                        } else if (check == 9) {

                            dialog.dismiss()
                        }


                    }
                } else {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }


}