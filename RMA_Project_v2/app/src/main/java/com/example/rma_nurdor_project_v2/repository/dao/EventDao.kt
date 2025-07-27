package com.example.rma_nurdor_project_v2.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.rma_nurdor_project_v2.model.Event
import java.time.LocalDateTime

@Dao
interface EventDao {

    @Query("SELECT * FROM event")
    fun getEvents(): List<Event>

    @Query("SELECT * FROM event WHERE idEvent IN (:eventIds)")
    fun getEventsWithIds(eventIds: List<Int>): List<Event>

    //@Query("SELECT * FROM event WHERE idEvent IN (SELECT event FROM events_log WHERE volunteer = :idVolunteer)")
    @Query("SELECT * FROM event WHERE idEvent IN (SELECT DISTINCT event FROM events_log WHERE volunteer = :idVolunteer) AND dateTime(endTime) >= dateTime(:now) ORDER BY startTime")
    fun getEventsByVolunteerId(idVolunteer: Int, now: String): List<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvents(eventsLogs: List<Event>)

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

    @Query("SELECT cityName FROM city WHERE zipCode = (SELECT city FROM event WHERE idEvent = :idEvent)")
    fun getCityNameForEvent(idEvent: Int): String
}