package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.dto.EventsLogDto
import com.example.nurdor_volunteer_app_v3.dto.UpdatePresenceDto
import com.example.nurdor_volunteer_app_v3.model.EventsLog
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class EventsLogRepository(db: AppDatabase) {

    private val api = RetrofitInstance.instance
    private val mEventsLogDao = db.eventsLogDao()

    suspend fun fetchAll() {
        try {
            val response = api.fetchAllEventsLogs().awaitResponse()
            if(response.isSuccessful) {
                response.body()?.let { eventsLogDtos ->
                    val eventsLogs = eventsLogDtos.map { e ->
                        EventsLog(
                            e.id,
                            e.volunteer,
                            e.event,
                            e.isPresent,
                            e.note
                        )
                    }
                    val insertAsync = CoroutineScope(Dispatchers.IO).async {
                        mEventsLogDao.insertEventsLogs(eventsLogs)
                    }
                    insertAsync.await()
                }
            } else {
                // create dialog that displays this
                Log.e("retrofitApi1", "Error during events log fetching: ${response.raw().message}")
            }
        } catch (e: Exception) {
            Log.e("retrofitApi1", "Exception during events log fetching: ${e.message}")
        }
    }

    suspend fun updatePresence(isPresent: Byte, idVolunteer: Int, idEvent: Int): String {
        try {
            val eventsLogDtoAsync = CoroutineScope(Dispatchers.IO).async {
                mEventsLogDao.findByIdVolunteerAndIdEvent(idVolunteer, idEvent)?.idEventsLog?.let {
                    return@async UpdatePresenceDto(idVolunteer, idEvent, isPresent, null)
                }
            }
            val dto = eventsLogDtoAsync.await()
            return dto?.let {
                val response = api.updatePresence(dto).awaitResponse()
                if(response.isSuccessful) {
                    val res = updateIsPresentByEventIdAndVolunteerId(isPresent, idEvent, idVolunteer)
                    if(res == 1)
                        "Successfully ${if(isPresent == 1.toByte()) " joined to the " else " left the "} event!"
                    else
                        "ERROR: during updating the presence - returned != 1 updated rows!!!"
                } else {
                    "ERROR: during updating the presence: ${response.raw().message}: ${response.errorBody()?.string()}"
                }
            } ?: "ERROR: during updating the presence: response body is null"
        } catch (e: Exception) {
            return "ERROR: Exception during updating the presence: ${e.message}"
        }
    }

    fun findAll() = mEventsLogDao.findAll()

    suspend fun updateIsPresentByEventIdAndVolunteerId(isPresent: Byte, idEvent: Int, idVolunteer: Int): Int {
        return withContext(Dispatchers.IO) {
            mEventsLogDao.updateIsPresentByEventIdAndVolunteerId(isPresent, idEvent, idVolunteer)
        }
    }

    suspend fun updateIsPresentByEventId(isPresent: Byte, idEvent: Int): Int {
        return withContext(Dispatchers.IO) {
            mEventsLogDao.updateIsPresentByIdEvent(isPresent, idEvent)
        }
    }
}