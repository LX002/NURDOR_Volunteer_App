package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

    val upcomingEventsByVolunteerId =
        eventRepository.findUpcomingEventsByVolunteerId(PreferenceHelper.getIdVolunteer(application))

    suspend fun fetchAll() {
        eventRepository.fetchEvents()
    }

}