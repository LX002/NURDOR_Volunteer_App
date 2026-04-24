package com.example.nurdor_volunteer_app_v3.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nurdor_volunteer_app_v3.model.EventsLog

@Dao
interface EventsLogDao {

    @Query("SELECT * FROM events_log")
    fun findAll(): LiveData<List<EventsLog>>

    @Query("SELECT * FROM events_log WHERE volunteer = :idVolunteer AND event = :idEvent")
    fun findByIdVolunteerAndIdEvent(idVolunteer: Int, idEvent: Int): EventsLog?

    @Query("UPDATE events_log SET isPresent = :isPresent WHERE event = :idEvent")
    fun updateIsPresentByIdEvent(isPresent: Byte, idEvent: Int): Int

    @Query("UPDATE events_log SET isPresent = :isPresent WHERE event = :idEvent AND volunteer = :idVolunteer")
    fun updateIsPresentByEventIdAndVolunteerId(isPresent: Byte, idEvent: Int, idVolunteer: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEventsLogs(eventsLogs: List<EventsLog>): List<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertEventsLogsWithAbort(eventsLogs: List<EventsLog>): List<Long>

    @Query("DELETE FROM events_log WHERE volunteer = :idVolunteer AND event = :idEvent")
    fun deleteEventsLog(idEvent: Int, idVolunteer: Int): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertLog(log: EventsLog): Long

    @Query("SELECT * FROM events_log WHERE volunteer = :idVolunteer AND event = :idEvent AND note = 'initLog'")
    fun getInitLog(idVolunteer: Int, idEvent: Int): EventsLog?

    @Update
    fun updateInitLog(eventsLog: EventsLog): Int

}