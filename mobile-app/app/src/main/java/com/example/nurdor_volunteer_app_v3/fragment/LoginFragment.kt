package com.example.nurdor_volunteer_app_v3.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.edit
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nurdor_volunteer_app_v3.NurdorVolunteerApplication
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.activity.HomeActivity
import com.example.nurdor_volunteer_app_v3.dto.authDto.LoginResponseDto
import com.example.nurdor_volunteer_app_v3.fragment.dialog.DisplayMessageDialog
import com.example.nurdor_volunteer_app_v3.utils.JwtUtils
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper
import com.example.nurdor_volunteer_app_v3.viewModel.AuthViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.CityViewModel
import kotlinx.coroutines.launch

class LoginFragment: Fragment() {

    private lateinit var callback : OnRegisterFragmentListener
    private lateinit var authViewModel: AuthViewModel
    private lateinit var cityViewModel: CityViewModel

    private var txtUsername : EditText? = null
    private var txtPassword : EditText? = null
    interface OnRegisterFragmentListener {
        fun showRegisterForm()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as OnRegisterFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authViewModel = ViewModelProvider(this)[AuthViewModel::class]
        cityViewModel = ViewModelProvider(this)[CityViewModel::class]
        val view: View? = if(isLandscape()) {
           inflater.inflate(R.layout.fragment_login_land, container, false)
        } else {
            inflater.inflate(R.layout.fragment_login, container, false)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogin : Button? = view.findViewById(R.id.btnLogin)
        val btnShowSignIn : Button? = view.findViewById(R.id.btnShowSignIn)
        txtUsername = view.findViewById(R.id.txtUsername)
        txtPassword = view.findViewById(R.id.txtPassword)

        if(authViewModel.loginUsername.isNotBlank()) txtUsername?.setText(authViewModel.loginUsername)
        if(authViewModel.loginPassword.isNotBlank()) txtPassword?.setText(authViewModel.loginPassword)

        txtUsername?.doAfterTextChanged { text ->
            authViewModel.loginUsername = text.toString()
            Log.i("loginFragment", "username value: ${authViewModel.loginUsername}")
        }

        txtPassword?.doAfterTextChanged { text ->
            authViewModel.loginPassword = text.toString()
            Log.i("loginFragment", "password value: ${authViewModel.loginPassword}")
        }

        btnLogin?.setOnClickListener {
            lifecycleScope.launch {
                loginAndRedirectToHome(
                    txtUsername?.text.toString(),
                    txtPassword?.text.toString()
                )
            }
        }

        btnShowSignIn?.setOnClickListener {
            callback.showRegisterForm()
        }
    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    private suspend fun loginAndRedirectToHome(username: String, password: String) {
        val (loginResponseDto, message) = authViewModel.login(username, password)
        if(message.contains("SUCCESS") && loginResponseDto != null) {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            editPreferences(loginResponseDto)
            startActivity(intent)
            requireActivity().finish()
        } else {
            if(isAdded && !parentFragmentManager.isStateSaved) {
                DisplayMessageDialog.newInstance(message, false).show(parentFragmentManager, "displayMessageDialogFragment")
            }
        }
    }

    private fun editPreferences(loginResponseDto: LoginResponseDto) {
        val encryptedPrefs = NurdorVolunteerApplication.encryptedPrefs
        encryptedPrefs.edit { putString("jwt_token", loginResponseDto.accessToken) }
        PreferenceHelper.setIdVolunteer(requireContext(), loginResponseDto.volunteerId)
        val role = JwtUtils.getRoleFromToken(loginResponseDto.accessToken)
        role?.let { PreferenceHelper.setIsAdmin(requireContext(), role.roleName == "ROLE_ADMIN") }
        Log.i("observersLog", "preferences: ID: ${loginResponseDto.volunteerId} role: $role")

    }
}