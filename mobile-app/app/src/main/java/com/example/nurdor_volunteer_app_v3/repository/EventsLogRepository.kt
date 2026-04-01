package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.dto.EventsLogDto
import com.example.nurdor_volunteer_app_v3.model.EventsLog
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
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
                    mEventsLogDao.insertEventsLogs(eventsLogs)
                }
            } else {
                // create dialog that displays this
                Log.e("retrofitApi1", "Error during events log fetching: ${response.raw().message}")
            }
        } catch (e: Exception) {
            Log.e("retrofitApi1", "Exception during events log fetching: ${e.message}")
        }
    }

    suspend fun findAll() = withContext(Dispatchers.IO) { mEventsLogDao.findAll() }
}