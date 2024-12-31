package com.pcbtraining.pcb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pcbtraining.pcb.databinding.ActivityWashdiaBinding

class washdiaActivity : AppCompatActivity() {

    lateinit var binding : ActivityWashdiaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWashdiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.coursew.setOnClickListener {
            val intent = Intent(this, WashingMCourseActivity::class.java)
            startActivity(intent)
        }

        binding.coursewd.setOnClickListener {
            val intent = Intent(this, WashingMCourseActivity::class.java)
            startActivity(intent)
        }

    }
}