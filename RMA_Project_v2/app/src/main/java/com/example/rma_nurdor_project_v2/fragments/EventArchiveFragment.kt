package com.example.rma_nurdor_project_v2.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.rcv_adapters.EventArchiveAdapter
import com.example.rma_nurdor_project_v2.viewModel.EventArchiveViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EventArchiveFragment : Fragment() {

    private lateinit var collapsedFilterFrameView: View
    private lateinit var expandedFilterFrameView: View
    private lateinit var filterFrame: FrameLayout
    private lateinit var eventArchiveViewModel: EventArchiveViewModel

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedSort: Int = 0
    private var selectedCity: City = City("", "")
    private var searchResults = emptyList<Event>()
    private var archiveRcView: RecyclerView? = null
    //private var searchSwitch: Boolean = false
    private var isFirstLaunch = true
    private var loadedCities = emptyList<City>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventArchiveViewModel = ViewModelProvider(requireActivity())[EventArchiveViewModel::class.java]
        // Inflate the layout for this fragment
        collapsedFilterFrameView = inflater.inflate(R.layout.frame_filter_archive_collapsed, container, false)
        expandedFilterFrameView = inflater.inflate(R.layout.frame_filter_archive_expanded, container, false)
        return inflater.inflate(R.layout.fragment_event_archive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("eventArchiveDebug", "usao u on create")

        restoreAndLoadData()

        filterFrame = view.findViewById(R.id.filterArchiveFrame)

        initFiltersFrame()

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

        val setDisplayedEvent = { e: Event ->
            eventArchiveViewModel.displayedEvent = e
        }

        val getVolunteersAtEvent = { e: Event ->
            CoroutineScope(Dispatchers.IO).async {
                e.idEvent?.let { eventArchiveViewModel.getVolunteersAtEvent(it) }
            }
        }

        eventArchiveViewModel.getVolunteersAtEventAsync = getVolunteersAtEvent

        archiveRcView = view.findViewById(R.id.archiveRcView)
        archiveRcView?.itemAnimator = DefaultItemAnimator()
        archiveRcView?.layoutManager = LinearLayoutManager(requireContext())
        //archiveRcView?.adapter = EventArchiveAdapter(emptyList(), setDisplayedEvent, getCityName)
        val archivedEventsDef = CoroutineScope(Dispatchers.IO).async { eventArchiveViewModel.getArchivedEvents() }
        CoroutineScope(Dispatchers.Main).launch {
            archiveRcView?.adapter = EventArchiveAdapter(archivedEventsDef.await(), setDisplayedEvent, getCityName)

            eventArchiveViewModel.archivedEvents.observe(viewLifecycleOwner) { events ->
                val adapter = archiveRcView?.adapter as EventArchiveAdapter
                if(!eventArchiveViewModel.searchSwitch && !compareLists("archived", adapter.elements, events)) {
                    adapter.updateContent(events)
                }
            }

            eventArchiveViewModel.searchResults.observe(viewLifecycleOwner) { results ->
                val adapter = archiveRcView?.adapter as EventArchiveAdapter
                if(eventArchiveViewModel.searchSwitch && !compareLists("search results", adapter.elements, results)) {
                    adapter.updateContent(results)
                }
            }
        }
    }

    private fun collapseFilterFrame() {
        filterFrame.removeAllViews()
        filterFrame.addView(collapsedFilterFrameView)
    }

    private fun expandFilterFrame() {
        filterFrame.removeAllViews()
        filterFrame.addView(expandedFilterFrameView)
    }

    private fun initFiltersFrame() {
        filterFrame.addView(collapsedFilterFrameView)
        // collapsed view
        val btnExpand: ImageButton = collapsedFilterFrameView.findViewById(R.id.btnExpandFilters2)
        btnExpand.setOnClickListener { expandFilterFrame() }

        // expanded view
        selectedMonth = eventArchiveViewModel.selectedMonth
        selectedYear = eventArchiveViewModel.selectedYear
        selectedSort = eventArchiveViewModel.selectedSort

        val btnCollapse: ImageButton = expandedFilterFrameView.findViewById(R.id.btnCollapseFilters2)
        btnCollapse.setOnClickListener { collapseFilterFrame() }

        val spinnerYear: Spinner = expandedFilterFrameView.findViewById(R.id.spinnerYear)
        val yearsString = mutableListOf<String>("")
        for(i in LocalDateTime.now().year downTo 2003) { yearsString.add(i.toString()) }
        val yearsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, yearsString)
        spinnerYear.adapter = yearsAdapter
        if(selectedYear != 0) {
            spinnerYear.setSelection(yearsAdapter.getPosition(selectedYear.toString()))
        } else {
            spinnerYear.setSelection(0)
        }

        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                yearsAdapter.getItem(p2)?.let {
                    selectedYear = if(p2 == 0) 0 else it.toInt()
                    eventArchiveViewModel.selectedYear = selectedYear
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val spinnerMonth: Spinner = expandedFilterFrameView.findViewById(R.id.spinnerMonth)
        val months: List<String> = resources.getStringArray(R.array.months).toList()
        val monthsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        spinnerMonth.adapter = monthsAdapter
        spinnerMonth.setSelection(selectedMonth)
        //spinnerMonth.setSelection(LocalDateTime.now().monthValue)
        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedMonth = p2
                eventArchiveViewModel.selectedMonth = selectedMonth
                Log.i("queryEventArchive", "$selectedMonth selected month")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val spinnerCity: Spinner = expandedFilterFrameView.findViewById(R.id.spinnerEventCity)
        val cityAdapterDef = CoroutineScope(Dispatchers.IO).async {
            ArrayAdapter<City>(requireContext(), android.R.layout.simple_spinner_dropdown_item, eventArchiveViewModel.getLoadedCities())
        }

        CoroutineScope(Dispatchers.Main).launch {
            val adapter = cityAdapterDef.await()
            spinnerCity.adapter = adapter
            spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    adapter.getItem(p2)?.let {
                        selectedCity = it
                        eventArchiveViewModel.selectedCity = it
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
            eventArchiveViewModel.allCities.observe(viewLifecycleOwner) { allCities ->
                adapter.clear()
                adapter.addAll(listOf(City("", "")) + allCities)
                adapter.notifyDataSetChanged()
                spinnerCity.setSelection(adapter.getPosition(eventArchiveViewModel.selectedCity))
                loadedCities = allCities
            }
        }

        val spinnerSort = expandedFilterFrameView.findViewById<Spinner>(R.id.spinnerSortArchiveBy)
        val sortAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("", "Name", "City (zipCode)", "Date"))
        spinnerSort.adapter = sortAdapter
        spinnerSort.setSelection(0)
        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedSort = p2
                eventArchiveViewModel.selectedSort = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val txtEventName: EditText = expandedFilterFrameView.findViewById(R.id.txtFilterEventName)
        txtEventName.doAfterTextChanged { text ->
            eventArchiveViewModel.eventName = text.toString()
        }
        txtEventName.setText(eventArchiveViewModel.eventName)

        val btnSearch: Button = expandedFilterFrameView.findViewById(R.id.btnSearchArchive)
        btnSearch.setOnClickListener {
            eventArchiveViewModel.searchSwitch = true
            CoroutineScope(Dispatchers.Main).launch {
                val eventName = txtEventName.text.toString()
                searchResults = eventArchiveViewModel.fetchFilterResults(eventName, selectedYear, selectedMonth, selectedCity.zipCode, selectedSort)
                if(searchResults.isEmpty()) {
                    when {
                        eventName.isBlank() && selectedYear == 0 && selectedMonth == 0 && selectedCity.zipCode.isBlank() && selectedSort == 0 -> Toast.makeText(requireActivity(), "All filter fields are blank!", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(requireActivity(), "No filter results!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val btnResetFilters: Button = expandedFilterFrameView.findViewById(R.id.btnResetFilterArchive)
        btnResetFilters.setOnClickListener {
            eventArchiveViewModel.searchSwitch = false
            val archivedEventsDef = CoroutineScope(Dispatchers.IO).async { eventArchiveViewModel.getArchivedEvents() }
            CoroutineScope(Dispatchers.Main).launch {
                (archiveRcView?.adapter as EventArchiveAdapter).updateContent(archivedEventsDef.await())
                eventArchiveViewModel.loadAllEvents() // trigeruje observere!
            }

        }


    }

    override fun onResume() {
        super.onResume()
        if(isFirstLaunch) {
            isFirstLaunch = false
            return
        }
        Log.i("eventArchiveDebug", "usao u on resume")
        restoreAndLoadData()
    }

    override fun onPause() {
        super.onPause()
        Log.i("eventArchiveDebug", "usao u on pausee")
        //restoreAndLoadData()
    }

    private fun restoreAndLoadData() {
        searchResults = if(eventArchiveViewModel.searchSwitch && eventArchiveViewModel.searchResults.value != null) {
            eventArchiveViewModel.searchResults.value!!
        } else {
            emptyList()
        }
        //ovde ponovni load
        if(!eventArchiveViewModel.searchSwitch) {
            lifecycleScope.launch {
                loadedCities = eventArchiveViewModel.getLoadedCities()
                eventArchiveViewModel.loadAllEvents()
                eventArchiveViewModel.loadAllCities()
                eventArchiveViewModel.loadAllVolunteers()
            }
        }
    }

    private fun compareLists(msg: String, l1: List<Event>, l2: List<Event>): Boolean {
        //Log.i("eventArchiveDebug", "metoda pozvana iz $msg observera ${l1.toSet() == l2.toSet()}")
        return l1.toSet() == l2.toSet()
    }
}