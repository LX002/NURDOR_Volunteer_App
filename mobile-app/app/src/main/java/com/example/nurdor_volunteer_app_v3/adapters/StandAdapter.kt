package com.example.nurdor_volunteer_app_v3.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.model.Stand

class StandAdapter(var stands: MutableList<Stand>, val isAdmin: Boolean): RecyclerView.Adapter<StandAdapter.StandViewHolder>() {

    class StandViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(stand: Stand) {
            val txtStandName = itemView.findViewById<TextView>(R.id.txtStandName)
            val txtCurrentDonations = itemView.findViewById<TextView>(R.id.txtCurrentDonations)

            txtStandName.text = stand.standName
            txtCurrentDonations.text = stand.totalDonations.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_stand_admin, parent, false)
        return StandViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StandViewHolder, position: Int) {
        holder.bind(stands[position])
    }

    override fun getItemCount(): Int = stands.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateStands(newStands: List<Stand>) {
        stands = newStands as MutableList
        notifyDataSetChanged()
    }
}