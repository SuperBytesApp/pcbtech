package com.pcbtraining.pcb.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Display.FLAG_SECURE
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.pcbtraining.pcb.R

class SplashActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    lateinit var Idialog : AlertDialog
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)


        val rootLayout = findViewById<ConstraintLayout>(R.id.splash)

        // Check if the device is in landscape mode
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        // Check if the device is a tablet
        val isTablet = (resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE

        if (isTablet && isLandscape) {
            // Set phone-like size in landscape mode on tablet
            val params = rootLayout.layoutParams
            params.width = resources.getDimensionPixelSize(R.dimen.phone_width)
            params.height = resources.getDimensionPixelSize(R.dimen.phone_height)
            rootLayout.layoutParams = params
        }



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

