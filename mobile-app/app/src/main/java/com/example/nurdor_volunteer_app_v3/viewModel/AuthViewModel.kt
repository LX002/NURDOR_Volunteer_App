package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application): AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    suspend fun login(username: String, password: String) {
        withContext(Dispatchers.IO) {
            authRepository.login(username, password)?.let { loginData ->
                // checkpoint 2
                // [NOTE TO MYSELF] change this to DataStore in later version of project
                // encryptedshared preferences in AuthViewModel, use application.context
            }
        }
    }
}