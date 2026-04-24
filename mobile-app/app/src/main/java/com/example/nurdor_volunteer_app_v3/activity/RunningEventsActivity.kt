package com.example.nurdor_volunteer_app_v3.activity

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.SystemBarStyle
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
import com.example.nurdor_volunteer_app_v3.fragment.dialog.DisplayMessageDialog
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.viewModel.CityViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.EventViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.EventsLogViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.StandViewModel
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class RunningEventsActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var cityViewModel: CityViewModel
    private lateinit var standViewModel: StandViewModel
    private lateinit var eventsLogViewModel: EventsLogViewModel // add setting isPresent to 0?

    private val findCityByZipCode: (String) -> City? =  { zipCode -> cityViewModel.findByZipCode(zipCode).value }
    private val endEventByIdEvent: (Int) -> Unit = { idEvent ->
        lifecycleScope.launch {
            val endEventResultDto = eventViewModel.fetchEndEventResult(idEvent)
            val standsIds = endEventResultDto.stands.map { s -> s.id }
            val totalDonations = endEventResultDto.totalDonations
            if(standsIds.isNotEmpty()) {
                val numOfUpdatedEventRows = eventViewModel.startOrEndEventByIdEvent(idEvent, false, totalDonations)
                val numOfUpdatedStandRows = standViewModel.updateIdEventByStandIds(standsIds, null)
                val numOfUpdatedLogRows = eventsLogViewModel.updateIsPresentByEventId(0, idEvent)
                Log.i("eventOnOff", "$numOfUpdatedEventRows $numOfUpdatedStandRows $numOfUpdatedLogRows")
                if (numOfUpdatedEventRows == 1 && numOfUpdatedStandRows == standsIds.size) {
                    if(numOfUpdatedLogRows != 0) {
                        DisplayMessageDialog.newInstance("SUCCESS: Event with id $idEvent is successfully ended!\nTotal donations - $totalDonations RSD", false).show(supportFragmentManager, "displayMessageDialogFragment")
                    } else {
                        DisplayMessageDialog.newInstance("SUCCESS: Event with id $idEvent is successfully ended, no volunteers to \"kick out\".\nTotal donations - $totalDonations RSD\"", false).show(supportFragmentManager, "displayMessageDialogFragment")
                    }
                } else {
                    DisplayMessageDialog.newInstance("WARNING: Event with id $idEvent didn't end properly!", false).show(supportFragmentManager, "displayMessageDialogFragment")
                }
            } else {
                DisplayMessageDialog.newInstance("ERROR: ${endEventResultDto.message}", false).show(supportFragmentManager, "displayMessageDialogFragment")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.rgb(0, 191, 51)))
        setContentView(R.layout.activity_running_events)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val leftPadding = maxOf(bars.left, cutout.left)
            val rightPadding = maxOf(bars.right, cutout.right)
            v.setPadding(leftPadding, bars.top, rightPadding, bars.bottom)
            insets
        }

        if(isLandscape()) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }

        supportFragmentManager.setFragmentResultListener("display_message_result", this) { _, bundle ->
            if(bundle.getString("status") == "SUCCESS") { finish() }
        }

        eventViewModel = ViewModelProvider(this)[EventViewModel::class]
        cityViewModel = ViewModelProvider(this)[CityViewModel::class]
        standViewModel = ViewModelProvider(this)[StandViewModel::class]
        eventsLogViewModel = ViewModelProvider(this)[EventsLogViewModel::class]

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
            val message = "${eventViewModel.fetchAll()}|${cityViewModel.fetchAll()}|${standViewModel.fetchAll()}"
            if(!message.contains("SUCCESS") && !supportFragmentManager.isStateSaved) {
                DisplayMessageDialog.newInstance(message, false).show(supportFragmentManager, "displayMessageDialogFragment")
            }
        }
    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}