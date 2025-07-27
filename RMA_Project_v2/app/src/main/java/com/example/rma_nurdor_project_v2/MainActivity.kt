package com.example.rma_nurdor_project_v2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rma_nurdor_project_v2.utils.NotificationConstants
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper

class MainActivity : AppCompatActivity() {

    private var uiManager: UiModeManager? = null

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NotificationConstants.CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStart() {
        super.onStart()
        Log.i("mainOnCreate", "mainactivity onstart")
        createNotificationChannel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("mainOnCreate", "mainactivity oncreate")
        super.onCreate(savedInstanceState)
        //applyThemes()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        uiManager = getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager

        Handler(Looper.getMainLooper()).postDelayed({
            val networkState = checkNetworkConnections()
            val intent: Intent
            if(networkState.first || networkState.second) {
                if (PreferenceHelper.isLoggedIn(this)) {
                    intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    //finish()
                } else {
                    intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    //finish()
                }
            } else {
                intent = Intent(this, NoConnectionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }, 1500)
    }

    private fun checkNetworkConnections(): Pair<Boolean, Boolean> {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connMgr.activeNetwork
        val networkCapabilities = connMgr.getNetworkCapabilities(activeNetwork)

        val isWifi = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val isMobileData = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true

        return Pair(isWifi, isMobileData)
    }
}