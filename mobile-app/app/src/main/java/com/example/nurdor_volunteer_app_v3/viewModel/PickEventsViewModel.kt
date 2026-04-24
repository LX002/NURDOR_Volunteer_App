package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.nurdor_volunteer_app_v3.dto.eventDto.CreateEventsLogDto
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.EventRepository
import com.example.nurdor_volunteer_app_v3.repository.EventsLogRepository
import com.example.nurdor_volunteer_app_v3.utils.EventSearchFilter
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper

class PickEventsViewModel(application: Application): AndroidViewModel(application) {

    private val db = DatabaseClient.getInstance(application).appDatabase
    private val eventRepository = EventRepository(db)
    private val eventsLogRepository = EventsLogRepository(db)
    private val idVolunteer = PreferenceHelper.getIdVolunteer(application)

    val pickedEvents = mutableSetOf<Event>()
    var findBy = "By city"
    var sortBy = "Start time ascending"
    var searchTxt = PreferenceHelper.getNearestCity(application) as String
    val searchFilter = MutableLiveData(EventSearchFilter(findBy, sortBy, searchTxt))
    val eventsToPick: LiveData<List<Event>> =  searchFilter.switchMap { f ->
        when(f.findBy) {
            "By event name" -> { findByEventNameResult(f) }
            "By city" -> { findByCityNameResult(f) }
            else -> eventRepository.findUpcomingEvents()
        }
    }

    suspend fun fetchAllEvents(): String {
        return eventRepository.fetchEvents()
    }

    suspend fun insertLogs(): String {
        val createEventsLogDtos = pickedEvents.map { e -> CreateEventsLogDto(idVolunteer, e.idEvent as Int, 0.toByte(), "initLog") }
        return eventsLogRepository.insertLogs(createEventsLogDtos)
    }

    fun updateFilter() {
        searchFilter.value = EventSearchFilter(findBy, sortBy, searchTxt)
    }

    private fun findByEventNameResult(f: @JvmSuppressWildcards EventSearchFilter): LiveData<List<Event>> {
        return when(f.sortBy) {
            "City ascending" -> { eventRepository.findUpcomingEventsByEventNameSortedByCityName(idVolunteer, f.searchTxt) }
            "City descending" -> { eventRepository.findUpcomingEventsByEventNameSortedByCityNameDesc(idVolunteer, f.searchTxt) }
            "Start time ascending" -> { eventRepository.findUpcomingEventsByEventNameSortedByStartTime(idVolunteer, f.searchTxt) }
            "Start time descending" -> { eventRepository.findUpcomingEventsByEventNameSortedByStartTimeDesc(idVolunteer, f.searchTxt) }
            else -> eventRepository.findUpcomingEvents()
        }
    }

    private fun findByCityNameResult(f: @JvmSuppressWildcards EventSearchFilter): LiveData<List<Event>> {
        return when(f.sortBy) {
            "Event name ascending" -> { eventRepository.findUpcomingEventsByCityNameSortedByEventName(idVolunteer, f.searchTxt) }
            "Event name descending" -> { eventRepository.findUpcomingEventsByCityNameSortedByEventNameDesc(idVolunteer, f.searchTxt) }
            "Start time ascending" -> { eventRepository.findUpcomingEventsByCityNameSortedByStartTime(idVolunteer, f.searchTxt) }
            "Start time descending" -> { eventRepository.findUpcomingEventsByCityNameSortedByStartTimeDesc(idVolunteer, f.searchTxt) }
            else -> eventRepository.findUpcomingEvents()
        }
    }
}