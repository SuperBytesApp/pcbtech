package com.livedata.pcbtechadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast

class WebUpoadActivity : AppCompatActivity() {


    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_upoad)

        val selectedOption = intent.getIntExtra("option", -1)  // -1 is default if no data passed


        webView = findViewById(R.id.webView)

        // Enable JavaScript
        webView.settings.javaScriptEnabled = true

        // Enable file access
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true

        // Enable WebView debugging (for Chrome Developer Tools)
        WebView.setWebContentsDebuggingEnabled(true)

        // Handle file uploads


        if (selectedOption == 1){


            // Load a web page
            val url = "https://pcbtech.in/pcbadmin/vupload.php"
            webView.loadUrl(url)
            Toast.makeText(this, "Selected Option: $selectedOption", Toast.LENGTH_SHORT).show()

        }else if (selectedOption == 2){



            // Load a web page
            val url = "https://pcbtech.in/pcbadmin/rupload.php"
            webView.loadUrl(url)
            Toast.makeText(this, "Selected Option: $selectedOption", Toast.LENGTH_SHORT).show()

        }else if (selectedOption == 3){



            // Load a web page
            val url = "https://pcbtech.in/pcbadmin/wupload.php"
            webView.loadUrl(url)
            Toast.makeText(this, "Selected Option: $selectedOption", Toast.LENGTH_SHORT).show()

        }else{
            Toast.makeText(this, "Selected Option: $selectedOption", Toast.LENGTH_SHORT).show()
        }

        // Use the data (e.g., display it or perform actions based on it)
        Toast.makeText(this, "Selected Option: $selectedOption", Toast.LENGTH_SHORT).show()



    }


}