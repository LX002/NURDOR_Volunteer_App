package com.example.nurdor_volunteer_app_v3.fragment.dialog;

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.nurdor_volunteer_app_v3.R
import com.google.android.material.appbar.MaterialToolbar

class DisplayMessageDialog: DialogFragment() {
    companion object {
        fun newInstance(message: String, isResultRequired: Boolean): DisplayMessageDialog {
            return DisplayMessageDialog().apply {
                arguments = Bundle().apply {
                    putString("msg", message)
                    putBoolean("isResultRequired", isResultRequired)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val messageSplit = requireArguments().getString("msg")?.splitToSequence(":", ignoreCase = false, limit = 2)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_display_message, null)

        val toolbar = dialogView.findViewById<MaterialToolbar>(R.id.messageDialogToolbar)
        val txtMessage = dialogView.findViewById<TextView>(R.id.txtMessage)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        setUpComponents(toolbar, txtMessage, btnClose, messageSplit?.toList())
        dialogBuilder.setView(dialogView)
        return dialogBuilder.create()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpComponents(toolbar: MaterialToolbar, txtMessage: TextView, btnClose: Button, content: List<String>?) {
        content?.let {
            val (title, color) = when(content[0]) {
                "SUCCESS" -> Pair(getString(R.string.info), ContextCompat.getColor(requireContext(), R.color.nurdor_green))
                "WARNING" -> Pair(getString(R.string.info), ContextCompat.getColor(requireContext(), R.color.warning))
                "ERROR" -> Pair(getString(R.string.info), ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
                "EXCEPTION" -> Pair(getString(R.string.info), ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                else -> throw IllegalStateException("Invalid title parameter: DisplayMessageDialog.setUpComponents")
            }

            txtMessage.movementMethod = ScrollingMovementMethod()
            val msg = content[1]
            if(msg.contains("|")) {
                txtMessage.text = ""
                for((i, m) in msg.split("|").withIndex()) {
                    if(i == 0) txtMessage.text = content[0]
                    txtMessage.text = "${txtMessage.text}\n$m"
                }
            } else {
                txtMessage.text = msg.trim()
            }

            toolbar.title = title
            toolbar.setBackgroundColor(color)
            btnClose.setBackgroundColor(color)
        }
        btnClose.setOnClickListener {
            if(requireArguments().getBoolean("isResultRequired", false)) {
                parentFragmentManager.setFragmentResult("display_message_result", Bundle().apply {
                    putString("status", content?.get(0))
                })
            }
            dismiss()
        }
    }
}
