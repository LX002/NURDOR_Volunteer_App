package com.example.rma_nurdor_project_v2.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.rcv_adapters.ArchivedEventVolunteersAdapter
import com.example.rma_nurdor_project_v2.viewModel.EventArchiveViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VolunteersListDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val eventArchiveViewModel = ViewModelProvider(requireActivity())[EventArchiveViewModel::class.java]

        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_archived_event_dialog, null)

        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        btnClose.setOnClickListener { dismiss() }

        val volunteersRcView = dialogView.findViewById<RecyclerView>(R.id.volunteersListRcView)
        volunteersRcView.itemAnimator = DefaultItemAnimator()
        volunteersRcView.layoutManager = LinearLayoutManager(requireActivity())
        CoroutineScope(Dispatchers.Main).launch {
            eventArchiveViewModel.getVolunteersAtEventAsync?.let { getVolunteers ->
                eventArchiveViewModel.displayedEvent?.let { e ->
                    val volunteers = getVolunteers.invoke(e).await()
                    if(!volunteers.isNullOrEmpty())
                        volunteersRcView.adapter = ArchivedEventVolunteersAdapter(volunteers)
                    else
                        Toast.makeText(requireContext(), "No volunteers were present in this event!", Toast.LENGTH_SHORT).show()
                }
            }
        }


        builder.setView(dialogView)
        return builder.create()
    }
}