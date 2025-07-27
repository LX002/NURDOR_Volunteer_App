package com.example.rma_nurdor_project_v2.repository

import android.content.Context
import android.util.Log
import com.example.rma_nurdor_project_v2.AppDatabase
import com.example.rma_nurdor_project_v2.DatabaseClient
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_project_demo_v1.dto.CityDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class CityRepository (db: AppDatabase) {
    private val retrofitApi = RetrofitInstance.instance
    private val mCityDao = db.cityDao()

    fun insertCity(city: City) {
        // ovo omogucava da se insert vrsi na drugoj niti
        CoroutineScope(Dispatchers.IO).launch {
            if(mCityDao.getCities().isEmpty() || !mCityDao.getCities().contains(city)) {
                Log.i("inserting1", "Inserting city: $city")
                mCityDao.insertCity(city)
            } else {
                Log.i("inserting1", "City $city is already in database!")
            }
        }
    }

    suspend fun getCities(): List<City> {
        return withContext(Dispatchers.IO) {
            fetchCities()
            Log.i("after_inserting1", "returning mAllCities ${mCityDao.getCities()}")
            val citiesList = mCityDao.getCities()
            Log.i("actualReturn", "actual return of cities: $citiesList")
            citiesList
        }
    }

    suspend fun getLoadedCities(): List<City> {
        return withContext(Dispatchers.IO) { mCityDao.getCities() }
    }

    private fun getRetrofitCities() {
        retrofitApi.getCities().enqueue(object: Callback<List<CityDto>> {
            override fun onResponse(call: Call<List<CityDto>>, response: Response<List<CityDto>>) {
                if(response.isSuccessful && response.body() != null) {
                    val cityEntities = response.body()!!.map { cityDto ->
                        City(cityDto.zipCode, cityDto.cityName)
                    }
                    Log.i("retrofitApi1", "City dto list fetched!")
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.i("inserting1", "Inserting data in local database...")
                        Log.i("insertingList", "$cityEntities")
                        mCityDao.insertCities(cityEntities)
                        if(cityEntities.size < mCityDao.getCities().size) {
                            mCityDao.deleteCities(mCityDao.getCities().filter { it !in cityEntities })
                        }
                    }
                } else {
                    Log.i("retrofiApi1", "City dto list is empty....")
                }
            }

            override fun onFailure(call: Call<List<CityDto>>, t: Throwable) {
                Log.e("retrofitApi1", "City list is empty and error occurred: ${t.message}\n${t.stackTrace}")
            }
        })
    }

    private suspend fun fetchCities() {
        try {
            val response = retrofitApi.getCities().awaitResponse()
            if (response.isSuccessful) {
                val cityEntities = response.body()!!.map { cityDto ->
                    City(cityDto.zipCode, cityDto.cityName)
                }
                Log.i("retrofitApi1", "City dto list fetched!")
                withContext(Dispatchers.IO) {
                    Log.i("inserting1", "Inserting data in local database...")
                    Log.i("insertingList", "$cityEntities")
                    mCityDao.insertCities(cityEntities)
                    if(cityEntities.size < mCityDao.getCities().size) {
                        mCityDao.deleteCities(mCityDao.getCities().filter { it !in cityEntities })
                    }
                }
            } else {
                Log.e("retrofitApi1", "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("retrofitApi1", "Network error: ${e.message}")
        }
    }
}