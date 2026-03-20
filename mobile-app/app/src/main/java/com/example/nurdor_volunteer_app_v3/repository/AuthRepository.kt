package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.dto.LoginResponseDto
import com.example.nurdor_volunteer_app_v3.dto.RegisterResponseDto
import com.example.nurdor_volunteer_app_v3.model.Volunteer
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import com.example.rma_project_demo_v1.dto.LoginDto
import com.example.nurdor_volunteer_app_v3.dto.RegisterDto
import retrofit2.awaitResponse

class AuthRepository(db: AppDatabase) {

    private val api = RetrofitInstance.instance
    private val mVolunteerDao = db.volunteerDao()

    suspend fun login(username: String, password: String): LoginResponseDto? {
        val loginDto = LoginDto(username, password)
        try {
            val response = api.login(loginDto).awaitResponse()
            if(response.isSuccessful) {
                val loginData = response.body()?.get("data") as LoginResponseDto?
                // [NOTE TO MYSELF] change this to DataStore in later version of project
                // encryptedshared preferences in AuthViewModel, use application.context
                return loginData
            } else {
                Log.e("retrofitApi1", "Login response failure! ${response.body()?.get("message")}")
            }
        } catch(e: Exception) {
            // [NOTE TO SELF] change this to dialog popup...
            Log.e("retrofitApi1", "Exception during login: ${e.message}")
            return null
        }
        return null
    }

    suspend fun register(registerDto: RegisterDto): Int {
        return try {
            val response = api.register(registerDto).awaitResponse()
            if(response.isSuccessful) {
                val registerData = response.body()?.get("data") as RegisterResponseDto
                val volunteer = Volunteer(
                    registerData.id,
                    registerDto.name,
                    registerDto.surname,
                    registerDto.address,
                    registerDto.phoneNumber,
                    registerDto.email,
                    registerDto.username,
                    registerDto.profilePicture,
                    registerDto.nearestCity,
                    registerDto.volunteerRole
                )
                mVolunteerDao.insert(volunteer)
            } else {
                0
            }
        } catch (e: Exception) {
            -1
        }
    }
}