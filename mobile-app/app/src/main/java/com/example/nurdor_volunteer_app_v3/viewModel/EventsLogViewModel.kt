package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.nurdor_volunteer_app_v3.model.EventsLog
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.EventsLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class EventsLogViewModel(application: Application): AndroidViewModel(application) {

    private val eventsLogRepository =
        EventsLogRepository(DatabaseClient.getInstance(application).appDatabase)

    val allEventsLogs = MutableLiveData<List<EventsLog>>()

    suspend fun fetchAll() {
        val eventsLogsAsync = CoroutineScope(Dispatchers.IO).async {
            eventsLogRepository.fetchAll()
            eventsLogRepository.findAll()
        }
        allEventsLogs.value = eventsLogsAsync.await()
    }
}