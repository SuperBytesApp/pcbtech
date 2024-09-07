package com.pcbtraining.pcb.ui.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.DiagramAdapter
import com.pcbtraining.pcb.databinding.FragmentSensorValueBinding
import com.pcbtraining.pcb.databinding.FragmentTestpointBinding
import com.pcbtraining.pcb.function.ApiService
import com.pcbtraining.pcb.function.LoadingDialog
import com.pcbtraining.pcb.model.DiagramItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class TestpointFragment : Fragment() {

    private lateinit var binding: FragmentTestpointBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var linksAdapter: DiagramAdapter

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pcbtech.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTestpointBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val loadingDialog = LoadingDialog(requireContext())

        loadingDialog.show()

        recyclerView = root.findViewById(R.id.testpointrecycler)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val diagramItems = ArrayList<DiagramItem>() // Initialize with an empty list
        linksAdapter = DiagramAdapter(requireContext(), diagramItems, diagramItems)
        recyclerView.adapter = linksAdapter

        // Call the Coroutine to get diagrams from the server
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val result = apiService.getTestPoint()
                // Update the UI on the main thread
                linksAdapter.updateData(result)
                loadingDialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
                loadingDialog.dismiss()
            }
        }

        // Set up the search bar
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Handle before text change
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Handle on text change
                linksAdapter.filterData(charSequence?.toString().orEmpty())
            }

            override fun afterTextChanged(editable: Editable?) {
                // Handle after text change
            }
        })

        return root
    }
}
