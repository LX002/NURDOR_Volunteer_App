package com.example.rma_nurdor_project_v2.rcv_adapters

import android.content.Intent
import android.net.Uri
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.fragments.ProfileDetailsDialog
import com.example.rma_nurdor_project_v2.model.Volunteer

class ArchivedEventVolunteersAdapter(private var volunteers: List<Volunteer>): RecyclerView.Adapter<ArchivedEventVolunteersAdapter.ArchivedEventVolunteersViewHolder>() {
    val elements
        get() = volunteers

    class ArchivedEventVolunteersViewHolder(view: View) : ViewHolder(view) {
        fun bind(v: Volunteer) {
            val txtVolunteer: TextView = itemView.findViewById(R.id.txtVolunteer)
            val profileImgView: ImageView = itemView.findViewById(R.id.profileImgView)
            val btnShowMenu: ImageButton = itemView.findViewById(R.id.btnShowMenu)
            btnShowMenu.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.transparent))

            btnShowMenu.setOnClickListener {
                val popup = PopupMenu(itemView.context, it)
                val inflater: MenuInflater = popup.menuInflater
                inflater.inflate(R.menu.menu_archived_event_volunteer, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    val activity = itemView.context as? AppCompatActivity
                    when(item.itemId) {
                        R.id.optionCall -> {
                            activity?.let { a ->
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${v.phoneNumber}"))
                                if(intent.resolveActivity(a.packageManager) != null) {
                                    a.startActivity(intent)
                                }
                            }
                            true
                        }
                        R.id.optionMessage -> {
                            activity?.let { a ->
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${v.phoneNumber}"))
                                if(intent.resolveActivity(a.packageManager) != null) {
                                    a.startActivity(intent)
                                }
                            }
                            true
                        }
                        R.id.optionSendEmail -> {
                            activity?.let { a ->
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_EMAIL, v.email)
                                }
                                if(intent.resolveActivity(a.packageManager) != null) {
                                    a.startActivity(intent)
                                }
                            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchivedEventVolunteersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_volunteers_item, parent, false)
        return ArchivedEventVolunteersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return volunteers.size
    }

    override fun onBindViewHolder(holder: ArchivedEventVolunteersViewHolder, position: Int) {
        holder.bind(volunteers[position])
    }

    fun updateVolunteers(volunteersList: List<Volunteer>) {
        volunteers = volunteersList
        notifyDataSetChanged()
    }
}