package com.example.nurdor_volunteer_app_v3.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.nurdor_volunteer_app_v3.viewModel.EventsLogViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventsLogViewModel: EventsLogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        eventViewModel = ViewModelProvider(this)[EventViewModel::class]
        eventsLogViewModel = ViewModelProvider(this)[EventsLogViewModel::class]

        lifecycleScope.launch {
            eventViewModel.fetchAll()
            eventsLogViewModel.fetchAll()
        }

        val toolbar: MaterialToolbar = findViewById(R.id.toolbarHome)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        setSupportActionBar(toolbar)

        val fabAddEvents: FloatingActionButton = findViewById(R.id.fabAddEvent)
        fabAddEvents.setOnClickListener {
            //val intent = Intent(this, AddEventActivity::class.java)
            //startActivity(intent)
        }

        val eventsRcView: RecyclerView = findViewById(R.id.eventsRcView)
        val homeEventsAdapter = HomeEventsAdapter(mutableListOf())
        eventsRcView.itemAnimator = DefaultItemAnimator()
        eventsRcView.layoutManager = LinearLayoutManager(this)
        eventsRcView.adapter = homeEventsAdapter

        eventViewModel.upcomingEvents.observe(this) { events ->
            Log.i("observersLog", "upcoming events: $events")
            homeEventsAdapter.updateEvents(events as MutableList<Event>)
        }
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
            item.itemId == R.id.itemLogout -> {
                logout()
                true
            }
            PreferenceHelper.isAdmin(this) && item.itemId == R.id.itemAdminWindow -> {
//                val intent = Intent(this, AdminWindowActivity::class.java)
//                startActivity(intent)
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
}