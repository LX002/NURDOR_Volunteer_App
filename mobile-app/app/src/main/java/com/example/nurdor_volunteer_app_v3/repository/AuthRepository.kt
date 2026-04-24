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

    suspend fun login(username: String, password: String): Pair<LoginResponseDto?, String> {
        val loginDto = LoginDto(username, password)
        try {
            val response = api.login(loginDto).awaitResponse()
            val gson = Gson()
            if(response.isSuccessful) {
                val loginData = gson.fromJson(gson.toJson(response.body()?.get("data") as? LinkedTreeMap<*, *>), LoginResponseDto::class.java)
                val message = gson.fromJson(gson.toJson(response.body()?.get("message")), String::class.java)
                return Pair(loginData, "SUCCESS: $message")
            } else {
                val errorMap = gson.fromJson(response.errorBody()?.string(), Map::class.java)
                return Pair(null, "ERROR: Login response unsuccessful - ${errorMap["message"]?.toString() ?: response.raw().message}")
            }
        } catch(e: Exception) {
            return Pair(null, "EXCEPTION: During login - ${e.message}")
        }
    }

    suspend fun register(registerDto: RegisterDto): String {
        return try {
            val response = api.register(registerDto).awaitResponse()
            val gson = Gson()
            if(response.isSuccessful) {
                val registerData = gson.fromJson(gson.toJson(response.body()?.get("data") as? LinkedTreeMap<*, *>), RegisterResponseDto::class.java)
                val message = gson.fromJson(gson.toJson(response.body()?.get("message")), String::class.java)

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
                val id = insertAsync.await()
                if(id < 1) {
                    "WARNING: $message BUT volunteer is not saved into Room database!"
                } else {
                    "SUCCESS: $message [ID - ${id}]"
                }
            } else {
                val errorMap = gson.fromJson(response.errorBody()?.string(), Map::class.java)
                "ERROR: Registration unsuccessful - ${errorMap["message"]?.toString() ?: response.raw().message}"
            }
        } catch (e: Exception) {
            "EXCEPTION: During registration - ${e.message}"
        }
    }

    fun findVolunteerById(idVolunteer: Int) = mVolunteerDao.findVolunteerById(idVolunteer)
}