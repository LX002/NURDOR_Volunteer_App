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
        val dialogView = layoutInflater.inflate(R.layout.add_donation_dialog, null)

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
            when {
                response.contains("SUCCESS:") -> {
                    val numOfUpdatedRows = standViewModel.addDonation(donationDto)
                    val message = response.split(":")[1]
                    if(numOfUpdatedRows == 1) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "STAND_${donationDto.idStand} wasn't updated!", Toast.LENGTH_SHORT).show()
                    }
                }
                response.contains("ERROR:") -> {
                    val message = "Error during adding donation: ${response.split(":")[1]}"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}