package com.example.nurdor_volunteer_app_v3.fragment.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import android.widget.EditText
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class TimePickerDialogFragment() : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    companion object {
        fun newInstance(requestKey: String): TimePickerDialogFragment {
            return TimePickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("request_key", requestKey)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(requireContext(), this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val requestKey = requireArguments().getString("request_key") as String
        val time = LocalTime.of(hourOfDay, minute)
        val formatedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
        val pickerResult = Bundle().apply {
            putString("picked_time", formatedTime)
        }
        parentFragmentManager.setFragmentResult(requestKey, pickerResult)
    }
}