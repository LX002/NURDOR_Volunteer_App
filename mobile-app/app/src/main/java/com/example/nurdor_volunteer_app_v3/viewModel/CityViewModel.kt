package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.repository.CityRepository
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityViewModel(application: Application): AndroidViewModel(application) {

    private val cityRepository: CityRepository =
        CityRepository(DatabaseClient.getInstance(application).appDatabase)

    val allCities = MutableLiveData<List<City>>()

    suspend fun findAll() = withContext(Dispatchers.IO) { cityRepository.findAll() }

    suspend fun fetchAll() {
        withContext(Dispatchers.IO) {
            cityRepository.fetchAll()
        }
        allCities.value = cityRepository.findAll()
    }
}