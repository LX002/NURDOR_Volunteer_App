package com.example.rma_nurdor_project_v2.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rma_nurdor_project_v2.DatabaseClient
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.repository.CityRepository
import com.example.rma_nurdor_project_v2.repository.VolunteerRepository
import com.example.rma_nurdor_project_v2.repository.VolunteerRoleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VolunteerViewModel(application: Application) : AndroidViewModel(application) {

    private val volunteerRepository: VolunteerRepository

    init {
        val db = DatabaseClient.getInstance(application).appDatabase
        volunteerRepository = VolunteerRepository(db)
    }

    private val _allVolunteers = MutableLiveData<List<Volunteer>>()
    private val _presentVolunteers = MutableLiveData<List<Volunteer>>()

    val allVolunteers: LiveData<List<Volunteer>>
        get() = _allVolunteers
    val presentVolunteers: LiveData<List<Volunteer>>
        get() = _presentVolunteers

    // fetchuje sve volontere i uzima prisutne iz Room baze
    suspend fun loadAllVolunteers(idEvent: Int) {
        try {
            val volunteers = withContext(Dispatchers.IO) {
                volunteerRepository.getVolunteers()
            }
            val presentVolunteers = withContext(Dispatchers.IO) {
                volunteerRepository.getPresentVolunteers(idEvent)
            }
            _allVolunteers.value = volunteers
            _presentVolunteers.value = presentVolunteers

        } catch (e: Exception) {
            _allVolunteers.value = emptyList()
            _presentVolunteers.value = emptyList()
        }
    }

    suspend fun loadPresentVolunteers(idEvent: Int) {
        try {
            val volunteers = withContext(Dispatchers.IO) {
                volunteerRepository.getPresentVolunteers(idEvent)
            }
            _presentVolunteers.value = volunteers
        } catch (e: Exception) {
            _presentVolunteers.value = emptyList()
        }
    }

    suspend fun getLoadedPresentVolunteers(idEvent: Int): List<Volunteer> {
        return withContext(Dispatchers.IO) { volunteerRepository.getPresentVolunteers(idEvent) }
    }
}