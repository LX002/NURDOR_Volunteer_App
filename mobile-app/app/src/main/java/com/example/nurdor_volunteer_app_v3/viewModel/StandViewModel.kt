package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.dto.standDto.DonationDto
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.StandRepository

class StandViewModel(application: Application): AndroidViewModel(application) {

    private val standRepository =
        StandRepository(DatabaseClient.getInstance(application).appDatabase)

    val allStands = standRepository.findAll()

    fun findByIdEvent(idEvent: Int) =
        standRepository.findByIdEvent(idEvent)

    suspend fun fetchAll(): String {
        return standRepository.fetchAllStands()
    }

    suspend fun fetchDonationResponse(donationDto: DonationDto): String {
        return standRepository.fetchDonationResponse(donationDto)
    }

    suspend fun updateIdEventByStandIds(ids: List<Int>, idEvent: Int?): Int {
        return standRepository.updateIdEventByStandIds(ids, idEvent)
    }

    suspend fun addDonation(donationDto: DonationDto): Int {
        return standRepository.updateTotalDonations(donationDto)
    }
}