package com.example.rma_nurdor_project_v2.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.rma_nurdor_project_v2.DatabaseClient
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.repository.CityRepository
import com.example.rma_nurdor_project_v2.repository.EventRepository
import com.example.rma_nurdor_project_v2.repository.VolunteerRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventArchiveViewModel(application: Application) : AndroidViewModel(application) {

    private var cityRepository: CityRepository
    private var eventRepository: EventRepository
    private var volunteerRepository: VolunteerRepository

    private var _allCities = MutableLiveData<List<City>>()
    val allCities
        get() = _allCities

    private var _archivedEvents = MutableLiveData<List<Event>>()
    val archivedEvents
        get() = _archivedEvents

    private var _allEvents = MutableLiveData<List<Event>>()
    val allEvents
        get() = _allEvents

    private var _searchResults = MutableLiveData<List<Event>>()
    val searchResults
        get() = _searchResults

    private var _allVolunteers = MutableLiveData<List<Volunteer>>()
    val allVolunteers
        get() = _allVolunteers

    private var _volunteersAtEvent = MutableLiveData<List<Volunteer>>()
    val volunteersAtEvent
        get() = _volunteersAtEvent

    var getVolunteersAtEventAsync: ((Event) -> Deferred<List<Volunteer>?>)? = null
    var displayedEvent: Event? = null
    var searchSwitch = false

    var eventName = ""
    var selectedMonth = 0
    var selectedYear = 0
    var selectedSort = 0
    var selectedCity = City("", "")

    init {
        val db = DatabaseClient.getInstance(application).appDatabase
        cityRepository = CityRepository(db)
        eventRepository = EventRepository(db)
        volunteerRepository = VolunteerRepository(db)
    }

    suspend fun loadAllCities() {
//        _allCities.value = withContext(Dispatchers.IO) {
//            cityRepository.getLoadedCities()
//        }
        try {
            val cities = withContext(Dispatchers.IO) {
                cityRepository.getCities()
            }
            _allCities.value = cities
        } catch (e: Exception) {
            _allCities.value = emptyList()
        }
    }

    suspend fun getLoadedCities(): List<City> {
        return try {
            withContext(Dispatchers.IO) {
                listOf(City("", "")) + cityRepository.getLoadedCities()
            }
        } catch (e: Exception) {
            emptyList<City>()
        }
    }

    suspend fun loadAllEvents() {
        try {
            Log.i("eventArchiveDebugViewModel", "usao u loadAllEvents")
            val events = withContext(Dispatchers.IO) { eventRepository.getEvents() }
            val archivedEvents = withContext(Dispatchers.IO) { eventRepository.getArchivedEvents() }
            _allEvents.value = events
            _archivedEvents.value = archivedEvents
            _searchResults.value = emptyList()
        } catch (e: Exception) {
            _allEvents.value = emptyList()
            _archivedEvents.value = emptyList()
            _searchResults.value = emptyList()
        }
    }

    suspend fun getArchivedEvents(): List<Event> {
        return try {
            withContext(Dispatchers.IO) { eventRepository.getArchivedEvents() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchFilterResults(eventName: String, year: Int, month: Int, zipCode: String, sortBy: Int): List<Event> {
        return try {
            val results = withContext(Dispatchers.IO) {
                eventRepository.fetchArchiveSearchResults(eventName, year, month, zipCode, sortBy)
            }
            _searchResults.value = results
            results
        } catch (e: Exception) {
            _searchResults.value = emptyList()
            emptyList()
        }
    }

    suspend fun loadAllVolunteers() {
        try {
            val volunteers = withContext(Dispatchers.IO) {
                volunteerRepository.getVolunteers()
            }
            _allVolunteers.value = volunteers
        } catch (e: Exception) {
            _allVolunteers.value = emptyList()
        }
    }

    suspend fun getVolunteersAtEvent(idEvent: Int): List<Volunteer> {
        return try {
            val volunteers = withContext(Dispatchers.IO) {
                volunteerRepository.getVolunteersAtEvent(idEvent)
            }
            volunteers
        } catch (e: Exception) {
            emptyList<Volunteer>()
        }
    }

    suspend fun getCityName(e: Event): String {
        return eventRepository.getCityName(e)
    }
}