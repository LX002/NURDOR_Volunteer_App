package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.dto.eventsLogDto.UpdatePresenceDto
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.EventsLogRepository

class EventsLogViewModel(application: Application): AndroidViewModel(application) {

    private val eventsLogRepository =
        EventsLogRepository(DatabaseClient.getInstance(application).appDatabase)

    var allEventsLogs = eventsLogRepository.findAll()

    suspend fun fetchAll(): String {
       return eventsLogRepository.fetchAll()
    }

    suspend fun updateLastSeenTimestamp(updatePresenceDto: UpdatePresenceDto) {
        eventsLogRepository.updateLastSeenTimestamp(updatePresenceDto)
    }

    suspend fun updatePresence(isPresent: Byte, idEvent: Int, idVolunteer: Int): String =
        eventsLogRepository.updatePresence(isPresent, idVolunteer, idEvent)

    suspend fun updateIsPresentByEventId(isPresent: Byte, idEvent: Int): Int {
        return eventsLogRepository.updateIsPresentByEventId(isPresent, idEvent)
    }

    suspend fun deleteEventsLog(idEvent: Int, idVolunteer: Int): String {
        return eventsLogRepository.deleteEventsLog(idEvent, idVolunteer)
    }
}