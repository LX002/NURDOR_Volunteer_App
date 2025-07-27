package com.example.rma_nurdor_project_v2.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.rma_nurdor_project_v2.AppDatabase
import com.example.rma_nurdor_project_v2.dto.EventsLogDto
import com.example.rma_nurdor_project_v2.model.EventsLog
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class EventsLogRepository(db: AppDatabase) {

    private val retrofitApi = RetrofitInstance.instance
    private val mEventsLogDao = db.eventsLogDao()

    suspend fun getEventsLogs(): List<EventsLog> {
        return withContext(Dispatchers.IO) {
            fetchEventsLogs()
            Log.i("after_inserting1", "returning mAllEventsLogs ${mEventsLogDao.getEventsLogs()}")
            val eventsLogsList = mEventsLogDao.getEventsLogs()
            Log.i("actualReturn", "actual return of event logs: $eventsLogsList")
            eventsLogsList
        }
    }

    suspend fun getEventsLogsByVolunteerId(idVolunteer: Int): List<EventsLog> {
        withContext(Dispatchers.IO) { fetchEventsLogs() }
        return mEventsLogDao.getEventsLogsByVolunteerId(idVolunteer)
    }

    suspend fun fetchEventsLogs() {
        try {
            val response = retrofitApi.getEventsLogs().awaitResponse()
            if (response.isSuccessful) {
                val eventsLogEntities = response.body()!!.map { eventsLogDto ->
                    EventsLog(eventsLogDto.id, eventsLogDto.volunteer, eventsLogDto.event, eventsLogDto.isPresent, eventsLogDto.note)
                }
                Log.i("retrofitApi1", "Event logs dto list fetched!")
                CoroutineScope(Dispatchers.IO).launch {
                    Log.i("inserting1", "Inserting data in local database...")
                    Log.i("insertingList", "$eventsLogEntities")
                    mEventsLogDao.insertEventsLogs(eventsLogEntities)
                    if(eventsLogEntities.size < mEventsLogDao.getEventsLogs().size) {
                        mEventsLogDao.deleteEventsLogs(mEventsLogDao.getEventsLogs().filter { it !in eventsLogEntities })
                    }
                }
            } else {
                Log.e("retrofitApi1", "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("retrofitApi1", "Network error: ${e.message}")
        }
    }

    suspend fun insertInitLogs(initLogs: List<EventsLog>): Int {
        val logsIds = mutableListOf<Long>()
        val initLogsDtos = mutableListOf<EventsLogDto>()
        val result = withContext(Dispatchers.IO) {
            try {
                logsIds.addAll(mEventsLogDao.insertEventsLogsWithAbort(initLogs))
                0
            } catch (e: SQLiteConstraintException) { -1 }
        }

        if(result == 0 && logsIds.isNotEmpty()) {
            var i = 0
            initLogs.forEach { log ->
                val logDto = EventsLogDto(logsIds[i].toInt(), log.volunteer, log.event, log.isPresent, log.note)
                initLogsDtos.add(logDto)
                i++
            }
            retrofitApi.insertInitLogs(initLogsDtos).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.isSuccessful && response.body() != null) {
                        val areSaved = response.body()!!
                        if(areSaved) {
                            Log.i("initLogsInsertion", "Saved selected initLogs to mysql database!")
                        } else {
                            Log.i("initLogsInsertion", "Saving selected initLogs body returned false!")
                        }
                    } else {
                        Log.i("initLogsInsertion", "Response is null / not successful!")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.i("initLogsInsertion", "Saving initLogs failed! Error message: ${t.stackTrace}")
                }
            })
        }

        return result
    }

    suspend fun insertLog(log: EventsLog): Long {
        val id = withContext(Dispatchers.IO) {
            try {
                mEventsLogDao.insertLog(log)
            } catch (e: SQLiteConstraintException) {
                -1
            } catch (e: Exception) {
                -2
            }
        }

        if(id > 0) {
            retrofitApi.insertLog(EventsLogDto(
                id.toInt(), log.volunteer, log.event, log.isPresent, log.note
            )).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.isSuccessful && response.body() != null) {
                        val isSaved = !response.body()!!  //ako je sacuvan response body je false!!!
                        if(isSaved) {
                            Log.i("insertEventsLog", "eLog $id saved in mysql")
                        } else {
                            Log.i("insertEventsLog", "eLog $id not saved in mysql")
                        }
                    } else {
                        Log.i("insertEventsLog", "eLog $id not saved in mysql and response is not successful")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.i("insertEventsLog", "eLog $id saving failure -> ${t.stackTrace}")
                }
            })
        }
        return id
    }

    suspend fun markAsPresent(idVolunteer: Int, idEvent: Int, isPresent: Byte): Int {
        val pair = withContext(Dispatchers.IO) {
            val log = mEventsLogDao.getInitLog(idVolunteer, idEvent)
            log?.isPresent = isPresent
            if(log != null) Pair(mEventsLogDao.updateInitLog(log), log) else Pair(0, null)
        }

        val log = pair.second
        val rowsUpdated = pair.first

        if(rowsUpdated == 1) {
            retrofitApi.markAsPresent(
                EventsLogDto(log?.idEventsLog!!, log.volunteer, log.event, isPresent, log.note)
            ).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.isSuccessful && response.body() != null) {
                        val isUpdated = !response.body()!!  //ako je sacuvan response body je false!!!
                        if(isUpdated) {
                            Log.i("markAsPresent", "volunteer ${log.volunteer} event ${log.event} isPresent ${log.isPresent} marked successfully")
                        } else {
                            Log.i("markAsPresent", "volunteer ${log.volunteer} event ${log.event} isPresent ${log.isPresent} not marked (null)")
                        }
                    } else {
                        Log.i("markAsPresent", "marking response not successful and response body is null!")
                    }
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.i("markAsPresent", "marking failure -> ${t.stackTrace}")
                }
            })
        }
        
        return rowsUpdated
    }
}