package com.example.nurdor_volunteer_app_v3.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.utils.ImageUtils
import com.example.nurdor_volunteer_app_v3.worker.PdfDownloadWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PickEventsAdapter(
    var events: MutableList<Event>,
    val pickOrUnpickEvent: (Event, Boolean) -> Boolean,
    val getCityNameByZipCode: suspend (String) -> String?
): RecyclerView.Adapter<PickEventsAdapter.EventToPickViewHolder>() {

    inner class EventToPickViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(event: Event) {
            val startDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(event.startTime)
            val (date, time) = startDateTime.split(" ")

            val imgEvent = itemView.findViewById<ImageView>(R.id.imgEvent)
            ImageUtils.loadImageIntoIntoImageView(event.eventImg, R.drawable.unknown_event, imgEvent, itemView.context)

            val txtEventName = itemView.findViewById<TextView>(R.id.txtEventName)
            txtEventName.text = event.eventName

            val txtDate = itemView.findViewById<TextView>(R.id.txtDate)
            txtDate.text = date

            val txtTime = itemView.findViewById<TextView>(R.id.txtTime)
            txtTime.text = time

            val txtCity = itemView.findViewById<TextView>(R.id.txtCity)
            CoroutineScope(Dispatchers.Main).launch {
                txtCity.text = getCityNameByZipCode(event.city) ?: event.city
            }

            val cbPickEvent = itemView.findViewById<CheckBox>(R.id.cbPickEvent)
            cbPickEvent.setOnCheckedChangeListener { _, checked ->
                pickOrUnpickEvent(event, checked)
            }

            val btnDownloadBrochure = itemView.findViewById<ImageButton>(R.id.btnDownloadBrochure)
            btnDownloadBrochure.setOnClickListener {
                val workData = workDataOf("eventId" to event.idEvent, "eventName" to event.eventName)
                val downloadWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<PdfDownloadWorker>().setInputData(workData).build()
                if(itemView.context is AppCompatActivity) {
                    val appContext = itemView.context.applicationContext
                    WorkManager.getInstance(appContext).enqueue(downloadWorkRequest)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventToPickViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_pick_events, parent, false)

        return EventToPickViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventToPickViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(newEvents: List<Event>) {
        events = newEvents as MutableList
        notifyDataSetChanged()
    }

}