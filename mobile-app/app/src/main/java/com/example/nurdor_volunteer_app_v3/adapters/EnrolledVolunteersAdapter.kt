package com.example.nurdor_volunteer_app_v3.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.activity.VolunteerProfileActivity
import com.example.nurdor_volunteer_app_v3.model.Volunteer
import com.example.nurdor_volunteer_app_v3.utils.ContactUtils
import com.example.nurdor_volunteer_app_v3.utils.ImageUtils
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper

class EnrolledVolunteersAdapter(
    private var enrolledVolunteers: MutableList<Volunteer>,
): RecyclerView.Adapter<EnrolledVolunteersAdapter.EnrolledVolunteerHolder>() {

    class EnrolledVolunteerHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(volunteer: Volunteer) {
            itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->

            }

            val imageView: ImageView = itemView.findViewById(R.id.volunteerImg)
            val txtNameSurname: TextView = itemView.findViewById(R.id.txtNameSurname)
            val btnMenu: ImageButton = itemView.findViewById(R.id.btnMenu)

            txtNameSurname.text = "${volunteer.name} ${volunteer.surname}"
            ImageUtils.loadImageIntoIntoImageView(volunteer.profilePicture, R.drawable.unknown_avatar, imageView, itemView.context)
            btnMenu.setOnClickListener {
                showPopUpMenu(itemView.context, volunteer, it)
            }
        }

        private fun showPopUpMenu(
            context: Context,
            volunteer: Volunteer,
            anchor: View
        ) {
            val popupMenu = PopupMenu(context, anchor)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.enrolled_volunteer_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.itemCall -> {
                        ContactUtils.dialPhoneNumber(volunteer.phoneNumber, context)
                        true
                    }

                    R.id.itemSendEmail -> {
                        ContactUtils.composeEmail(arrayOf(volunteer.email), context)
                        true
                    }

                    R.id.itemDetails -> {
                        val intent = Intent(context, VolunteerProfileActivity::class.java).apply {
                            putExtra("nameSurname", "${volunteer.name} ${volunteer.surname}")
                            putExtra("phoneNumber", volunteer.phoneNumber)
                            putExtra("email", volunteer.email)
                            putExtra("address", volunteer.address)
                            putExtra("profilePicture", volunteer.profilePicture)
                        }
                        context.startActivity(intent)
                        true
                    }
                    // TODO() implement add / remove volunteer from event in other activities / fragments / menus
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EnrolledVolunteerHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_enrolled_volunteer, parent, false)
        return EnrolledVolunteerHolder(view)
    }

    override fun onBindViewHolder(
        holder: EnrolledVolunteerHolder,
        position: Int
    ) {
        holder.bind(enrolledVolunteers[position])
    }

    override fun getItemCount() = enrolledVolunteers.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateEnrolledVolunteers(volunteers: List<Volunteer>) {
        enrolledVolunteers = volunteers as MutableList
        notifyDataSetChanged()
    }
}