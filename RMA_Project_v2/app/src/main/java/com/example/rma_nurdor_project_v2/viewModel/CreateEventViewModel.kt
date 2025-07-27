package com.example.rma_nurdor_project_v2.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rma_nurdor_project_v2.DatabaseClient
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.repository.EventRepository
import com.example.rma_nurdor_project_v2.repository.EventsLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CreateEventViewModel(application: Application) : AndroidViewModel(application) {

    private val eventRepository: EventRepository
    private val eventsLogRepository: EventsLogRepository
    private val eventFieldsValidationMap: MutableMap<String, Boolean>
    private val _isCreatingEnabled = MutableLiveData<Boolean>(false)
    private val _validationMessage = MutableLiveData<String>("")

    private var _eventCoordinates = MutableLiveData<Triple<Double, Double, String?>?>()
    private var _eventImg = MutableLiveData<ByteArray?>()
    private var _eventImgName = MutableLiveData<String?>()

    var eventName = ""
    var eventDescription = ""
    var startTime = ""
    var endTime = ""
    var startDate = ""
    var endDate = ""
    var selectedCity: City? = null

    val eventCoordinates: MutableLiveData<Triple<Double, Double, String?>?>
        get() = _eventCoordinates
    val eventImg: MutableLiveData<ByteArray?>
        get() = _eventImg
    val eventImgName: MutableLiveData<String?>
        get() = _eventImgName

    init {
        val db = DatabaseClient.getInstance(application).appDatabase
        eventRepository = EventRepository(db)
        eventsLogRepository = EventsLogRepository(db)
        eventFieldsValidationMap = mutableMapOf<String, Boolean>(
            "eventName" to false,
            "eventDesc" to false,
            "startDate" to false,
            "startTime" to false,
            "endDate" to false,
            "endTime" to false,
            "image" to false,
            "locationDesc" to false
        )

    }

    val isCreatingEnabled: LiveData<Boolean>
        get() = _isCreatingEnabled
    val validationMessage: LiveData<String>
        get() = _validationMessage

    fun validateField(fieldName: String, fieldContent: String) {
        when(fieldName) {
            "eventName" -> { 
                eventFieldsValidationMap[fieldName] = fieldContent.isNotBlank()
                eventName = fieldContent
                if(eventFieldsValidationMap[fieldName] == false) _validationMessage.value = "Event name must not be blank!"
            }
            "eventDesc" -> { 
                eventFieldsValidationMap[fieldName] = fieldContent.isNotBlank()
                eventDescription = fieldContent
                if(eventFieldsValidationMap[fieldName] == false) _validationMessage.value = "Event description must not be blank!"
            }
            "startDate" -> { 
                eventFieldsValidationMap[fieldName] = fieldContent.isNotBlank() && fieldContent.isParsableToLocalDate("dd.MM.yyyy")
                if(eventFieldsValidationMap[fieldName] == false) {
                    _validationMessage.value = if(fieldContent.isBlank()) "Start date must be picked!" else "Unable to parse entered date!" 
                } else {
                    startDate = fieldContent
                }
            }
            "startTime" -> { 
                eventFieldsValidationMap[fieldName] = fieldContent.isNotBlank() && fieldContent.isParsableToLocalTime("HH:mm")
                if(eventFieldsValidationMap[fieldName] == false) {
                    _validationMessage.value = if(fieldContent.isBlank()) "Start time must be picked!" else "Unable to parse entered time!"
                } else {
                    startTime = fieldContent
                }
            }
            "endDate" -> { 
                eventFieldsValidationMap[fieldName] = fieldContent.isNotBlank() && fieldContent.isParsableToLocalDate("dd.MM.yyyy")
                if(eventFieldsValidationMap[fieldName] == false) {
                    _validationMessage.value = if(fieldContent.isBlank()) "End date must be picked!" else "Unable to parse entered date!"
                } else {
                    endDate = fieldContent
                }
            }
            "endTime" -> { 
                eventFieldsValidationMap[fieldName] = fieldContent.isNotBlank() && fieldContent.isParsableToLocalTime("HH:mm")
                if(eventFieldsValidationMap[fieldName] == false) {
                    _validationMessage.value = if(fieldContent.isBlank()) "End time must be picked!" else "Unable to parse entered time!"
                } else {
                    endTime = fieldContent
                }
            }
            "image" -> { 
                eventFieldsValidationMap[fieldName] = fieldContent.isNotBlank()
                if(eventFieldsValidationMap[fieldName] == false) _validationMessage.value = "Image must be picked!"
            }
            "locationDesc" -> { 
                eventFieldsValidationMap[fieldName] = fieldContent.isNotBlank()
                if(eventFieldsValidationMap[fieldName] == false) _validationMessage.value = "Location must be picked!"
            }
            else -> Log.e("createEvent", "Wrong txt field name!")
        }
        
        _isCreatingEnabled.value = checkAllFields()

        Log.i("validationCreateEvent", "field: $fieldName,  fieldContent: $fieldContent, validation msg: ${_validationMessage.value}, en: ${_isCreatingEnabled.value}")
    }

    private fun String.isParsableToLocalDate(format: String = "dd.MM.yyyy"): Boolean {
        return try {
            LocalDate.parse(this, DateTimeFormatter.ofPattern(format))
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    private fun String.isParsableToLocalTime(format: String = "HH:mm"): Boolean {
        return try {
            LocalTime.parse(this, DateTimeFormatter.ofPattern(format))
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
    
    private fun checkAllFields(): Boolean {
        eventFieldsValidationMap.forEach {
            if(!it.value) return false
        }
        // dodatna validacija za datume
        try {
            val start = LocalDateTime.parse("$startDate $startTime", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            val end = LocalDateTime.parse("$endDate $endTime", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            return when {
                start.isAfter(end) -> {
                    _validationMessage.value = "Event start date and time must be before end date and time!"
                    false
                }
                start.isEqual(end) -> {
                    _validationMessage.value = "Event start date and time is same as end date and time!"
                    false
                }
                start.isBefore(LocalDateTime.now()) -> {
                    _validationMessage.value = "Start date and time is in past!"
                    false
                }
                end.isBefore(LocalDateTime.now()) -> {
                    _validationMessage.value = "End date and time is in past!"
                    false
                }
                else -> {
                    _validationMessage.value = "All good!"
                    true
                }
            }
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun insertEvent(event: Event): Long {
        return withContext(Dispatchers.IO) { eventRepository.insertOrReplaceEvent(event) }
    }
}