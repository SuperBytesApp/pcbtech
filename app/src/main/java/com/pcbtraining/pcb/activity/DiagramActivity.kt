package com.pcbtraining.pcb.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.joanzapata.pdfview.PDFView
import com.pcbtraining.pcb.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class DiagramActivity : AppCompatActivity() {

    lateinit var dialog: ProgressDialog
    lateinit var pdfView: PDFView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagram)

        // Disable screenshots and screen recording
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        // Initialize ProgressDialog
        dialog = ProgressDialog(this).apply {
            setMessage("Loading..")
            setCancelable(false)
            show()
        }

        // Find the PDFView
        pdfView = findViewById(R.id.pdfview)

        // Get the PDF URL from intent extras
        val pdfUrl = intent.getStringExtra("VIDEO_URL")

        // Download and display the PDF file
        if (pdfUrl != null) {
            DownloadPdfTask().execute(pdfUrl)
        } else {
            Toast.makeText(this, "Invalid PDF URL", Toast.LENGTH_SHORT).show()
        }
    }

    // AsyncTask to download PDF and display it in the PDFView
    private inner class DownloadPdfTask : AsyncTask<String, Void, File?>() {

        override fun doInBackground(vararg params: String): File? {
            return try {
                val url = URL(params[0])
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.connect()

                // Download the file
                val input = BufferedInputStream(url.openStream())
                val outputPath = cacheDir.absolutePath + "/downloaded_pdf.pdf"
                val outputFile = File(outputPath)
                val output = FileOutputStream(outputFile)

                val data = ByteArray(1024)
                var count: Int
                while (input.read(data, 0, 1024).also { count = it } != -1) {
                    output.write(data, 0, count)
                }

                output.flush()
                output.close()
                input.close()

                outputFile
            } catch (e: Exception) {
                Log.e("DownloadPdfTask", "Error downloading PDF: ${e.message}")
                null
            }
        }
        override fun onPostExecute(result: File?) {
            dialog.dismiss()
            if (result != null) {
                // Load the PDF using the new library
                pdfView.fromFile(result)
                    .defaultPage(0)
                    .showMinimap(false) // Disable minimap
                    .enableSwipe(true)  // Enable swipe for navigation
                    .swipeVertical(true) // Enable vertical swipe
                    .onLoad { Log.d("PdfActivity", "PDF loaded successfully") }
                    .load()
            } else {
                Toast.makeText(this@DiagramActivity, "Failed to load PDF", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
