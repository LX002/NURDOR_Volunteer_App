package com.example.nurdor_volunteer_app_v3.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.utils.ImageUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PickEventsAdapter(
    var events: MutableList<Event>,
    val pickOrUnpickEvent: (Event, Boolean) -> Boolean,
    val getCityNameByZipCode: (String) -> String?
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
            txtCity.text = getCityNameByZipCode(event.city) ?: event.city

            val cbPickEvent = itemView.findViewById<CheckBox>(R.id.cbPickEvent)
            cbPickEvent.setOnCheckedChangeListener { _, checked ->
                pickOrUnpickEvent(event, checked)
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