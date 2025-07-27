package com.example.rma_nurdor_project_v2

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rma_nurdor_project_v2.fragments.DatePickerFragment
import com.example.rma_nurdor_project_v2.fragments.TimePickerFragment
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.model.EventsLog
import com.example.rma_nurdor_project_v2.rcv_adapters.AddEventsAdapter
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.example.rma_nurdor_project_v2.viewModel.AddEventsViewModel
import com.example.rma_nurdor_project_v2.viewModel.EventDetailsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AddEventActivity : AppCompatActivity() {

    private lateinit var addEventsViewModel: AddEventsViewModel
    private lateinit var eventDetailsViewModel: EventDetailsViewModel
    private lateinit var collapsedFilterFrameView: View
    private lateinit var expandedFilterFrameView: View
    private lateinit var filtersFrame: FrameLayout
    private lateinit var spinnerCity: Spinner
    private lateinit var spinnerSortBy: Spinner


    private var selectedEvents = mutableListOf<Event>()
    private var pickedDate: LocalDate? = null
    private var pickedTime: LocalTime? = null
    private var selectedCity: City? = null
    private var selectedSort: Int = 0
    private var startDateTime: LocalDateTime? = null
    private var searchResults: List<Event> = emptyList()
    private var loadedCities: List<City> = emptyList()
    private var isFirstLaunch = true

    private lateinit var txtNearestEvents: TextView
    private lateinit var txtOtherEvents: TextView
    private lateinit var txtFilteredEvents: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addEventsViewModel = ViewModelProvider(this)[AddEventsViewModel::class.java]
        eventDetailsViewModel = ViewModelProvider(this)[EventDetailsViewModel::class.java]
        if(addEventsViewModel.searchResults.value.isNullOrEmpty()) {
            lifecycleScope.launch {
                loadedCities = addEventsViewModel.getLoadedCities()
                addEventsViewModel.loadAllEvents()
                addEventsViewModel.loadAllEventsLogs()
                addEventsViewModel.loadAllCities()
            }
        }


        setStatusBarColor(ContextCompat.getColor(this, R.color.nurdor_green_2))

        val toolbar: MaterialToolbar = findViewById(R.id.toolbarAddEvents)
        val selectedEvents = addEventsViewModel.storedSelection
        toolbar.title = "Add your events (${selectedEvents.size})"
        toolbar.setNavigationOnClickListener { finish() }
        setSupportActionBar(toolbar)

        txtNearestEvents = findViewById(R.id.textView22)
        txtOtherEvents = findViewById(R.id.textView21)
        txtFilteredEvents = findViewById(R.id.textView23)

        filtersFrame = findViewById(R.id.frameFilters)

        collapsedFilterFrameView = layoutInflater.inflate(R.layout.frame_filter_collapsed, filtersFrame, false)
        expandedFilterFrameView = layoutInflater.inflate(R.layout.frame_filter_expanded, filtersFrame, false)

        filtersFrame.addView(collapsedFilterFrameView)

        initFiltersFrame()

        val isSelectedEvent = { e: Event -> selectedEvents.contains(e) }

        val onItemClicked = { e: Event, onItemAdded: (Event) -> Unit, onItemRemoved: (Event) -> Unit ->
            eventDetailsViewModel.event = e
            eventDetailsViewModel.onItemAdded = onItemAdded
            eventDetailsViewModel.onItemRemoved = onItemRemoved
        }

        val onItemAdded = { event: Event, cardView: CardView, itemView: View ->
            if(!selectedEvents.contains(event)) {
                selectedEvents.add(event)
                Log.i("listSizes", "${selectedEvents.size} <----> ${addEventsViewModel.storedSelection.size}")
                toolbar.title = "Add your events (${selectedEvents.size})"
                cardView.setCardBackgroundColor(getCardStyle().first)
                getAllTextViewsIn(cardView).forEach {
                    it.setTextColor(getTextStyle().first)
                }
            }
        }

        val onItemRemoved = { event: Event, cardView: CardView, itemView: View ->
            if(selectedEvents.contains(event)) {
                selectedEvents.remove(event)
                Log.i("listSizes", "${selectedEvents.size} <----> ${addEventsViewModel.storedSelection.size}")
                toolbar.title = "Add your events (${selectedEvents.size})"
                cardView.setCardBackgroundColor(getCardStyle().second)
                getAllTextViewsIn(cardView).forEach {
                    it.setTextColor(getTextStyle().second)
                }
            }
        }

        val getCityName: (Event) -> String = { e: Event ->
            var cityName = ""
            var i = 0
            while(cityName == "" && i < loadedCities.size) {
                val loadedCity = loadedCities[i]
                if(loadedCity.zipCode == e.city) {
                    cityName = loadedCity.cityName
                }
                i++
            }
            cityName
        }

        val nearestEventsRcView: RecyclerView = findViewById(R.id.nearestEventsRcView)
        nearestEventsRcView.layoutManager = LinearLayoutManager(this)
        nearestEventsRcView.itemAnimator = DefaultItemAnimator()
        nearestEventsRcView.adapter = AddEventsAdapter(mutableListOf<Event>(), onItemAdded, onItemRemoved, isSelectedEvent, getCardStyle(), getTextStyle(), onItemClicked, getCityName)

        val otherEventsRcView: RecyclerView = findViewById(R.id.otherEventsRcView)
        otherEventsRcView.layoutManager = LinearLayoutManager(this)
        otherEventsRcView.itemAnimator = DefaultItemAnimator()
        otherEventsRcView.adapter = AddEventsAdapter(mutableListOf<Event>(), onItemAdded, onItemRemoved, isSelectedEvent, getCardStyle(), getTextStyle(), onItemClicked, getCityName)

        val nearestCity = PreferenceHelper.getVolunteerNearestCity(this)
        CoroutineScope(Dispatchers.Main).launch {
            Log.i("retrofitApi1", "coroutine za update")
            Log.i("retrofitApi1", "nearest: ${addEventsViewModel.assignNearestEvents(nearestCity)}")
            Log.i("retrofitApi1", "other za update ${addEventsViewModel.assignOtherEvents(nearestCity)}")
            if(addEventsViewModel.searchResults.value.isNullOrEmpty()) {
                (nearestEventsRcView.adapter as AddEventsAdapter).updateContent(addEventsViewModel.assignNearestEvents(nearestCity))
                (otherEventsRcView.adapter as AddEventsAdapter).updateContent(addEventsViewModel.assignOtherEvents(nearestCity))
            }
        }

        val searchRcView: RecyclerView = findViewById(R.id.searchRcView)
        searchRcView.layoutManager = LinearLayoutManager(this)
        searchRcView.itemAnimator = DefaultItemAnimator()
        searchRcView.adapter = AddEventsAdapter(mutableListOf<Event>(), onItemAdded, onItemRemoved, isSelectedEvent, getCardStyle(), getTextStyle(), onItemClicked, getCityName)

        addEventsViewModel.nearestEvents.observe(this) { nearestEvents ->
            val adapter = (nearestEventsRcView.adapter as AddEventsAdapter)
            val searchAdapter = (searchRcView.adapter as AddEventsAdapter)
            if(addEventsViewModel.searchResults.value.isNullOrEmpty()) {
                adapter.updateContent(nearestEvents)
                searchAdapter.updateContent(emptyList())
            } else {
                adapter.updateContent(emptyList())
            }
        }

        addEventsViewModel.otherEvents.observe(this) { otherEvents ->
            val adapter = (otherEventsRcView.adapter as AddEventsAdapter)
            val searchAdapter = (searchRcView.adapter as AddEventsAdapter)
            if(addEventsViewModel.searchResults.value.isNullOrEmpty()) {
                adapter.updateContent(otherEvents)
                searchAdapter.updateContent(emptyList())
            } else {
                adapter.updateContent(emptyList())
            }
        }

        addEventsViewModel.searchResults.observe(this) { searchResults ->
            val otherAdapter = (otherEventsRcView.adapter as AddEventsAdapter)
            val nearestAdapter = (nearestEventsRcView.adapter as AddEventsAdapter)
            val searchAdapter = (searchRcView.adapter as AddEventsAdapter)
            if(!searchResults.isNullOrEmpty()) {
                searchAdapter.updateContent(searchResults)
                nearestAdapter.updateContent(emptyList())
                otherAdapter.updateContent(emptyList())
            }
        }

        val fabFinish: FloatingActionButton = findViewById(R.id.fabFinish2)
        fabFinish.setOnClickListener {
            val context = this
            val initLogs = mutableListOf<EventsLog>()
            val idVolunteer = PreferenceHelper.getIdVolunteer(context)
            selectedEvents.forEach {
                initLogs.add(EventsLog(volunteer = idVolunteer, event = it.idEvent!!, isPresent = 0, note = "initLog"))
            }
            CoroutineScope(Dispatchers.Main).launch {
                addEventsViewModel.loadAllEventsLogs() // ponovo ucitavanje logova pred insert u events_log!
                val result = addEventsViewModel.insertInitLogs(initLogs)
                if(result == 0) {
                    Toast.makeText(context, "Selected events are added to your list!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Something went wrong! Selected events were not added to your list", Toast.LENGTH_SHORT).show()
                }
                context.finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("debugAddEventActivity", "usao u on pause")
        if(addEventsViewModel.searchResults.value.isNullOrEmpty()) {
            lifecycleScope.launch {
                addEventsViewModel.loadAllEvents()
                addEventsViewModel.loadAllEventsLogs()
                addEventsViewModel.loadAllCities()
            }
        }
        selectedEvents = addEventsViewModel.storedSelection
    }

    override fun onResume() {
        super.onResume()
        if(isFirstLaunch) {
            isFirstLaunch = false
            return
        }
        if(addEventsViewModel.searchResults.value.isNullOrEmpty()) {
            lifecycleScope.launch {
                addEventsViewModel.loadAllEvents()
                addEventsViewModel.loadAllEventsLogs()
                addEventsViewModel.loadAllCities()
            }
        }
        selectedEvents = addEventsViewModel.storedSelection
    }

    private fun collapseFiltersFrame() {
        filtersFrame.removeAllViews()
        filtersFrame.addView(collapsedFilterFrameView)
    }

    private fun expandFiltersFrame() {
        filtersFrame.removeAllViews()
        filtersFrame.addView(expandedFilterFrameView)
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

    private fun initFiltersFrame() {
        // collapsed
        val btnExpandFilters: ImageButton = collapsedFilterFrameView.findViewById(R.id.btnExpandFilters)
        btnExpandFilters.setOnClickListener { expandFiltersFrame() }

        // expanded
        val btnCollapseFilters: ImageButton = expandedFilterFrameView.findViewById(R.id.btnCollapseFilters)
        val btnFilterStartDate: ImageButton = expandedFilterFrameView.findViewById(R.id.btnFilterStartDate)
        val btnFilterStartTime: ImageButton = expandedFilterFrameView.findViewById(R.id.btnFilterStartTime)
        val btnSearch: Button = expandedFilterFrameView.findViewById(R.id.btnSearch)
        val btnResetFilters: Button = expandedFilterFrameView.findViewById(R.id.btnResetFilters)
        val txtFilterEventName: EditText = expandedFilterFrameView.findViewById(R.id.filterTxtEventName)
        val txtFilterStartDate: TextView = expandedFilterFrameView.findViewById(R.id.filterTxtStartDate)
        val txtFilterStartTime: TextView = expandedFilterFrameView.findViewById(R.id.filterTxtStartTime)
        spinnerCity = expandedFilterFrameView.findViewById(R.id.filterSpinnerCity)
        spinnerSortBy = expandedFilterFrameView.findViewById(R.id.spinnerSortBy)

        txtFilterEventName.doAfterTextChanged {
            addEventsViewModel.eventName = it.toString()
        }
        txtFilterEventName.setText(addEventsViewModel.eventName)
        selectedSort = addEventsViewModel.selectedSort

        if(addEventsViewModel.pickedDate != null) {
            pickedDate = addEventsViewModel.pickedDate
            txtFilterStartDate.text = pickedDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }

        if(addEventsViewModel.pickedTime != null) {
            pickedTime = addEventsViewModel.pickedTime
            txtFilterStartTime.text = pickedTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        val adapterDef = CoroutineScope(Dispatchers.IO).async {
            ArrayAdapter(this@AddEventActivity, android.R.layout.simple_spinner_dropdown_item, addEventsViewModel.getLoadedCities())
        }
        CoroutineScope(Dispatchers.Main).launch {
            val cityAdapter = adapterDef.await()
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCity.adapter = cityAdapter
            spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedCity = cityAdapter.getItem(p2)
                    addEventsViewModel.selectedCity = selectedCity
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.e("AddEventActivityFilter", "Nothing selected in city spinner!")
                }
            }

            addEventsViewModel.allCities.observe(this@AddEventActivity) { allCities ->
                cityAdapter.clear()
                cityAdapter.addAll(listOf(City("", "")) + allCities)
                cityAdapter.notifyDataSetChanged()
                loadedCities = allCities
                spinnerCity.setSelection(cityAdapter.getPosition(addEventsViewModel.selectedCity))
            }
        }

        val sortTypes: List<String> = resources.getStringArray(R.array.sort_add_events_types).toList()
        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortTypes)// kasnije u resursima napravi srp / eng verziju sting array-a
        spinnerSortBy.adapter = sortAdapter
        spinnerSortBy.setSelection(selectedSort)
        spinnerSortBy.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedSort = p2
                addEventsViewModel.selectedSort = selectedSort
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        btnCollapseFilters.setOnClickListener { collapseFiltersFrame() }
        btnFilterStartDate.setOnClickListener {
            val datePickerFragment = DatePickerFragment(null, txtFilterStartDate)
            datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
        }
        btnFilterStartTime.setOnClickListener {
            val timePickerFragment = TimePickerFragment(null, txtFilterStartTime)
            timePickerFragment.show(supportFragmentManager, "TimePickerFragment")
        }
        btnSearch.setOnClickListener {
            Log.i("debugAddEventActivity", "search klik")
            CoroutineScope(Dispatchers.Main).launch {
                //addEventsViewModel.loadAllEvents()
                searchResults = addEventsViewModel.assignFilterResults(txtFilterEventName.text.toString(), selectedCity?.zipCode, startDateTime, selectedSort)
                if(searchResults.isEmpty()) {
                    when {
                        txtFilterEventName.text.toString().isBlank() && selectedCity?.zipCode.isNullOrBlank() && startDateTime == null && selectedSort == 0 -> Toast.makeText(this@AddEventActivity, "All filter fields are empty!", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@AddEventActivity, "No filter results", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnResetFilters.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                //addEventsViewModel.loadAllEvents()
                addEventsViewModel.searchResults.value = emptyList()
                addEventsViewModel.resetFilters()
            }
        }

        txtFilterStartDate.doAfterTextChanged {
            updateStartDateTime(txtFilterStartDate.text, txtFilterStartTime.text)
        }
        txtFilterStartTime.doAfterTextChanged {
            updateStartDateTime(txtFilterStartDate.text, txtFilterStartTime.text)
        }

    }

    private fun updateStartDateTime(dateCharSequence: CharSequence?, timeCharSequence: CharSequence?) {
        Log.i("checkDateAddEvent", "$dateCharSequence $timeCharSequence")
        if(dateCharSequence != "Pick start date" && !dateCharSequence.isNullOrBlank()) {
            pickedDate = LocalDate.parse(dateCharSequence, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            addEventsViewModel.pickedDate = pickedDate
        }
        if(timeCharSequence != "Pick start time" && !timeCharSequence.isNullOrBlank()) {
            pickedTime = LocalTime.parse(timeCharSequence, DateTimeFormatter.ofPattern("HH:mm"))
            addEventsViewModel.pickedTime = pickedTime
        }

        if(pickedDate != null && pickedTime != null)
            startDateTime = LocalDateTime.of(pickedDate, pickedTime)
    }

    private fun compareLists(l1: List<Event>, l2: List<Event>): Boolean {
        return l1.toSet() == l2.toSet()
    }

    private fun isDarkMode(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val uiModeManager = this.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
        } else {
            (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
    }

    private fun getCardStyle(): Pair<Int, Int> {
        val selectedCtx = ContextThemeWrapper(this, R.style.CardView_Selected_1)


        val defaultCtx = if(isDarkMode()) {
            ContextThemeWrapper(this, R.style.CardView_Dark_1)
        } else {
            ContextThemeWrapper(this, R.style.CardView_Light_1)
        }

        val selectedAtrr = selectedCtx.obtainStyledAttributes(intArrayOf(
            androidx.cardview.R.attr.cardBackgroundColor
        ))
        val defaultAtrr = defaultCtx.obtainStyledAttributes(intArrayOf(
            androidx.cardview.R.attr.cardBackgroundColor
        ))

        val stylePair = Pair(
            selectedAtrr.getColor(0, 0),
            defaultAtrr.getColor(0, 0)
        )
        selectedAtrr.recycle()
        defaultAtrr.recycle()
        return stylePair
    }

    private fun getTextStyle(): Pair<Int, Int> {
        val selectedCtx = ContextThemeWrapper(this, R.style.TextView_Selected)

        val defaultCtx = if(isDarkMode()) {
            ContextThemeWrapper(this, R.style.TextView_Dark)
        } else {
            ContextThemeWrapper(this, R.style.TextView_Light)
        }

        val selectedAtrr = selectedCtx.obtainStyledAttributes(intArrayOf(
            android.R.attr.textColor
        ))
        val defaultAtrr = defaultCtx.obtainStyledAttributes(intArrayOf(
            android.R.attr.textColor
        ))

        val stylePair = Pair(
            selectedAtrr.getColor(0, 0),
            defaultAtrr.getColor(0, 0)
        )
        selectedAtrr.recycle()
        defaultAtrr.recycle()
        return stylePair
    }

    private fun getAllTextViewsIn(viewGroup: ViewGroup): List<TextView> {
        val txtViews = mutableListOf<TextView>()
        for(i in 0 until viewGroup.childCount) {
            if(viewGroup.getChildAt(i) is TextView) txtViews.add(viewGroup.getChildAt(i) as TextView)
        }
        return txtViews
    }
}