package com.example.nurdor_volunteer_app_v3.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.fragment.dialog.SetUpStandsForEventDialog
import com.example.nurdor_volunteer_app_v3.viewModel.StandViewModel

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

        val rcvStands = view.findViewById<RecyclerView>(R.id.rcvStands)
        val standAdapter
        rcvStands.layoutManager = LinearLayoutManager(requireContext())

    }
}