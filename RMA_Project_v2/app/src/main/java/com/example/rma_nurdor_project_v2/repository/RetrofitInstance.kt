package com.example.rma_nurdor_project_v2.repository

import ApiInterface
import com.example.rma_nurdor_project_v2.utils.NetworkUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    //private const val BASE_URL = "http://192.168.0.102:8080/"
    //private const val BASE_URL = "http://10.5.8.161:8080/"
    private const val BASE_URL = "http://147.91.162.124:8080/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiInterface by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiInterface::class.java)
    }
}