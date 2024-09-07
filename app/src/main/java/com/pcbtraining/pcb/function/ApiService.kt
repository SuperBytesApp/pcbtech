package com.pcbtraining.pcb.function

import com.pcbtraining.pcb.model.CourseItems
import com.pcbtraining.pcb.model.DiagramItem
import retrofit2.http.GET

interface ApiService {

    @GET("pcbadmin/get_links.php")
    suspend fun getLinks(): List<CourseItems>

    @GET("pcbadmin/get_diag.php")
    suspend fun getDiagrams(): List<DiagramItem>

    @GET("pcbadmin/get_svalue.php")
    suspend fun getSensorvalue(): List<DiagramItem>

   @GET("pcbadmin/get_imgtestpoint.php")
    suspend fun getTestPoint(): List<DiagramItem>


}
