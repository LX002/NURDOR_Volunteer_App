package com.example.rma_nurdor_project_v2.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rma_nurdor_project_v2.model.Volunteer

@Dao
interface VolunteerDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertOrReplaceVolunteer(volunteer: Volunteer): Long

    @Query("SELECT * FROM volunteer WHERE email = :email")
    fun findVolunteerByEmail(email: String): Volunteer?

    @Query("SELECT * FROM volunteer WHERE username = :username")
    fun findVolunteerByUsername(username: String): Volunteer?

    @Query("SELECT * FROM volunteer")
    fun getVolunteers(): List<Volunteer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceVolunteers(volunteersList: List<Volunteer>)

    @Delete
    fun deleteVolunteers(volunteers: List<Volunteer>)

    @Query("SELECT * FROM volunteer WHERE id IN (SELECT DISTINCT volunteer FROM events_log WHERE event = :idEvent AND isPresent = 1) ORDER BY surname")
    fun getPresentVolunteers(idEvent: Int): List<Volunteer>

    @Query("SELECT * FROM volunteer WHERE id = :idVolunteer")
    fun getVolunteerById(idVolunteer: Int): Volunteer?

    @Query("SELECT * FROM volunteer WHERE id IN (SELECT DISTINCT volunteer FROM events_log WHERE event = :idEvent)")
    fun getVolunteersAtEvent(idEvent: Int): List<Volunteer>

}