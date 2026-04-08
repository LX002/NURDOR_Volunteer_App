package com.example.nurdor_volunteer_app_v3.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import com.example.nurdor_volunteer_app_v3.viewModel.StandViewModel
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class RunningEventsActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var cityViewModel: CityViewModel
    private lateinit var standViewModel: StandViewModel

    private val findCityByZipCode: (String) -> City? =  { zipCode -> cityViewModel.findByZipCode(zipCode).value }
    private val endEventByIdEvent: (Int) -> Unit = { idEvent ->
        lifecycleScope.launch {
            val endEventResultDto = eventViewModel.fetchEndEventResult(idEvent)
            val standsIds = endEventResultDto.stands.map { s -> s.id }
            if(standsIds.isNotEmpty()) {
                val numOfUpdatedEventRows = eventViewModel.updateIsStarted(idEvent, false)
                val numOfUpdatedStandRows = standViewModel.updateIdEventByStandIds(standsIds, null)
                Log.i("eventOnOff", "$numOfUpdatedEventRows $numOfUpdatedStandRows")
                if (numOfUpdatedEventRows == 1 && numOfUpdatedStandRows == standsIds.size) {
                    Toast.makeText(this@RunningEventsActivity, "Event with id: $idEvent is successfully ended!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RunningEventsActivity, "Whoops, event with id: $idEvent didn't end!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@RunningEventsActivity, "Error: ${endEventResultDto.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

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
        standViewModel = ViewModelProvider(this)[StandViewModel::class]

        fetchData()

        val toolbar = findViewById<MaterialToolbar>(R.id.runningEventsToolbar)
        setSupportActionBar(toolbar)
        val rcvRunningEvents = findViewById<RecyclerView>(R.id.rcvRunningEvents)
        val runningEventsAdapter = RunningEventsAdapter(mutableListOf(), findCityByZipCode, endEventByIdEvent)
        rcvRunningEvents.layoutManager = LinearLayoutManager(this)
        rcvRunningEvents.adapter = runningEventsAdapter

        eventViewModel.runningEvents.observe(this) { events ->
            runningEventsAdapter.updateEvents(events)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_running_events_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.itemRefresh) {
            fetchData()
            return true
        }
        return false
    }

    fun fetchData() {
        lifecycleScope.launch {
            eventViewModel.fetchAll()
            cityViewModel.fetchAll()
            standViewModel.fetchAll()
        }
    }
}