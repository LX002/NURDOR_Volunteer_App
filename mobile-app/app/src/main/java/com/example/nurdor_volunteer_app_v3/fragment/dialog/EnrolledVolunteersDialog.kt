package com.example.nurdor_volunteer_app_v3.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.adapters.EnrolledVolunteersAdapter
import com.example.nurdor_volunteer_app_v3.viewModel.VolunteerViewModel
import kotlinx.coroutines.launch
import kotlin.text.contains

class EnrolledVolunteersDialog: DialogFragment() {

    private lateinit var volunteerViewModel: VolunteerViewModel

    companion object {
        fun newInstance(idEvent: Int): EnrolledVolunteersDialog {
            return EnrolledVolunteersDialog().apply {
                arguments = Bundle().apply {
                    putInt("idEvent", idEvent)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val idEvent = requireArguments().getInt("idEvent")
        volunteerViewModel = ViewModelProvider(this)[VolunteerViewModel::class]
        lifecycleScope.launch {
            Log.i("enrolled", "fetching all vols")
            val message = volunteerViewModel.fetchAll()
            if(isAdded && !message.contains("SUCCESS")  && !parentFragmentManager.isStateSaved) {
                DisplayMessageDialog.newInstance(message, false).show(parentFragmentManager, "displayMessageDialogFragment")
            }
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_enrolled_volunteers, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())

        val rcEnrolledVolunteers = dialogView.findViewById<RecyclerView>(R.id.rcEnrolledVolunteers)
        val enrolledVolunteersAdapter = EnrolledVolunteersAdapter(mutableListOf())
        rcEnrolledVolunteers.layoutManager = LinearLayoutManager(requireContext())
        rcEnrolledVolunteers.adapter = enrolledVolunteersAdapter

        volunteerViewModel.findEnrolledVolunteersByIdEvent(idEvent).observe(this) { volunteers ->
            Log.i("enrolled", "enrolled vols: $volunteers")
            enrolledVolunteersAdapter.updateEnrolledVolunteers(volunteers)
        }

        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        btnClose.setOnClickListener {
            dismiss()
        }

        dialogBuilder.setView(dialogView)
        return dialogBuilder.create()
    }
}