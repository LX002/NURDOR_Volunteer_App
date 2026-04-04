package com.example.nurdor_volunteer_app_v3.retrofit

import com.example.nurdor_volunteer_app_v3.dto.CityDto
import com.example.nurdor_volunteer_app_v3.dto.EventDto
import com.example.nurdor_volunteer_app_v3.dto.EventsLogDto
import com.example.rma_project_demo_v1.dto.LoginDto
import com.example.nurdor_volunteer_app_v3.dto.RegisterDto
import com.example.nurdor_volunteer_app_v3.dto.VolunteerDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {

    // auth-service calls
    @POST("/login")
    fun login(@Body volunteer: LoginDto): Call<Map<String, Object>>

    @POST("/register")
    fun register(@Body volunteer: RegisterDto): Call<Map<String, Object>>

    // volunteer-service calls
    @GET("/admin/volunteers")
    fun fetchAllVolunteers(): Call<List<VolunteerDto>>
    @GET("/cities")
    fun fetchAllCities(): Call<List<CityDto>>

    // events-log-service calls
    @GET("/volunteer/allEventsLogs")
    fun fetchAllEventsLogs(): Call<List<EventsLogDto>>

    // event-service calls
    @GET("/volunteer/eventPdf/{idEvent}")
    fun downloadPdfWithEventById(@Path("idEvent") idEvent: Int): Call<ResponseBody>

    @GET("/volunteer/getEvents")
    fun fetchAllEvents(): Call<List<EventDto>>

}