package com.example.nurdor_volunteer_app_v3.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.nurdor_volunteer_app_v3.model.Event
import java.time.LocalDateTime

@Dao
interface EventDao {

    companion object {
        const val FIND_UNPICKED_UPCOMING_EVENTS_BY_VOLUNTEER_ID = "SELECT * FROM event WHERE event.idEvent NOT IN(SELECT DISTINCT events_log.event FROM events_log WHERE events_log.volunteer = :idVolunteer)"
        const val FIND_UPCOMING_EVENTS_BY_CITY_NAME = "$FIND_UNPICKED_UPCOMING_EVENTS_BY_VOLUNTEER_ID AND event.city = (SELECT city.zipCode FROM city WHERE city.cityName = :cityName)"
        const val FIND_UPCOMING_EVENTS_BY_EVENT_NAME = "$FIND_UNPICKED_UPCOMING_EVENTS_BY_VOLUNTEER_ID AND event.eventName = :eventName"
        const val FIND_UPCOMING_EVENTS_BY_EVENT_NAME_WITH_JOIN = "SELECT event.* FROM event JOIN city ON event.city = city.zipCode WHERE event.idEvent NOT IN(SELECT DISTINCT events_log.event FROM events_log WHERE events_log.volunteer = :idVolunteer) AND event.eventName = :eventName"
    }
    @Query("SELECT * FROM event")
    fun findAll(): LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE idEvent = :idEvent")
    fun findById(idEvent: Int): Event

    @Query("SELECT * FROM event WHERE idEvent IN (SELECT event FROM events_log WHERE volunteer = :volunteerId) AND endTime >= :now")
    fun findUpcomingEventsByVolunteerId(volunteerId: Int, now: LocalDateTime): LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE endTime >= :now")
    fun findUpcomingEvents(now: LocalDateTime): LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE isStarted = 1")
    fun findRunningEvents(): LiveData<List<Event>>

    @Query("UPDATE event SET isStarted = :isStarted, totalDonations = :totalDonations WHERE idEvent = :idEvent")
    fun startOrEndEventByIdEvent(idEvent: Int, isStarted: Byte, totalDonations: Long): Int

    //@Query("SELECT * FROM event WHERE idEvent IN (SELECT event FROM events_log WHERE volunteer = :idVolunteer)")
    @Query("SELECT * FROM event WHERE idEvent IN (SELECT DISTINCT event FROM events_log WHERE volunteer = :idVolunteer) AND dateTime(endTime) >= dateTime(:now) ORDER BY startTime")
    fun getEventsByVolunteerId(idVolunteer: Int, now: String): List<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvents(eventsLogs: List<Event>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: Event)

    @Delete
    fun deleteEvents(eventsLogs: List<Event>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceEvent(event: Event): Long

    @Query("SELECT * FROM event WHERE eventName = :eventName AND startTime = :startTime")
    fun getEventByNameAndStartTime(eventName: String, startTime: String): Event?

    @Query("SELECT * FROM event WHERE city = :zipCode AND idEvent NOT IN (SELECT event FROM events_log WHERE volunteer = :idVolunteer) AND dateTime(endTime) >= dateTime(:now)")
    fun getNearestEvents(zipCode: String, idVolunteer: Int, now: String): List<Event>

    @Query("SELECT * FROM event WHERE city != :zipCode AND idEvent NOT IN (SELECT event FROM events_log WHERE volunteer = :idVolunteer) AND dateTime(endTime) >= dateTime(:now)")
    fun getOtherEvents(zipCode: String, idVolunteer: Int, now: String): List<Event>

    @RawQuery
    fun fetchSearchResults(query: SupportSQLiteQuery): List<Event>

    @Query("SELECT * FROM event WHERE dateTime(endTime) < dateTime(:now)")
    fun getArchivedEvents(now: String): List<Event>

    @RawQuery
    fun fetchArchiveSearchResults(query: SupportSQLiteQuery): List<Event>

    // use this?
    @Query("SELECT cityName FROM city WHERE zipCode = (SELECT city FROM event WHERE idEvent = :idEvent)")
    fun getCityNameForEvent(idEvent: Int): String

    // new for event search n stuff // find by city
    
    @Query("$FIND_UPCOMING_EVENTS_BY_CITY_NAME ORDER BY event.startTime")
    fun findUpcomingEventsByCityNameSortedByStartTime(idVolunteer: Int, cityName: String): LiveData<List<Event>>

    @Query("$FIND_UPCOMING_EVENTS_BY_CITY_NAME ORDER BY event.startTime DESC")
    fun findUpcomingEventsByCityNameSortedByStartTimeDesc(idVolunteer: Int, cityName: String): LiveData<List<Event>>

    @Query("$FIND_UPCOMING_EVENTS_BY_CITY_NAME ORDER BY event.eventName")
    fun findUpcomingEventsByCityNameSortedByEventName(idVolunteer: Int, cityName: String): LiveData<List<Event>>

    @Query("$FIND_UPCOMING_EVENTS_BY_CITY_NAME ORDER BY event.eventName DESC")
    fun findUpcomingEventsByCityNameSortedByEventNameDesc(idVolunteer: Int, cityName: String): LiveData<List<Event>>

    //find by event name

    @Query("$FIND_UPCOMING_EVENTS_BY_EVENT_NAME ORDER BY event.startTime")
    fun findUpcomingEventsByEventNameSortedByStartTime(idVolunteer: Int, eventName: String): LiveData<List<Event>>

    @Query("$FIND_UPCOMING_EVENTS_BY_EVENT_NAME ORDER BY event.startTime DESC")
    fun findUpcomingEventsByEventNameSortedByStartTimeDesc(idVolunteer: Int, eventName: String): LiveData<List<Event>>

    @Query("$FIND_UPCOMING_EVENTS_BY_EVENT_NAME_WITH_JOIN ORDER BY city.cityName")
    fun findUpcomingEventsByEventNameSortedByCityName(idVolunteer: Int, eventName: String): LiveData<List<Event>>

    @Query("$FIND_UPCOMING_EVENTS_BY_EVENT_NAME_WITH_JOIN ORDER BY city.cityName DESC")
    fun findUpcomingEventsByEventNameSortedByCityNameDesc(idVolunteer: Int, eventName: String): LiveData<List<Event>>
}