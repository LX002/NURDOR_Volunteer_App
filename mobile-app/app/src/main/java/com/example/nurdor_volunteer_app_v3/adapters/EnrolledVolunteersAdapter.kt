package com.example.nurdor_volunteer_app_v3.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.model.Volunteer
import com.example.nurdor_volunteer_app_v3.utils.ImageUtils

class EnrolledVolunteersAdapter(private var enrolledVolunteers: MutableList<Volunteer>): RecyclerView.Adapter<EnrolledVolunteersAdapter.EnrolledVolunteerHolder>() {

    companion object {
        private fun loadImageIntoIntoImageView(img: String?, imageView: ImageView, itemViewContext: Context) {
            img?.let {
                val mimeType = ImageUtils.getMimeType(img)
                mimeType?.let {
                    Glide.with(itemViewContext)
                        .asBitmap()
                        .load("data:$mimeType;base64,$img")
                        .override(250, 250)
                        .centerCrop()
                        .into(imageView)
                }
            } ?: {
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }

    class EnrolledVolunteerHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(volunteer: Volunteer) {
            val imageView: ImageView = itemView.findViewById(R.id.volunteerImg)
            val txtNameSurname: TextView = itemView.findViewById(R.id.txtNameSurname)

            txtNameSurname.text = "${volunteer.name} ${volunteer.surname}"
            loadImageIntoIntoImageView(volunteer.profilePicture, imageView, itemView.context)
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