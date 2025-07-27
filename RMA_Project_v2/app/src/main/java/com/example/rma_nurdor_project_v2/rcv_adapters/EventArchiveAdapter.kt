package com.example.rma_nurdor_project_v2.rcv_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.fragments.VolunteersListDialog
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.rcv_adapters.AddEventsAdapter.AddNearestEventsViewHolder
import com.example.rma_nurdor_project_v2.rcv_adapters.EventArchiveAdapter.Companion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventArchiveAdapter(
    private var archivedEvents: List<Event>,
    private val setDisplayedEvent: (Event) -> Unit,
    private val getCityName: (Event) -> String
): RecyclerView.Adapter<EventArchiveAdapter.EventArchiveViewHolder>() {

    val elements
        get() = archivedEvents

    private companion object {
        fun changeDateFormat(inputDateString: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val inputDate = LocalDateTime.parse(inputDateString, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            val outputDate = inputDate.format(outputFormatter)
            return outputDate.toString()
        }

        fun getMimeType(eventImg: String): String? {
            val typeIdentifier = eventImg.take(20)
            return when {
                typeIdentifier.startsWith("iVBORw0KGgo") -> "image/png"
                typeIdentifier.startsWith("/9j/") -> "image/jpeg"
                typeIdentifier.startsWith("R0lGODdh") -> "image/gif"
                else -> null
            }
        }
    }
    
    class EventArchiveViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val viewScope = CoroutineScope(
            SupervisorJob() + Dispatchers.Main.immediate
        )

        fun bind(event: Event, setDisplayedEvent: (Event) -> Unit, getCityName: (Event) -> String) {
            val txtEventName: TextView = itemView.findViewById(R.id.txtEventName)
            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val txtTime: TextView = itemView.findViewById(R.id.txtTime)
            val txtCity: TextView = itemView.findViewById(R.id.txtCity)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val btnOptions: ImageButton = itemView.findViewById(R.id.btnOptions)

            val formattedStartTime = changeDateFormat(event.startTime)
            txtEventName.text = event.eventName
            txtDate.text = formattedStartTime.split(" ")[0]
            txtTime.text = formattedStartTime.split(" ")[1]
            // ovde ide grad
//            viewScope.coroutineContext.cancelChildren()
//            viewScope.launch {
//                try {
//                    txtCity.text = "${getCityName(event).await()} (${event.city})"
//                } catch (e: Exception) {
//                    txtCity.text = itemView.context.getString(R.string.unknown)
//                }
//            }
            txtCity.text = getCityName(event)

            if(event.eventImg != null && event.eventImg != "null") {
                try {
                    val mimeType = getMimeType(event.eventImg)
                    mimeType?.let {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .placeholder(R.drawable.ic_launcher_background)
                            .load("data:$mimeType;base64," + event.eventImg)
                            .override(250, 250)
                            .centerCrop()
                            .into(imageView)
                    }
                } catch (e: Exception) {
                    imageView.setImageResource(R.drawable.ic_launcher_background)
                }
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }

            itemView.setOnClickListener {
                val activity = itemView.context as? AppCompatActivity
                setDisplayedEvent(event)
                activity?.let { appCompatActivity ->
                    val existingDialog = appCompatActivity.supportFragmentManager.findFragmentByTag("VolunteersListDialog")
                    if(existingDialog == null) {
                        VolunteersListDialog().show(appCompatActivity.supportFragmentManager, "VolunteersListDialog")
                    }
                } ?: Toast.makeText(itemView.context, "Item view context is not an AppCompatActivity!", Toast.LENGTH_SHORT).show()
            }

            btnOptions.setOnClickListener {
                //Add options menu - enter collected donations value (also create column for it in database
            }
        }

//        fun cancel() {
//            viewScope.coroutineContext.cancel()
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventArchiveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_archive_item, parent, false)
        return EventArchiveViewHolder(view)
    }

    override fun getItemCount(): Int {
        return archivedEvents.size
    }

    override fun onBindViewHolder(holder: EventArchiveViewHolder, position: Int) {
        holder.bind(archivedEvents[position], setDisplayedEvent, getCityName)
    }

//    override fun onViewRecycled(holder: EventArchiveViewHolder) {
//        super.onViewRecycled(holder)
//        holder.cancel()
//    }

    fun updateContent(events: List<Event>) {
        archivedEvents = events
        notifyDataSetChanged()
    }
}