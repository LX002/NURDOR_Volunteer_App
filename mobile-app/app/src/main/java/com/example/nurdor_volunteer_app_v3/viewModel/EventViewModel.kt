package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.dto.EndEventResultDto
import com.example.nurdor_volunteer_app_v3.dto.StartEventDto
import com.example.nurdor_volunteer_app_v3.dto.StartEventResultDto
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.EventRepository
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper

class EventViewModel(application: Application): AndroidViewModel(application) {

    private val eventRepository =
        EventRepository(DatabaseClient.getInstance(application).appDatabase)

    val allEvents =
        eventRepository.findAll()
    val upcomingEvents =
        eventRepository.findUpcomingEvents()

    val runningEvents =
        eventRepository.findRunningEvents()

    val upcomingEventsByVolunteerId =
        eventRepository.findUpcomingEventsByVolunteerId(PreferenceHelper.getIdVolunteer(application))

    suspend fun fetchAll() {
        eventRepository.fetchEvents()
    }

    suspend fun fetchStartEventResult(startEventDto: StartEventDto): StartEventResultDto {
        return eventRepository.fetchStartEventResult(startEventDto)
    }

    suspend fun fetchEndEventResult(idEvent: Int): EndEventResultDto {
        return eventRepository.fetchEndEventResult(idEvent)
    }

    suspend fun updateIsStarted(idEvent: Int, isStarted: Boolean): Int {
        return if(isStarted) {
            eventRepository.startEventByIdEvent(idEvent)
        } else {
            eventRepository.endEventByIdEvent(idEvent)
        }
    }

}