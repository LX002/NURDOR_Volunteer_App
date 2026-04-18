package com.example.nurdor_volunteer_app_v3.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nurdor_volunteer_app_v3.model.Volunteer

@Dao
interface VolunteerDao {

    companion object {
        const val PRESENT_VOLUNTEERS_QUERY
            = "SELECT * FROM volunteer WHERE id IN (SELECT DISTINCT volunteer FROM events_log WHERE event = :idEvent AND isPresent = 1)"
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(volunteer: Volunteer): Long

    @Query("SELECT * FROM volunteer WHERE email = :email")
    fun findVolunteerByEmail(email: String): Volunteer?

    @Query("SELECT * FROM volunteer WHERE username = :username")
    fun findVolunteerByUsername(username: String): Volunteer?

    @Query("SELECT * FROM volunteer")
    fun findAll(): LiveData<List<Volunteer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceVolunteers(volunteersList: List<Volunteer>)

    @Query("SELECT * FROM volunteer WHERE id IN (SELECT volunteer FROM events_log WHERE event = :idEvent AND note = 'initLog' )")
    fun findEnrolledVolunteersByIdEvent(idEvent: Int): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY ORDER BY id")
    fun findPresentVolunteersByIdEvent(idEvent: Int): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY ORDER BY name")
    fun findPresentVolunteersByIdEventSortedByName(idEvent: Int): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY ORDER BY name DESC")
    fun findPresentVolunteersByIdEventSortedByNameDesc(idEvent: Int): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY ORDER BY surname")
    fun findPresentVolunteersByIdEventSortedBySurname(idEvent: Int): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY ORDER BY surname DESC")
    fun findPresentVolunteersByIdEventSortedBySurnameDesc(idEvent: Int): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(name) LIKE LOWER(:name) || '%' ORDER BY id")
    fun findPresentVolunteersByIdEventAndName(idEvent: Int, name: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(name) LIKE LOWER(:name) || '%' ORDER BY name")
    fun findPresentVolunteersByIdEventAndNameSortByName(idEvent: Int, name: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(name) LIKE LOWER(:name) || '%' ORDER BY name DESC")
    fun findPresentVolunteersByIdEventAndNameSortByNameDesc(idEvent: Int, name: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(name) LIKE LOWER(:name) || '%' ORDER BY surname")
    fun findPresentVolunteersByIdEventAndNameSortBySurname(idEvent: Int, name: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(name) LIKE LOWER(:name) || '%' ORDER BY surname DESC")
    fun findPresentVolunteersByIdEventAndNameSortBySurnameDesc(idEvent: Int, name: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(name) LIKE LOWER(:surname) || '%' ORDER BY id")
    fun findPresentVolunteersByIdEventAndSurname(idEvent: Int, surname: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(surname) LIKE LOWER(:surname) || '%' ORDER BY name")
    fun findPresentVolunteersByIdEventAndSurnameSortByName(idEvent: Int, surname: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(surname) LIKE LOWER(:surname) || '%' ORDER BY name DESC")
    fun findPresentVolunteersByIdEventAndSurnameSortByNameDesc(idEvent: Int, surname: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(surname) LIKE LOWER(:surname) || '%' ORDER BY surname")
    fun findPresentVolunteersByIdEventAndSurnameSortBySurname(idEvent: Int, surname: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(surname) LIKE LOWER(:surname) || '%' ORDER BY surname DESC")
    fun findPresentVolunteersByIdEventAndSurnameSortBySurnameDesc(idEvent: Int, surname: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND phoneNumber LIKE :phoneNumber || '%'")
    fun findPresentVolunteersByIdEventAndPhoneNumber(idEvent: Int, phoneNumber: String?): LiveData<List<Volunteer>>

    @Query("$PRESENT_VOLUNTEERS_QUERY AND LOWER(username) LIKE LOWER(:username) || '%'")
    fun findPresentVolunteersByIdEventAndUsername(idEvent: Int, username: String?): LiveData<List<Volunteer>>

    @Query("SELECT * FROM volunteer WHERE id = :idVolunteer")
    fun findVolunteerById(idVolunteer: Int): LiveData<Volunteer>
}