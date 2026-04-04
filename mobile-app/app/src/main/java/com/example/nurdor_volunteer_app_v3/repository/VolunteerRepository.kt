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

    suspend fun fetchVolunteers() {
        try {
            val response = api.fetchAllVolunteers().awaitResponse()
            if(response.isSuccessful) {
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

    fun findEnrolledVolunteersByIdEvent(idEvent: Int) =
        mVolunteerDao.findEnrolledVolunteersByIdEvent(idEvent)
}