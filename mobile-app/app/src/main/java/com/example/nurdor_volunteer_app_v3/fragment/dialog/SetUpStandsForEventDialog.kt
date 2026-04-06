package com.example.nurdor_volunteer_app_v3.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.nurdor_volunteer_app_v3.R

class SetUpStandsForEventDialog: DialogFragment() {

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

        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.set_up_stands_for_event_dialog, null)

        val spinnerNumberOfStands = dialogView.findViewById<Spinner>(R.id.spinnerNumberOfStands)
        spinnerNumberOfStands.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf(1, 2, 3, 4, 5, 6)
        )

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener { dismiss() }

        val btnStartEvent = dialogView.findViewById<Button>(R.id.btnStartEvent)
        btnStartEvent.setOnClickListener {
            // TODO() start event
        }

        dialogBuilder.setView(dialogView)
        return dialogBuilder.create()
    }
}