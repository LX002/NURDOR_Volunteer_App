package com.example.nurdor_volunteer_app_v3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nurdor_volunteer_app_v3.activity.AuthActivity
import com.example.nurdor_volunteer_app_v3.activity.HomeActivity
import com.example.nurdor_volunteer_app_v3.activity.NoInternetConnectionActivity
import com.example.nurdor_volunteer_app_v3.utils.NotificationConstants
import com.example.nurdor_volunteer_app_v3.viewModel.CityViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.EventViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.EventsLogViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cityViewModel: CityViewModel
    private lateinit var eventViewModel: EventViewModel

    private lateinit var eventsLogsViewModel: EventsLogViewModel

    override fun onStart() {
        super.onStart()
        createNotificationChannel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cityViewModel = ViewModelProvider(this)[CityViewModel::class]
        eventViewModel = ViewModelProvider(this)[EventViewModel::class]
        eventsLogsViewModel = ViewModelProvider(this)[EventsLogViewModel::class]

        var eventsAreFetched = false
        var eventsLogsAreFetched = false
        val networkState = checkNetworkConnection()
        if(networkState.first || networkState.second) {
            if(!NurdorVolunteerApplication.encryptedPrefs.getString("jwt_token", null).isNullOrBlank()) {
                lifecycleScope.launch {
                    eventViewModel.fetchAll()
                    eventsLogsViewModel.fetchAll()
                }

                eventViewModel.allEvents.observe(this) {
                    eventsAreFetched = true
                    if(eventsLogsAreFetched) launchActivity(HomeActivity::class.java)
                }

                eventsLogsViewModel.allEventsLogs.observe(this) {
                    eventsLogsAreFetched = true
                    if(eventsAreFetched) launchActivity(HomeActivity::class.java)
                }
            } else {
                lifecycleScope.launch {
                    cityViewModel.fetchAll()
                }
                cityViewModel.allCities.observe(this) { cities ->
                    if(cities.isNotEmpty()) {
                        launchActivity(AuthActivity::class.java)
                    }
                }
            }
        } else {
            launchActivity(NoInternetConnectionActivity::class.java)
        }
    }

    private fun createNotificationChannel() {
        val name = NotificationConstants.CHANNEL_ID
        val description = "Nurdor App notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(name, description, importance)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkNetworkConnection(): Pair<Boolean, Boolean> {
        val connectionManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectionManager.activeNetwork
        val networkCapabilities = connectionManager.getNetworkCapabilities(activeNetwork)

        val isWifi = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val isCellular = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true

        return Pair(isWifi, isCellular)
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
}