package com.example.nurdor_volunteer_app_v3.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.utils.DateTimeUtils
import com.example.nurdor_volunteer_app_v3.utils.ImageUtils
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper
import java.time.LocalDateTime
import java.util.Base64
import androidx.core.net.toUri
import com.example.nurdor_volunteer_app_v3.fragment.dialog.EnrolledVolunteersDialog
import com.example.nurdor_volunteer_app_v3.fragment.dialog.SetUpStandsForEventDialog
import com.example.nurdor_volunteer_app_v3.utils.PdfDownloadWorker

class HomeEventsAdapter(private var events: MutableList<Event>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPosition: Int = -1
    companion object {
        private const val NORMAL_LAYOUT = 0
        private const val EXPANDED_LAYOUT = 1

        private fun isFirstActionButonEnabled(event: Event, itemView: View): Boolean {
            return if(PreferenceHelper.isAdmin(itemView.context)) {
                LocalDateTime.now().isAfter(event.startTime)
                        && LocalDateTime.now().isBefore(event.endTime)
            } else {
                LocalDateTime.now().isAfter(event.startTime)
                        && LocalDateTime.now().isBefore(event.endTime)
                        && event.isStarted == 1.toByte()
            }
        }
    }

    val elements: List<Event>
        get() = events

    class EventViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(event: Event) {
            val txtEventName: TextView = itemView.findViewById(R.id.txtEventName)
            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val txtTime: TextView = itemView.findViewById(R.id.txtTime)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)

            val formattedStartTime = DateTimeUtils.changeDateFormat(event.startTime)
            txtEventName.text = event.eventName
            txtDate.text = formattedStartTime.split(" ")[0]
            txtTime.text = formattedStartTime.split(" ")[1]

            ImageUtils.loadImageIntoIntoImageView(event.eventImg, R.drawable.unknown_event ,imageView, itemView.context)
        }
    }

    class ExpandedEventViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(event: Event) {
            val isAdmin = PreferenceHelper.isAdmin(itemView.context)

            val txtEventName: TextView = itemView.findViewById(R.id.txtEventName)
            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val txtTime: TextView = itemView.findViewById(R.id.txtTime)
            val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val firstActionButton: Button = itemView.findViewById(R.id.btnJoin)
            val btnLocation: Button = itemView.findViewById(R.id.btnLocation)
            val btnShare: Button = itemView.findViewById(R.id.btnShare)
            val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
            val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)

            val formattedStartTime = DateTimeUtils.changeDateFormat(event.startTime)
            txtDate.text = formattedStartTime.split(" ")[0]
            txtTime.text = formattedStartTime.split(" ")[1]
            txtEventName.text = event.eventName
            txtDuration.text = "${DateTimeUtils.calculateDuration(event.startTime, event.endTime)} min"
            txtDescription.text = event.description
            txtLocation.text = if(!event.locationDesc.isNullOrBlank()) event.locationDesc else "Unknown"

            firstActionButton.isEnabled = isFirstActionButonEnabled(event, itemView)

            ImageUtils.loadImageIntoIntoImageView(event.eventImg, R.drawable.unknown_event, imageView, itemView.context)

            firstActionButton.setOnClickListener {
                if(isAdmin) {
                    // TODO(): start event block
                    // prvo npr dijalog koji daje ponudu za broj standova
                    event.idEvent?.let { SetUpStandsForEventDialog.newInstance(it).show((itemView.context as AppCompatActivity).supportFragmentManager, "setUpStandsForEventDialog") }
                    // nakon toga start eventa sa dva tab fragmenta - prisutni volonteri i standovi i njihov status
                    // refresh opcija za oba, zavrsavanjem se vraca na home screen
                    // admin ima i opciju u home meniju started events kojima moze opet pristupiti i pratiti stanje
                } else {
                    // TODO(): join event block
                //val eventIntent = Intent(itemView.context, EventActivity::class.java)
//                eventIntent.putExtra("idEvent", event.idEvent)
//                eventIntent.putExtra("eventName", event.eventName)
//                itemView.context.startActivity(eventIntent)
                }
            }

            btnLocation.setOnClickListener {
                val googleMapsUri =
                    "geo:0,0?q=${event.latitude},${event.longitude}(${Uri.encode(event.eventName)})".toUri()
                val mapIntent = Intent(Intent.ACTION_VIEW, googleMapsUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                itemView.context.startActivity(mapIntent)
            }

            btnShare.setOnClickListener {
                val workData = workDataOf("eventId" to event.idEvent, "eventName" to event.eventName)
                val downloadWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<PdfDownloadWorker>().setInputData(workData).build()
                if(itemView.context is AppCompatActivity) {
                    val appContext = itemView.context.applicationContext
                    WorkManager.getInstance(appContext).enqueue(downloadWorkRequest)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val type = if(position == selectedPosition) EXPANDED_LAYOUT else NORMAL_LAYOUT
        return type
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == EXPANDED_LAYOUT) {
            val view = inflater.inflate(R.layout.item_home_events_expanded, parent, false)
            ExpandedEventViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_home_events, parent, false)
            EventViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if(holder is EventViewHolder) {
            holder.bind(events[position])
        } else if (holder is ExpandedEventViewHolder) {
            holder.bind(events[position])
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = if(selectedPosition == position) -1 else position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }

        holder.itemView.setOnLongClickListener { view ->
            val context = view.context
            val idEvent = events[position].idEvent
            if(context is AppCompatActivity && idEvent != null) {
                EnrolledVolunteersDialog.newInstance(idEvent).show(context.supportFragmentManager, "enrolledVolunteersDialog")
            }
            true
        }
    }

    override fun getItemCount(): Int = elements.size

    fun addEvent(event: Event) {
        events.add(event)
        notifyItemChanged(events.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addEvents(eventsToAdd: List<Event>?) {
        eventsToAdd?.let {
            val startPosition = if(events.isEmpty()) 0 else events.size
            events.addAll(eventsToAdd)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeEvent(event: Event) {
        events.isNotEmpty().let {
            val removePos = events.indexOf(event)
            val success = events.remove(event)
            if(success) notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        events.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(events: MutableList<Event>) {
        this.events = events
        notifyDataSetChanged()
    }
}