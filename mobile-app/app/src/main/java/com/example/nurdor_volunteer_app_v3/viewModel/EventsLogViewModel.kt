package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.EventsLogRepository

class EventsLogViewModel(application: Application): AndroidViewModel(application) {

    private val eventsLogRepository =
        EventsLogRepository(DatabaseClient.getInstance(application).appDatabase)

    var allEventsLogs = eventsLogRepository.findAll()

    suspend fun fetchAll() {
        eventsLogRepository.fetchAll()
    }

    suspend fun updatePresence(isPresent: Byte, idEvent: Int, idVolunteer: Int): String =
        eventsLogRepository.updatePresence(isPresent, idVolunteer, idEvent)

    suspend fun updateIsPresentByEventId(isPresent: Byte, idEvent: Int): Int {
        return eventsLogRepository.updateIsPresentByEventId(isPresent, idEvent)
    }
}