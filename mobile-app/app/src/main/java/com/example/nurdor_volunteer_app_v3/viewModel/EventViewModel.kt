package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.application
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.model.VolunteerRole
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.EventRepository
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class EventViewModel(application: Application): AndroidViewModel(application) {

    private val eventRepository =
        EventRepository(DatabaseClient.getInstance(application).appDatabase)

    val allEvents =
        eventRepository.findAll()
    val upcomingEvents =
        eventRepository.findUpcomingEvents()

    val upcomingEventsByVolunteerId =
        eventRepository.findUpcomingEventsByVolunteerId(PreferenceHelper.getIdVolunteer(application))

    suspend fun fetchAll() {
        eventRepository.fetchEvents()
    }

}