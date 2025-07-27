package com.example.rma_nurdor_project_v2

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.rcv_adapters.EventsAdapter
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.example.rma_nurdor_project_v2.viewModel.EventViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private var idVolunteer: Int = -1
    private var eventsRcView: RecyclerView? = null
    private var isFirstLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("debugEventsList", "usao u onCreate")
        enableEdgeToEdge()
        setContentView(R.layout.home_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idVolunteer = PreferenceHelper.getIdVolunteer(this)

        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        lifecycleScope.launch {
            Log.i("debugEventsList", "usao u onCreate lifecycle scope")
            eventViewModel.loadEvents()
            eventViewModel.loadEventsLogs()
            eventViewModel.loadEventsForVolunteer(idVolunteer)
        }

        val statusBarColor = ContextCompat.getColor(this, R.color.nurdor_green_2)
        setStatusBarColor(statusBarColor)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbarHome)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        setSupportActionBar(toolbar)

        val fabAddEvents: FloatingActionButton = findViewById(R.id.fabAddEvent)
        fabAddEvents.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)
        }

        val homeContext = this
        eventsRcView = findViewById(R.id.eventsRcView)
        eventsRcView?.layoutManager = LinearLayoutManager(homeContext)
        eventsRcView?.itemAnimator = DefaultItemAnimator()

        // ideja - dodaj observere za sve eventove i event logove pored eventsForVolunteer observera
        eventsRcView?.adapter = EventsAdapter(mutableListOf<Event>())
        CoroutineScope(Dispatchers.Main).launch {
            (eventsRcView?.adapter as EventsAdapter).updateEvents(eventViewModel.getLoadedEventsByVolunteerId(idVolunteer))
        }

        eventViewModel.eventsForVolunteer.observe(this) { events ->
            Log.i("debugEventsList", "usao u OnCreate eventsForVolunteer observer. Events:\n")
            val adapter = (eventsRcView?.adapter as EventsAdapter)
            if(!events.isNullOrEmpty() && !compareLists(events, adapter.elements)) {
                adapter.updateEvents(events as MutableList<Event>)
            }
        }
        eventViewModel.allEvents.observe(this) {
            Log.i("debugEventsList", "usao u OnCreate allEvents observer")
            val adapter = (eventsRcView?.adapter as EventsAdapter)
            if(!eventViewModel.eventsForVolunteer.value.isNullOrEmpty()) {
                adapter.updateEvents(eventViewModel.eventsForVolunteer.value as MutableList<Event>)
            }
        }
        eventViewModel.eventsLogs.observe(this) {
            Log.i("debugEventsList", "usao u OnCreate eventsLogs observer")
            val adapter = (eventsRcView?.adapter as EventsAdapter)
            if(!eventViewModel.eventsForVolunteer.value.isNullOrEmpty()) {
                adapter.updateEvents(eventViewModel.eventsForVolunteer.value as MutableList<Event>)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        if(!PreferenceHelper.isAdmin(this)) {
            inflater.inflate(R.menu.menu_home, menu)
            Log.i("onCreateOptionsMenuMeth", "inflated for regular")
        } else {
            inflater.inflate(R.menu.menu_home_admin, menu)
            Log.i("onCreateOptionsMenuMeth", "inflated for admin")
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.itemSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            item.itemId == R.id.itemLogout -> {
                logout()
                true
            }
            PreferenceHelper.isAdmin(this) && item.itemId == R.id.itemAdminWindow -> {
                val intent = Intent(this, AdminWindowActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("debugEventsList", "usao u onPause")
        idVolunteer = PreferenceHelper.getIdVolunteer(this)
        lifecycleScope.launch {
            Log.i("debugEventsList", "usao u onPause lifecycle scope")
            eventViewModel.loadEvents()
            eventViewModel.loadEventsLogs()
            eventViewModel.loadEventsForVolunteer(idVolunteer)
        }
    }

    override fun onResume() {
        super.onResume()

        if(isFirstLaunch) {
            isFirstLaunch = false
            return
        }

        val ctx = this
        lifecycleScope.launch {
            Log.i("debugEventsList", "usao u onResume lifecycle scope")
            eventViewModel.loadEvents()
            eventViewModel.loadEventsLogs()
            eventViewModel.loadEventsForVolunteer(PreferenceHelper.getIdVolunteer(ctx))
        }
    }

    private fun logout() {
        PreferenceHelper.setLoggedIn(this, false)
        PreferenceHelper.setIsAdmin(this, false)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 (API 31) and above
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.statusBarColor = ContextCompat.getColor(window.context, R.color.nurdor_green_2)
        } else {
            window.statusBarColor = ContextCompat.getColor(window.context, R.color.nurdor_green_2)
        }
    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    private fun compareLists(l1: List<Event>, l2: List<Event>): Boolean {
        return l1.toSet() == l2.toSet()
    }
}