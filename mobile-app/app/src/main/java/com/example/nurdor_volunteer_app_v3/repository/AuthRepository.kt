package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.dto.authDto.LoginResponseDto
import com.example.nurdor_volunteer_app_v3.dto.authDto.RegisterResponseDto
import com.example.nurdor_volunteer_app_v3.model.Volunteer
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import com.example.nurdor_volunteer_app_v3.dto.authDto.LoginDto
import com.example.nurdor_volunteer_app_v3.dto.authDto.RegisterDto
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.awaitResponse

class AuthRepository(db: AppDatabase) {

    private val api = RetrofitInstance.instance
    private val mVolunteerDao = db.volunteerDao()

    suspend fun login(username: String, password: String): LoginResponseDto? {
        val loginDto = LoginDto(username, password)
        try {
            val response = api.login(loginDto).awaitResponse()
            if(response.isSuccessful) {
                val gson = Gson()
                val loginData = gson.fromJson(
                    gson.toJson(response.body()?.get("data") as? LinkedTreeMap<*, *>),
                    LoginResponseDto::class.java
                )
                Log.i("loginListener", "Login response successful")
                return loginData
            } else {
                Log.e("loginListener", "Login response unsuccessful ${response.raw().message} ${response.errorBody()?.string()}")
            }
        } catch(e: Exception) {
            // [NOTE TO SELF] change this to dialog / toast popup...
            Log.e("loginListener", "Exception during login: ${e.message}")
        }
        return null
    }

    suspend fun register(registerDto: RegisterDto): Int {
        return try {
            val response = api.register(registerDto).awaitResponse()
            if(response.isSuccessful) {
                val gson = Gson()
                val registerData = gson.fromJson(
                    gson.toJson(response.body()?.get("data") as? LinkedTreeMap<*, *>),
                    RegisterResponseDto::class.java)

                val volunteer = Volunteer(
                    registerData.id,
                    registerDto.name,
                    registerDto.surname,
                    registerDto.address,
                    registerDto.phoneNumber,
                    registerDto.email,
                    registerDto.username,
                    registerDto.profilePicture,
                    registerDto.zipCode,
                    registerDto.volunteerRole
                )
                Log.i("signInButtonListener", "register response successful!")
                val insertAsync = CoroutineScope(Dispatchers.IO).async {
                    mVolunteerDao.insert(volunteer).toInt()
                }
                insertAsync.await()
            } else {
                Log.i("signInButtonListener", "Register unsuccessful: ${response.raw().message}!")
                0
            }
        } catch (e: Exception) {
            Log.i("signInButtonListener", "Exception during registration: ${e.message}!")
            -1
        }
    }

    fun findVolunteerById(idVolunteer: Int) = mVolunteerDao.findVolunteerById(idVolunteer)

}