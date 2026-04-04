package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.VolunteerRepository

class VolunteerViewModel(application: Application): AndroidViewModel(application) {

    private val volunteerRepository =
        VolunteerRepository(DatabaseClient.getInstance(application).appDatabase)


    fun findEnrolledVolunteersByIdEvent(idEvent: Int) =
        volunteerRepository.findEnrolledVolunteersByIdEvent(idEvent)
}