package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.nurdor_volunteer_app_v3.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.example.nurdor_volunteer_app_v3.NurdorVolunteerApplication
import com.example.nurdor_volunteer_app_v3.dto.RegisterDto
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.model.VolunteerRole
import com.example.nurdor_volunteer_app_v3.repository.CityRepository
import com.example.nurdor_volunteer_app_v3.repository.DatabaseClient

class AuthViewModel(application: Application): AndroidViewModel(application) {

    private val db = DatabaseClient.getInstance(application).appDatabase
    private val authRepository = AuthRepository(db)
    private val cityRepository = CityRepository(db)

    val isSignInEnabled = MutableLiveData<Boolean>(false)

    var selectedCity = City("", "")
    var selectedRole = VolunteerRole(0, "")
    var profileImg: ByteArray? = null
    val signInTextFieldsValues = mutableListOf<String?>("", "", "", "", "", "", "", "", "")
    val validationMsgs = mutableListOf<String?>("", "", "", "", "", "", "", "", "")

    suspend fun login(username: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            var success = false
            authRepository.login(username, password)?.let { loginData ->
                // [NOTE TO MYSELF] change this to DataStore in later version of project
                val encryptedPrefs = NurdorVolunteerApplication.encryptedPrefs
                encryptedPrefs.edit { putString("jwt_token", loginData.token) }
                success = true
            }

            if(!success) {
                Log.e("retrofitApi1", "Login data is null!")
            }
            return@withContext success
        }
    }

    suspend fun register(registerDto: RegisterDto): Int {
        return withContext(Dispatchers.IO) {
            return@withContext authRepository.register(registerDto);
        }
    }

    suspend fun findCities(): List<City> {
        return withContext(Dispatchers.IO) {
            return@withContext cityRepository.findAll()
        }
    }

    // [NOTE TO SELF] validation of sign in fields
    fun validatePhone(phone: CharSequence?) {
        validationMsgs[4] = when {
            phone!!.isBlank() -> "Phone number is blank!"
            !Regex("([+][0-9]{2,}|06)([0-9]+)").containsMatchIn(phone) -> "Wrong phone number format!"
            else -> null
        }
        checkSignInValidity()
    }

    fun validatePassword(password: CharSequence?) {
        validationMsgs[7] = when {
            password!!.isBlank() -> "Password is blank!"
            !Regex("[ !\"#\$%&'()*+,-./:;<=>?@^_`{|}~]").containsMatchIn(password) -> "Password doesn't contain special characters!"
            !Regex("[A-Z]").containsMatchIn(password) -> "Password doesn't contain large letters!"
            !Regex("[a-z]").containsMatchIn(password) -> "Password doesn't contain small letters!"
            !Regex("[0-9]").containsMatchIn(password) -> "Password doesn't contain numbers!"
            password.length < 8 -> "Password is too weak!"
            else -> null
        }
        Log.i("fieldValidation", "passwordValidation: ${validationMsgs[7]}")
        checkSignInValidity()
    }

    fun validateAddress(address: CharSequence?) {
        validationMsgs[3] = when {
            address!!.isBlank() -> "Address is blank!"
            !Regex("[A-Za-zŠšŽžĆćČčĐđ ]+ (?:\\d+|[a-z]+), [0-9]+ [A-Za-zŠšŽžĆćČčĐđ ]+").containsMatchIn(address!!) -> "Address format is: [street] [No], [zip] [city]!"
            else -> null
        }
        Log.i("fieldValidation", "addressValidation: ${validationMsgs[3]}")
        checkSignInValidity()
    }

    fun validateEmail(email: CharSequence?) {
        validationMsgs[5] = when {
            email!!.isBlank() -> "Email is blank!"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Incorrect email format!"
            else -> null
        }
        Log.i("fieldValidation", "emailValidation: ${validationMsgs[5]}")
        checkSignInValidity()
    }

    fun validateUsername(username: CharSequence?) {
        validationMsgs[6] = when {
            username!!.isBlank() -> "Username is blank!"
            else -> null
        }
        Log.i("fieldValidation", "usernameValidation: ${validationMsgs[6]}")
        checkSignInValidity()
    }

    fun isFormFilled(): Int {
        if(signInTextFieldsValues[6] != signInTextFieldsValues[7]) {
            Log.i("fieldValidation", "Passwords don't match!")
            return -2
        }
        if(selectedCity == City("", "") || selectedRole == VolunteerRole(0, "")) return -1
        signInTextFieldsValues.forEach { if(it?.isBlank() == true) return 0 }
        return 1
    }

    private fun checkSignInValidity() {
        isSignInEnabled.value =
            validationMsgs[4].equals(null)
                && validationMsgs[3].equals(null)
                && validationMsgs[7].equals(null)
                && validationMsgs[5].equals(null)
                && validationMsgs[6].equals(null)
    }
}