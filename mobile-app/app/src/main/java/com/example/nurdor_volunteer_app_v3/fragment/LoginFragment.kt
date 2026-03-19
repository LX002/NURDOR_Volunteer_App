package com.example.nurdor_volunteer_app_v3.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.Fragment

class LoginFragment: Fragment() {

    private lateinit var callback : OnSignInFragmentListener


    private var usernameValue: CharSequence? = ""
    private var passwordValue: CharSequence? = ""

    private var txtUsername : EditText? = null
    private var txtPassword : EditText? = null
    interface OnSignInFragmentListener {
        fun showSignInForm()
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