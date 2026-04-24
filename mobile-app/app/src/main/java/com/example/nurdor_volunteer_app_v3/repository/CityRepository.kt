package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.dto.errorDto.ErrorEntityDto
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class CityRepository(db: AppDatabase) {

    private val api = RetrofitInstance.instance
    private val mCityDao = db.cityDao()

    suspend fun fetchAll(): String {
        return try {
            val response = api.fetchAllCities().awaitResponse()
            if(response.isSuccessful) {
                val cities =  response.body()?.let { cityDtos ->
                    cityDtos.map { c -> City(c.zipCode, c.cityName) }
                }

                val insertAsync = CoroutineScope(Dispatchers.IO).async {
                    cities?.let { mCityDao.insertCities(cities) }
                }
                insertAsync.await()
                "SUCCESS: Cities fetched!"
            } else {
                "ERROR: During city fetching: ${response.raw().message}"
            }
        } catch(e: Exception) {
            "EXCEPTION: During fetching of cities: ${e.message}"
        }
    }

    fun findAll() = mCityDao.findAll()

    fun findByZipCode(zipCode: String) = mCityDao.findByZipCode(zipCode)
}