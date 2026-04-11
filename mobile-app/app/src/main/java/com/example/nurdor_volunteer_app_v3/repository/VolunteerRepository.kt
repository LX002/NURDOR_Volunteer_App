package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.model.Volunteer
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.awaitResponse

class VolunteerRepository(db: AppDatabase) {

    private val api = RetrofitInstance.instance
    private val mVolunteerDao = db.volunteerDao()

    suspend fun fetchAll() {
        try {
            val response = api.fetchAllVolunteers().awaitResponse()
            if(response.isSuccessful) {
                Log.i("enrolled", "response body success")
                val volunteers = response.body()?.let { volunteersDtos ->
                    volunteersDtos.map { v -> Volunteer(
                        v.id,
                        v.name,
                        v.surname,
                        v.address,
                        v.phoneNumber,
                        v.email,
                        v.username,
                        v.profilePicture,
                        v.nearestCity,
                        v.volunteerRole
                    )}
                }

                val insertAsync = CoroutineScope(Dispatchers.IO).async {
                    Log.i("enrolled", "inserting enrolled vols")
                    volunteers?.let { mVolunteerDao.insertOrReplaceVolunteers(volunteers) }
                }

                insertAsync.await()
            } else {
                Log.i("retrofitApi1", "Fetching volunteers unsuccessful! Message: ${response.raw().message}")
            }
        } catch(e: Exception) {
            Log.i("retrofitApi1", "Exception during volunteers fetch: ${e.message}")
        }
    }

    fun findPresentVolunteersByIdEvent(idEvent: Int) =
        mVolunteerDao.findPresentVolunteersByIdEvent(idEvent)

    fun findPresentVolunteersByIdEventSortedByName(idEvent: Int) =
        mVolunteerDao.findPresentVolunteersByIdEventSortedByName(idEvent)

    fun findPresentVolunteersByIdEventSortedByNameDesc(idEvent: Int) =
        mVolunteerDao.findPresentVolunteersByIdEventSortedByNameDesc(idEvent)

    fun findPresentVolunteersByIdEventSortedBySurname(idEvent: Int) =
        mVolunteerDao.findPresentVolunteersByIdEventSortedBySurname(idEvent)

    fun findPresentVolunteersByIdEventSortedBySurnameDesc(idEvent: Int) =
        mVolunteerDao.findPresentVolunteersByIdEventSortedBySurnameDesc(idEvent)

    // find by name
    fun findPresentVolunteersByIdEventAndName(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndName(idEvent, value)

    fun findPresentVolunteersByIdEventAndNameSortByName(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndNameSortByName(idEvent, value)

    fun findPresentVolunteersByIdEventAndNameSortByNameDesc(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndNameSortByNameDesc(idEvent, value)
    fun findPresentVolunteersByIdEventAndNameSortBySurname(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndNameSortBySurname(idEvent, value)

    fun findPresentVolunteersByIdEventAndNameSortBySurnameDesc(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndNameSortBySurnameDesc(idEvent, value)

    // find by surname
    fun findPresentVolunteersByIdEventAndSurname(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndSurname(idEvent, value)
    fun findPresentVolunteersByIdEventAndSurnameSortByName(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndSurnameSortByName(idEvent, value)

    fun findPresentVolunteersByIdEventAndSurnameSortByNameDesc(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndSurnameSortByNameDesc(idEvent, value)

    fun findPresentVolunteersByIdEventAndSurnameSortBySurname(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndSurnameSortBySurname(idEvent, value)

    fun findPresentVolunteersByIdEventAndSurnameSortBySurnameDesc(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndSurnameSortBySurnameDesc(idEvent, value)

    // ...
    fun findPresentVolunteersByIdEventAndPhoneNumber(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndPhoneNumber(idEvent, value)

    fun findPresentVolunteersByIdEventAndUsername(idEvent: Int, value: String) =
        mVolunteerDao.findPresentVolunteersByIdEventAndUsername(idEvent, value)

    fun findEnrolledVolunteersByIdEvent(idEvent: Int) =
        mVolunteerDao.findEnrolledVolunteersByIdEvent(idEvent)
}