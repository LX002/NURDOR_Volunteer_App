package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.dto.eventDto.CreateEventsLogDto
import com.example.nurdor_volunteer_app_v3.dto.eventsLogDto.UpdatePresenceDto
import com.example.nurdor_volunteer_app_v3.model.EventsLog
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import com.example.nurdor_volunteer_app_v3.utils.DateTimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.time.LocalDateTime

class EventsLogRepository(db: AppDatabase) {

    private val api = RetrofitInstance.instance
    private val mEventsLogDao = db.eventsLogDao()

    suspend fun fetchAll(): String {
        return try {
            val response = api.fetchAllEventsLogs().awaitResponse()
            if(response.isSuccessful) {
                val eventsLogDtos = response.body()
                if(eventsLogDtos != null) {
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
                    "SUCCESS: Event logs fetched!"
                } else { "ERROR: During events log fetching: response body is NULL!" }
            } else {
                "ERROR: During events log fetching: ${response.raw().message}"
            }
        } catch (e: Exception) {
            "EXCEPTION: During events log fetching: ${e.message}"
        }
    }

    suspend fun updatePresence(isPresent: Byte, idVolunteer: Int, idEvent: Int): String {
        try {
            val eventsLogDtoAsync = CoroutineScope(Dispatchers.IO).async {
                mEventsLogDao.findByIdVolunteerAndIdEvent(idVolunteer, idEvent)?.idEventsLog?.let {
                    return@async UpdatePresenceDto(
                        idVolunteer, idEvent, isPresent,
                        if(isPresent == 1.toByte()) {
                            DateTimeUtils.changeDateFormat(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss")
                        } else { "initLog" }
                    )
                }
            }
            val dto = eventsLogDtoAsync.await()
            return dto?.let {
                val response = api.updatePresence(dto).awaitResponse()
                if(response.isSuccessful) {
                    val res = updateIsPresentByEventIdAndVolunteerId(isPresent, idEvent, idVolunteer)
                    if(res == 1)
                        "SUCCESS: Successfully ${if(isPresent == 1.toByte()) " joined to the " else " left the "} event!"
                    else
                        "WARNING: During updating the presence - not properly updated in Room database!"
                } else {
                    "ERROR: During updating the presence: ${response.raw().message}: ${
                        response.errorBody()?.string()
                    }"
                }
            } ?: "ERROR: During updating the presence: response body is null"
        } catch (e: Exception) {
            return "EXCEPTION: During updating the presence: ${e.message}"
        }
    }

    suspend fun insertLogs(eventsLogs: List<CreateEventsLogDto>): String {
        return try {
            val response = api.insertEventLog(eventsLogs).awaitResponse()
            if(response.isSuccessful) {
                if(response.body() == null) {
                    "ERROR: Picking events (inserting logs) response body is null!"
                } else {
                    val resultLogs = response.body()?.map { dto -> EventsLog(
                        dto.id,
                        dto.volunteer,
                        dto.event,
                        dto.isPresent,
                        dto.note
                    )}
                    val insertAsync = CoroutineScope(Dispatchers.IO).async {
                        if(resultLogs?.isNotEmpty() == true) {
                            mEventsLogDao.insertEventsLogs(resultLogs)
                        } else { listOf() }
                    }
                    val ids = insertAsync.await()
                    if(ids.isNotEmpty()) {
                        "SUCCESS: picked events (logs) are successfully saved"
                    } else {
                        "WARNING: logs (picked events) are not properly inserted in Room database!"
                    }

                }
            } else { "ERROR: Picking events (inserting logs) response is not successful! ${response.raw().message}" }
        } catch (e: Exception) { "EXCEPTION: during inserting picked events (logs): ${e.message}" }
    }

    suspend fun updateLastSeenTimestamp(updatePresenceDto: UpdatePresenceDto) {
        try {
            api.updateLastSeenTimestamp(updatePresenceDto).awaitResponse()
        } catch (e: Exception) {
            return
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

    suspend fun deleteEventsLog(idEvent: Int, idVolunteer: Int): String {
        val deletedRows = withContext(Dispatchers.IO) {
            mEventsLogDao.deleteEventsLog(idEvent, idVolunteer)
        }

        if(deletedRows == 1) {
            try {
                val response = api.deleteEventsLog(idEvent, idVolunteer).awaitResponse()
                return if(response.isSuccessful) response.body() as String else "ERROR: Event log is not deleted on server! ${response.errorBody()?.string()}"
            } catch(e: Exception) {
                return "EXCEPTION: During deleting event log on server: ${e.message}"
            }
        } else { return "ERROR: event log isn't deleted!" }
    }
}