package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import android.widget.Toast
import com.example.nurdor_volunteer_app_v3.model.Stand
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.awaitResponse

class StandRepository(db: AppDatabase) {

    private val api = RetrofitInstance.instance
    private val mStandDao = db.standDao()

    suspend fun fetchAllStands() {
        try {
            val response = api.fetchAllStands().awaitResponse()
            if(response.isSuccessful) {
                val stands = response.body()?.let { standDtos ->
                    standDtos.map { s -> Stand(s.id, s.standName, s.donations, s.idEvent) }
                }
                val insertAsync = CoroutineScope(Dispatchers.IO).async {
                    stands?.let { mStandDao.insertOrReplaceStands(stands) }
                }
            } else {
                Log.i("retrofitApi1", "Error during fetching of stands: ${response.raw().message}")
            }
        } catch (e: Exception) {
            Log.i("retrofitApi1", "Exception during fetching of stands: ${e.message}")
        }
    }

    fun findAll() = mStandDao.findAll()

    fun findByIdEvent(idEvent: Int) = mStandDao.findByIdEvent(idEvent)
    suspend fun updateIdEventByStandIds(ids: List<Int>, idEvent: Int?): Int {
        return mStandDao.updateIdEventByStandIds(idEvent, ids)
    }
}