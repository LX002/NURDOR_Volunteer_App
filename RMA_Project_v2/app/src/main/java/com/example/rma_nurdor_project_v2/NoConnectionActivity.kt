package com.example.rma_nurdor_project_v2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper

class NoConnectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_no_connection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRefresh: Button = findViewById(R.id.btnRefresh)
        btnRefresh.setOnClickListener {
            val networkState = checkNetworkConnections()
            if(networkState.first || networkState.second) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show()
            }
        }
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