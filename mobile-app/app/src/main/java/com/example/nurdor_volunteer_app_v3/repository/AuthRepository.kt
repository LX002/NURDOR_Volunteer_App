package com.example.nurdor_volunteer_app_v3.repository

import androidx.security.crypto.EncryptedSharedPreferences
import com.example.nurdor_volunteer_app_v3.dto.LoginResponseDto
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import com.example.rma_project_demo_v1.dto.LoginDto
import retrofit2.awaitResponse

class AuthRepository {

    private val api = RetrofitInstance.instance

    suspend fun login(username: String, password: String): LoginResponseDto? {
        val loginDto = LoginDto(username, password)
        try {
            val response = api.login(loginDto).awaitResponse()
            if(response.isSuccessful) {
                val loginData = response.body()?.get("data") as LoginResponseDto?
                // [NOTE TO MYSELF] change this to DataStore in later version of project
                // encryptedshared preferences in AuthViewModel, use application.context
                return loginData
            }
        } catch(e: Exception) {
            return null
        }
        return null
    }
}