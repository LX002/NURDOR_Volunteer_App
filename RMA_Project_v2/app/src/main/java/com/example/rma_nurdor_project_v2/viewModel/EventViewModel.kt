package com.example.rma_nurdor_project_v2.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rma_nurdor_project_v2.DatabaseClient
import com.example.rma_nurdor_project_v2.dto.EventsLogDto
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.model.EventsLog
import com.example.rma_nurdor_project_v2.repository.EventRepository
import com.example.rma_nurdor_project_v2.repository.EventsLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val eventRepository: EventRepository
    private val eventsLogRepository: EventsLogRepository

    private val _allEvents = MutableLiveData<List<Event>>()
    private val _allEventsLogs = MutableLiveData<List<EventsLog>>()
    private val _eventsForIds = MutableLiveData<List<Event>>()
    private val _eventsLogsForVolunteer = MutableLiveData<List<EventsLog>>()
    private val _eventsForVolunteer = MutableLiveData<List<Event>>()

    init {
        val db = DatabaseClient.getInstance(application).appDatabase
        eventRepository = EventRepository(db)
        eventsLogRepository = EventsLogRepository(db)
    }

    val allEvents: LiveData<List<Event>>
        get() = _allEvents
    val eventsLogs: LiveData<List<EventsLog>>
        get() = _allEventsLogs
    val eventsForIds: LiveData<List<Event>>
        get() = _eventsForIds
    val eventsLogsByVolunteer: LiveData<List<EventsLog>>
        get() = _eventsLogsForVolunteer
    val eventsForVolunteer: LiveData<List<Event>>
        get() = _eventsForVolunteer

    suspend fun getLoadedEvents(): MutableList<Event> {
        return try {
            eventRepository.getLoadedEvents()
        } catch (e: Exception) {
            mutableListOf<Event>()
        }
    }

    suspend fun getLoadedEventsByVolunteerId(id: Int): MutableList<Event> {
        return try {
            eventRepository.getLoadedEventsByVolunteerId(id) as MutableList<Event>
        } catch (e: Exception) {
            mutableListOf<Event>()
        }
    }

    suspend fun loadEvents() {
        try {
            val eventsList = withContext(Dispatchers.IO) {
                eventRepository.getEvents()
            }
            _allEvents.value = eventsList
        } catch (e: Exception) {
            _allEvents.value = emptyList()
        }
    }

    suspend fun loadEventsLogs() {
        try {
            val eventsLogsList = withContext(Dispatchers.IO) {
                eventsLogRepository.getEventsLogs()
            }
            _allEventsLogs.value = eventsLogsList
        } catch (e: Exception) {
            _allEventsLogs.value = emptyList()
        }
    }

    suspend fun loadEventsWithIds(eventIds: List<Int>) {
        try {
            val eventsList = withContext(Dispatchers.IO) {
                eventRepository.getEventsWithIds(eventIds)
            }
            _eventsForIds.value = eventsList

        } catch (e: Exception) {
            _eventsForIds.value = emptyList()
        }
    }

    suspend fun loadEventsLogsByVolunteerId(idVolunteer: Int) {
        try {
            val eventsLogsList = withContext(Dispatchers.IO) {
                eventsLogRepository.getEventsLogsByVolunteerId(idVolunteer)
            }
            _eventsLogsForVolunteer.value = eventsLogsList

        } catch (e: Exception) {
            _eventsLogsForVolunteer.value = emptyList()
        }
    }

    suspend fun loadEventsForVolunteer(idVolunteer: Int) {
        try {
            val eventsList = withContext(Dispatchers.IO) {
                eventRepository.getLoadedEventsByVolunteerId(idVolunteer)
            }
            _eventsForVolunteer.value = eventsList
            Log.i("onStartEvents", "inside view model state: ${eventsList.size}")
        } catch (e: Exception) {
            _eventsForVolunteer.value = emptyList()
        }
    }

    suspend fun insertLog(log: EventsLog): Long {
        return withContext(Dispatchers.IO) { eventsLogRepository.insertLog(log) }
    }

    suspend fun markAsPresent(idVolunteer: Int, idEvent: Int, isPresent: Byte): Int {
        return withContext(Dispatchers.IO) { eventsLogRepository.markAsPresent(idVolunteer, idEvent, isPresent) }
    }

}