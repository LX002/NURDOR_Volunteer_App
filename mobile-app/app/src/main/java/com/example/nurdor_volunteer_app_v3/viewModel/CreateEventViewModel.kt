package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import android.widget.EditText
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.dto.eventDto.CreateEventDto
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.EventRepository
import com.example.nurdor_volunteer_app_v3.utils.DateTimeUtils

class CreateEventViewModel(application: Application): AndroidViewModel(application) {

    private val eventRepository = EventRepository(DatabaseClient.getInstance(application).appDatabase)
    var locationPin = Triple(45.2454010, 19.8524717, "PMF Novi Sad")
    val editTxtFields = mutableListOf("", "", "", "", "", "", "", "")
    var selectedImage = ""
    var selectedCity = City("", "")

    suspend fun createEvent(): String =
        if(isFormFilled() && selectedCity != City("", "")) {
            eventRepository.createEvent(
                CreateEventDto(
                    editTxtFields[0],
                    editTxtFields[1],
                    0,
                    "${editTxtFields[2]} ${editTxtFields[3]}",
                    "${editTxtFields[4]} ${editTxtFields[5]}",
                    locationPin.first,
                    locationPin.second,
                    selectedImage.ifBlank { null },
                    editTxtFields[7],
                    0,
                    selectedCity.zipCode
                )
            )
        } else { "ERROR: form is not valid / city is not selected!" }


    fun isFormFilled(): Boolean {
        for((i , txtField) in editTxtFields.withIndex()) {
            if(txtField.isBlank() && i != 6) {
                return false
            }
        }
        return true
    }

    fun validateStartAndEndDateTime(): String {
        var message = ""
        val startDate = editTxtFields[2]
        val startTime = editTxtFields[3]
        val endDate = editTxtFields[4]
        val endTime = editTxtFields[5]
        if(startDate.isNotBlank() && startTime.isNotBlank() && endDate.isNotBlank() && endTime.isNotBlank()) {
            val (start, end) = try {
                Pair(DateTimeUtils.covertToLocalDateTime("$startDate $startTime", "dd/MM/yyyy HH:mm"),
                    DateTimeUtils.covertToLocalDateTime("$endDate $endTime", "dd/MM/yyyy HH:mm"))
            } catch (e: Exception) {
                message = "EXCEPTION: cannot convert to LocalDateTime ${e.message}!"
                return message
            }

            message = if(start.isAfter(end) || start.isEqual(end)) {
                "ERROR: event start date and time must be BEFORE end date and time!"
            } else {
                "SUCCESS"
            }
        }
        return message
    }
}