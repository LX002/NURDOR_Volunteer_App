package com.example.nurdor_volunteer_app_v3.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.adapters.EnrolledVolunteersAdapter
import com.example.nurdor_volunteer_app_v3.viewModel.EventsLogViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.VolunteerViewModel
import kotlinx.coroutines.launch

class PresentVolunteersFragment: Fragment() {

    private lateinit var volunteerViewModel: VolunteerViewModel
    private lateinit var eventsLogViewModel: EventsLogViewModel

    companion object {
        fun newInstance(idEvent: Int): PresentVolunteersFragment {
            Log.i("StatisticsActivity", "new instance fragment")
            return PresentVolunteersFragment().apply {
                arguments = Bundle().apply {
                    putInt("idEvent", idEvent)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        volunteerViewModel = ViewModelProvider(this)[VolunteerViewModel::class]
        eventsLogViewModel = ViewModelProvider(this)[EventsLogViewModel::class]
        return inflater.inflate(R.layout.fragment_present_volunteers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()

        val idEvent = requireArguments().getInt("idEvent")
        val txtSearch = view.findViewById<EditText>(R.id.txtSearch)
        val btnSearch = view.findViewById<ImageButton>(R.id.btnSearch)
        val spinnerSortBy = view.findViewById<Spinner>(R.id.spinnerSortBy)
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("Default", "Name ascending", "Surname ascending", "Name descending", "Surname descending")
        )
        spinnerSortBy.adapter = spinnerAdapter
        spinnerSortBy.setSelection(volunteerViewModel.spinnerPos)
        spinnerSortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                volunteerViewModel.spinnerPos = p2
                volunteerViewModel.sortBy = spinnerAdapter.getItem(p2)?.lowercase() as String
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
        val rcvPresentVolunteers = view.findViewById<RecyclerView>(R.id.rcvStands)
        val presentVolunteersAdapter = EnrolledVolunteersAdapter(mutableListOf())
        rcvPresentVolunteers.layoutManager = LinearLayoutManager(requireContext())
        rcvPresentVolunteers.adapter = presentVolunteersAdapter

        volunteerViewModel.presentVolunteers.observe(viewLifecycleOwner) { volunteers ->
            Log.i("presentVolunteers", "$volunteers")
            Log.i("presentVolunteers", "${volunteerViewModel.searchFilter.value}")
            volunteers?.let { presentVolunteersAdapter.updateEnrolledVolunteers(volunteers) }
        }

        txtSearch.doAfterTextChanged { text ->
            volunteerViewModel.searchTxt = text.toString()
        }

        btnSearch.setOnClickListener {
            volunteerViewModel.updateFilter(idEvent)
        }
        btnSearch.setOnLongClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.menu_volunteers_find_by, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                volunteerViewModel.findBy = item.title.toString()
                true
            }
            popupMenu.show()
            true
        }
    }

    private fun fetchData() {
        lifecycleScope.launch {
            volunteerViewModel.fetchAll()
            eventsLogViewModel.fetchAll()
            volunteerViewModel.updateFilter(requireArguments().getInt("idEvent"))
        }
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }
}