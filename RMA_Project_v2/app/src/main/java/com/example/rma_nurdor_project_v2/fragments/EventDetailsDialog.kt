package com.example.rma_nurdor_project_v2.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.viewModel.EventDetailsViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.fragment.app.activityViewModels

class EventDetailsDialog : DialogFragment() {

    private lateinit var eventDetailsViewModel: EventDetailsViewModel
    private lateinit var event: Event
    private lateinit var onItemAdded: (Event) -> Unit
    private lateinit var onItemRemoved: (Event) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        eventDetailsViewModel = ViewModelProvider(requireActivity())[EventDetailsViewModel::class.java]
        Log.i("viewModelState", "${eventDetailsViewModel.event}, ${eventDetailsViewModel.onItemAdded}, ${eventDetailsViewModel.onItemRemoved}")

        eventDetailsViewModel.event?.let { event = eventDetailsViewModel.event!! }
        eventDetailsViewModel.onItemAdded?.let { onItemAdded = eventDetailsViewModel.onItemAdded!! }
        eventDetailsViewModel.onItemRemoved?.let { onItemRemoved = eventDetailsViewModel.onItemRemoved!! }

        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_event_details_dialog, null)

        val txtDetailEventName = dialogView.findViewById<TextView>(R.id.txtDetailEventName)
        val txtDuration = dialogView.findViewById<TextView>(R.id.txtDuration2)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)
        val btnRemove = dialogView.findViewById<Button>(R.id.btnRemove)
        val btnDetailLocation = dialogView.findViewById<ImageButton>(R.id.btnDetailLocation)
        val profileImageView = dialogView.findViewById<ImageView>(R.id.eventImageView)

        txtDetailEventName.text = event.eventName
        txtDuration.text = "${calculateDuration(event.startTime, event.endTime)} min"
        if(event.eventImg.isNullOrBlank()) {
            profileImageView.setImageResource(R.drawable.ic_launcher_background)
        } else {
            val mimeType = getMimeType(event.eventImg as String)
            mimeType?.let {
                Glide.with(requireActivity())
                    .asBitmap()
                    .load("data:$mimeType;base64," + event.eventImg)
                    .into(profileImageView)
            }
        }

        btnAdd.setOnClickListener {
            onItemAdded(event)
            dismiss()
        }

        btnRemove.setOnClickListener {
            onItemRemoved(event)
            dismiss()
        }

        btnDetailLocation.setOnClickListener {
            val googleMapsUri = Uri.parse("geo:0,0?q=${event.latitude},${event.longitude}(${Uri.encode(event.eventName)})")
            val mapIntent = Intent(Intent.ACTION_VIEW, googleMapsUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            requireActivity().startActivity(mapIntent)
        }

        builder.setView(dialogView)
        return builder.create()
    }

    private fun getMimeType(profileImg: String): String? {
        val typeIdentifier = profileImg.take(20)
        return when {
            typeIdentifier.startsWith("iVBORw0KGgo") -> "image/png"
            typeIdentifier.startsWith("/9j/") -> "image/jpeg"
            typeIdentifier.startsWith("R0lGODdh") -> "image/gif"
            else -> null
        }
    }

    private fun calculateDuration(startTime: String, endTime: String): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val startLocalDateTime = LocalDateTime.parse(startTime, formatter)
        val endLocalDateTime = LocalDateTime.parse(endTime, formatter)

        return Duration.between(startLocalDateTime, endLocalDateTime).seconds / 60
    }
}