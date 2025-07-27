package com.example.rma_nurdor_project_v2

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.example.rma_nurdor_project_v2.viewModel.LoginViewModel
import com.example.rma_project_demo_v1.fragments.LoginFragment
import com.example.rma_project_demo_v1.fragments.SignInFragment

class LoginActivity : AppCompatActivity(), LoginFragment.OnSignInFragmentListener, SignInFragment.OnLoginFragmentListener {

    private lateinit var signInFragment : SignInFragment
    private lateinit var loginFragment : LoginFragment
    private var oldScreenOrientation: Int? = null
    private var newScreenOrientation: Int? = null
    var fragmentIndicator = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, maxOf(systemBars.bottom, ime.bottom))
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
        savedInstanceState?.getInt("fragmentIndicator")?.let { indicator ->
            fragmentIndicator = indicator
            Log.i("fragmentIndicator", "$indicator <-> $fragmentIndicator")

            val newOri = savedInstanceState.getInt("newOrientation")
            val oldOri = savedInstanceState.getInt("oldOrientation")
            val transaction = supportFragmentManager.beginTransaction()

            when(fragmentIndicator) {
                0 -> {
                    if(newOri == oldOri) {
                        transaction.replace(R.id.loginFrame, loginFragment)
                        transaction.replace(R.id.signInFrame, signInFragment)
                        Log.i("fragmentIndicator", "oba aktivna, landscape")
                    } else {
                        transaction.replace(R.id.mainFrame, loginFragment)
                        fragmentIndicator = 1
                    }
                }
                1 -> {
                    // provera rotacije
                    when (newOri) {
                        oldOri -> {
                            if(oldOri == Configuration.ORIENTATION_PORTRAIT) {
                                transaction.replace(R.id.mainFrame, loginFragment)
                                Log.i("fragmentIndicator", "login frg, portrait")
                            } else {
                                transaction.replace(R.id.loginFrame, loginFragment)
                                transaction.replace(R.id.signInFrame, signInFragment)
                                Log.i("fragmentIndicator", "oba aktivna, landscape")
                            }
                        }
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            transaction.replace(R.id.loginFrame, loginFragment)
                            transaction.replace(R.id.signInFrame, signInFragment)
                            Log.i("fragmentIndicator", "oba aktivna, landscape")
                        }
                        Configuration.ORIENTATION_PORTRAIT -> {
                            transaction.replace(R.id.mainFrame, loginFragment)
                            Log.i("fragmentIndicator", "login frg, portrait")
                        }
                    }
                }
                2 -> {
                    // provera rotacije
                    when (newOri) {
                        oldOri -> {
                            if(oldOri == Configuration.ORIENTATION_PORTRAIT) {
                                transaction.replace(R.id.mainFrame, signInFragment)
                                Log.i("fragmentIndicator", "sign in, portrait")
                            } else {
                                transaction.replace(R.id.loginFrame, loginFragment)
                                transaction.replace(R.id.signInFrame, signInFragment)
                                Log.i("fragmentIndicator", "oba aktivna, landscape")
                            }
                        }
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            transaction.replace(R.id.loginFrame, loginFragment)
                            transaction.replace(R.id.signInFrame, signInFragment)
                            Log.i("fragmentIndicator", "oba aktivna, landscape")
                        }
                        Configuration.ORIENTATION_PORTRAIT -> {
                            transaction.replace(R.id.mainFrame, signInFragment)
                            Log.i("fragmentIndicator", "sign in, portrait")
                        }
                    }
                }
            }
            transaction.commit()
        } ?: let {
            Log.i("fragmentIndicator", "indicator null <-> $fragmentIndicator")
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
}