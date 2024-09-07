package com.pcbtraining.pcb.ui.frag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.pcbtraining.pcb.R

class TestPointActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_point)



        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        val targetFragment = TestpointFragment()
        // Replace the current fragment with the target fragment
        transaction.replace(R.id.testpointfrag, targetFragment)
        transaction.addToBackStack(null) // Add to back stack to handle back navigation
        transaction.commit()
    }
}