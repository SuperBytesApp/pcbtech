package com.pcbtraining.pcb.activity

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.adapter.DiagramAdapter
import com.pcbtraining.pcb.adapter.LinksAdapter
import com.pcbtraining.pcb.adapter.SoftAdapter
import com.pcbtraining.pcb.databinding.ActivitySoftwareBinding
import com.pcbtraining.pcb.model.CourseItems
import com.pcbtraining.pcb.model.DiagramItem
import com.pcbtraining.pcb.model.SoftwareItems
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class SoftwareActivity : AppCompatActivity() {

    lateinit var binding: ActivitySoftwareBinding
    private lateinit var linksAdapter: SoftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoftwareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.softrecycler.layoutManager = LinearLayoutManager(this)

        val diagramItems = ArrayList<SoftwareItems>()
        linksAdapter = SoftAdapter(this, diagramItems, diagramItems) { link ->
            // Handle item click, e.g., start the video

        }
        binding.softrecycler.adapter = linksAdapter

        // Call the AsyncTask to get links from the server
        GetLinksTask().execute()



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



//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                linksAdapter.filterData(newText.orEmpty())
//                return true
//            }
//        })
//








    }

    private inner class GetLinksTask : AsyncTask<Void, Void, List<SoftwareItems>>() {

        override fun doInBackground(vararg params: Void?): List<SoftwareItems>? {
            val courseItems: MutableList<SoftwareItems> = ArrayList()

            try {
                val url = URL("https://pcbtech.in/pcbadmin/get_soft.php")
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                try {
                    val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val response = StringBuilder()

                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line).append('\n')
                    }

                    // Parse JSON array
                    val jsonArray = JSONArray(response.toString())
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        val link = jsonObject.getString("link")
                        val img = jsonObject.getString("name")
                        courseItems.add(SoftwareItems(link, img))

                    }

                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return courseItems
        }

        override fun onPostExecute(result: List<SoftwareItems>?) {
            // Handle the result (list of links) here
            if (result != null) {
                linksAdapter.updateData(result)
            }
        }
    }





}