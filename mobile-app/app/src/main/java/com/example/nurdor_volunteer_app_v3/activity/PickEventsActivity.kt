package com.example.nurdor_volunteer_app_v3.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.adapters.PickEventsAdapter
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.viewModel.CityViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.PickEventsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class PickEventsActivity : AppCompatActivity() {

    private lateinit var pickEventsViewModel: PickEventsViewModel
    private lateinit var cityViewModel: CityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pick_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pickEventsViewModel = ViewModelProvider(this)[PickEventsViewModel::class]
        cityViewModel = ViewModelProvider(this)[CityViewModel::class]

        lifecycleScope.launch {
            pickEventsViewModel.fetchAllEvents()
            cityViewModel.fetchAll()
        }

        val pickOrUnpickEvent = { event: Event, checked: Boolean ->
            if(checked)
                pickEventsViewModel.pickedEvents.add(event)
            else
                pickEventsViewModel.pickedEvents.remove(event)
        }

        Log.i("searchFilterLog", "${pickEventsViewModel.searchFilter.value}")
        val getCityNameByZipCode: (String) -> String? = { zipCode -> cityViewModel.findByZipCode(zipCode).value?.cityName }

        val rcvPickEvents = findViewById<RecyclerView>(R.id.rcvEventsToPick)
        val pickEventsAdapter = PickEventsAdapter(mutableListOf(), pickOrUnpickEvent, getCityNameByZipCode)
        rcvPickEvents.layoutManager = LinearLayoutManager(this)
        rcvPickEvents.adapter = pickEventsAdapter
        pickEventsViewModel.eventsToPick.observe(this) { events ->
            pickEventsAdapter.updateEvents(events)
        }

        val txtSearch = findViewById<EditText>(R.id.txtSearch)
        txtSearch.doAfterTextChanged { pickEventsViewModel.searchTxt = txtSearch.text.toString() }

        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        btnSearch.setOnClickListener {
            pickEventsViewModel.updateFilter()
            Log.i("searchFilterLog", "${pickEventsViewModel.searchFilter.value}")
        }
        btnSearch.setOnLongClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.menu_event_find_by, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                pickEventsViewModel.findBy = item.title.toString()
                pickEventsViewModel.updateFilter()
                true
            }
            popupMenu.show()
            true
        }

        val spinnerSortBy = findViewById<Spinner>(R.id.spinnerSortBy)
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("City ascending", "City descending", "Start time ascending", "Start time descending")
        )
        spinnerSortBy.adapter = spinnerAdapter
        spinnerSortBy.setSelection(2)
        spinnerSortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                pickEventsViewModel.sortBy = spinnerAdapter.getItem(p2) as String
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        pickEventsViewModel.searchFilter.observe(this) { f ->
            spinnerAdapter.clear()
            when(f.findBy) {
                "By event name" -> spinnerAdapter.addAll("City ascending", "City descending", "Start time ascending", "Start time descending")
                "By city" -> spinnerAdapter.addAll("Event name ascending", "Event name descending", "Start time ascending", "Start time descending")
                else -> return@observe
            }
        }

        val fabFinish = findViewById<FloatingActionButton>(R.id.fabFinish2)
        fabFinish.setOnClickListener {
            lifecycleScope.launch {
                if(pickEventsViewModel.pickedEvents.isNotEmpty()) {
                    val message = pickEventsViewModel.insertLogs()
                    Toast.makeText(this@PickEventsActivity, message, Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@PickEventsActivity, "Pick events first...", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}