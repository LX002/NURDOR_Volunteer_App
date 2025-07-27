package com.example.rma_nurdor_project_v2.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.rma_nurdor_project_v2.R

class AboutDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_about_dialog, null)

        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        btnClose.setOnClickListener { dismiss() }

        builder.setView(dialogView)
        return builder.create()
    }
}