package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.model.VolunteerRole
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class EventViewModel(application: Application): AndroidViewModel(application) {

    private val eventRepository =
        EventRepository(DatabaseClient.getInstance(application).appDatabase)

    val allEvents = MutableLiveData<List<Event>>()
    val upcomingEvents = MutableLiveData<List<Event>>()

    suspend fun fetchAll() {
        val eventsFetch = CoroutineScope(Dispatchers.IO).async {
            eventRepository.fetchEvents()
            eventRepository.findAll()
        }
        allEvents.value = eventsFetch.await()
    }

    suspend fun findUpcomingEventsForAdmin() {
        val eventsFetch = CoroutineScope(Dispatchers.IO).async {
            eventRepository.findUpcomingEvents()
        }
        upcomingEvents.value = eventsFetch.await()
    }
    suspend fun findUpcomingEventsByVolunteerId(volunteerId: Int) {
        val eventsFetch = CoroutineScope(Dispatchers.IO).async {
            eventRepository.findUpcomingEventsByVolunteerId(volunteerId)
        }
        upcomingEvents.value = eventsFetch.await()
    }

}