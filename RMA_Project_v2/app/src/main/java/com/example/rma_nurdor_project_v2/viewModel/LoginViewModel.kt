package com.example.rma_nurdor_project_v2.viewModel

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rma_nurdor_project_v2.DatabaseClient
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.model.VolunteerRole
import com.example.rma_nurdor_project_v2.repository.CityRepository
import com.example.rma_nurdor_project_v2.repository.VolunteerRepository
import com.example.rma_nurdor_project_v2.repository.VolunteerRoleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val cityRepository: CityRepository
    private val volunteerRoleRepository: VolunteerRoleRepository
    private val volunteerRepository: VolunteerRepository

    init {
        val db = DatabaseClient.getInstance(application).appDatabase
        cityRepository = CityRepository(db)
        volunteerRoleRepository = VolunteerRoleRepository(db)
        volunteerRepository = VolunteerRepository(db)
    }

    private val _cities = MutableLiveData<List<City>>()
    private val _volunteerRoles = MutableLiveData<List<VolunteerRole>>()
    private val _volunteers = MutableLiveData<List<Volunteer>>()
    private val _isSignInEnabled = MutableLiveData<Boolean>(false)
    private val _addressValidation = MutableLiveData<String?>("")
    private val _usernameValidation = MutableLiveData<String?>("")
    private val _passwordValidation = MutableLiveData<String?>("")
    private val _emailValidation = MutableLiveData<String?>("")
    private val _phoneValidation = MutableLiveData<String?>("")

    private var _profileImg = MutableLiveData<ByteArray?>()
    private var _profileImgName = MutableLiveData<String?>()

    val cities: LiveData<List<City>>
        get() = _cities
    val volunteerRoles: LiveData<List<VolunteerRole>>
        get() = _volunteerRoles
    val volunteers: LiveData<List<Volunteer>>
        get() = _volunteers
    val isSignInEnabled: LiveData<Boolean>
        get() = _isSignInEnabled
    val profileImg: MutableLiveData<ByteArray?>
        get() = _profileImg
    val profileImgName: MutableLiveData<String?>
        get() = _profileImgName

    // login fields values
    var usernameValue = ""
    var passwordValue = ""

    // sign in fields values
    val signInFieldsValues = mutableListOf<String>("", "", "", "", "", "", "", "")
    var selectedCity: City? = City("11000", "Beograd")
    var selectedRole: VolunteerRole? = VolunteerRole(1, "admin")

    suspend fun getLoadedCities(): MutableList<City> {
        return try {
            withContext(Dispatchers.IO) { cityRepository.getLoadedCities() as MutableList<City> }
        } catch (e: Exception) {
            mutableListOf<City>()
        }
    }

    suspend fun getLoadedRoles(): MutableList<VolunteerRole> {
        return try {
            withContext(Dispatchers.IO) { volunteerRoleRepository.getLoadedRoles() as MutableList<VolunteerRole> }
        } catch (e: Exception) {
            mutableListOf<VolunteerRole>()
        }
    }

    suspend fun getRoleById(idRole: Int): VolunteerRole? {
        return try {
            withContext(Dispatchers.IO) {
                volunteerRoleRepository.getRoleById(idRole)
            }
        } catch (e: Exception) {
            Log.i("onCreateOptionsMenuMeth", "exception in viewmodel ${e.message}")
            null
        }
    }

    suspend fun loadCities() {
        try {
            val cityList = withContext(Dispatchers.IO) {
                cityRepository.getCities()
            }
            _cities.value = cityList
        } catch (e: Exception) {
            _cities.value = emptyList()
        }
    }

    suspend fun loadVolunteerRoles() {
        try {
            val volunteerRolesList = withContext(Dispatchers.IO) {
                volunteerRoleRepository.getVolunteerRoles()
            }
            _volunteerRoles.value = volunteerRolesList
        } catch (e: Exception) {
            _volunteerRoles.value = emptyList()
        }
    }

    suspend fun loadVolunteers() {
        try {
            val volunteerList = withContext(Dispatchers.IO) {
                volunteerRepository.getVolunteers()
            }
            _volunteers.value = volunteerList
        } catch (e: Exception) {
            _volunteers.value = emptyList()
        }
    }

    suspend fun insertOrReplaceVolunteer(volunteer: Volunteer): Long {
        return withContext(Dispatchers.IO) {
            volunteerRepository.insertOrReplaceVolunteer(volunteer)
        }
    }

    fun validatePhone(phone: CharSequence?) {
        _phoneValidation.value = when {
            phone!!.isBlank() -> "Phone number is blank!"
            !Regex("([+][0-9]{2,}|06)([0-9]+)").containsMatchIn(phone) -> "Wrong phone number format!"
            else -> null
        }
        checkSignInValidity()
    }

    fun validatePassword(password: CharSequence?) {
        _passwordValidation.value = when {
            password!!.isBlank() -> "Password is blank!"
            !Regex("[ !\"#\$%&'()*+,-./:;<=>?@^_`{|}~]").containsMatchIn(password) -> "Password doesn't contain special characters!"
            !Regex("[A-Z]").containsMatchIn(password) -> "Password doesn't contain large letters!"
            !Regex("[a-z]").containsMatchIn(password) -> "Password doesn't contain small letters!"
            !Regex("[0-9]").containsMatchIn(password) -> "Password doesn't contain numbers!"
            password.length < 8 -> "Password is too weak!"
            else -> null
        }
        Log.i("fieldValidation", "passwordValidation: ${_passwordValidation.value}")
        checkSignInValidity()
    }

    fun validateAddress(address: CharSequence?) {
        _addressValidation.value = when {
            address!!.isBlank() -> "Address is blank!"
            !Regex("[A-Za-zŠšŽžĆćČčĐđ ]+ (?:\\d+|[a-z]+), [0-9]+ [A-Za-zŠšŽžĆćČčĐđ ]+").containsMatchIn(address!!) -> "Address format is: [street] [No], [zip] [city]!"
            else -> null
        }
        Log.i("fieldValidation", "addressValidation: ${_addressValidation.value}")
        checkSignInValidity()
    }

    fun validateEmail(email: CharSequence?) {
        _emailValidation.value = when {
            email!!.isBlank() -> "Email is blank!"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Incorrect email format!"
            else -> null
        }
        Log.i("fieldValidation", "emailValidation: ${_emailValidation.value}")
        checkSignInValidity()
    }

    fun validateUsername(username: CharSequence?) {
        _usernameValidation.value = when {
            username!!.isBlank() -> "Username is blank!"
            else -> null
        }
        Log.i("fieldValidation", "usernameValidation: ${_usernameValidation.value}")
        checkSignInValidity()
    }

    fun isFormFilled(txtValues: List<String>, nearestCity: City?, volunteerRole: VolunteerRole?): Int {
        if(txtValues[6] != txtValues[7]) {
            Log.i("fieldValidation", "Passwords don't match!")
            return -2
        }
        if(nearestCity == null || volunteerRole == null) return -1
        txtValues.forEach { if(it.isBlank()) return 0 }
        return 1
    }

    suspend fun getVolunteerByUsername(username: String): Volunteer? {
        return try {
            withContext(Dispatchers.IO) {
                volunteerRepository.getVolunteerByUsername(username)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun checkSignInValidity() {
        _isSignInEnabled.value = _phoneValidation.value == null && _addressValidation.value == null && _passwordValidation.value == null && _emailValidation.value == null && _usernameValidation.value == null
    }
}