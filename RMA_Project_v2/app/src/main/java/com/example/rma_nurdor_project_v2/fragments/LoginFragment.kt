package com.example.rma_project_demo_v1.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.HomeActivity
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.model.VolunteerRole
import com.example.rma_nurdor_project_v2.utils.PasswordUtils
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.example.rma_nurdor_project_v2.repository.CityRepository
import com.example.rma_nurdor_project_v2.repository.VolunteerRepository
import com.example.rma_nurdor_project_v2.repository.VolunteerRoleRepository
import com.example.rma_nurdor_project_v2.viewModel.EventViewModel
import com.example.rma_nurdor_project_v2.viewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment() : Fragment() {

    private lateinit var callback : OnSignInFragmentListener
    private lateinit var loginViewModel: LoginViewModel

    private var usernameValue: CharSequence? = ""
    private var passwordValue: CharSequence? = ""

    private var txtUsername : EditText? = null
    private var txtPassword : EditText? = null
    interface OnSignInFragmentListener {
        fun showSignInForm()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as OnSignInFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //load data from room / retrofit
        loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
        // Inflate the layout for this fragment
        val view : View? = if(!isLandscape()) {
            inflater.inflate(R.layout.fragment_login, container, false)
        } else {
            inflater.inflate(R.layout.fragment_login_land, container, false)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(PreferenceHelper.isFirstLaunch(requireContext())) {
            Log.i("onStartEvents", "first launch, setting it to false!")
            PreferenceHelper.setFirstLaunch(requireContext(), false)
            val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
            val eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
            lifecycleScope.launch {
                Log.i("onStartEvents", "first launch, loading all...")
                loginViewModel.loadVolunteers()
                loginViewModel.loadVolunteerRoles()
                loginViewModel.loadCities()
                eventViewModel.loadEvents()
                eventViewModel.loadEventsLogs()
            }
        }

        lifecycleScope.launch {
            loginViewModel.loadVolunteers()
            loginViewModel.loadVolunteerRoles()
        }

        val btnLogin : Button? = view.findViewById(R.id.btnLogin)
        val btnShowSignIn : Button? = view.findViewById(R.id.btnShowSignIn)
        txtUsername = view.findViewById(R.id.txtUsername)
        txtPassword = view.findViewById(R.id.txtPassword)

        //view.post {
            if(loginViewModel.usernameValue.isNotBlank()) txtUsername?.setText(loginViewModel.usernameValue)
            if(loginViewModel.passwordValue.isNotBlank()) txtPassword?.setText(loginViewModel.passwordValue)
        //}

        txtUsername?.doAfterTextChanged { text ->
            loginViewModel.usernameValue = text.toString()
            Log.i("loginViewModelFields", "username value: ${loginViewModel.usernameValue}")
        }

        txtPassword?.doAfterTextChanged { text ->
            loginViewModel.passwordValue = text.toString()
            Log.i("loginViewModelFields", "password value: ${loginViewModel.passwordValue}")
        }

        btnLogin?.setOnClickListener {
            val username = txtUsername?.text.toString()
            val password = txtPassword?.text.toString()
            loginViewModel.volunteers.observe(viewLifecycleOwner) { volunteers ->
                if(volunteers.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val volunteer = withContext(Dispatchers.IO) { loginViewModel.getVolunteerByUsername(username) }
                        Log.i("loginVolunteer1", "$volunteer")
                        when {
                            volunteer == null -> Toast.makeText(context, "Invalid username!", Toast.LENGTH_SHORT).show()
                            !PasswordUtils.verifyPassword(password, volunteer.password) -> Toast.makeText(context, "Invalid password!", Toast.LENGTH_SHORT).show()
                            else -> showHome(volunteer)
                        }
                    }
                } else {
                    Log.i("loginVolunteer1", "Volunteers are not loaded yet!")
                }
            }
        }

        btnShowSignIn?.setOnClickListener {
            callback.showSignInForm()
        }
    }

    private fun showHome(volunteer: Volunteer) {
        CoroutineScope(Dispatchers.Main).launch {
            PreferenceHelper.setLoggedIn(requireContext(), true)
            val volunteerRole: VolunteerRole? = withContext(Dispatchers.IO) {
                Log.i("onCreateOptionsMenuMeth", "${volunteer.volunteerRole} role of volunteer")
                loginViewModel.getRoleById(volunteer.volunteerRole)
            }
            Log.i("onCreateOptionsMenuMeth", "${volunteerRole?.roleName}")
            if(volunteerRole?.roleName == "admin") {
                PreferenceHelper.setIsAdmin(requireContext(), true)
            } else {
                PreferenceHelper.setIsAdmin(requireContext(), false)
            }

            Toast.makeText(context, "Welcome ${volunteer.username}!", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), HomeActivity::class.java)
            intent.putExtra("username", volunteer.username)
            Log.i("onStartEvents", "${volunteer.id} idVolunteer sent")
            PreferenceHelper.setIdVolunteer(requireContext(), volunteer.id!!)
            PreferenceHelper.setVolunteerNearestCity(requireContext(), volunteer.nearestCity)
            startActivity(intent)
            requireActivity().finish()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("usernameValue", txtUsername?.text.toString())
        outState.putString("passwordValue", txtPassword?.text.toString())
    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}