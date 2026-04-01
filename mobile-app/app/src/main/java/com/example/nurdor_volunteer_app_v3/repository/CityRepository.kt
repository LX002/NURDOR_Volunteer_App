package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class CityRepository(db: AppDatabase) {

    private val api = RetrofitInstance.instance
    private val mCityDao = db.cityDao()

    suspend fun fetchAll() {
        try {
            val response = api.fetchAllCities().awaitResponse()
            if(response.isSuccessful) {
                Log.i("retrofitApi1", "City dto list fetched!")
                val cities =  response.body()?.let { cityDtos ->
                    cityDtos.map { c -> City(c.zipCode, c.cityName) }
                }
                withContext(Dispatchers.IO) {
                    cities?.let { mCityDao.insertCities(cities) }
                }
            } else {
                // create dialog that displays this
                Log.e("retrofitApi1", "Error during city fetching: ${response.raw().message}")
            }
        } catch(e: Exception) {
            // create dialog that displays this
            Log.e("retrofitApi1", "Fetching exception: ${e.message}")
        }
    }

    suspend fun findAll(): List<City> = withContext(Dispatchers.IO) { mCityDao.findAll() }
}