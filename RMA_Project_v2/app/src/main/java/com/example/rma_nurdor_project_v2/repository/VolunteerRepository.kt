package com.example.rma_nurdor_project_v2.repository

import android.content.Context
import android.util.Log
import com.example.rma_nurdor_project_v2.AppDatabase
import com.example.rma_nurdor_project_v2.DatabaseClient
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_project_demo_v1.dto.VolunteerExpandedDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class VolunteerRepository(db: AppDatabase) {
    private val retrofitApi = RetrofitInstance.instance
    private val mVolunteerDao = db.volunteerDao()

    suspend fun insertOrReplaceVolunteer(volunteer: Volunteer): Long {
        //insertovanje / update u sqlite bazu
        val id = withContext(Dispatchers.IO) {
            fetchVolunteers() // prvo fetch ukoliko se neko sign in-ovao pre volontera sa istim podacima...
            if(mVolunteerDao.findVolunteerByUsername(volunteer.username) != null) {
                Log.i("insertVolunteer", "Volunteer username conflict")
                return@withContext -1
            }
            if(mVolunteerDao.findVolunteerByEmail(volunteer.email) != null) {
                Log.i("insertVolunteer", "Volunteer email conflict")
                return@withContext -2
            }
            mVolunteerDao.insertOrReplaceVolunteer(volunteer)
        }

        //insertovanje u mysql bazu, retrofit (kasnije update deo)
        if(id > 0) {
            Log.i("insertVolunteer", "Volunteer $id saved in sqlite!")
            retrofitApi.saveVolunteer(VolunteerExpandedDto(
                id.toInt(), volunteer.name, volunteer.surname,
                volunteer.address, volunteer.phoneNumber,
                volunteer.email, volunteer.username,
                volunteer.password, volunteer.profilePicture.toString(),
                volunteer.nearestCity, volunteer.volunteerRole
            )).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.isSuccessful && response.body() != null) {
                        val isSaved = !response.body()!!  //ako je sacuvan response body je false!!!
                        if(isSaved) {
                            Log.i("insertVolunteer", "Volunteer $id saved in mysql")
                        } else {
                            Log.i("insertVolunteer", "Volunteer $id not saved in mysql")
                        }
                    } else {
                        Log.i("insertVolunteer", "Volunteer $id not saved in mysql and response is not successful")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.i("insertVolunteer", "Volunteer $id saving failure -> ${t.stackTrace}")
                }

            })
        }
        return id
    }

    suspend fun getVolunteerByEmail(email: String): Volunteer? {
        return withContext(Dispatchers.IO) {
            mVolunteerDao.findVolunteerByUsername(email)
        }
    }

    suspend fun getVolunteerByUsername(username: String): Volunteer? {
        return withContext(Dispatchers.IO) {
            mVolunteerDao.findVolunteerByUsername(username)
        }
    }

    suspend fun getVolunteers(): List<Volunteer> {
        withContext(Dispatchers.IO) { fetchVolunteers() }
        Log.i("after_inserting1", "returning volunteers ${mVolunteerDao.getVolunteers()}")
        val volunteers = mVolunteerDao.getVolunteers()
        Log.i("actualReturn", "actual return of volunteers: $volunteers")
        return volunteers
    }

    private fun getRetrofitVolunteers() {
        retrofitApi.getVolunteers().enqueue(object : Callback<List<VolunteerExpandedDto>> {
            override fun onResponse(call: Call<List<VolunteerExpandedDto>>, response: Response<List<VolunteerExpandedDto>>) {
                if(response.isSuccessful) {
                    val volunteersList = response.body()!!.map {
                        Volunteer(it.id, it.name, it.surname,
                                  it.address, it.phoneNumber, it.email,
                                  it.username, it.password, it.profilePicture, it.nearestCity!!, it.volunteerRole!!)
                    }
                    Log.i("retrofitApi1", "Volunteer dto list fetched!")
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.i("inserting1", "Inserting data in local database...")
                        Log.i("insertingList", "$volunteersList")
                        mVolunteerDao.insertOrReplaceVolunteers(volunteersList)
                        if(volunteersList.size < mVolunteerDao.getVolunteers().size) {
                            Log.i("delete", "Deleting volunteers in local database...")
                            mVolunteerDao.deleteVolunteers(mVolunteerDao.getVolunteers().filter { it !in volunteersList })
                        }
                    }
                } else {
                    Log.i("retrofitApi1", "Volunteer dto list is empty!")
                }
            }

            override fun onFailure(call: Call<List<VolunteerExpandedDto>>, t: Throwable) {
                Log.e("retrofitApi1", "Volunteer list is empty and error occurred: ${t.message}\n${t.stackTrace}")
            }

        })
    }

    private suspend fun fetchVolunteers() {
        try {
            val response = retrofitApi.getVolunteers().awaitResponse()
            if (response.isSuccessful) {
                val volunteersList = response.body()!!.map {
                    Volunteer(it.id, it.name, it.surname,
                        it.address, it.phoneNumber, it.email,
                        it.username, it.password, it.profilePicture, it.nearestCity!!, it.volunteerRole!!)
                }
                Log.i("retrofitApi1", "Volunteer dto list fetched!")
                CoroutineScope(Dispatchers.IO).launch {
                    Log.i("inserting1", "Inserting data in local database...")
                    Log.i("insertingList", "$volunteersList")
                    mVolunteerDao.insertOrReplaceVolunteers(volunteersList)
                    if(volunteersList.size < mVolunteerDao.getVolunteers().size) {
                        Log.i("delete", "Deleting volunteers in local database...")
                        mVolunteerDao.deleteVolunteers(mVolunteerDao.getVolunteers().filter { it !in volunteersList })
                    }
                }
            } else {
                Log.e("retrofitApi1", "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("retrofitApi1", "Network error: ${e.message}")
        }
    }

    fun getPresentVolunteers(idEvent: Int): List<Volunteer> {
        return mVolunteerDao.getPresentVolunteers(idEvent)
    }

    fun getVolunteerById(idVolunteer: Int): Volunteer? {
        return mVolunteerDao.getVolunteerById(idVolunteer)
    }

    suspend fun getVolunteersAtEvent(idEvent: Int): List<Volunteer> {
        return withContext(Dispatchers.IO) {
            mVolunteerDao.getVolunteersAtEvent(idEvent)
        }
    }
}