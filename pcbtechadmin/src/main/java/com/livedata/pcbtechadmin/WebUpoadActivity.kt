package com.livedata.pcbtechadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class WebUpoadActivity : AppCompatActivity() {


    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_upoad)

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


        // Load a web page
        val url = "https://pcbtech.in/pcbadmin/vupload.php"
        webView.loadUrl(url)
    }


}