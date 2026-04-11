package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.nurdor_volunteer_app_v3.model.Volunteer
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient
import com.example.nurdor_volunteer_app_v3.repository.VolunteerRepository
import com.example.nurdor_volunteer_app_v3.utils.VolunteerSearchFilter

class VolunteerViewModel(application: Application): AndroidViewModel(application) {

    private val volunteerRepository =
        VolunteerRepository(DatabaseClient.getInstance(application).appDatabase)

    var spinnerPos = 0
    var sortBy = "default"
    var findBy = "Default"
    var searchTxt = ""
    val searchFilter = MutableLiveData<VolunteerSearchFilter>()
    val presentVolunteers: LiveData<List<Volunteer>> = searchFilter.switchMap { f ->
        when(f.findBy) {
            "Default" -> sortedResult(f)
            "By name" -> findByNameResult(f)
            "By surname" -> findBySurnameResult(f)
            "By phone number" -> volunteerRepository.findPresentVolunteersByIdEventAndPhoneNumber(f.idEvent, f.searchTxt)
            "By username" -> volunteerRepository.findPresentVolunteersByIdEventAndUsername(f.idEvent, f.searchTxt)
            else -> volunteerRepository.findPresentVolunteersByIdEvent(f.idEvent)
        }
    }

    suspend fun fetchAll() {
        volunteerRepository.fetchAll()
    }

    fun findEnrolledVolunteersByIdEvent(idEvent: Int) =
        volunteerRepository.findEnrolledVolunteersByIdEvent(idEvent)

    private fun sortedResult(f: @JvmSuppressWildcards VolunteerSearchFilter): LiveData<List<Volunteer>> =
        when (f.sortBy) {
            "name ascending" -> volunteerRepository.findPresentVolunteersByIdEventSortedByName(f.idEvent)
            "name descending" -> volunteerRepository.findPresentVolunteersByIdEventSortedByNameDesc(f.idEvent)
            "surname ascending" -> volunteerRepository.findPresentVolunteersByIdEventSortedBySurname(f.idEvent)
            "surname descending" -> volunteerRepository.findPresentVolunteersByIdEventSortedBySurnameDesc(f.idEvent)
            else -> volunteerRepository.findPresentVolunteersByIdEvent(f.idEvent)
        }

    private fun findByNameResult(f: @JvmSuppressWildcards VolunteerSearchFilter): LiveData<List<Volunteer>> =
        when (f.sortBy) {
            "name ascending" -> volunteerRepository.findPresentVolunteersByIdEventAndNameSortByName(f.idEvent, f.searchTxt)
            "name descending" -> volunteerRepository.findPresentVolunteersByIdEventAndNameSortByNameDesc(f.idEvent, f.searchTxt)
            "surname ascending" -> volunteerRepository.findPresentVolunteersByIdEventAndNameSortBySurname(f.idEvent, f.searchTxt)
            "surname descending" -> volunteerRepository.findPresentVolunteersByIdEventAndNameSortBySurnameDesc(f.idEvent, f.searchTxt)
            else -> volunteerRepository.findPresentVolunteersByIdEventAndName(f.idEvent, f.searchTxt)
        }

    private fun findBySurnameResult(f: @JvmSuppressWildcards VolunteerSearchFilter): LiveData<List<Volunteer>> =
        when (f.sortBy) {
            "name ascending" -> volunteerRepository.findPresentVolunteersByIdEventAndSurnameSortByName(f.idEvent, f.searchTxt)
            "name descending" -> volunteerRepository.findPresentVolunteersByIdEventAndSurnameSortByNameDesc(f.idEvent, f.searchTxt)
            "surname ascending" -> volunteerRepository.findPresentVolunteersByIdEventAndSurnameSortBySurname(f.idEvent, f.searchTxt)
            "surname descending" -> volunteerRepository.findPresentVolunteersByIdEventAndSurnameSortBySurnameDesc(f.idEvent, f.searchTxt)
            else -> volunteerRepository.findPresentVolunteersByIdEventAndSurname(f.idEvent, f.searchTxt)
        }

    fun updateFilter(idEvent: Int) {
        searchFilter.value = VolunteerSearchFilter(idEvent, findBy, sortBy, searchTxt)
    }
}