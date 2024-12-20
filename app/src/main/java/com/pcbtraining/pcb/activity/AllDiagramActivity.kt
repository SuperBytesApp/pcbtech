package com.pcbtraining.pcb.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.AllDiagramAdapter
import com.pcbtraining.pcb.adapter.DiagramAdapter
import com.pcbtraining.pcb.databinding.ActivityAllDiagramBinding
import com.pcbtraining.pcb.model.DiagramData

class AllDiagramActivity : AppCompatActivity() {

    lateinit var binding: ActivityAllDiagramBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDiagramBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = FirebaseDatabase.getInstance()
        val diagramRef = database.getReference("diagram")

        tabletscreencenter()

// ...

// Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = AllDiagramAdapter(2) // You need to create this adapter
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL,false)

// Attach a listener to read the data
        diagramRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val diagramList = mutableListOf<DiagramData>()

                for (dataSnapshot in snapshot.children) {
                    val diagram = dataSnapshot.getValue(DiagramData::class.java)
                    diagram?.let { diagramList.add(it) }
                }

                adapter.submitList(diagramList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

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