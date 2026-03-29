package com.example.nurdor_volunteer_app_v3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nurdor_volunteer_app_v3.activity.AuthActivity
import com.example.nurdor_volunteer_app_v3.activity.HomeActivity
import com.example.nurdor_volunteer_app_v3.activity.NoInternetConnectionActivity
import com.example.nurdor_volunteer_app_v3.viewModel.CityViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cityViewModel: CityViewModel

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

        // add fetching remote data and storing into room?
        // yes, fetch cities only, volunteer has login / register dto, after login rest of data (except other volunteers if volunteer role?)
        // if admin logs in fetch other volunteers
        // after login do another logo load and then activity with events

        cityViewModel = ViewModelProvider(this)[CityViewModel::class]

        val networkState = checkNetworkConnection()
        if(networkState.first || networkState.second) {
            lifecycleScope.launch {
                cityViewModel.fetchAll()
            }

            cityViewModel.allCities.observe(this) { cities ->
                if(cities.isNotEmpty()) {
                    if(!NurdorVolunteerApplication.encryptedPrefs.getString("jwt_token", null).isNullOrBlank()) {
                        val intent = Intent(this@MainActivity, HomeActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@MainActivity, AuthActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                        startActivity(intent)
                    }
                }
            }
        } else {
            // checkpoint 1
            val intent = Intent(this, NoInternetConnectionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
    }

    private fun createNotificationChannel() {
        val name = "nurdor_app_channel"
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
}