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


        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)



        dialog = ProgressDialog(this)
        dialog.setMessage("Loading..")
        dialog.setCancelable(false)

        dialog.show()

        val pdfView: PDFView = findViewById(R.id.pdfView)

        // Get the PDF URL
        val pdfUrl =  intent.getStringExtra("VIDEO_URL")

        // Download the PDF file
        DownloadPdfTask(pdfView).execute(pdfUrl)
    }

    private inner class DownloadPdfTask(private val pdfView: PDFView) :
        AsyncTask<String, Void, File?>() {

        override fun doInBackground(vararg params: String): File? {
            try {
                val url = URL(params[0])
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.connect()

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
                dialog.dismiss()

                return outputFile
            } catch (e: Exception) {
                dialog.dismiss()
                Log.e("DownloadPdfTask", "Error downloading PDF: ${e.message}")
                return null
            }
        }

        override fun onPostExecute(result: File?) {
            if (result != null) {
                pdfView.fromFile(result)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false) // Set this to true if you want horizontal scrolling
                    .enableDoubletap(true)
                    .onLoad {
                        dialog.dismiss()
                        Log.d("PdfActivity", "PDF loaded successfully")
                    }
                    .onError { t ->
                        dialog.dismiss()
                        Log.e("PdfActivity", "Error loading PDF: $t")
                    }
                    .enableAnnotationRendering(false)
                    .enableAntialiasing(true)
                    .spacing(0)
                    .autoSpacing(true) // Set this to true to enable automatic spacing between pages
                    .pageFitPolicy(FitPolicy.BOTH)
                    .pageSnap(true)
                    .pageFling(false)
                    .nightMode(false)
                    .load()
            } else {
                // Handle download error
            }
        }
    }
}