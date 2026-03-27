package com.example.nurdor_volunteer_app_v3.retrofit

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.example.nurdor_volunteer_app_v3.NurdorVolunteerApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.40.141:8765"

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(NurdorVolunteerApplication.encryptedPrefs))
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
