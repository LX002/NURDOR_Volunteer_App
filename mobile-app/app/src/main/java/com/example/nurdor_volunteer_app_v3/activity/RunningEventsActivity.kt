package com.example.nurdor_volunteer_app_v3.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.adapters.RunningEventsAdapter
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.viewModel.CityViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.EventViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RunningEventsActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var cityViewModel: CityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_running_events)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        eventViewModel = ViewModelProvider(this)[EventViewModel::class]
        cityViewModel = ViewModelProvider(this)[CityViewModel::class]

        lifecycleScope.launch {
            eventViewModel.fetchAll()
            cityViewModel.fetchAll()
        }

        val findCityByZipCode: (String) -> City? =  { zipCode -> cityViewModel.findByZipCode(zipCode).value }

        val rcvRunningEvents = findViewById<RecyclerView>(R.id.rcvRunningEvents)
        val runningEventsAdapter = RunningEventsAdapter(mutableListOf(), findCityByZipCode)
        rcvRunningEvents.layoutManager = LinearLayoutManager(this)
        rcvRunningEvents.adapter = runningEventsAdapter

        // TODO(): launch this activity when some event is started, test it
        eventViewModel.runningEvents.observe(this) { events ->
            runningEventsAdapter.updateEvents(events)
        }
    }
}