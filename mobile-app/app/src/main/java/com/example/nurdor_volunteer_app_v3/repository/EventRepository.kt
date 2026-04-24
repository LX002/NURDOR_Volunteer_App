package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.nurdor_volunteer_app_v3.dto.eventDto.CreateEventDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.EndEventResultDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.EventDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.StartEventDto
import com.example.nurdor_volunteer_app_v3.dto.eventDto.StartEventResultDto
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventRepository(db: AppDatabase) {

    val gson = Gson()
    private val api = RetrofitInstance.instance
    private val mEventDao = db.eventDao()
    private val inputFormatters = Pair(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"), DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    suspend fun fetchEvents(): String {
        return try {
            val response = api.fetchAllEvents().awaitResponse()
            if(response.isSuccessful) {
                Log.i("retrofitApi1", "Event dto list fetched!")
                val events = response.body()?.let { eventDtos ->
                    eventDtos.map { e -> Event(
                        e.id,
                        e.eventName,
                        e.description,
                        e.isStarted,
                        LocalDateTime.parse(e.startTime, inputFormatters.first),
                        LocalDateTime.parse(e.endTime, inputFormatters.first),
                        e.latitude,
                        e.longitude,
                        e.eventImg,
                        e.locationDesc,
                        e.totalDonations,
                        e.city
                    )}
                }
                if(events != null) {
                    val insertAsync = CoroutineScope(Dispatchers.IO).async {
                        mEventDao.insertEvents(events)
                    }
                    insertAsync.await()
                    "SUCCESS: Events fetched!"
                } else {
                    "ERROR: During events fetching - response body is NULL!"
                }
            } else {
                "ERROR: During events fetching: ${response.raw().message}"
            }
        } catch(e: Exception) {
            "EXCEPTION: During events fetching: ${e.message}"
        }
    }

    suspend fun createEvent(eventDto: CreateEventDto): String {
        try {
            val response = api.createEvent(eventDto).awaitResponse()
            return if(response.isSuccessful) {
                if(response.body() != null) {
                    val e = response.body() as EventDto
                    val event = Event(
                        e.id, e.eventName, e.description, e.isStarted,
                        LocalDateTime.parse(e.startTime, inputFormatters.second),
                        LocalDateTime.parse(e.endTime, inputFormatters.second), e.latitude,
                        e.longitude, e.eventImg, e.locationDesc, e.totalDonations, e.city
                    )
                    val insertAsync = CoroutineScope(Dispatchers.IO).async {
                        mEventDao.insertEvent(event)
                    }
                    insertAsync.await()
                    "SUCCESS: Event created at ID: ${e.id}"
                } else {
                    "ERROR: Create event $eventDto -> response body is null!"
                }
            } else { "ERROR: Create event $eventDto -> response body unsuccessful - ${response.raw().message}" }
        } catch (e: Exception) {
            return "EXCEPTION: During creation of event $eventDto -> ${e.message}"
        }
    }

    fun findAll() =
        mEventDao.findAll()

    fun findUpcomingEventsByVolunteerId(volunteerId: Int) =
        mEventDao.findUpcomingEventsByVolunteerId(volunteerId, LocalDateTime.now())

    fun findUpcomingEvents() =
        mEventDao.findUpcomingEvents(LocalDateTime.now())

    fun findRunningEvents() =
        mEventDao.findRunningEvents()

    suspend fun deleteEvent(event: Event): String {
        val deletedRows = withContext(Dispatchers.IO) {
            mEventDao.deleteEvent(event)
        }

        if(deletedRows == 1) {
            try {
                val response = api.deleteEvent(event.idEvent as Int).awaitResponse()
                return if(response.isSuccessful) response.body() as String else "ERROR: Event is not deleted on server! ${response.errorBody()?.string()}"
            } catch(e: Exception) {
                return "EXCEPTION: During deleting event on server: ${e.message}"
            }
        } else { return "ERROR: event isn't deleted!" }
    }

    suspend fun fetchStartEventResult(startEventDto: StartEventDto): StartEventResultDto {
        try {
            val response = api.startEvent(startEventDto).awaitResponse()
            return if(response.isSuccessful) {
                response.body() ?: StartEventResultDto("ERROR: Start of event [ID: ${startEventDto.idEvent}] response body is null!", listOf())
            } else {
                StartEventResultDto("ERROR: ${response.raw().message}", listOf())
            }
        } catch(e: Exception) {
            return StartEventResultDto("EXCEPTION: During remote starting of event [ID: ${startEventDto.idEvent}]: ${e.message}", listOf())
        }
    }

    suspend fun fetchEndEventResult(idEvent: Int): EndEventResultDto {
        try {
            val response = api.endEvent(idEvent).awaitResponse()
            return if(response.isSuccessful) {
                response.body() ?: EndEventResultDto("ERROR: Ending of event [ID: $idEvent] response body is null!", 0, listOf())
            } else {
                EndEventResultDto("ERROR: ${response.raw().message}", 0, listOf())
            }
        } catch(e: Exception) {
            return EndEventResultDto("EXCEPTION: During remote starting of event [ID: $idEvent]: ${e.message}", 0, listOf())
        }
    }

    suspend fun findById(idEvent: Int) {
        return withContext(Dispatchers.IO) {
            mEventDao.findById(idEvent)
        }
    }

    suspend fun startEventByIdEvent(idEvent: Int, totalDonations: Long): Int {
        return withContext(Dispatchers.IO) {
            mEventDao.startOrEndEventByIdEvent(idEvent, 1.toByte(), totalDonations)
        }
    }

    suspend fun endEventByIdEvent(idEvent: Int, totalDonations: Long): Int {
        return withContext(Dispatchers.IO) {
            mEventDao.startOrEndEventByIdEvent(idEvent, 0.toByte(), totalDonations)
        }
    }
    
    fun findUpcomingEventsByCityNameSortedByStartTime(idVolunteer: Int, zipCode: String): LiveData<List<Event>> =
        mEventDao.findUpcomingEventsByCityNameSortedByStartTime(idVolunteer, zipCode)
    
    fun findUpcomingEventsByCityNameSortedByStartTimeDesc(idVolunteer: Int, zipCode: String): LiveData<List<Event>> =
        mEventDao.findUpcomingEventsByCityNameSortedByStartTimeDesc(idVolunteer, zipCode)
    
    fun findUpcomingEventsByCityNameSortedByEventName(idVolunteer: Int, zipCode: String): LiveData<List<Event>> =
        mEventDao.findUpcomingEventsByCityNameSortedByEventName(idVolunteer, zipCode)
    
    fun findUpcomingEventsByCityNameSortedByEventNameDesc(idVolunteer: Int, zipCode: String): LiveData<List<Event>> =
        mEventDao.findUpcomingEventsByCityNameSortedByEventNameDesc(idVolunteer, zipCode)

    //find by event name
    fun findUpcomingEventsByEventNameSortedByStartTime(idVolunteer: Int, eventName: String): LiveData<List<Event>> =
        mEventDao.findUpcomingEventsByEventNameSortedByStartTime(idVolunteer, eventName)
    
    fun findUpcomingEventsByEventNameSortedByStartTimeDesc(idVolunteer: Int, eventName: String): LiveData<List<Event>> =
        mEventDao.findUpcomingEventsByEventNameSortedByStartTimeDesc(idVolunteer, eventName)
    
    fun findUpcomingEventsByEventNameSortedByCityName(idVolunteer: Int, eventName: String): LiveData<List<Event>> =
        mEventDao.findUpcomingEventsByEventNameSortedByCityName(idVolunteer, eventName)
    
    fun findUpcomingEventsByEventNameSortedByCityNameDesc(idVolunteer: Int, eventName: String): LiveData<List<Event>> =
        mEventDao.findUpcomingEventsByEventNameSortedByCityNameDesc(idVolunteer, eventName)
        
}