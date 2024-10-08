package com.pcbtraining.pcb.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.AllDiagramAdapter
import com.pcbtraining.pcb.adapter.DiagramAdapter
import com.pcbtraining.pcb.adapter.DiagramAdapterr
import com.pcbtraining.pcb.adapter.ImagePagerAdapter
import com.pcbtraining.pcb.databinding.ActivityAllDiagrampdfBinding
import com.pcbtraining.pcb.databinding.ActivitySensorBinding
import com.pcbtraining.pcb.model.DiagramData
import com.pcbtraining.pcb.model.Product
import com.pcbtraining.pcb.ui.frag.CourseDiagramFragment
import com.pcbtraining.pcb.ui.frag.SensorValueFragment

@Suppress("DEPRECATION")
class SensorActivity : AppCompatActivity() {

    private lateinit var linksAdapter: DiagramAdapterr
    lateinit var binding : ActivitySensorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        tabletscreencenter()
        //        // Retrieve the data from the Intent
//        val diagram: DiagramData? = intent.getSerializableExtra("product") as? DiagramData
//
//
//
//        if (diagram != null) {
//
//            val database = FirebaseDatabase.getInstance()
//            val diagramRef = database.getReference("diagram").child(diagram.key).child("pdf")
//
//
//            val recyclerView: RecyclerView = findViewById(R.id.allpdfrecy)
//            val linksAdapter = DiagramAdapterr(this,ArrayList()) { link ->
//                // Handle item click, e.g., start the video
//            }
//            recyclerView.adapter = linksAdapter
//            recyclerView.layoutManager = LinearLayoutManager(this)
//
//
//
//            diagramRef.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val diagramList = mutableListOf<DiagramData>()
//
//                    for (dataSnapshot in snapshot.children) {
//                        val diagram = dataSnapshot.getValue(DiagramData::class.java)
//                        diagram?.let { diagramList.add(it) }
//                    }
//
//                    linksAdapter.updateData(diagramList)
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle error
//                }
//            })
//
//
//
//        }
//

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        val targetFragment = SensorValueFragment()
        // Replace the current fragment with the target fragment
        transaction.replace(R.id.sensorfrag, targetFragment)
        transaction.addToBackStack(null) // Add to back stack to handle back navigation
        transaction.commit()

    }

    fun tabletscreencenter(){
        val rootLayout = findViewById<FrameLayout>(R.id.container)

// Check if the device is in landscape mode
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

// Check if the device is a tablet
        val isTablet = (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE

        if (isTablet && isLandscape) {
            // Set phone-like size in landscape mode on tablet
            val params = rootLayout.layoutParams
            params.width = resources.getDimensionPixelSize(R.dimen.phone_width)
            params.height = resources.getDimensionPixelSize(R.dimen.phone_height)
            rootLayout.layoutParams = params

            // Request layout update to apply changes
            rootLayout.requestLayout()
        }


    }


}