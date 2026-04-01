package com.example.nurdor_volunteer_app_v3.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.adapters.HomeEventsAdapter
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper
import com.example.nurdor_volunteer_app_v3.viewModel.EventViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel

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

        val upcomingEventsAsync = CoroutineScope(Dispatchers.IO).async {
            val context = this@HomeActivity
            if(PreferenceHelper.isAdmin(context)) {
                eventViewModel.findUpcomingEventsForAdmin()
            } else {
                eventViewModel.findUpcomingEventsByVolunteerId(PreferenceHelper.getIdVolunteer(context))
            }
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
        eventsRcView.itemAnimator = DefaultItemAnimator()
        eventsRcView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.Main).launch {
            upcomingEventsAsync.await()
            val homeEventsAdapter = HomeEventsAdapter(mutableListOf())
            homeEventsAdapter.addEvents(eventViewModel.upcomingEvents.value)
            eventsRcView.adapter = homeEventsAdapter
        }
    }
}