package com.example.nurdor_volunteer_app_v3.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.dto.standDto.DonationDto
import com.example.nurdor_volunteer_app_v3.viewModel.StandViewModel
import kotlinx.coroutines.launch

class AddDonationDialog: DialogFragment() {

    private lateinit var standViewModel: StandViewModel

    companion object {
        fun newInstance(idEvent: Int, idStand: Int): AddDonationDialog {
            return AddDonationDialog().apply {
                arguments = Bundle().apply {
                    putInt("idEvent", idEvent)
                    putInt("idStand", idStand)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val idEvent = requireArguments().getInt("idEvent")
        val idStand = requireArguments().getInt("idStand")
        standViewModel = ViewModelProvider(this)[StandViewModel::class]

        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_donation, null)

        val btnAddDonation = dialogView.findViewById<Button>(R.id.btnAddAmount)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val txtAmount = dialogView.findViewById<EditText>(R.id.txtAmount)

        btnAddDonation.setOnClickListener {
            val stringValue = txtAmount.text.toString()
            if(stringValue.isNotBlank()) {
                val parsedValue = stringValue.toIntOrNull()
                parsedValue?.let {
                    addDonation(DonationDto(parsedValue, idEvent, idStand))
                } ?: run {
                    Toast.makeText(requireContext(), "ERROR: Amount input is null", Toast.LENGTH_SHORT).show()
                }
            }


        }
        btnCancel.setOnClickListener { dismiss() }

        return dialogBuilder.setView(dialogView).create()
    }

    private fun addDonation(donationDto: DonationDto) {
        lifecycleScope.launch {
            val response = standViewModel.fetchDonationResponse(donationDto)
            if(response.contains("SUCCESS:")) {
                val numOfUpdatedRows = standViewModel.addDonation(donationDto)
                if(numOfUpdatedRows == 1) {
                    DisplayMessageDialog.newInstance("$response. Donation saved successfully", false).show(parentFragmentManager, "displayMessageDialogFragment")
                    dismiss()
                } else {
                    DisplayMessageDialog.newInstance("WARNING: ${response.split(":")[1].trim()}. Donation isn't saved into Room!", false).show(parentFragmentManager, "displayMessageDialogFragment")
                    dismiss()
                }
            } else {
                DisplayMessageDialog.newInstance(response, false).show(parentFragmentManager, "displayMessageDialogFragment")
                dismiss()
            }
        }
    }
}