package com.pcbtraining.pcb.ui.frag

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.DiagramAdapter
import com.pcbtraining.pcb.adapter.LinksAdapter
import com.pcbtraining.pcb.databinding.FragmentCourseSeenBinding
import com.pcbtraining.pcb.function.ApiService
import com.pcbtraining.pcb.function.LoadingDialog
import com.pcbtraining.pcb.model.CourseItems
import com.pcbtraining.pcb.model.DiagramItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class CourseSeenFragment : Fragment() {

    private lateinit var binding: FragmentCourseSeenBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var linksAdapter: LinksAdapter

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
        binding = FragmentCourseSeenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val loadingDialog = LoadingDialog(requireContext())

        loadingDialog.show()

        recyclerView = root.findViewById(R.id.courserecy)
        recyclerView.layoutManager = LinearLayoutManager(context)
        linksAdapter = LinksAdapter(requireContext(), ArrayList())
        recyclerView.adapter = linksAdapter

        // Use CoroutineScope to launch a coroutine on the IO dispatcher
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = apiService.getLinks()
                withContext(Dispatchers.Main) {
                    // Update the UI on the main thread
                    linksAdapter.updateData(result)
                   loadingDialog.dismiss()

                }
            } catch (e: Exception) {
                e.printStackTrace()
               loadingDialog.dismiss()
            }
        }

        return root
    }
}
