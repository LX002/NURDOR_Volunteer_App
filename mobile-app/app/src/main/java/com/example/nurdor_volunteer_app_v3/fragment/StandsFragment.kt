package com.example.nurdor_volunteer_app_v3.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.adapters.StandAdapter
import com.example.nurdor_volunteer_app_v3.fragment.dialog.DisplayMessageDialog
import com.example.nurdor_volunteer_app_v3.fragment.dialog.SetUpStandsForEventDialog
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper
import com.example.nurdor_volunteer_app_v3.viewModel.StandViewModel
import kotlinx.coroutines.launch

class StandsFragment: Fragment() {

    private lateinit var standViewModel: StandViewModel
    companion object {
        fun newInstance(idEvent: Int): StandsFragment {
            return StandsFragment().apply {
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
        standViewModel = ViewModelProvider(this)[StandViewModel::class]
        return inflater.inflate(R.layout.fragment_stands, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()
        val idEvent = requireArguments().getInt("idEvent")
        val isAdmin = PreferenceHelper.isAdmin(requireContext())

        val rcvStands = view.findViewById<RecyclerView>(R.id.rcvStands)
        val standAdapter = StandAdapter(mutableListOf(), idEvent, isAdmin)
        rcvStands.layoutManager = LinearLayoutManager(requireContext())
        rcvStands.adapter = standAdapter

        val txtDonationsSum = view.findViewById<TextView>(R.id.txtDonationsSum)

        val btnRefresh = view.findViewById<ImageButton>(R.id.btnRefresh)
        btnRefresh.setOnClickListener {
            fetchData()
        }

        standViewModel.findByIdEvent(idEvent).observe(viewLifecycleOwner) { stands ->
            standAdapter.updateStands(stands)
            txtDonationsSum.text = stands.sumOf { s -> s.totalDonations }.toString()
        }

    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        lifecycleScope.launch {
            val message = standViewModel.fetchAll()
            if(isAdded && !message.contains("SUCCESS")  && !parentFragmentManager.isStateSaved) {
                DisplayMessageDialog.newInstance(message, false).show(parentFragmentManager, "displayMessageDialogFragment")
            }
        }
    }
}