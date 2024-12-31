package com.pcbtraining.pcb.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.RefigiratorCourseActivity
import com.pcbtraining.pcb.WashingMCourseActivity
import com.pcbtraining.pcb.databinding.ActivityRefigDiaBinding
import com.pcbtraining.pcb.databinding.ActivityWashdiaBinding

class RefigDiaActivity : AppCompatActivity() {

    lateinit var binding : ActivityRefigDiaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefigDiaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.courser.setOnClickListener {
            val intent = Intent(this, RefigiratorCourseActivity::class.java)
            startActivity(intent)
        }

        binding.courserd.setOnClickListener {
            val intent = Intent(this, WashingMCourseActivity::class.java)
            startActivity(intent)
        }

    }
}