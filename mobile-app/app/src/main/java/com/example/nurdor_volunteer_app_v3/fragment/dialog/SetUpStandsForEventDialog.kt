package com.example.nurdor_volunteer_app_v3.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.dto.eventDto.StartEventDto
import com.example.nurdor_volunteer_app_v3.viewModel.EventViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.StandViewModel
import kotlinx.coroutines.launch

class SetUpStandsForEventDialog: DialogFragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var standViewModel: StandViewModel
    companion object {
        fun newInstance(idEvent: Int): SetUpStandsForEventDialog {
            return SetUpStandsForEventDialog().apply {
                arguments = Bundle().apply {
                    putInt("idEvent", idEvent)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val idEvent = requireArguments().getInt("idEvent")
        eventViewModel = ViewModelProvider(this)[EventViewModel::class]
        standViewModel = ViewModelProvider(this)[StandViewModel::class]
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

        val btnStartEvent = dialogView.findViewById<Button>(R.id.btnAddAmount)
        btnStartEvent.setOnClickListener {
            startEvent(idEvent, spinnerNumberOfStands.selectedItem as Int)
        }

        dialogBuilder.setView(dialogView)
        return dialogBuilder.create()
    }

    private fun startEvent(
        idEvent: Int,
        numberOfStands: Int
    ) {
        lifecycleScope.launch {
            val startEventResult = eventViewModel.fetchStartEventResult(
                StartEventDto(idEvent, numberOfStands)
            )
            val standsIds = startEventResult.stands.map { s -> s.id }
            if (standsIds.isNotEmpty()) {
                val numOfUpdatedEventRows = eventViewModel.startOrEndEventByIdEvent(idEvent, true, 0)
                val numOfUpdatedStandRows = standViewModel.updateIdEventByStandIds(standsIds, idEvent)
                Log.i("eventOnOff", "$numOfUpdatedEventRows $numOfUpdatedStandRows")
                if (numOfUpdatedEventRows == 1 && numOfUpdatedStandRows == standsIds.size) {
                    Toast.makeText(requireContext(), "Event with id: $idEvent is successfully started!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Whoops, event with id: $idEvent didn't start!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Error: ${startEventResult.message}", Toast.LENGTH_LONG).show()
            }
            dismiss()
        }
    }
}