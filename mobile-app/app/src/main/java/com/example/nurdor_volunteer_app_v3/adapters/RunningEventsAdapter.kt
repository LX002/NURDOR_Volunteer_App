package com.example.nurdor_volunteer_app_v3.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.activity.StatisticsActivity
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.utils.ImageUtils

class RunningEventsAdapter(
    private var events: MutableList<Event>,
    private val findCityByZipCode: (String) -> City?,
    private val endEventByIdEvent: (Int) -> Unit
): RecyclerView.Adapter<RunningEventsAdapter.RunningEventViewHolder>() {

    inner class RunningEventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(event: Event) {
            val eventImageView = itemView.findViewById<ImageView>(R.id.eventImageView)
            val txtEventName = itemView.findViewById<TextView>(R.id.txtEventName)
            val txtCity = itemView.findViewById<TextView>(R.id.txtCity)
            val btnMenu = itemView.findViewById<ImageButton>(R.id.btnMenu)

            ImageUtils.loadImageIntoIntoImageView(event.eventImg, R.drawable.unknown_event, eventImageView, itemView.context)
            txtEventName.text = event.eventName
            txtCity.text = findCityByZipCode(event.city)?.cityName ?: event.city
            btnMenu.setOnClickListener {
                showPopUpMenu(itemView.context, event, it)
            }
        }

        private fun showPopUpMenu(
            context: Context,
            event: Event,
            anchor: View
        ) {
            val popupMenu = PopupMenu(context, anchor)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.menu_running_event, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.itemEndEvent -> {
                        event.idEvent?.let { endEventByIdEvent(it) }
                        true
                    }
                    R.id.itemStatistics -> {
                        val intent = Intent(context, StatisticsActivity::class.java)
                        intent.putExtra("idEvent", event.idEvent)
                        context.startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RunningEventViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_running_events,
                parent,
                false
            )
        return RunningEventViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: RunningEventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(updatedEvents: List<Event>) {
        events = updatedEvents as MutableList<Event>
        notifyDataSetChanged()
    }
}