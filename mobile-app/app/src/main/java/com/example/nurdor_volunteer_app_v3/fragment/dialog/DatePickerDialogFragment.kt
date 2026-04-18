package com.example.nurdor_volunteer_app_v3.fragment.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class DatePickerDialogFragment() : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        fun newInstance(requestKey: String): DatePickerDialogFragment {
            return DatePickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("request_key", requestKey)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val requestKey = requireArguments().getString("request_key") as String
        val date = LocalDate.of(year, month + 1, day)
        val formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val pickerResult = Bundle().apply {
            putString("picked_date", formattedDate)
        }
        parentFragmentManager.setFragmentResult(requestKey, pickerResult)
    }
}