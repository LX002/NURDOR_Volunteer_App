package com.example.nurdor_volunteer_app_v3.retrofit

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val encryptedPrefs: SharedPreferences): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = encryptedPrefs.getString("jwt_token", null)
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }


}