package com.pcbtraining.pcb.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.pcbtraining.pcb.databinding.ActivityVideoPlayerBinding

@Suppress("DEPRECATION")
class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        val videoUrl = intent.getStringExtra("VIDEO_URL")


        if (videoUrl != null) {
            // Initialize WebView settings
            initWebViewSettings()

            // Set up WebViewClient to handle navigation within the WebView
            binding.vedioweb.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return true
                }
            }

            // Load the video URL
            binding.vedioweb.loadUrl(videoUrl)
        }


    }

    private fun initWebViewSettings() {
        // Enable JavaScript
        binding.vedioweb.settings.javaScriptEnabled = true

        // Enable hardware acceleration for better performance
        binding.vedioweb.setLayerType(View.LAYER_TYPE_HARDWARE, null)


        // Enable zoom controls
        binding.vedioweb.settings.setSupportZoom(true)
        binding.vedioweb.settings.builtInZoomControls = true
        binding.vedioweb.settings.displayZoomControls = false

        // Enable responsive layout
        binding.vedioweb.settings.useWideViewPort = true
        binding.vedioweb.settings.loadWithOverviewMode = true

        // Enable video support
        binding.vedioweb.settings.mediaPlaybackRequiresUserGesture = false
    }





}
