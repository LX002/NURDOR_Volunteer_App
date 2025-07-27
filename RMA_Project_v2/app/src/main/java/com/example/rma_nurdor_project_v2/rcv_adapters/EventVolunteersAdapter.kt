package com.example.rma_nurdor_project_v2.rcv_adapters

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.fragments.ProfileDetailsDialog
import com.example.rma_nurdor_project_v2.model.Volunteer

class EventVolunteersAdapter(
    private var volunteers: List<Volunteer>,
    private val onItemDetailsClick: (Volunteer) -> Unit
) : RecyclerView.Adapter<EventVolunteersAdapter.EventsVolunteersViewHolder>() {

    val elements
        get() = volunteers

    class EventsVolunteersViewHolder(view: View) : ViewHolder(view) {
        fun bind(v: Volunteer, onItemDetailsClick: (Volunteer) -> Unit) {
            val txtVolunteer: TextView = itemView.findViewById(R.id.txtVolunteer)
            val profileImgView: ImageView = itemView.findViewById(R.id.profileImgView)
            val btnShowMenu: ImageButton = itemView.findViewById(R.id.btnShowMenu)

            btnShowMenu.setOnClickListener {
                val popup = PopupMenu(itemView.context, it)
                val inflater: MenuInflater = popup.menuInflater
                inflater.inflate(R.menu.menu_volunteer, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.itemCall -> {
                            // implementiraj otvaranje telefona za broj od volontera
                            true
                        }
                        R.id.itemDetails -> {
                            //uradi ovo i za drugi dijalog!!
                            //proveri da li radi
                            onItemDetailsClick(v)
                            val activity = itemView.context as? AppCompatActivity
                            activity?.let { appCompatActivity ->
                                val existingDialog = appCompatActivity.supportFragmentManager.findFragmentByTag("ProfileDetailsDialog")
                                if(existingDialog == null) {
                                    ProfileDetailsDialog().show(appCompatActivity.supportFragmentManager, "ProfileDetailsDialog")
                                }
                            } ?: Toast.makeText(itemView.context, "Item view context is not an AppCompatActivity!", Toast.LENGTH_SHORT).show()
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }

            if(v.profilePicture.isNullOrBlank()) {
                profileImgView.setImageResource(R.drawable.ic_launcher_background)
            } else {
                val mimeType = getMimeType(v.profilePicture)
                mimeType?.let {
                    Glide.with(itemView.context)
                        .asBitmap()
                        .load("data:$mimeType;base64," + v.profilePicture)
                        .override(250, 250)
                        .centerCrop()
                        .into(profileImgView)
                }
            }

            txtVolunteer.text = "${v.name} ${v.surname}"
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsVolunteersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_volunteers_item, parent, false)
        return EventsVolunteersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return volunteers.size
    }

    override fun onBindViewHolder(holder: EventsVolunteersViewHolder, position: Int) {
        holder.bind(volunteers[position], onItemDetailsClick)
    }

    fun updateVolunteers(volunteersList: List<Volunteer>) {
        volunteers = volunteersList
        notifyDataSetChanged()
    }
}