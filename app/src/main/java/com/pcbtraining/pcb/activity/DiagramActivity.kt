package com.pcbtraining.pcb.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.pcbtraining.pcb.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class DiagramActivity : AppCompatActivity() {

    lateinit var dialog: ProgressDialog

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
        val pdfView: PDFView = findViewById(R.id.pdfView)

        // Get the PDF URL from intent extras
        val pdfUrl = intent.getStringExtra("VIDEO_URL")

        // Download and display the PDF file
        if (pdfUrl != null) {
            DownloadPdfTask(pdfView).execute(pdfUrl)
        } else {
            Toast.makeText(this, "Invalid PDF URL", Toast.LENGTH_SHORT).show()
        }
    }

    // AsyncTask to download PDF and display it in the PDFView
    private inner class DownloadPdfTask(private val pdfView: PDFView) : AsyncTask<String, Void, File?>() {

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
                pdfView.fromFile(result)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false) // Vertical scrolling for better user experience
                    .enableDoubletap(true)
                    .onLoad {
                        Log.d("PdfActivity", "PDF loaded successfully")
                    }
                    .onError { t ->
                        Log.e("PdfActivity", "Error loading PDF: $t")
                    }
                    .enableAnnotationRendering(false)
                    .enableAntialiasing(true)
                    .spacing(0) // Set spacing between pages to 0 to remove gaps
                    .autoSpacing(false) // Disable automatic spacing between pages
                    .pageFitPolicy(FitPolicy.BOTH) // Fit both width and height of the page
                    .pageSnap(true) // Enable snapping to pages when scrolling
                    .pageFling(true) // Enable fling gesture to snap to next/previous page
                    .nightMode(false) // Disable night mode
                    .load()
            } else {
                Toast.makeText(this@DiagramActivity, "Failed to load PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
