package com.livedata.pcbtechadmin

import android.annotation.SuppressLint
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

class AddImgtestpointActivity : AppCompatActivity() {


    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    companion object {
        private const val FILE_UPLOAD_REQUEST_CODE = 1
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_imgtestpoint)

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
        webView.webChromeClient = object : WebChromeClient() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@AddImgtestpointActivity.filePathCallback = filePathCallback
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                startActivityForResult(intent, AddImgtestpointActivity.FILE_UPLOAD_REQUEST_CODE)
                return true
            }
        }

        // Handle page navigation within WebView
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        // Load a web page
        val url = "https://pcbtech.in/course/imgupload.php"
        webView.loadUrl(url)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddImgtestpointActivity.FILE_UPLOAD_REQUEST_CODE) {
            // Handle file upload result
            val result = data?.data?.let { arrayOf(it) }
            filePathCallback?.onReceiveValue(result)
            filePathCallback = null
        }
    }
}
