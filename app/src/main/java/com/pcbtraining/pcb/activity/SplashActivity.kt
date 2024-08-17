package com.pcbtraining.pcb.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Display.FLAG_SECURE
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.pcbtraining.pcb.R

class SplashActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    lateinit var Idialog : AlertDialog
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)



        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert!")
        builder.setMessage("Check Your Internet Connection !")


        builder.setPositiveButton("OK") {
                dialog, which -> dialog.dismiss()
            finish()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss()
            finish() }
        Idialog = builder.create()


//
//        val prg = findViewById<ProgressBar>(R.id.progressBar)
//
//        // This is used to hide the status bar and make
//        // the splash screen as a full screen activity.
//                  var bar = 0
//        Thread {
//            while (bar < 100) {
//                bar += 1
//                try {
//                    Thread.sleep(31)
//                    prg.progress = bar
//                } catch (exp: InterruptedException) {
//                    exp.printStackTrace()
//                }
//            }
//        }.start()


//            window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//           )
// we used the postDelayed(Runnable, time) method
// to send a message with a delayed time.

        isNetworkConnected()


    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkConnected(): Boolean {
        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.activeNetwork == null){

            Idialog.show()


        }else{
            Handler(Looper.getMainLooper()).postDelayed({



                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000) // 3000 is the delayed time in milliseconds.


        }
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }



}

