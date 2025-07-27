package com.example.rma_nurdor_project_v2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rma_nurdor_project_v2.rcv_adapters.EventVolunteersAdapter
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.viewModel.EventViewModel
import com.example.rma_nurdor_project_v2.viewModel.ProfileDetailsViewModel
import com.example.rma_nurdor_project_v2.viewModel.VolunteerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventVolunteersFragment() : Fragment() {

    private lateinit var volunteerViewModel: VolunteerViewModel
    private lateinit var eventViewModel: EventViewModel
    private lateinit var profileDetailsViewModel: ProfileDetailsViewModel

    private var idEvent: Int? = null
    private var isFirstLaunch = true

    companion object {
        private const val ARG_ID_EVENT = "id_event"
        fun newInstance(idEvent: Int): EventVolunteersFragment {
            return EventVolunteersFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID_EVENT, idEvent)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        volunteerViewModel = ViewModelProvider(this)[VolunteerViewModel::class.java]
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        profileDetailsViewModel = ViewModelProvider(requireActivity())[ProfileDetailsViewModel::class.java]
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_volunteers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idEvent = arguments?.getInt(ARG_ID_EVENT)
        lifecycleScope.launch {
            eventViewModel.loadEventsLogs()
            idEvent?.let { volunteerViewModel.loadAllVolunteers(idEvent!!) }
        }

        val onItemDetailsClick = { v: Volunteer ->
            profileDetailsViewModel.volunteer = v
        }
        val volunteersRcView: RecyclerView = view.findViewById(R.id.volunteersRcView)
        val volunteersAdapter = if(volunteerViewModel.presentVolunteers.value.isNullOrEmpty()) {
            Log.i("presentVolunteersDebug", "present volunteers in viewmodel is null / empty")
            EventVolunteersAdapter(listOf<Volunteer>(), onItemDetailsClick)
        } else {
            Log.i("presentVolunteersDebug", "present volunteers in viewmodel is not null / empty")
            EventVolunteersAdapter(volunteerViewModel.presentVolunteers.value!!, onItemDetailsClick)
        }
        volunteersRcView.layoutManager = LinearLayoutManager(requireContext())
        volunteersRcView.itemAnimator = DefaultItemAnimator()
        volunteersRcView.adapter = volunteersAdapter

        volunteerViewModel.presentVolunteers.observe(viewLifecycleOwner) { volunteers ->
            Log.i("eventVolunteersFragment", "$volunteers")
            (volunteersRcView.adapter as EventVolunteersAdapter).updateVolunteers(volunteers)
        }

        eventViewModel.eventsLogs.observe(viewLifecycleOwner) {
            idEvent?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    val volunteers = volunteerViewModel.getLoadedPresentVolunteers(idEvent!!)
                    Log.i("eventVolunteersFragmentLoaded", "$volunteers")
                    (volunteersRcView.adapter as EventVolunteersAdapter).updateVolunteers(volunteers)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(isFirstLaunch) {
            isFirstLaunch = false
            return
        }
        lifecycleScope.launch {
            eventViewModel.loadEventsLogs()
            idEvent?.let { volunteerViewModel.loadAllVolunteers(idEvent!!) }
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            eventViewModel.loadEventsLogs()
            idEvent?.let { volunteerViewModel.loadAllVolunteers(idEvent!!) }
        }
    }
}