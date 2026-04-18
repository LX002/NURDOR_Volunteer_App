package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.dto.eventDto.CreateEventDto
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.EventRepository

class CreateEventViewModel(application: Application): AndroidViewModel(application) {

    private val eventRepository = EventRepository(DatabaseClient.getInstance(application).appDatabase)
    var locationPin = Triple(45.2454010, 19.8524717, "PMF Novi Sad")
    val editTxtFields = mutableListOf("", "", "", "", "", "", "", "")
    var selectedImage = ""
    var selectedCity = City("", "")

    suspend fun createEvent(): String =
        if(isFormValid() && selectedCity != City("", "")) {
            eventRepository.createEvent(
                CreateEventDto(
                    editTxtFields[0],
                    editTxtFields[1],
                    0,
                    "${editTxtFields[2]} ${editTxtFields[3]}",
                    "${editTxtFields[4]} ${editTxtFields[5]}",
                    locationPin.first,
                    locationPin.second,
                    selectedImage,
                    editTxtFields[7],
                    0,
                    selectedCity.zipCode
                )
            )
        } else { "ERROR: form is not valid / city is not selected!" }


    fun isFormValid(): Boolean {
        editTxtFields.forEach { if(it.isBlank()) return false }
        return true
    }
}