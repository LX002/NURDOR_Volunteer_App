package com.example.nurdor_volunteer_app_v3.repository

import android.util.Log
import com.example.nurdor_volunteer_app_v3.dto.standDto.DonationDto
import com.example.nurdor_volunteer_app_v3.model.Stand
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
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
                insertAsync.await()
            } else {
                Log.i("retrofitApi1", "Error during fetching of stands: ${response.raw().message}")
            }
        } catch (e: Exception) {
            Log.i("retrofitApi1", "Exception during fetching of stands: ${e.message}")
        }
    }

    suspend fun fetchDonationResponse(donationDto: DonationDto): String {
        try {
            val response = api.fetchDonationResponse(donationDto).awaitResponse()
            return if(response.isSuccessful) {
                Log.i("currentDonations", "response success: $response")
                response.body() ?: "ERROR:Donation response is null!"
            } else {
                Log.i("currentDonations", "response failure else branch: $response")
                "ERROR:${response.raw().message}"
            }
        } catch (e: Exception) {
            Log.i("currentDonations", "response failure ex catch")
            return "ERROR:Exception during sending donation:\nmessage: ${e.message}\ncause: ${e.cause?.message}\nstackTrace:${e.stackTrace.toString()}"
        }
    }

    fun findAll() = mStandDao.findAll()

    fun findByIdEvent(idEvent: Int) = mStandDao.findByIdEvent(idEvent)
    suspend fun updateIdEventByStandIds(ids: List<Int>, idEvent: Int?): Int {
        return withContext(Dispatchers.IO) {
            mStandDao.updateIdEventByStandIds(idEvent, ids)
        }
    }

    suspend fun updateTotalDonations(donationDto: DonationDto): Int {
        return withContext(Dispatchers.IO) {
            var currentDonations = mStandDao.findById(donationDto.idStand).totalDonations
            Log.i("currentDonations", "current $: $currentDonations")
            val num = mStandDao.updateTotalDonations(currentDonations + donationDto.amount, donationDto.idStand)
            currentDonations = mStandDao.findById(donationDto.idStand).totalDonations
            Log.i("currentDonations", "current $: $currentDonations")
            num
        }
    }
}