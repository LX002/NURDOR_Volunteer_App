package com.example.nurdor_volunteer_app_v3.activity

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.MainActivity
import com.example.nurdor_volunteer_app_v3.NurdorVolunteerApplication
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.adapters.HomeEventsAdapter
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper
import com.example.nurdor_volunteer_app_v3.viewModel.EventViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.work.impl.utils.PREFERENCE_FILE_KEY
import com.example.nurdor_volunteer_app_v3.fragment.dialog.DisplayMessageDialog
import com.example.nurdor_volunteer_app_v3.viewModel.AuthViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.CityViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.EventsLogViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.VolunteerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var cityViewModel: CityViewModel
    private lateinit var eventsLogViewModel: EventsLogViewModel
    private lateinit var authViewModel: AuthViewModel

    private lateinit var volunteerViewModel: VolunteerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.rgb(0, 191, 51)))
        setContentView(R.layout.activity_home)
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

        eventViewModel = ViewModelProvider(this)[EventViewModel::class]
        eventsLogViewModel = ViewModelProvider(this)[EventsLogViewModel::class]
        cityViewModel = ViewModelProvider(this)[CityViewModel::class]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class]
        volunteerViewModel = ViewModelProvider(this)[VolunteerViewModel::class]

        refresh()

        authViewModel.findVolunteerById(PreferenceHelper.getIdVolunteer(this)).observe(this) { v ->
            v?.let {
                Log.i("setCityNearest", "${v.name} -> volonter ulogovan")
                cityViewModel.findByZipCode(v.nearestCity).observe(this) { city ->
                    city?.let {
                        PreferenceHelper.setNearestCity(this@HomeActivity, city.cityName)
                        Log.i("setCityNearest", "${v.nearestCity} ${city.cityName}")
                    }
                }
            }
        }

        val toolbar: MaterialToolbar = findViewById(R.id.toolbarHome)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        setSupportActionBar(toolbar)

        val fabAddEvents: FloatingActionButton = findViewById(R.id.fabAddEvent)
        fabAddEvents.setOnClickListener {
            lateinit var intent: Intent
            if(PreferenceHelper.isAdmin(this)) {
                intent = Intent(this, CreateEventActivity::class.java)
            } else {
                intent = Intent(this, PickEventsActivity::class.java)
            }
            startActivity(intent)
        }

        val eventsRcView: RecyclerView = findViewById(R.id.eventsRcView)
        val homeEventsAdapter = HomeEventsAdapter(mutableListOf())
        homeEventsAdapter.deleteEvent = { event -> lifecycleScope.launch {
            val message = deleteEvent(event)
            if(message.contains("SUCCESS")) {
                homeEventsAdapter.removeEvent(event)
            }
        }}
        if(!PreferenceHelper.isAdmin(this)) {
            homeEventsAdapter.joinEvent = { idVolunteer, idEvent -> joinEvent(idEvent, idVolunteer) }
        }
        eventsRcView.itemAnimator = DefaultItemAnimator()
        eventsRcView.layoutManager = LinearLayoutManager(this)
        eventsRcView.adapter = homeEventsAdapter

        setUpEventsObserver(homeEventsAdapter)
    }

    fun joinEvent(idEvent: Int, idVolunteer: Int) {
        lifecycleScope.launch {
            val context = this@HomeActivity
            val message = eventsLogViewModel.updatePresence(1, idEvent, idVolunteer)
            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (!message.contains("ERROR") && !message.contains("EXCEPTION")) {
                val intent = Intent(context, RunningEventStatisticsActivity::class.java).apply {
                    putExtra("idEvent", idEvent)
                    putExtra("idVolunteer", idVolunteer)
                }
                context.startActivity(intent)
            } else {
                DisplayMessageDialog.newInstance(message, false).show(supportFragmentManager, "displayMessageDialogFragment")
            }
        }
    }

    suspend fun deleteEvent(event: Event): String {
        return CoroutineScope(Dispatchers.Main).async {
            val context = this@HomeActivity
            var message = ""
            if(PreferenceHelper.isAdmin(context)) {
                message = eventViewModel.deleteEvent(event)
            } else {
                message = eventsLogViewModel.deleteEventsLog(event.idEvent as Int, PreferenceHelper.getIdVolunteer(context))
            }
            DisplayMessageDialog.newInstance(message, false).show(supportFragmentManager, "displayMessageDialogFragment")
            message
        }.await()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        if(!PreferenceHelper.isAdmin(this)) {
            inflater.inflate(R.menu.menu_home, menu)
        } else {
            inflater.inflate(R.menu.menu_home_admin, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.itemSettings -> {
                true
            }
            item.itemId == R.id.itemRefresh -> {
                refresh()
                true
            }
            item.itemId == R.id.itemLogout -> {
                logout()
                true
            }
            PreferenceHelper.isAdmin(this) && item.itemId == R.id.itemRunningEvents -> {
                val intent = Intent(this, RunningEventsActivity::class.java)
                startActivity(intent)
                true
            }
            PreferenceHelper.isAdmin(this) && item.itemId == R.id.itemArchivedEvents -> {
                // TODO() launch event archive? if you make it
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        NurdorVolunteerApplication.encryptedPrefs.edit { remove("jwt_token") }
        PreferenceHelper.setIsAdmin(this, false)
        PreferenceHelper.setIdVolunteer(this, 0)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun refresh() {
        lifecycleScope.launch {
            val message = "${volunteerViewModel.fetchAll()}|${eventViewModel.fetchAll()}|${eventsLogViewModel.fetchAll()}"
            if(!message.contains("SUCCESS") && !supportFragmentManager.isStateSaved) {
                DisplayMessageDialog.newInstance(message, false).show(supportFragmentManager, "displayMessageDialogFragment")
            }
        }
    }

    private fun setUpEventsObserver(homeEventsAdapter: HomeEventsAdapter) {
        if(!PreferenceHelper.isAdmin(this)) {
            eventViewModel.upcomingEventsByVolunteerId.observe(this) { events ->
                Log.i("upcomingEvents", "upcoming events role volunteer: ${events.size}")
                homeEventsAdapter.updateEvents(events as MutableList<Event>)
            }
        } else {
            eventViewModel.upcomingEvents.observe(this) { events ->
                Log.i("upcomingEvents", "upcoming events role admin: ${events.size}")
                homeEventsAdapter.updateEvents(events as MutableList<Event>)
            }
        }
    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}