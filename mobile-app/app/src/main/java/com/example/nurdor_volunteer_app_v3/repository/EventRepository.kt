package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.dto.EndEventResultDto
import com.example.nurdor_volunteer_app_v3.dto.StartEventDto
import com.example.nurdor_volunteer_app_v3.dto.StartEventResultDto
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

class EventRepository(db: AppDatabase) {

    private val api = RetrofitInstance.instance
    private val mEventDao = db.eventDao()
    private val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    suspend fun fetchEvents() {
        try {
            val response = api.fetchAllEvents().awaitResponse()
            if(response.isSuccessful) {
                Log.i("retrofitApi1", "Event dto list fetched!")
                val events = response.body()?.let { eventDtos ->
                    eventDtos.map { e -> Event(
                        e.id,
                        e.eventName,
                        e.description,
                        e.isStarted,
                        LocalDateTime.parse(e.startTime, inputFormatter),
                        LocalDateTime.parse(e.endTime, inputFormatter),
                        e.latitude,
                        e.longitude,
                        e.eventImg,
                        e.locationDesc,
                        e.totalDonations,
                        e.city
                    )}
                }
                val insertAsync = CoroutineScope(Dispatchers.IO).async {
                    events?.let { mEventDao.insertEvents(events) }
                }
                insertAsync.await()
            } else {
                // create dialog that displays this
                Log.e("retrofitApi1", "Error during event fetching: ${response.raw().message}")
            }
        } catch(e: Exception) {
            // create dialog that displays this
            Log.e("retrofitApi1", "Fetching events exception: ${e.message}")
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

    suspend fun fetchStartEventResult(startEventDto: StartEventDto): StartEventResultDto {
        try {
            val response = api.startEvent(startEventDto).awaitResponse()
            return if(response.isSuccessful) {
                response.body() ?: StartEventResultDto("Remote start of event - response body is null!", listOf())
            } else {
                StartEventResultDto(response.raw().message, listOf())
            }
        } catch(e: Exception) {
            return StartEventResultDto("Exception during remote starting of event: " + e.message, listOf())
        }
    }

    suspend fun fetchEndEventResult(idEvent: Int): EndEventResultDto {
        try {
            val response = api.endEvent(idEvent).awaitResponse()
            return if(response.isSuccessful) {
                response.body() ?: EndEventResultDto("Remote start of event - response body is null!", 0, listOf())
            } else {
                EndEventResultDto(response.raw().message, 0, listOf())
            }
        } catch(e: Exception) {
            return EndEventResultDto("Exception during remote starting of event: " + e.message, 0, listOf())
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
}