package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.StandRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StandViewModel(application: Application): AndroidViewModel(application) {

    private val standRepository =
        StandRepository(DatabaseClient.getInstance(application).appDatabase)

    val allStands = standRepository.findAll()

    fun findByIdEvent(idEvent: Int) =
        standRepository.findByIdEvent(idEvent)

    suspend fun fetchAll() {
        standRepository.fetchAllStands()
    }

    suspend fun updateIdEventByStandIds(ids: List<Int>, idEvent: Int?): Int {
        return withContext(Dispatchers.IO) {
            standRepository.updateIdEventByStandIds(ids, idEvent)
        }
    }
}