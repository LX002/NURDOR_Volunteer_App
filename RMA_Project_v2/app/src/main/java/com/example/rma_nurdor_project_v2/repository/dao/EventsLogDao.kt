package com.example.rma_nurdor_project_v2.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rma_nurdor_project_v2.model.EventsLog

@Dao
interface EventsLogDao {

    @Query("SELECT * FROM events_log")
    fun getEventsLogs(): List<EventsLog>

    //@Query("SELECT * FROM events_log WHERE idEventsLog IN (SELECT MIN(idEventsLog) FROM events_log WHERE volunteer = :idVolunteer GROUP BY volunteer, event)")
    @Query("SELECT * FROM events_log WHERE volunteer = :idVolunteer")
    fun getEventsLogsByVolunteerId(idVolunteer: Int) : List<EventsLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEventsLogs(eventsLogs: List<EventsLog>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertEventsLogsWithAbort(eventsLogs: List<EventsLog>): List<Long>

    @Delete
    fun deleteEventsLogs(eventsLogs: List<EventsLog>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertLog(log: EventsLog): Long

    @Query("SELECT * FROM events_log WHERE volunteer = :idVolunteer AND event = :idEvent AND note = 'initLog'")
    fun getInitLog(idVolunteer: Int, idEvent: Int): EventsLog?

    @Update
    fun updateInitLog(eventsLog: EventsLog): Int

}