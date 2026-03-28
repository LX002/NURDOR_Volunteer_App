package com.example.nurdor_volunteer_app_v3.activity

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.fragment.LoginFragment
import com.example.nurdor_volunteer_app_v3.fragment.SignInFragment

class AuthActivity : AppCompatActivity(), LoginFragment.OnSignInFragmentListener, SignInFragment.OnLoginFragmentListener {

    private lateinit var signInFragment : SignInFragment
    private lateinit var loginFragment : LoginFragment
    private var oldScreenOrientation: Int? = null
    private var newScreenOrientation: Int? = null
    var fragmentIndicator = 0
    var previousPortraitFragmentIndicator = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sfm = supportFragmentManager

        onBackPressedDispatcher.addCallback(this) {
            if (sfm.backStackEntryCount > 0) {
                sfm.popBackStack()
            } else {
                finish()
            }
        }

        signInFragment = SignInFragment()
        loginFragment = LoginFragment()

        if(isTwoPanelView()) {
            fragmentIndicator = 0
            previousPortraitFragmentIndicator = savedInstanceState?.getInt("fragmentIndicator") ?: 0
            Log.i("fragmentIndicator", "ind: $fragmentIndicator")
            sfm.beginTransaction()
                .replace(R.id.loginFrame, loginFragment, "loginFrgLand")
                .replace(R.id.signInFrame, signInFragment, "signInFrgLand")
                .commit()
        } else {
            fragmentIndicator = savedInstanceState?.getInt("fragmentIndicator") ?: 1
            Log.i("fragmentIndicator", "ind: $fragmentIndicator")
            sfm.beginTransaction()
                .replace(
                    R.id.mainFrame,
                    if(fragmentIndicator == 1) loginFragment else signInFragment,
                    if(fragmentIndicator == 1) "loginFrgPort" else "signInFrgPort")
                .commit()
        }
    }

    private fun isTwoPanelView(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    override fun showSignInForm() {
        if(!isTwoPanelView()) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainFrame, signInFragment)
            //transaction.addToBackStack(null);
            transaction.commit()
            //supportFragmentManager.executePendingTransactions()
            fragmentIndicator = 2
        }
    }

    override fun newSignInHandling() {
        if(!isTwoPanelView()) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainFrame, loginFragment)
            //transaction.addToBackStack(null);
            transaction.commit()
            //supportFragmentManager.executePendingTransactions()
            fragmentIndicator = 1
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val indicatorToSave = if(isTwoPanelView()) previousPortraitFragmentIndicator else fragmentIndicator
        Log.i("fragmentIndicator", "onsaveinstance ind: $indicatorToSave")
        outState.putInt("fragmentIndicator", indicatorToSave)

    }

}