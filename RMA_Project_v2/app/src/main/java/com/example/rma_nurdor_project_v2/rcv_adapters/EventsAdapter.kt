package com.example.rma_nurdor_project_v2.rcv_adapters

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.example.rma_nurdor_project_v2.EventActivity
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.utils.PdfDownloadWorker
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventsAdapter(private var events: MutableList<Event>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPosition: Int? = null

    companion object {
        private const val NORMAL_LAYOUT = 0
        private const val EXPANDED_LAYOUT = 1

        fun changeDateFormat(inputDateString: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val inputDate = LocalDateTime.parse(inputDateString, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            val outputDate = inputDate.format(outputFormatter)
            return outputDate.toString()
        }

        fun calculateDuration(startTime: String, endTime: String): Long {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            val startLocalDateTime = LocalDateTime.parse(startTime, formatter)
            val endLocalDateTime = LocalDateTime.parse(endTime, formatter)

            return Duration.between(startLocalDateTime, endLocalDateTime).seconds / 60
        }
    }

    val elements: List<Event>
        get() = events

    class EventViewHolder(view: View) : ViewHolder(view) {
        fun bind(event: Event) {
            val txtEventName: TextView = itemView.findViewById(R.id.txtEventName)
            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val txtTime: TextView = itemView.findViewById(R.id.txtTime)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)

            val formattedStartTime = changeDateFormat(event.startTime) // dodaje se T
            txtEventName.text = event.eventName
            txtDate.text = formattedStartTime.split(" ")[0]
            txtTime.text = formattedStartTime.split(" ")[1]


            if(event.eventImg != null) {
                //Log.i("retrofitApi1", "Event img first 10: ${event.eventImg.take(10)}")
                val mimeType = getMimeType(event.eventImg)
                mimeType?.let {
                    Glide.with(itemView.context)
                        .asBitmap()
                        .load("data:$mimeType;base64," + event.eventImg)
                        .override(250, 250)
                        .centerCrop()
                        .into(imageView)
                }
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }
        }

        private fun getMimeType(eventImg: String): String? {
            val typeIdentifier = eventImg.take(20)
            return when {
                typeIdentifier.startsWith("iVBORw0KGgo") -> "image/png"
                typeIdentifier.startsWith("/9j/") -> "image/jpeg"
                typeIdentifier.startsWith("R0lGODdh") -> "image/gif"
                else -> null
            }
        }
    }

    class ExpandedEventViewHolder(view: View) : ViewHolder(view) {
        fun bind(event: Event) {
            val txtEventName: TextView = itemView.findViewById(R.id.txtEventName)
            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val txtTime: TextView = itemView.findViewById(R.id.txtTime)
            val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val btnJoin: Button = itemView.findViewById(R.id.btnJoin)
            val btnLocation: Button = itemView.findViewById(R.id.btnLocation)
            val btnShare: Button = itemView.findViewById(R.id.btnShare)
            val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
            val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)

            val formattedStartTime = changeDateFormat(event.startTime)
            val formattedEndTime = changeDateFormat(event.endTime)
            txtEventName.text = event.eventName
            txtDate.text = formattedStartTime.split(" ")[0]
            txtTime.text = formattedStartTime.split(" ")[1]
            txtDuration.text = "${calculateDuration(formattedStartTime, formattedEndTime)} min"
            txtDescription.text = event.description
            txtLocation.text = if(!event.locationDesc.isNullOrBlank()) event.locationDesc else "Unknown"
            btnJoin.isEnabled = LocalDateTime.now().isAfter(LocalDateTime.parse(formattedStartTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                              && LocalDateTime.now().isBefore(LocalDateTime.parse(formattedEndTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))

            if(event.eventImg != null) {
                //Log.i("retrofitApi1", "Event img first 10: ${event.eventImg.take(10)}")
                val mimeType = getMimeType(event.eventImg)
                mimeType?.let {
                    Glide.with(itemView.context)
                        .asBitmap()
                        .load("data:$mimeType;base64," + event.eventImg)
                        .override(250, 250)
                        .centerCrop()
                        .into(imageView)
                }
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }

            btnJoin.setOnClickListener {
                val eventIntent = Intent(itemView.context, EventActivity::class.java)
                eventIntent.putExtra("idEvent", event.idEvent)
                eventIntent.putExtra("eventName", event.eventName)
                itemView.context.startActivity(eventIntent)
            }

            btnLocation.setOnClickListener {
                val googleMapsUri =
                    Uri.parse("geo:0,0?q=${event.latitude},${event.longitude}(${Uri.encode(event.eventName)})")
                val mapIntent = Intent(Intent.ACTION_VIEW, googleMapsUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                itemView.context.startActivity(mapIntent)
            }

            btnShare.setOnClickListener {
//                CoroutineScope(Dispatchers.Main).launch {
//                    val pdfUri = PdfUtils.downloadPdf(itemView.context, event.idEvent!!, event.eventName + "-info.pdf")
//                    openPdf(pdfUri, itemView.context)
//                }

                val workData = workDataOf("eventId" to event.idEvent!!, "eventName" to event.eventName)
                val downloadWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<PdfDownloadWorker>().setInputData(workData).build()
                if(itemView.context is AppCompatActivity) {
                    val appContext = itemView.context.applicationContext
                    WorkManager.getInstance(appContext).enqueue(downloadWorkRequest)
                }
            }
        }

        private fun getMimeType(eventImg: String): String? {
            val typeIdentifier = eventImg.take(20)
            return when {
                typeIdentifier.startsWith("iVBORw0KGgo") -> "image/png"
                typeIdentifier.startsWith("/9j/") -> "image/jpeg"
                typeIdentifier.startsWith("R0lGODdh") -> "image/gif"
                else -> null
            }
        }

        private fun openPdf(pdfUri: Uri?, context: Context) {
            if(pdfUri != null) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(pdfUri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "No PDF viewer installed!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "PDF not found!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val type = if(position == selectedPosition) EXPANDED_LAYOUT else NORMAL_LAYOUT
        Log.i("notifyItemChangedLog", "getItemViewType trigerovan: vraca $type, pozicija: $position")
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == EXPANDED_LAYOUT) {
            Log.i("notifyItemChangedLog", "ekspandiran layout u onCreateViewHolder")
            val view = inflater.inflate(R.layout.exp_events_logs_item, parent, false)
            ExpandedEventViewHolder(view)
        } else {
            Log.i("notifyItemChangedLog", "obican layout u onCreateViewHolder")
            val view = inflater.inflate(R.layout.events_logs_item, parent, false)
            EventViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("notifyItemChangedLog", "usao u onBindViewHolder")
        if(holder is EventViewHolder) {
            Log.i("notifyItemChangedLog", "holder je obican")
            holder.bind(events[position])
        } else if (holder is ExpandedEventViewHolder) {
            Log.i("notifyItemChangedLog", "holder je ekspandiran")
            holder.bind(events[position])
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = if(selectedPosition == position) null else position
            Log.i("notifyItemChangedLog", "pre notify $previousPosition | $selectedPosition | $position")
            notifyItemChanged(previousPosition ?: -1)
            notifyItemChanged(selectedPosition ?: -1)
            Log.i("notifyItemChangedLog", "posle notify $previousPosition | $selectedPosition | $position")
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    fun updateEvents(events: MutableList<Event>) {
        Log.i("debugEventsList", "usao u updateEvents")
        events.forEach { Log.i("debugEventsList", "event: ${it.eventName}") }
        this.events = events
        notifyDataSetChanged()
    }

    fun addEvents(events: List<Event>) {
        this.events.addAll(events)
        notifyDataSetChanged()
    }
}