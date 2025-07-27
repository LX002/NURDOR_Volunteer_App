package com.example.rma_nurdor_project_v2.repository

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.rma_nurdor_project_v2.AppDatabase
import com.example.rma_nurdor_project_v2.dto.EventDto
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventRepository(db: AppDatabase) {

    private val retrofitApi = RetrofitInstance.instance
    private val eventsLogRepository = EventsLogRepository(db)
    private val mEventDao = db.eventDao()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    suspend fun getEvents(): List<Event> {
        return withContext(Dispatchers.IO) {
            fetchEvents()
            Log.i("after_inserting1", "returning mAllEvents ${mEventDao.getEvents()}")
            val eventsList = mEventDao.getEvents()
            Log.i("actualReturn", "actual return of events: $eventsList")
            eventsList
        }
    }

    suspend fun getLoadedEvents(): MutableList<Event> = withContext(Dispatchers.IO) { mEventDao.getEvents() as MutableList<Event> }

    suspend fun getEventsWithIds(eventIds: List<Int>): List<Event> {
        withContext(Dispatchers.IO) {
            fetchEvents()
        }
        return mEventDao.getEventsWithIds(eventIds)
    }

    suspend fun getEventsByVolunteerId(idVolunteer: Int): List<Event> {
        return withContext(Dispatchers.IO) {
            fetchEvents()
            eventsLogRepository.fetchEventsLogs()
            // ova dva prethodno moraju jer live data dobija podatke... ?
            mEventDao.getEventsByVolunteerId(idVolunteer, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        }
    }

    suspend fun getLoadedEventsByVolunteerId(idVolunteer: Int): List<Event> {
        return withContext(Dispatchers.IO) {
            mEventDao.getEventsByVolunteerId(idVolunteer, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        }
    }

    suspend fun getNearestEvents(zipCode: String, idVolunteer: Int): List<Event> {
        return withContext(Dispatchers.IO) { mEventDao.getNearestEvents(zipCode, idVolunteer, LocalDateTime.now().format(formatter)) }
    }

    suspend fun getOtherEvents(zipCode: String, idVolunteer: Int): List<Event> {
        return withContext(Dispatchers.IO) { mEventDao.getOtherEvents(zipCode, idVolunteer, LocalDateTime.now().format(formatter)) }
    }

    private fun getRetrofitEvents() {
        retrofitApi.getEvents().enqueue(object: Callback<List<EventDto>> {
            override fun onResponse(call: Call<List<EventDto>>, response: Response<List<EventDto>>) {
                if(response.isSuccessful && response.body() != null) {
                    val eventEntities = response.body()!!.map {eventDto ->
                        Event(eventDto.id, eventDto.eventName,
                              eventDto.description, eventDto.startTime,
                              eventDto.endTime, eventDto.latitude,
                              eventDto.longitude, eventDto.eventImg,
                              eventDto.locationDesc, eventDto.city)
                    }
                    Log.i("retrofitApi1", "Events dto list fetched!")
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.i("inserting1", "Inserting data in local database...")
                        Log.i("insertingList", "$eventEntities")
                        mEventDao.insertEvents(eventEntities)
                        if(eventEntities.size < mEventDao.getEvents().size) {
                            mEventDao.deleteEvents(mEventDao.getEvents().filter { it !in eventEntities })
                        }
                    }
                } else {
                    Log.i("retrofiApi1", "Events dto list is empty....")
                }
            }

            override fun onFailure(call: Call<List<EventDto>>, t: Throwable) {
                Log.e("retrofitApi1", "Events list is empty and error occurred: ${t.message}\n${t.stackTrace}")
            }
        })
    }

    private suspend fun fetchEvents() {
        try {
            val response = retrofitApi.getEvents().awaitResponse()
            if (response.isSuccessful) {
                var img: ByteArray? = null
                val eventEntities = response.body()!!.map {eventDto ->
                    //img = Base64.decode(eventDto.eventImg, Base64.DEFAULT)
                    Event(eventDto.id, eventDto.eventName,
                        eventDto.description, eventDto.startTime,
                        eventDto.endTime, eventDto.latitude,
                        eventDto.longitude, eventDto.eventImg,
                        eventDto.locationDesc, eventDto.city)
                }
                Log.i("retrofitApi1", "Events dto list fetched!")
                //Log.i("retrofitApi1", "Events dto conversion of image: ${img?.size}")
                CoroutineScope(Dispatchers.IO).launch {
                    Log.i("inserting1", "Inserting data in local database...")
                    Log.i("insertingList", "$eventEntities")
                    mEventDao.insertEvents(eventEntities)
                    if(eventEntities.size < mEventDao.getEvents().size) {
                        mEventDao.deleteEvents(mEventDao.getEvents().filter { it !in eventEntities })
                    }
                }
            } else {
                Log.e("retrofitApi1", "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("retrofitApi1", "Network error: ${e.message}")
        }
    }

    suspend fun insertOrReplaceEvent(event: Event): Long {
        val id = withContext(Dispatchers.IO) {
            if(mEventDao.getEventByNameAndStartTime(event.eventName, event.startTime) != null)
                return@withContext -1

            mEventDao.insertOrReplaceEvent(event)
        }

        if(id > 0) {
            //val encodedImg = Base64.encodeToString(event.eventImg, Base64.DEFAULT).replace("\n", "")
            retrofitApi.insertEvent(
                EventDto(
                id.toInt(), event.eventName,
                event.description, event.startTime,
                event.endTime, event.latitude,
                event.longitude, event.eventImg,
                event.locationDesc, event.city
                )
            ).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.isSuccessful && response.body() != null) {
                        val isSaved = !response.body()!!  //ako je sacuvan response body je false!!!
                        if(isSaved) {
                            Log.i("insertEvent", "Event $id saved in mysql")
                        } else {
                            Log.i("insertEvent", "Event $id not saved in mysql")
                        }
                    } else {
                        Log.i("insertEvent", "Event $id not saved in mysql and response is not successful")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.i("insertEvent", "Event $id saving failure -> ${t.stackTrace}")
                }
            })
        }
        return id
    }

    suspend fun fetchSearchResults(eventName: String?, zipCode: String?, startDateTime: LocalDateTime?, sortBy: Int, idVolunteer: Int): List<Event> {
        Log.i("fetchSearchResultsDebug", "$eventName, $zipCode, $startDateTime, $sortBy, idVol: $idVolunteer")
        if(eventName.isNullOrBlank() && zipCode.isNullOrBlank() && startDateTime == null && sortBy == 0)
            throw IllegalArgumentException("All search filters are blank!")

        val stringDateTime = startDateTime?.format(formatter)
        var queryString: String = ""
        val conditions = mutableListOf<String>()
        conditions.add("idEvent NOT IN (SELECT event FROM events_log WHERE volunteer = $idVolunteer)")
        conditions.add("dateTime(endTime) >= dateTime('${LocalDateTime.now().format(formatter)}')")

        if(!eventName.isNullOrBlank()) conditions.add("eventName = '$eventName'")
        if(!zipCode.isNullOrBlank() && zipCode != "0") conditions.add("city = '$zipCode'")
        if(!stringDateTime.isNullOrBlank()) conditions.add("dateTime(startTime) = dateTime('$stringDateTime')")

        val join = if(sortBy == 2) "JOIN city ON event.city = city.zipCode " else ""

        queryString = "SELECT * FROM event ${join}WHERE " + conditions.joinToString(" AND ")
        queryString += when(sortBy) {
            1 -> " ORDER BY eventName"
            2 -> " ORDER BY city.cityName"
            3 -> " ORDER BY startTime"
            else -> ""
        }
        Log.i("debugAddEventActivity", queryString)

        return withContext(Dispatchers.IO) {
            mEventDao.fetchSearchResults(SimpleSQLiteQuery(queryString))
        }
    }

    suspend fun getArchivedEvents(): List<Event> {
        return withContext(Dispatchers.IO) {
            mEventDao.getArchivedEvents(LocalDateTime.now().format(formatter))
        }
    }

    suspend fun fetchArchiveSearchResults(eventName: String, year: Int, month: Int, zipCode: String, sortBy: Int): List<Event> {
        if(eventName.isBlank() && year < 2003 && month == 0 && zipCode.isBlank() && sortBy == 0) {
            throw IllegalArgumentException("All filter fields are blank!")
        }

        val conditions = mutableListOf<String>()
        val now = LocalDateTime.now().format(formatter)
        val orderBy = when(sortBy) {
            1 -> " ORDER BY eventName"
            2 -> " ORDER BY city.cityName" // ukoliko se popravi coroutine / nekako drugacije dobavljanje imena grada ubaci join i city.cityName u query (vazi i za searc u add activity)
            3 -> " ORDER BY startTime"
            else -> ""
        }
        conditions.add("dateTime(endTime) < dateTime('$now')")

        if(eventName.isNotBlank()) { conditions.add("eventName = '$eventName'") }

        if(year >= 2003 && month > 0) {
            Log.i("queryEventArchive", "$month $year usao u if")
            val longerMonths = listOf(1, 3, 5, 7, 8, 10, 12)
            val shorterMonths = listOf(4, 6, 9, 11)
            val february = 2
            val intervalStart = LocalDateTime.of(year, month, 1, 0,0)
            var intervalEnd = ""

            when {
                longerMonths.contains(month) -> {
                    intervalEnd = LocalDateTime.of(year, month, 31, 23, 59).format(formatter)
                    conditions.add("dateTime(startTime) >= dateTime('$intervalStart')")
                    conditions.add("dateTime(startTime) <= dateTime('$intervalEnd')")
                }
                shorterMonths.contains(month) -> {
                    intervalEnd = LocalDateTime.of(year, month, 30, 23, 59).format(formatter)
                    conditions.add("dateTime(startTime) >= dateTime('$intervalStart')")
                    conditions.add("dateTime(startTime) <= dateTime('$intervalEnd')")
                }
                month == february -> {
                    intervalEnd = if(year % 4 == 0) {
                        LocalDateTime.of(year, month, 29, 23, 59).format(formatter)
                    } else {
                        LocalDateTime.of(year, month, 28, 23, 59).format(formatter)
                    }
                    conditions.add("dateTime(startTime) >= dateTime('$intervalStart')")
                    conditions.add("dateTime(startTime) <= dateTime('$intervalEnd')")
                }
                else -> throw IllegalArgumentException("Month value is not valid, it must be between 1 and 12 (both inclusive)")
            }
        }

        if(zipCode.isNotBlank()) { conditions.add("city = '$zipCode'") }

        val join = if(sortBy == 2) "JOIN city ON event.city = city.zipCode " else ""

        //napravi varijantu upita kada je selektovan sort grada sa join
        val query = SimpleSQLiteQuery("SELECT * FROM event ${join}WHERE ${conditions.joinToString(" AND ")} $orderBy")
        Log.i("queryEventArchive", "SELECT * FROM event ${join}WHERE ${conditions.joinToString(" AND ")} $orderBy")
        return withContext(Dispatchers.IO) { mEventDao.fetchArchiveSearchResults(query) }
    }

    suspend fun getCityName(e: Event): String {
        return withContext(Dispatchers.IO) {
            mEventDao.getCityNameForEvent(e.idEvent!!)
        }
    }
}