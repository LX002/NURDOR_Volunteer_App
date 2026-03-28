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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nurdor_volunteer_app_v3.NurdorVolunteerApplication
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.activity.HomeActivity
import com.example.nurdor_volunteer_app_v3.utils.JwtUtils
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper
import com.example.nurdor_volunteer_app_v3.viewModel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment: Fragment() {

    private lateinit var callback : OnSignInFragmentListener
    private lateinit var authViewModel: AuthViewModel

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authViewModel = ViewModelProvider(this)[AuthViewModel::class]
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
            val username = txtUsername?.text.toString()
            val password = txtPassword?.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                // [NOTE TO SELF] use https because of this
                val success = authViewModel.login(username, password)
                if(success) {
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    val token = NurdorVolunteerApplication.encryptedPrefs.getString("jwt_token", null)
                    val isAdmin = JwtUtils.getRoleFromToken(token).equals("ROLE_ADMIN")
                    PreferenceHelper.setIsAdmin(requireContext(), isAdmin)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Log.e("loginListener", "Login failure! No details available!")
                }
            }

        }

        btnShowSignIn?.setOnClickListener {
            callback.showSignInForm()
        }
    }

    // [NOTE TO SELF] not needed if I'm using view model fields?
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString("usernameValue", txtUsername?.text.toString())
//        outState.putString("passwordValue", txtPassword?.text.toString())
//    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}