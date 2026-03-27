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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this) {
            val fragmentManager = supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            } else {
                finish()
            }
        }

        oldScreenOrientation = resources.configuration.orientation
        signInFragment = SignInFragment()
        loginFragment = LoginFragment()

        savedInstanceState?.getInt("fragmentIndicator")?.let { f ->
            Log.i("fragmentIndicator", "$f <-> $fragmentIndicator")

            val newOri = savedInstanceState.getInt("newOrientation")
            val oldOri = savedInstanceState.getInt("oldOrientation")
            val transaction = supportFragmentManager.beginTransaction()

            when(fragmentIndicator) {
                0 -> {
                    if(newOri == oldOri) {
                        transaction.replace(R.id.loginFrame, loginFragment)
                        transaction.replace(R.id.signInFrame, signInFragment)
                        Log.i("authFragmentsTracker", "initial layout - landscape")
                    } else {
                        transaction.replace(R.id.mainFrame, loginFragment)
                        fragmentIndicator = 1
                        Log.i("authFragmentsTracker", "initial layout - portrait")
                    }
                }
                1 -> {
                    when (newOri) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            transaction.replace(R.id.loginFrame, loginFragment)
                            transaction.replace(R.id.signInFrame, signInFragment)
                        }
                        Configuration.ORIENTATION_PORTRAIT -> {
                            transaction.replace(R.id.mainFrame, loginFragment)
                        }
                    }
                }
                2 -> {
                    when (newOri) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            transaction.replace(R.id.loginFrame, loginFragment)
                            transaction.replace(R.id.signInFrame, signInFragment)
                        }
                        Configuration.ORIENTATION_PORTRAIT -> {
                            transaction.replace(R.id.mainFrame, signInFragment)
                        }
                    }
                }
            }
            transaction.commit()
        } ?: let {
            Log.i("authFragmentsTracker", "indicator null <-> $fragmentIndicator")
            val transaction = supportFragmentManager.beginTransaction()
            if(!isTwoPanelView()) {
                transaction.replace(R.id.mainFrame, loginFragment)
                fragmentIndicator = 1
            } else {
                transaction.replace(R.id.loginFrame, loginFragment)
                transaction.replace(R.id.signInFrame, signInFragment)
                fragmentIndicator = 0
            }
            transaction.commit()
        }
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        newScreenOrientation = newConfig.orientation
        recreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("fragmentIndicator", fragmentIndicator)
        oldScreenOrientation?.let { outState.putInt("oldOrientation", oldScreenOrientation!!) }
        newScreenOrientation?.let { outState.putInt("newOrientation", newScreenOrientation!!) }
    }

    private fun isTwoPanelView(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}