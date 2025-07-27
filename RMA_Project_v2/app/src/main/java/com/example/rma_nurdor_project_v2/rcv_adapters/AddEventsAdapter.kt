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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.fragments.EventDetailsDialog
import com.example.rma_nurdor_project_v2.model.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddEventsAdapter(
    private var events: List<Event>,
    private val onItemAdded: (Event, CardView, View) -> Unit,
    private val onItemRemoved: (Event, CardView, View) -> Unit,
    private val isSelectedEvent: (Event) -> Boolean,
    private val cardStylePair: Pair<Int, Int>,
    private val textStylePair: Pair<Int, Int>,
    private val onItemClicked: (Event, (Event) -> Unit, (Event) -> Unit) -> Unit,
    private val getCityName: (Event) -> String
) : RecyclerView.Adapter<AddEventsAdapter.AddNearestEventsViewHolder>() {

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

    val elements
        get() = events

    class AddNearestEventsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val viewScope = CoroutineScope(
            SupervisorJob() + Dispatchers.Main.immediate
        )

        fun bind(event: Event, onItemAdded: (Event, CardView, View) -> Unit, onItemRemoved: (Event, CardView, View) -> Unit,
                 isSelectedEvent: (Event) -> Boolean, cardStylePair: Pair<Int, Int>, textStylePair: Pair<Int, Int>,
                 onItemClicked: (Event, (Event) -> Unit, (Event) -> Unit) -> Unit, getCityName: (Event) -> String) {
            val txtCity: TextView = itemView.findViewById(R.id.txtCity)
            val txtEventName: TextView = itemView.findViewById(R.id.txtEventName)
            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val txtTime: TextView = itemView.findViewById(R.id.txtTime)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val btnAdd: ImageButton = itemView.findViewById(R.id.btnAdd)
            val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
            val cardView: CardView = itemView.findViewById(R.id.cardView)

            val formattedStartTime = changeDateFormat(event.startTime)
            txtEventName.text = event.eventName
            txtDate.text = formattedStartTime.split(" ")[0]
            txtTime.text = formattedStartTime.split(" ")[1]

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

            if(isSelectedEvent(event)) {
                cardView.setCardBackgroundColor(cardStylePair.first)
                val textViews = getAllTextViewsIn(cardView)
                textViews.forEach { it.setTextColor(cardStylePair.first) }
            } else {
                cardView.setCardBackgroundColor(cardStylePair.second)
                val textViews = getAllTextViewsIn(cardView)
                textViews.forEach { it.setTextColor(cardStylePair.second) }
            }

            val onItemAdded2 = { e: Event ->
                onItemAdded(e, cardView, itemView)
            }
            val onItemRemoved2 = { e: Event ->
                onItemRemoved(e, cardView, itemView)
            }
            btnAdd.setOnClickListener { onItemAdded2(event) }
            btnRemove.setOnClickListener { onItemRemoved2(event) }

            itemView.setOnClickListener {
                //ubacujes u view model tekuci event i onItemAdded / removed2
                onItemClicked(event, onItemAdded2, onItemRemoved2)
                val activity = itemView.context as? AppCompatActivity
                activity?.let { appCompatActivity ->
                    val existingDialog = appCompatActivity.supportFragmentManager.findFragmentByTag("EventDetailsDIalog")
                    if(existingDialog == null) {
                        EventDetailsDialog().show(appCompatActivity.supportFragmentManager, "EventDetailsDIalog")
                    }
                } ?: Toast.makeText(itemView.context, "Item view context is not an AppCompatActivity!", Toast.LENGTH_SHORT).show()
            }
        }

        private fun getAllTextViewsIn(viewGroup: ViewGroup): List<TextView> {
            val txtViews = mutableListOf<TextView>()
            for(i in 0 until viewGroup.childCount) {
                if(viewGroup.getChildAt(i) is TextView) txtViews.add(viewGroup.getChildAt(i) as TextView)
            }
            return txtViews
        }

//        fun cancel() {
//            viewScope.coroutineContext.cancel()
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddNearestEventsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_event_item, parent, false)
        return AddNearestEventsViewHolder(view)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: AddNearestEventsViewHolder, position: Int) {
        holder.bind(events[position], onItemAdded, onItemRemoved, isSelectedEvent, cardStylePair, textStylePair, onItemClicked, getCityName)
    }

//    override fun onViewRecycled(holder: AddNearestEventsViewHolder) {
//        super.onViewRecycled(holder)
//        holder.cancel()
//    }

    fun updateContent(eventsParam: List<Event>) {
        events = eventsParam
        notifyDataSetChanged()
    }

}