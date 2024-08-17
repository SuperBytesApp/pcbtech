package com.livedata.pcbtechadmin

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.livedata.pcbtechadmin.AddDataActivity.AddDiagramActivity

class DiagramEditActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagram_edit)
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
        val url = "https://pcbtech.in/pcbadmin/diaedit.php"
        webView.loadUrl(url)
    }


}
