package com.example.nurdor_volunteer_app_v3.retrofit

import com.example.nurdor_volunteer_app_v3.dto.CityDto
import com.example.rma_project_demo_v1.dto.LoginDto
import com.example.nurdor_volunteer_app_v3.dto.RegisterDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    // auth-service calls
    @POST("/login")
    fun login(@Body volunteer: LoginDto): Call<Map<String, Object>>

    @POST("/register")
    fun register(@Body volunteer: RegisterDto): Call<Map<String, Object>>

    // volunteer-service calls
    @GET("/cities")
    fun fetchCities(): Call<List<CityDto>>

}