package com.example.rma_nurdor_project_v2.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.rma_nurdor_project_v2.DatabaseClient
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.model.EventsLog
import com.example.rma_nurdor_project_v2.repository.CityRepository
import com.example.rma_nurdor_project_v2.repository.EventRepository
import com.example.rma_nurdor_project_v2.repository.EventsLogRepository
import com.example.rma_nurdor_project_v2.repository.VolunteerRepository
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AddEventsViewModel(application: Application): AndroidViewModel(application) {

    private val eventRepository: EventRepository
    private val eventsLogRepository: EventsLogRepository
    private val volunteerRepository: VolunteerRepository
    private val cityRepository: CityRepository
    private var idVolunteer: Int

    init {
        val db = DatabaseClient.getInstance(application).appDatabase
        eventRepository = EventRepository(db)
        eventsLogRepository = EventsLogRepository(db)
        volunteerRepository = VolunteerRepository(db)
        cityRepository = CityRepository(db)
        idVolunteer = PreferenceHelper.getIdVolunteer(application)
    }

    var storedSelection = mutableListOf<Event>()

    private val _selectedEvents = MutableLiveData<MutableList<Event>>()
    val selectedEvents
        get() = _selectedEvents

    private val _allEvents = MutableLiveData<List<Event>>()
    val allEvents
        get() = _allEvents

    private val _nearestEvents = MutableLiveData<List<Event>>()
    val nearestEvents
        get() = _nearestEvents

    private val _otherEvents = MutableLiveData<List<Event>>()
    val otherEvents
        get() = _otherEvents

    private val _searchResults = MutableLiveData<List<Event>>()
    val searchResults
        get() = _searchResults

    private val _allEventsLogs = MutableLiveData<List<EventsLog>>()
    val allEventsLogs
        get() = _allEventsLogs
    
    private val _allCities = MutableLiveData<List<City>>()
    val allCities
        get() = _allCities

    var eventName = ""
    var pickedDate: LocalDate? = null
    var pickedTime: LocalTime? = null
    var selectedSort = 0
    var selectedCity: City? = City("", "")

    suspend fun loadAllEvents() {
        try {
            val volunteer = withContext(Dispatchers.IO) { volunteerRepository.getVolunteerById(idVolunteer) }
            val allEvents = withContext(Dispatchers.IO) {
                eventRepository.getEvents()
            }
            _allEvents.value = allEvents
            _nearestEvents.value = assignNearestEvents(volunteer?.nearestCity)
            _otherEvents.value = assignOtherEvents(volunteer?.nearestCity)
            _searchResults.value = emptyList()

        } catch (e: Exception) {
            _allEvents.value = emptyList()
            _nearestEvents.value = emptyList()
            _otherEvents.value = emptyList()
        }
    }

    suspend fun assignNearestEvents(zipCode: String?): List<Event> {
        return try {
            if(!zipCode.isNullOrBlank()) {
                val nearestEvents = withContext(Dispatchers.IO) {
                    eventRepository.getNearestEvents(zipCode, idVolunteer)
                }
                nearestEvents
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun assignOtherEvents(zipCode: String?): List<Event> {
        return try {
            if(!zipCode.isNullOrBlank()) {
                val otherEvents = withContext(Dispatchers.IO) {
                    eventRepository.getOtherEvents(zipCode, idVolunteer)
                }
                otherEvents
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun assignFilterResults(eventName: String?, zipCode: String?, startDateTime: LocalDateTime?, sortBy: Int): List<Event> {
        return try {
            val results = withContext(Dispatchers.IO) { eventRepository.fetchSearchResults(eventName, zipCode, startDateTime, sortBy, idVolunteer) }
            _searchResults.value = results
            Log.i("debugAddEventActivity", "search results: $results")
            results
        } catch (e: Exception) {
            Log.i("debugAddEventActivity", "search exception, prazna lista resultova")
            emptyList()
        }
    }

    suspend fun loadAllEventsLogs() {
        try {
            val allEventsLogs = withContext(Dispatchers.IO) {
                eventsLogRepository.getEventsLogs()
            }
            _allEventsLogs.value = allEventsLogs
        } catch (e: Exception) {
            _allEventsLogs.value = emptyList()
        }
    }
    
    suspend fun loadAllCities() { 
        try {
            val allCities = withContext(Dispatchers.IO) {
                cityRepository.getCities()
            }
            _allCities.value = listOf(City("0", "")) + allCities
        } catch (e: Exception) {
            _allCities.value = emptyList()
        }
    }

    suspend fun insertInitLogs(initLogs: List<EventsLog>): Int {
        return withContext(Dispatchers.IO) {
            eventsLogRepository.insertInitLogs(initLogs)
        }
    }

    suspend fun getLoadedCities(): List<City> {
        return try {
            withContext(Dispatchers.IO) { listOf(City("", "")) + cityRepository.getLoadedCities() }
        } catch (e: Exception) {
            emptyList<City>()
        }
    }

    suspend fun getCityName(e: Event): String {
        return eventRepository.getCityName(e)
    }

    suspend fun resetFilters() {
        val volunteer = withContext(Dispatchers.IO) { volunteerRepository.getVolunteerById(idVolunteer) }
        _nearestEvents.value = assignNearestEvents(volunteer?.nearestCity)
        _otherEvents.value = assignOtherEvents(volunteer?.nearestCity)
    }

}