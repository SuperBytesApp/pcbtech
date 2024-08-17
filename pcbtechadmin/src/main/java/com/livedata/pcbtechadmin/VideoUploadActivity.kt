package com.livedata.pcbtechadmin

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.livedata.pcbtechadmin.databinding.ActivityVideoUploadBinding

class VideoUploadActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    lateinit var binding : ActivityVideoUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val editTextUrl = findViewById<EditText>(R.id.editTextUrl)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)

        binding.copy.visibility = View.GONE
        binding.upload.visibility = View.GONE

        buttonSubmit.setOnClickListener {
            val url = editTextUrl.text.toString()
            val htmlContent = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            margin: 0;
                            padding: 0;
                            background-color: black; 
                            overflow: hidden;
                        }
                    
                        .video-container iframe {
                            border: 0;
                            position: absolute;
                            top: 0;
                            height: 100%;
                            width: 100%;
                        }
                    </style>
                    <title>Responsive Video Player</title>
                </head>
                <body>
                    <div class="video-container">
                        <iframe 
                            id="video-frame"
                            src="$url"
                            loading="lazy" 
                            allow="accelerometer; gyroscope; autoplay; encrypted-media; picture-in-picture;" 
                            allowfullscreen="true"
                        ></iframe>
                    </div>
                </body>
                </html>
            """.trimIndent()

            if (htmlContent != "") {

                binding.code.text = htmlContent
                binding.copy.visibility = View.VISIBLE
                binding.upload.visibility = View.VISIBLE

            }

        }

        binding.upload.setOnClickListener {
            intent = Intent(this,WebUpoadActivity::class.java)
            startActivity(intent)

        }

        binding.copy.setOnClickListener {

            val text = binding.code.text.toString()

            // Copy the text to the clipboard
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)

            // Optionally, provide a message to the user indicating that the text has been copied
            Toast.makeText(applicationContext, "Text copied to clipboard", Toast.LENGTH_SHORT).show()

        }


    }
}